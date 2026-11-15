# mp4 вместо APNG: видео теста `ultron-allure` на каждом Compose Multiplatform таргете

Дата: 2026-11-03. Ветка: `feature/allure-multiplatform` (worktree `claude/mp4-video-feasibility-9952e9`). Статус: **реализовано** изменением `ultron-allure-mp4-test-video` (корневой `openspec/changes/`): JVM (JCodec 0.2.5, QP 34/28/22 через собственный `RateControl`, ключевой кадр раз в 10), iOS/macOS (`AVAssetWriter`) и браузер (WebCodecs, свой `Mp4Muxer`/`WebmMuxer` вместо npm `mp4-muxer`). Ответы на открытые вопросы — в `design.md` этого изменения («Open Questions»): последний кадр — дубль на `total - 1` мс, нечётные размеры паддятся всегда, QP фиксируется своим `RateControl` (в 0.2.5 есть только `H264FixedRateControl` по битрейту), строгого режима `MP4` нет, webm — запасной путь Chromium без H.264. Продолжает [2026-10-29-test-video-recording-feasibility.md](2026-10-29-test-video-recording-feasibility.md), где для skiko-таргетов был выбран APNG с формулировкой «H.264-кодера без нативного кода нет». Этот документ пересматривает именно этот вывод: источник кадров остаётся прежним (`FrameRecorder` держит PNG-кадры сцены с задержками), вопрос только в кодере и muxer'е на каждом таргете.

Легенда пометок — та же, что в предыдущем документе: **[спайк]** проверено одноразовым кодом в scratchpad этой сессии (код не закоммичен, цифры ниже сняты с него), **[исходники]**, **[доки]**, **[сообщество]**, **[не проверено]**.

## 1. Резюме

Ответ на вопрос «может ли каждая платформа выдать mp4»: **да, все пять skiko-таргетов могут выдать H.264 mp4, который Allure 2 и Allure 3 играют inline как `video/mp4`, без установки хостовых инструментов пользователем.** Три разных кодера, все три проверены спайком на этой машине:

| Таргет | Вердикт | Кодер + muxer | Что добавляется потребителю | Спайк: 30 кадров 480×800, переменные задержки |
|---|---|---|---|---|
| desktop/JVM (macOS, Linux, Windows) | **реализуемо сейчас**, с оговоркой по качеству/размеру | JCodec 0.2.5: `H264Encoder` (Baseline, pure Java) + `MP4Muxer`, timescale 1000, pts из задержек рекордера | jar 2.0 МБ, BSD-2 («FreeBSD»), зависимостей нет; только в `desktopMain`/`jvmMain`, Android-вариант не затронут | 1.3 с (≈30 мс/кадр кодирование + ≈10 мс/кадр декод PNG), **330 КБ**, `avc1` Baseline L4.0, `ffmpeg` декодирует без ошибок **[спайк]** |
| iOS-симулятор | **реализуемо сейчас** | `AVAssetWriter` + `AVAssetWriterInputPixelBufferAdaptor` (VideoToolbox) из `platform.AVFoundation`/`CoreVideo`/`CoreMedia` — platform-библиотеки Kotlin/Native, cinterop писать не нужно | 0 байт (системные фреймворки) | Kotlin/Native `.kexe`, запущенный внутри iPhone 16 / iOS 18.6 через `simctl spawn`: 2.1 с с холодным стартом, **14 КБ**, Constrained Baseline **[спайк]** |
| macOS native | **реализуемо сейчас** | то же, что iOS (общий `nativeMain`) | 0 байт | Тот же код на macos_arm64: 0.3 с, 14.5 КБ, Baseline **[спайк]** |
| js (Karma + ChromeHeadless) | **реализуемо с усилиями** | WebCodecs `VideoEncoder` (`avc1.42001E`, программный OpenH264 в Chrome) + `mp4-muxer` (npm, MIT, 76 КБ UMD) | npm-зависимость тестового бандла; async-точка в `UltronTestRunHook` (правка `ultron-compose`) | Google Chrome 156 headless, `--disable-gpu`: **57 мс**, **24 КБ**; mp4 прошёл через консоль base64 на хост и декодирован `ffmpeg` **[спайк]** |
| wasmJs (Karma + ChromeHeadless) | **реализуемо с усилиями** | то же, что js; интероп через `external`/`@JsFun` для wasm | то же | То же окружение (браузер один и тот же; Kotlin-код не проверялся) **[спайк + не проверено]** |

Общий каркас: `VideoEncoder` (новое имя для нынешнего `AnimationEncoder`) с `expect fun platformVideoEncoder(): VideoEncoder?` в `skikoMain`, `actual` в `jvmMain` / `nativeMain` / `jsWasmMain`; `ApngEncoder` остаётся как fallback, когда платформа вернула `null` или кодирование упало. Политика `videoFormat = AUTO | MP4 | APNG`, `AUTO` = mp4, если доступен.

Что меняется по сравнению с выводом 2026-10-29: «нет H.264-кодера без нативного кода» было верно только для JVM, и там он есть в чистой Java (JCodec). На iOS/macOS кодер — системный, на вебе — встроен в Chrome. Нативный код *в репозитории* не нужен нигде.

## 2. Общее для всех платформ

### 2.1 Источник кадров и обратное декодирование

`FrameRecorder` хранит уже закодированные PNG (ограничение памяти по `maxBytes`, дедупликация по байтам) — это сохраняется. mp4-кодер получает `List<ApngFrame>` (PNG + `delayMs` + подпись) в `onTestFinished` и декодирует PNG обратно в пиксели:

- JVM и native: skiko `Image.makeFromEncoded(png)` → `readPixels(Bitmap)` в `ImageInfo` N32/BGRA (`Image.readPixels(Bitmap)` и `Bitmap.readPixels(ImageInfo, …): ByteArray` есть в skiko 0.9.37.3 **[исходники, javap `skiko-awt`]**; на native тот же common-API skiko **[не проверено]**). Декод PNG в спайке через `ImageIO` стоил ≈10 мс/кадр при 480×800 — skiko будет не хуже.
- Браузер: `createImageBitmap(Blob([png]))` → `new VideoFrame(bitmap, {timestamp, duration})` — даёт `VideoFrame` 480×800 BGRA напрямую из PNG-байтов **[спайк]**.

Альтернатива «кормить кодер сырыми кадрами по мере захвата» отвергнута: ломает кольцевой буфер (`maxFrames`/`maxBytes`, «старые кадры выбрасываются первыми») и дедупликацию, а для JVM-кодера растягивает время теста на каждую операцию. Кодирование в конце теста из PNG — единый путь для всех платформ; его цена — декод N кадров.

### 2.2 Время кадров

mp4 несёт явные pts, поэтому переменные задержки рекордера (`delayMs` с clamp 500…3000 мс, последний кадр 1000 мс) переносятся один в один: timescale 1000, `pts(i) = Σ delay(0..i-1)`, `duration(i) = delay(i)`. Во всех трёх спайках `ffprobe` показал pts `0, 0.5, 1.4, 2.7` — ровно заданные `500/900/1300` мс. Длительность *последнего* кадра: JCodec пишет её из `Packet.duration` (итог 27.0 с); `AVAssetWriter` и `mp4-muxer` показали 26.6 с, то есть длительность последнего кадра надо задавать явно (`endSessionAtSourceTime(last pts + delay)` сработал не так, как ожидалось; `VideoFrame.duration` muxer учитывает, но итог отличается) — проверить при реализации **[спайк, частично]**. Это косметика: плеер остановится на последнем кадре в любом случае.

### 2.3 Что играет Allure

**[исходники, проверено 2026-11-03]** Allure 2 `allure-generator/src/main/javascript/features/attachments/model/builtinAttachmentType.mts`: `video/mp4`, `video/ogg`, `video/webm` → `AttachmentPreviewView.Video`; Allure 3 `packages/web-commons/src/attachments.ts`: те же три типа → `"video"`. Значит:

- `video/mp4` с H.264 играет inline в обеих версиях (как уже делает Android-вариант через `screenrecord`);
- `video/webm` (VP8/VP9) — тоже inline, это легальный fallback для браузера без H.264 (см. 5.4);
- `image/png` (APNG) остаётся универсальным fallback.

### 2.4 Чётные размеры

H.264 4:2:0 требует чётной ширины и высоты. JCodec бросает `IllegalArgumentException("Component 1 width should be a multiple of 2")` на 375×667 и требует `Picture.createCropped(⌈w/16⌉·16, ⌈h/16⌉·16, …, Rect(0,0,w₂,h₂))` с чётным crop; с паддингом 375×667 собрался и `ffprobe` показывает исходные 375×667 **[спайк]**. Для `AVAssetWriter` и WebCodecs нечётные размеры не проверялись **[не проверено]**: безопасная общая стратегия — paddить кадр до чётного размера на стороне кодера (одна строка/столбец дублируется), как в спайке.

### 2.5 Лицензии и размер

| Зависимость | Лицензия | Размер | Совместимость с Apache 2.0 артефакта на Maven Central |
|---|---|---|---|
| `org.jcodec:jcodec:0.2.5` | «FreeBSD» = BSD-2-Clause **[доки, pom]** | 2.0 МБ, 985 классов, без runtime-зависимостей (в pom только junit test-scope) | да, permissive; добавить в NOTICE |
| `mp4-muxer@5.2.1` (npm) | MIT **[доки, package.json]** | 76 КБ UMD / 68 КБ ESM | да; попадает только в тестовый бандл |
| `webm-muxer@5.0.3` (опциональный fallback) | MIT | ≈60 КБ | да |
| AVFoundation / VideoToolbox | системные | 0 | н/д |
| WebCodecs в Chrome | встроено | 0 | н/д |

ffmpeg-обёртки для JVM по-прежнему исключены: JavaCV тянет десятки МБ нативных бинарей на платформу, jave2/humble — GPL/AGPL, Jaffree требует ffmpeg на хосте.

### 2.6 CI без GPU

- JVM: JCodec — чистая Java, одинаков на macOS/Linux/Windows раннерах.
- Браузер: `VideoEncoder.isConfigSupported({codec:"avc1.42001E", hardwareAcceleration:"prefer-hardware"})` → `false`, `"prefer-software"` → `true` в headless с `--disable-gpu` **[спайк]** — кодер программный (OpenH264 внутри Chrome), GPU не нужен.
- iOS/macOS: VideoToolbox выбирает аппаратный кодер при наличии, иначе программный; в симуляторе на этой машине (M-серия) работает. На macOS-раннерах GitHub (виртуализированные M1/M2) аппаратный кодер может быть недоступен, программный H.264 в VideoToolbox есть всегда **[сообщество, не проверено]**.

## 3. desktop/JVM — JCodec

**Вердикт: реализуемо сейчас.** Pure-Java H.264 Baseline + собственный mp4-muxer, API подтверждён `javap` и спайком:

```kotlin
val ch = NIOUtils.writableChannel(file)              // или ByteBufferSeekableByteChannel → ByteArray без файла
val muxer = MP4Muxer.createMP4MuxerToChannel(ch)
val track = muxer.addVideoTrack(Codec.H264, VideoCodecMeta.createSimpleVideoCodecMeta(Size(w, h), ColorSpace.YUV420J))
val enc = H264Encoder.createH264Encoder().apply { keyInterval = 25 }
frames.forEachIndexed { i, f ->
    val rgb = pngToRgbPicture(f.png)                   // skiko readPixels → Picture(RGB, byte = value-128), паддинг до чётного/16
    val yuv = Picture.createCropped(rgb.width, rgb.height, ColorSpace.YUV420J, rgb.crop); RgbToYuv420j().transform(rgb, yuv)
    val ef = enc.encodeFrame(yuv, ByteBuffer.allocate(enc.estimateBufferSize(yuv)))
    track.addFrame(Packet.createPacket(ef.data, pts, 1000, f.delayMs.toLong(), i.toLong(), if (ef.isKeyFrame) KEY else INTER, null))
    pts += f.delayMs
}
muxer.finish()
```

Результаты спайка **[спайк]** (JDK 17, M-серия):

| Размер | Кадров | Всего | PNG-декод | H.264 | Файл | На кадр |
|---|---|---|---|---|---|---|
| 480×800 | 30 | 1.3 с | 0.33 с | 0.90 с | 330 КБ | 11 КБ |
| 480×800 | 100 | 3.8 с | 1.06 с | 2.52 с | 949 КБ | 9.5 КБ |
| 375×667 (паддинг) | 30 | 1.1 с | 0.28 с | 0.71 с | 249 КБ | 8.3 КБ |
| 1024×768 | 30 | 2.4 с | 0.56 с | 1.69 с | 572 КБ | 19 КБ |

Оговорки:

- **Размер и качество.** JCodec кодирует с фиксированным QP (`DumbRateControl` по умолчанию, есть `H264FixedRateControl(qp)`), без B-кадров и с простым поиском движения — тот же контент Chrome сжал в 24 КБ, VideoToolbox в 14 КБ. 10 КБ/кадр при 480×800 всё равно **меньше** PNG-кадра той же сцены в APNG (lossless), а при 200 кадрах — ≈2 МБ, в пределах нынешнего `maxBytes = 20 МиБ`. Настройка `videoQuality` → QP.
- **Время.** ≈40 мс/кадр (с декодом) в конце теста: 200 кадров ≈ 8 с один раз на тест, а не на операцию. Для `TEST_FAILURE` платят только упавшие тесты, APNG-переупаковка почти бесплатна — ещё один аргумент за `AUTO` с возможностью выбрать `APNG`.
- **Заброшенность.** Последний релиз 0.2.5 — июнь 2019 (`maven-metadata.xml` **[доки]**); issue-трекер жив, но релизов нет. Риск принимаем: API стабилен, зависимостей нет, Baseline-H.264 не меняется. При проблемах — форк класса `SequenceEncoder`-уровня невозможен без всего jar, так что fallback всегда APNG.
- **`java.desktop`.** JCodec-core не использует AWT/ImageIO (в jar нет классов `java.awt`/`javax.imageio` **[исходники]**); `jcodec-javase` не нужен. Декод PNG — через skiko, которая у `ultron-allure` уже есть. Модульный `java.base`-only runtime не ломается.
- **Куда класть.** `desktopMain` (или `jvmMain`) `implementation(libs.jcodec)`. `androidMain` не затрагивается: Android-вариант остаётся на `screenrecord`.
- **Linux/Windows CI** — без отличий (чистая Java); не проверялось на них, но и нечему отличаться.

Альтернативы на JVM: других pure-Java H.264-кодеров нет; `javax.imageio` даёт только GIF; VideoToolbox/Media Foundation через JNI — нативный код, отклонено. Если качество JCodec окажется неприемлемым, единственный «честный» путь — `java.lang.foreign` (Panama, JDK 22+) к системным кодерам, что выходит за JDK 17 проекта.

## 4. iOS-симулятор и macOS native — AVAssetWriter

**Вердикт: реализуемо сейчас.** Kotlin/Native 2.2.21 поставляет `platform.AVFoundation`, `CoreMedia`, `CoreVideo`, `VideoToolbox` (и `ImageIO`, `UniformTypeIdentifiers`) для `ios_simulator_arm64`, `ios_arm64`, `ios_x64`, `macos_arm64`, `macos_x64` **[исходники, `~/.konan/…/klib/platform/`]** — cinterop-дефиниций писать не нужно, код живёт в `nativeMain`.

Спайк **[спайк]** (`kotlinc-native -target ios_simulator_arm64 -p program`, запуск `xcrun simctl spawn <iPhone 16, iOS 18.6>`; и `-target macos_arm64` на хосте):

```kotlin
val writer = AVAssetWriter(NSURL.fileURLWithPath(path), AVFileTypeMPEG4, null)
val settings = mapOf(AVVideoCodecKey to AVVideoCodecTypeH264, AVVideoWidthKey to w, AVVideoHeightKey to h,
    AVVideoCompressionPropertiesKey to mapOf(AVVideoAverageBitRateKey to 2_000_000, AVVideoProfileLevelKey to AVVideoProfileLevelH264BaselineAutoLevel))
val input = AVAssetWriterInput(mediaType = AVMediaTypeVideo, outputSettings = settings).apply { expectsMediaDataInRealTime = false }
val adaptor = AVAssetWriterInputPixelBufferAdaptor(assetWriterInput = input, sourcePixelBufferAttributes = null)
writer.addInput(input); writer.startWriting(); writer.startSessionAtSourceTime(CMTimeMake(0, 1000))
frames.forEach { f ->
    CVPixelBufferCreate(kCFAllocatorDefault, w.toULong(), h.toULong(), kCVPixelFormatType_32BGRA, null, pbRef.ptr)
    // lock → скопировать BGRA кадра (skiko readPixels) с учётом bytesPerRow → unlock
    while (!input.readyForMoreMediaData) usleep(1000u)          // backpressure кодера
    adaptor.appendPixelBuffer(pb, CMTimeMake(pts, 1000)); pts += f.delayMs; CVPixelBufferRelease(pb)
}
writer.endSessionAtSourceTime(CMTimeMake(pts, 1000)); input.markAsFinished()
writer.finishWritingWithCompletionHandler { }; while (writer.status == AVAssetWriterStatusWriting) usleep(5000u)
```

| Где | Результат |
|---|---|
| macos_arm64, хост | `status=2 (completed)`, 0.30 с на 30 кадров (включая заливку буферов), 14.5 КБ, `h264` Baseline 480×800, 30 кадров, pts верные |
| ios_simulator_arm64, внутри симулятора | `status=2`, 2.1 с (включает холодный старт процесса в симуляторе), 14.2 КБ, Constrained Baseline, `ffmpeg` декодирует |

Что это закрывает из открытых вопросов прошлого документа: «AVAssetWriter на симуляторе — скорее всего работает, не проверено» → **работает**, причём из Kotlin/Native, не из Swift. `canApplyOutputSettings` вернул `true` на обеих платформах.

Детали реализации:

- **Потоки.** `AVAssetWriter` кодирует на своих внутренних очередях; `appendPixelBuffer` вызывается с потока теста и копирует буфер. Единственное ожидание — `readyForMoreMediaData` (в спайке ни разу не было `false` на 30 кадрах) и завершение `finishWriting`, которое спайк ждал опросом `status` с `usleep` — у тестового потока нет run loop, `dispatch_semaphore` тоже подойдёт. Сцена Compose в это время уже закрыта (`afterTest`), конфликтов с однопоточностью нет.
- **Файл.** `AVAssetWriter` пишет только в файл (URL), не в память: писать во временный файл рядом с результатами (на симуляторе `NSTemporaryDirectory()` или прямо в `SIMCTL_CHILD_ULTRON_ALLURE_RESULTS_DIR`), затем прочитать байты для `attachToTest` или положить файл сразу как вложение. Симулятор видит файловую систему хоста — спайк писал в scratchpad хоста.
- **Пиксели.** Кадр PNG → skiko `Image.makeFromEncoded` → `readPixels` в BGRA 8888 premul → `memcpy` построчно с учётом `CVPixelBufferGetBytesPerRow` (паддинг строк). Альтернатива без копии — `CVPixelBufferCreateWithBytes` поверх массива skiko, но тогда массив должен жить до `appendPixelBuffer` включительно; копия проще и дешевле ошибок.
- **Память.** Один `CVPixelBuffer` на кадр, освобождается сразу; кодер держит несколько кадров в очереди. Ничего похожего на «ImageIO держит все кадры до Finalize».
- **macOS native** использует ровно тот же `nativeMain`; `macosX64`/`iosX64` — тот же API, не проверялись **[не проверено]**.
- **Устройство (`iosArm64`)** — тот же код, тесты на устройстве в проекте не запускаются; не проверялось.

## 5. js и wasmJs — WebCodecs + mp4-muxer

**Вердикт: реализуемо с усилиями.** Технически всё подтверждено, усилия — в интеропе (двух диалектах: js и wasmJs), npm-зависимости и асинхронности.

### 5.1 Что проверено **[спайк]**

Google Chrome 156 — тот самый бинарь, который `karma-chrome-launcher` берёт из `CHROME_BIN`/системы для `useChromeHeadless()` (`composeApp/build.gradle.kts`), флаги `--headless --disable-gpu --disable-dev-shm-usage`; и Chrome for Testing 147 (Playwright):

| Проверка | Результат |
|---|---|
| `VideoEncoder.isConfigSupported` `avc1.42001E` / `42E01E` / `4D401E` / `64001E` / `42001F` | `true` (no-preference и prefer-software), `prefer-hardware` → `false` |
| `vp8`, `vp09.00.10.08`, `av01…` | `true`; `hev1` (HEVC) → `false` |
| `MediaRecorder.isTypeSupported` `video/webm;codecs=vp8`, `video/mp4;codecs=avc1`, `video/mp4` | `true` (Chrome ≥ 126 пишет mp4 из `MediaRecorder`) |
| 30 `VideoFrame` из canvas с `timestamp/duration` → `VideoEncoder(avc1.42001E, avc:{format:"avc"})` → `Mp4Muxer.Muxer({ArrayBufferTarget, fastStart:"in-memory"})` | 57 мс (Chrome 156) / 50 мс (147), 24 КБ, Constrained Baseline, pts верные |
| `createImageBitmap(PNG Blob)` → `new VideoFrame(bitmap)` | 480×800, формат BGRA — PNG-кадры рекордера пригодны без canvas |
| Транспорт: `console.log("… " + btoa(mp4))` → хост → `base64 -d` → `ffprobe`/`ffmpeg` | файл валиден, `moov` на месте (`fastStart`) |

### 5.2 Почему WebCodecs, а не MediaRecorder

`MediaRecorder` на `canvas.captureStream(0)` работает в реальном времени: кадр нужно нарисовать в DOM-`<canvas>`, вызвать `requestFrame()` и реально *ждать* `delayMs` между кадрами — тест с 30 кадрами по 0.5–3 с длился бы 27 с. Кроме того `MediaRecorder` в `video/mp4` пишет фрагментированный mp4 с таймстампами от реальных часов. WebCodecs даёт явные `timestamp`/`duration` и кодирует 30 кадров за 57 мс.

### 5.3 Как встроить

- **Декларации.** Kotlin stdlib не содержит WebCodecs (`VideoEncoder`, `VideoFrame`, `EncodedVideoChunk`) — нужны свои `external` объявления: для js — `external class VideoEncoder(init: dynamic)`, для wasmJs — `external class VideoEncoder : JsAny` плюс `@JsFun` для литералов конфигурации. Два набора деклараций (или `expect` с двумя `actual` в `jsMain`/`wasmJsMain` поверх общего `jsWasmMain`-интерфейса `BrowserVideoEncoder`).
- **npm.** `implementation(npm("mp4-muxer", "5.2.1"))` в `jsMain` и `wasmJsMain` зависимостях `ultron-allure` — Kotlin/JS и Kotlin/Wasm поддерживают npm-зависимости в библиотеках, они попадают в `kotlin-js-store/yarn.lock` потребителя. Альтернатива без npm — свой минимальный muxer (ftyp/moov/mdat с `avcC` из `description` первого chunk'а, ≈300–400 строк) — реалистично, но muxer на 76 КБ с MIT и тестами дешевле.
- **Асинхронность — главное изменение.** `VideoEncoder.flush()` возвращает Promise, синхронного пути нет. Сейчас `UltronTestRunHook.afterTest` и `VideoAttacher.onTestFinished` синхронные, а `UltronTestRunHooks.around` (`ultron-compose`) — `suspend inline`: добавить в `UltronTestRunHook` необязательный `suspend fun afterTestAsync(throwable: Throwable?) {}`, вызываемый из `around` перед `fireAfter`, и сделать `VideoEncoder.encode` suspend (на JVM/native — блокирующий `actual`, на jsWasm — `await()` через `kotlinx-coroutines` `asDeferred`/`await` или ручной `suspendCoroutine` над Promise). Тест на js/wasm и так возвращает Promise (`runComposeUiTest` там асинхронный — см. комментарий в `around`), поэтому ожидание внутри сцены законно. Это правка публичного API `ultron-compose`, обратно совместимая (default-реализация).
- **Транспорт и память.** `UltronAllureResults` держит все файлы в памяти и печатает base64 в консоль. mp4 24 КБ → 32 КБ base64 против APNG той же сцены в сотни КБ — mp4 *снижает* нагрузку на транспорт. Лимиты `videoLimits` остаются.
- **Worker'ы не нужны:** WebCodecs доступен в main thread; в wasmJs нет потоков, и это не мешает — кодер работает в процессах Chrome.

### 5.4 Fallback: Chromium без проприетарных кодеков

Google Chrome, Chrome for Testing и Edge включают OpenH264; **open-source Chromium** (`proprietary_codecs=false`, например пакеты Debian/Alpine в Docker-образах) может не иметь `avc1` в WebCodecs **[сообщество, не проверено — такого бинаря на машине нет]**. Поэтому `actual` для браузера обязан проверять `isConfigSupported` во время выполнения и деградировать: `avc1` → mp4; иначе `vp09`/`vp8` → `video/webm` через `webm-muxer` (MIT, тот же автор) — Allure 2/3 играют `video/webm` (2.3); иначе `null` → APNG. Firefox/Safari для Karma в проекте не используются; у Safari WebCodecs `VideoEncoder` есть с 16.4 — не проверялось.

## 6. Форма `expect`/`actual` и изменения в коде

```
ultron-allure/src/skikoMain/.../video/
  VideoEncoder.kt           interface VideoEncoder { val extension; val mimeType; suspend fun encode(frames: List<ApngFrame>): ByteArray }
                            (переименованный AnimationEncoder; ApngEncoder реализует его синхронно)
  PlatformVideoEncoder.kt   internal expect fun platformVideoEncoder(options: VideoOptions): VideoEncoder?
                            // null = на этом таргете/в этом окружении mp4 недоступен
  VideoFrames.kt            internal fun decodeFrame(png: ByteArray): RgbaFrame   // skiko makeFromEncoded + readPixels (JVM/native)
                            internal fun padToEven(frame): RgbaFrame
  VideoAttacher.kt          выбирает кодер: platformVideoEncoder() ?: ApngEncoder при videoFormat=AUTO;
                            при ошибке encode() логирует и повторяет ApngEncoder
ultron-allure/src/jvmMain/.../video/PlatformVideoEncoder.jvm.kt       actual → JcodecMp4Encoder (desktopMain dependency: libs.jcodec)
ultron-allure/src/nativeMain/.../video/PlatformVideoEncoder.native.kt actual → AvAssetWriterMp4Encoder
ultron-allure/src/jsWasmMain/.../video/PlatformVideoEncoder.jsWasm.kt actual → WebCodecsMp4Encoder (isConfigSupported → mp4 | webm | null)
ultron-allure/src/jsMain, wasmJsMain                                  external-декларации WebCodecs и mp4-muxer/webm-muxer, npm-зависимости
ultron-compose/.../UltronTestRunHooks.kt                              suspend fun afterTestAsync в UltronTestRunHook, вызов в around()
ultron-allure/.../config/UltronAllureConfig.skiko.kt                  videoFormat: AUTO | MP4 | APNG (default AUTO), videoQuality
docs/docs/common/allure-multiplatform.md                              раздел «Video of the test»: mp4 по умолчанию, APNG — fallback, таблица «что на каком таргете»
```

Семантика: `encode` получает те же `ApngFrame` (PNG + `delayMs`), так что `FrameRecorder`, лимиты, дедупликация, `captureVideoFrame` и тесты рекордера не меняются. Имя вложения остаётся `video`; расширение и MIME берутся из кодера (`mp4`/`video/mp4`, `webm`/`video/webm`, `png`/`image/png`). `ApngInfo` остаётся для APNG; для mp4 добавить `Mp4Info.isMp4(bytes)` (проверка `ftyp`) для smoke-тестов.

## 7. Трудоёмкость и риски

| Часть | Оценка | Главный риск |
|---|---|---|
| Общий каркас: интерфейс, `AUTO`/fallback, конфиг, декод PNG через skiko, паддинг, unit-тесты с golden-mp4 (`ftyp`/`moov`, число сэмплов) | 2 дня | — |
| JVM / JCodec | 2 дня | размер файла в 10 раз больше, чем у системных кодеров; заброшенный проект; при 1024×768 — ≈60 мс/кадр |
| iOS/macOS / AVAssetWriter | 3 дня (код + тест в `composeApp:iosSimulatorArm64Test` и `macosArm64Test`) | длительность последнего кадра; `bytesPerRow`; программный кодер в macOS-VM CI **[не проверено]** |
| Браузер / WebCodecs | 5–6 дней (два диалекта интеропа, npm, suspend-хук в `ultron-compose`, определение поддержки, webm-fallback, Karma-прогон js и wasm) | Chromium без H.264 → webm; `kotlin-js-store/yarn.lock` потребителей меняется; первый suspend-хук в `UltronTestRunHook` |
| Документация, release notes | 1 день | — |
| **Итого** | **≈13–14 дней** (≈3 недели) | |

Порядок внедрения: каркас → iOS/macOS (самый чистый результат, нулевая зависимость) → JVM → браузер. Каждый этап самостоятельно полезен, потому что `AUTO` на остальных таргетах продолжает отдавать APNG.

## 8. Открытые вопросы

1. Длительность последнего кадра у `AVAssetWriter` и `mp4-muxer` (26.6 с вместо 27.0 в спайке) — задавать явно и проверить `ffprobe -show_entries stream=duration`.
2. Нечётные размеры у `AVAssetWriter`/WebCodecs — paddить до чётного всегда или только для JCodec.
3. JCodec: подобрать QP (`H264FixedRateControl`) и `keyInterval` под «кадр на операцию»: каждый кадр сильно отличается от предыдущего, межкадровое сжатие даёт мало; возможно, выгоднее все кадры ключевые.
4. WebCodecs на open-source Chromium (без `avc1`) — проверить в Docker-образе с `chromium` перед объявлением дефолта `AUTO = mp4` в документации.
5. Программный кодер VideoToolbox на macOS-раннерах GitHub Actions (виртуализация) — прогнать `composeApp:iosSimulatorArm64Test` в CI, когда появится такой job (сейчас CI ничего из desktop/iOS/wasm-тестов не запускает).
6. Нужен ли `videoFormat = MP4` как строгий режим (ошибка вместо fallback) для CI, который хочет гарантированно mp4.

## 9. Источники

- Спайки этой сессии (не закоммичены): JCodec — `Spike.java` на `H264Encoder`/`MP4Muxer`/`Packet.createPacket`; Kotlin/Native — `Spike.kt` с `AVAssetWriter`, собран `kotlinc-native` 2.2.21 для `macos_arm64` и `ios_simulator_arm64`, запущен через `xcrun simctl spawn`; браузер — `spike.html` + `playwright-core`, Google Chrome 156 / Chrome for Testing 147 в headless.
- Allure 2 `builtinAttachmentType.mts`: https://github.com/allure-framework/allure2/blob/main/allure-generator/src/main/javascript/features/attachments/model/builtinAttachmentType.mts ; Allure 3 `attachments.ts`: https://github.com/allure-framework/allure3/blob/main/packages/web-commons/src/attachments.ts ; Allure attachments: https://allurereport.org/docs/attachments/
- JCodec: https://github.com/jcodec/jcodec ; Maven: https://repo1.maven.org/maven2/org/jcodec/jcodec/0.2.5/ (лицензия FreeBSD в pom, `maven-metadata.xml`: последний релиз 2019-06-16)
- mp4-muxer: https://github.com/Vanilagy/mp4-muxer ; webm-muxer: https://github.com/Vanilagy/webm-muxer
- WebCodecs: https://developer.mozilla.org/docs/Web/API/VideoEncoder ; https://developer.mozilla.org/docs/Web/API/VideoFrame/VideoFrame ; MediaRecorder mp4 в Chrome 126: https://developer.chrome.com/blog/mediarecorder-mp4
- AVAssetWriter: https://developer.apple.com/documentation/avfoundation/avassetwriter ; `AVAssetWriterInputPixelBufferAdaptor`: https://developer.apple.com/documentation/avfoundation/avassetwriterinputpixelbufferadaptor
- Kotlin/Native platform libs: https://kotlinlang.org/docs/native-platform-libs.html
- karma-chrome-launcher: https://github.com/karma-runner/karma-chrome-launcher/blob/master/index.js ; Chromium `proprietary_codecs`: https://chromium.googlesource.com/chromium/src/+/main/docs/linux/build_instructions.md
- skiko `Image.kt`/`Bitmap.kt`: https://github.com/JetBrains/skiko/tree/master/skiko/src/commonMain/kotlin/org/jetbrains/skia
