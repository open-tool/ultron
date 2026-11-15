# Запись видео прогона UI-тестов в Ultron — оценка осуществимости

Дата: 2026-10-29, ревизия 2026-10-30 по итогам независимого ревью. Ветка: `feature/allure-multiplatform`. Статус: **реализовано 2026-10-30** по рекомендованному MVP — APNG-запись по операциям в `ultron-allure` (`video/`, после слияния модулей 31 октября 2026 бывший `ultron-allure-multiplatform` живёт в его `commonMain`/`skikoMain`), `screenrecord` в `androidMain` того же модуля (`screenrecord/`, `ScreenRecordRunListener`), сбор браузерных результатов задачей `collectWasmAllureResults`; проверено на desktop, iOS-симуляторе, wasmJs и эмуляторе Pixel_7 API 33. Документ сохранён как обоснование решений.

Легенда пометок в тексте:

- **[ревью]** — исправлено или добавлено после ревью (проверено рецензентом по исходникам AOSP `UiAutomationConnection.java`, skiko, allure2 и кода репозитория);
- **[спайк]** — проверено одноразовым desktop-тестом в этом репозитории (удалён, артефакты лежали в scratchpad сессии);
- **[исходники]** — взято из исходного кода (skiko, compose-multiplatform-core, AOSP, android-test, allure-kotlin);
- **[доки]** — официальная документация;
- **[сообщество]** — блоги / StackOverflow / issue-трекеры, не подтверждено первоисточником;
- **[не проверено]** — предположение, требует проверки перед реализацией.

## 1. Резюме

| Таргет | Реалистичный подход | Ограничения | Трудоёмкость |
|---|---|---|---|
| Android (Espresso / UI Automator, `sample-app`) | `UiAutomation.executeShellCommand("screenrecord …")` в `testStarted`; в `testFinished` — отдельными вызовами `pidof screenrecord` / `ps -A -o PID,ARGS`, разбор вывода в Kotlin, `kill -2 <pid>`; затем файл → Allure `"video/mp4"` | `executeShellCommand` не вызывает shell — `$(…)`, пайпы и кавычки не работают **[ревью]**; PID наружу не отдаётся; лимит 180 с на API < 34; файл пишется под shell-UID, нужен перенос через `cat`; на эмуляторе работает не на всех образах; `MimeType.MP4` в `ultron-common` сейчас `audio/mp4` **[ревью]**; `MediaProjection` — не для библиотеки (диалог согласия, FGS на API 34+) | M (4–6 дней) + отдельная задача на instrumented-джоб в CI, которого сейчас нет **[ревью]** |
| Compose Multiplatform desktop/JVM | Покадрово: `captureToImage()` после каждой операции Ultron (listener), финальный кадр в `onTestFailed`; сборка в **APNG** (переупаковка `IDAT` из skiko-PNG в `fdAT`, без zlib) по умолчанию, **animated WebP** как опция; MP4 только через JCodec | В ui-test нет колбэка «кадр отрисован», только опрос; `captureToImage()` делает `waitForIdle()` (блокирующий, без таймаута) и не может сниматься после закрытия сцены; кадр снимается на операцию, а не на итерацию retry **[ревью]**; skiko кодирует только PNG/JPEG/WEBP; `image/webp` в Allure 2 показывается только с 2.35.0 **[ревью]**; GIF — 256 цветов и дорого | M (5–8 дней на recorder + APNG/WebP writer с тестами на прозрачные кадры + дедупликация и буфер + конфиг + Allure + docs) **[ревью]** |
| Compose Multiplatform iOS / macOS (Kotlin/Native) | Та же покадровая запись (общий `commonMain`-код: захват + APNG/WebP writer); при желании — H.264 MP4 через `platform.AVFoundation.AVAssetWriter` или GIF/APNG через `platform.ImageIO` | Результаты и так идут через host-директорию (`SIMCTL_CHILD_ULTRON_ALLURE_RESULTS_DIR`); AVAssetWriter на симуляторе — «скорее всего работает», не проверено; кадры кодировать сразу и держать в кольцевом буфере — сырые `Bitmap` по 3 MiB хранить нельзя **[ревью]** | S поверх desktop (общий код) / +M для AVAssetWriter |
| Web (js / wasmJs, Karma + headless Chrome) | Те же покадровые кадры в in-memory sink `UltronAllureResults` (APNG/WebP — общий код); альтернатива `canvas.captureStream(0)` + `MediaRecorder` (WebM) через вспомогательный `<canvas>` | Тестовая сцена рендерится в offscreen `Surface`, а не в DOM-canvas — для `MediaRecorder` нужен blit каждого кадра в `<canvas>` + `requestFrame()`; `MediaRecorder` асинхронный, файл нужно выгрузить из браузера (Karma middleware); все вложения прогона живут в памяти и уходят в консоль base64 (+33 %) — нужны `TEST_FAILURE` по умолчанию и лимит байт **[ревью]** | S для APNG/WebP (общий код) / M для WebM |

Сквозной вывод: **единственная по-настоящему «видео»-запись без хостовой обвязки есть только на Android (`screenrecord`)**. На всех skiko-таргетах честного реального времени нет (виртуальные часы, рендер по требованию), поэтому «видео» там — это последовательность кадров, снятых в детерминированных точках (операция Ultron / тик часов), и собранных в анимированный контейнер. Это даже удобнее для отчёта: каждый кадр соответствует шагу.

Главные цифры спайка на desktop (1024×768, M-серия Mac) **[спайк]**:

| Действие | Стоимость |
|---|---|
| `captureToImage()` + `advanceTimeByFrame()` | ≈1.4 мс/кадр |
| PNG encode (skiko) | ≈24 мс/кадр, ≈5 KiB |
| JPEG q80 (skiko) | ≈2 мс/кадр, ≈6 KiB |
| WebP q80 (skiko) | ≈22 мс/кадр, ≈2 KiB |
| 60 кадров → animated WebP (свой muxer) | 1.3 с, 123 KiB |
| 60 кадров → APNG (свой writer, deflate level 1, попиксельный `getColor`) | 0.57 с, 1 MiB |
| 60 кадров → GIF (`javax.imageio`) | 5.4 с, 151 KiB |

Все три файла распознаются `ffprobe -count_frames` как анимации по 60 кадров (`gif`, `apng`, `webp_anim`). **[ревью]** Это слабое доказательство: демультиплексор ffmpeg принял файл, но корректное воспроизведение в браузере (особенно для WebP с `ALPH` и флагами blend/dispose) не проверялось; код спайка удалён, цифры невоспроизводимы. При реализации спайк нужно сохранить как unit-тест, а файлы проверять `webpmux -info` / `anim_dump` из libwebp и открывать в Chrome и Safari. Спайк снимал почти пустую непрозрачную сцену (2 KiB на WebP-кадр, 5 KiB на PNG при 1024×768) — размеры на реальных экранах будут заметно больше.

## 2. Android (ultron-android / ultron-allure)

### 2.1 `screenrecord` через `UiAutomation.executeShellCommand` — рекомендуемый путь

Что есть **[исходники, AOSP `UiAutomation.java`, `UiAutomationConnection.java`]**: `UiAutomation.executeShellCommand(String): ParcelFileDescriptor` (API 21+) запускает команду от shell-UID — как `adb shell`. Возвращает только stdout-дескриптор, **PID процесса не отдаётся**.

**[ревью]** Важные детали реализации в `UiAutomationConnection.executeShellCommand`:

- процесс запускается через `Runtime.getRuntime().exec(command)` — перегрузка `exec(String)`, которая режет строку по пробелам и **не вызывает shell**. Подстановки `$(…)`, пайпы `|`, перенаправления и кавычки не обрабатываются: `kill -2 $(pidof screenrecord)` запустит `kill` с буквальным аргументом `$(pidof`. Обойти через `sh -c '…'` нельзя — токенизатор не понимает кавычек. Каждая команда должна быть одним простым вызовом, а вся логика (разбор `pidof`/`ps`, выбор PID по имени файла) — в Kotlin;
- `process.destroy()` вызывается только cleanup-потоком после того, как stdout дошёл до EOF. Если тест закроет свой конец pipe, а `screenrecord` что-то напишет в stdout, поток-копировщик получит EPIPE и `destroy()` убьёт процесс SIGKILL-ом — mp4 будет битым (нет `moov`). Поэтому дескриптор нужно держать открытым (и читать) до штатной остановки;
- `UiAutomation` нужно получать тем же getter'ом и с теми же флагами, что `UiAutomationScreenshoter.getUiAutomation()` (`ultron-common/src/androidMain/kotlin/com/atiurin/ultron/screenshot/UiAutomationScreenshoter.kt`): переподключение с другими флагами может сломать `UiDevice` **[не проверено]**.

Почему именно shell-UID, а не `Runtime.exec` **[сообщество]**: запуск `screenrecord` из процесса приложения упирается в SELinux (`avc: denied`, видео из одного кадра) — SO 53806119.

Схема **[сообщество + исходники `screenrecord.cpp`]**:

1. `testStarted`: `executeShellCommand("screenrecord --time-limit <N> --size 720x1280 --bit-rate 2000000 /data/local/tmp/ultron_<uniqueId>.mp4")` с уникальным именем файла. Дескриптор stdout читать в фоне до остановки (см. EPIPE выше). Старт `screenrecord` не мгновенный — первые сотни миллисекунд теста в запись не попадут (гипотеза: 0.5–1 с **[не проверено]**); при необходимости ждать появления файла или добавлять задержку.
2. `testFinished`/`testFailure` **[ревью]**: отдельным вызовом `executeShellCommand("pidof screenrecord")` (toybox, API 23+) или `executeShellCommand("ps -A -o PID,ARGS")` получить список процессов, в Kotlin выбрать PID, чья командная строка содержит наш уникальный файл, затем `executeShellCommand("kill -2 <pid>")`. SIGINT/SIGHUP ловятся `signalCatcher` и корректно закрывают muxer (пишется `moov`); SIGKILL оставляет битый mp4. Выбор PID по имени файла решает проблему параллельных инструментальных прогонов на одном устройстве.
3. Подождать завершения процесса: читать stdout до EOF (он закроется, когда процесс выйдет) либо опрашивать `pidof`.
4. Перенести файл в место, доступное тестовому процессу: `executeShellCommand("cat /data/local/tmp/… ")` → поток в файл кеша приложения (как сейчас `createCacheFile()`), либо `cp` в `/sdcard/…` (на API 29+ scoped storage — `cat` надёжнее).
5. Приложить: `Allure.attachment(name, file.inputStream(), "video/mp4", "mp4")` через `AttachUtil.attachFile`. **[ревью]** `MimeType` живёт в `ultron-common/src/commonMain/kotlin/com/atiurin/ultron/file/MimeType.kt`; `WEBP("image/webp")` там уже есть, а `MP4` объявлен как `"audio/mp4"` — с ним Allure не покажет плеер (видео-вьюер принимает только `video/mp4`, `video/ogg`, `video/webm`). Нужно либо добавить `VIDEO_MP4(".mp4", "video/mp4")` в `ultron-common` (исправление самого `MP4` ломает совместимость), либо передавать строку `"video/mp4"` явно. То есть для Android-пути правка в `ultron-common` всё-таки нужна или желательна.

Ограничения **[исходники `screenrecord.cpp`, сообщество]**:

- `kMaxTimeLimitSec = 180` по умолчанию; на новых релизах (ориентир — API 34) `--time-limit 0` снимает лимит, на старых 180 с — максимум. Marathon на API < 34 склеивает несколько файлов и честно пишет про потерю кадров между ними. Для Ultron разумно: задать `--time-limit` = тест-таймаут, а при истечении лимита либо перезапускать запись (с дыркой), либо документировать ограничение.
- Эмулятор: на современных x86_64/arm64 образах работает (программный AVC-кодер), на старых образах `screenrecord` может отсутствовать (`screenrecord: not found`) или падать с `Encoder failed`, лечится `--size`. Матрицы «API × образ» в доступных источниках нет — нужно прогнать в CI **[не проверено]**.
- Запись идёт в отдельном процессе, тестовый поток не нагружается — в отличие от опроса `takeScreenshot()`.
- Официального документа, одобряющего `screenrecord` из инструментации, нет; ближайший первоисточник — `platform_testing` в AOSP (collector для screen record, но он host-driven).

Куда встраивать: базовый класс `UltronRunListener` (`ultron-common/src/androidMain/.../runner/UltronRunListener.kt`) с `testStarted/testFinished/testFailure`, события раздаёт `UltronRunInformer`; в `ultron-allure` на нём построены `ScreenshotAttachRunListener`, `WindowHierarchyAttachRunListener`, `UltronLogAttachRunListener`, `LogcatAttachRunListener` (последний запускает shell-команду через `Runtime.exec`). Новый `ScreenRecordRunListener` + `AllureConfigParams.recordVideoPolicy: Set<AllureAttachStrategy>` + регистрация в `UltronAllureConfig.modify()`.

**[ревью]** Две вещи, которых в существующих листенерах нет:

- все attach-листенеры прикладывают артефакты в `testFailure`; вложение в `testFinished` никто ещё не делал. `UltronAllureTestRunner` регистрирует `UltronRunListener` аргументом `listener` до `super.onCreate`, поэтому он, вероятно, вызывается раньше Allure-листенера — но порядок `testFinished` относительно Allure-листенера нужно проверить, иначе вложение попадёт в уже закрытый тест-кейс **[не проверено]**. Так же важен порядок относительно `UltronAllureResultsTransferListener`, который копирует результаты в `testFinished`: видео должно попасть в результаты до копирования;
- значения `TEST_FINISH` в `AllureAttachStrategy` сейчас нет — ни в `ultron-allure/.../config/AllureAttachStrategy.kt`, ни в `ultron-allure-multiplatform/.../config/AllureAttachStrategy.kt` (там только `TEST_FAILURE`, `OPERATION_FAILURE`, `OPERATION_SUCCESS`, `OPERATION_FINISH`). Это новое значение с новой семантикой; добавлять его нужно в оба enum ради паритета, который обещает комментарий в multiplatform-enum.

### 2.2 `MediaProjection` — не рекомендуется

**[доки]**: требуется согласие пользователя на каждую сессию (`createScreenCaptureIntent()`), на API 34+ ещё и foreground service типа `mediaProjection` с объявлениями в манифесте приложения под тестом. Обход через `appops set <pkg> PROJECT_MEDIA allow` **[сообщество + AOSP-коммит]** известен и даже признан в коде фреймворка, но поведение различается по версиям/OEM, а на 34+ FGS всё равно нужен. Для библиотеки, которую подключают к чужому приложению, это требует правок продуктового манифеста — неприемлемо. Для CTS есть, для библиотек — прецедентов не найдено.

### 2.3 Хостовая обвязка (вне инструментального процесса)

- UTP/AGP **[исходники `tools/base/utp`]**: плагинов видео нет (`additional-test-output`, `retention` (снапшот эмулятора при падении), `device-info`, `coverage`, `logcat`, `apk-installer`, `emulator-control`). `emulatorControl {}` (AGP 8.1, incubating) лишь отдаёт тесту gRPC-эндпоинт эмулятора; видео-RPC там не задокументирован **[не проверено]**.
- `androidx.test.services` `TestStorage` **[исходники]**: `PlatformTestStorageRegistry.getInstance().openOutputFile("video/<test>.mp4")` → AGP забирает в `build/outputs/connected_android_test_additional_output/` (и `managed_device_android_test_additional_output/` для GMD). Требует `useTestStorageService=true` и установленных test-services. Это **транспорт**, а не запись; полезен как альтернатива Allure-вложению, если пользователь не использует Allure.
- Gradle Managed Devices: видео только у Firebase Test Lab (`firebaseTestLab.testOptions.results.recordVideo = true`), `gcloud firebase test android run --record-video`, Flank `record-video: true` **[доки]**.
- Хостовые раннеры: Marathon пишет видео через `adb shell screenrecord` (1280×720, 1 Mbps, 180 с, склейка на API < 34); Spoon (архив 2025) и Composer (архив 2019) — только GIF из скриншотов. Appium — тот же `screenrecord` с лимитом 180 с.
- `adb emu screenrecord start …` **[доки]** — только с хоста, из теста недоступно.

Вывод: хостовая обвязка — чужая зона ответственности (CI/раннер), Ultron может лишь документировать, как класть своё видео рядом с результатами Allure. Внутрипроцессный путь только один — 2.1.

### 2.4 Allure и видео

**[доки allurereport.org/docs/attachments]**: отчёт воспроизводит `video/mp4`, `video/ogg`, `video/webm` через `<video>`. **[сообщество, allure2#1358]**: mp4 должен быть H.264 (`avc1`) с финализированным `moov`, иначе — только ссылка на скачивание. `screenrecord` даёт именно H.264 mp4.

**[ревью, исходники allure2 `allure-generator/.../utils/attachmentType.js`, `AttachmentView.hbs`; Allure 3 `packages/web-commons/src/attachments.ts`]** Анимированные изображения:

- изображения рендерятся обычным `<img>` (Allure 2) или blob-URL в `<img>` (Allure 3), поэтому APNG, GIF и animated WebP браузер анимирует сам — открытый вопрос «анимирует ли viewer» закрыт;
- но в Allure 2 тегов 2.30.0, 2.32.0 и 2.34.0 типа `image/webp` в таблице нет (есть bmp, gif, tiff, jpeg, jpg, png и `image/*`). Поддержку добавил коммит «render webp attachments as images (fixes #1191, via #2978)» от 2025-07-10; первый тег с ним — **2.35.0**. В Allure 3 `image/webp` есть;
- `image/png` (APNG) и `image/gif` показываются как изображения во всех версиях. Многие CI закрепляют старый `allure-commandline` (в этом репозитории отчёт собирался 2.30.0), поэтому **дефолт `image/webp` небезопасен**; дефолтом должен быть APNG как `image/png`, WebP — опция с пометкой «нужен Allure 2.35+ или Allure 3».

## 3. Compose Multiplatform на skiko (desktop/JVM, iOS, macOS)

### 3.1 Источник кадров

**[исходники `ComposeUiTest.skiko.kt`, jb-main]**:

- `SkikoComposeUiTest` рисует в приватный `Surface.makeRasterN32Premul(width, height)`; `captureToImage()` = `waitForIdle()` + `surface.makeImageSnapshot()`; `redraw()` = `measureAndLayout` + `draw` без продвижения часов.
- Рендер-петля (`withRenderLoop`, 16 мс по виртуальным часам, `performFrame` + `redraw`) приватна; колбэков `invalidateDraw`/`invalidateLayout` нет (пустые лямбды). **Нет точки «кадр отрисован»** — только опрос.
- Следствие: рекордер либо (а) снимает кадр в точках, где тест и так синхронизирован — до/после операции Ultron (`UltronLifecycleListener.before/after`), либо (б) при `mainClock.autoAdvance = false` крутит `advanceTimeByFrame()` + `captureToImage()` — это документированный Android-паттерн тестирования анимаций с оговоркой о «серьёзной нагрузке на производительность».
- Режим (б) ломает семантику ожиданий Ultron (поллинг `OperationExecutor` идёт по `nowMs()` в реальном времени), поэтому для MVP берём **(а): кадр после каждой операции** плюс «кадр при падении теста» (последний уже есть как `test_failure_screenshot`). Кадр *перед* операцией — только опция, не дефолт (см. ниже про `waitForIdle`).
- **[ревью] Кадр на операцию ≠ кадр на итерацию retry.** `AbstractOperationLifecycle.execute` вызывает `before` один раз до `operationProcessor.process`, а `afterSuccess`/`afterFailure`/`after` — один раз после всей retry-петли `OperationExecutor`, включая повторы `execWithAssertion`. Значит, «видео» не покажет промежуточные состояния во время ожидания (исчезающий элемент, мигающий лоадер) — а именно это чаще всего нужно при разборе флейка. Кадр на каждую итерацию потребует новой точки в `ultron-common` (например, хук в `OperationExecutor` рядом с `doBetweenOperationRetry`); для MVP это явное ограничение. Вложенные операции (всё, кроме `operationsExcludedFromListeners` — сейчас `GET_LIST_ITEM`, `GET_LIST_ITEM_CHILD`) тоже вызывают листенеры и дадут лишние кадры. Soft assertions на запись не влияют: `afterFailure` срабатывает, тест падает позже на `verify`, `onTestFailed` всё равно приходит.
- **[ревью] `waitForIdle()` внутри `captureToImage()`** (`ComposeUiTest.skiko.kt`, строки 352–360) — блокирующий цикл без таймаута (в коде стоит TODO). При `autoAdvance = true` он сам двигает виртуальные часы (`advanceTimeByFrame`), пока сцена не станет idle; бесконечные анимации при этом отменяет `InfiniteAnimationPolicy` (строки 211–217). Следствия: кадр «до» операции продвигает время и доигрывает анимации до шага теста (вероятно, почти идемпотентно, потому что fetch семантики и так ждёт idle — гипотеза), а сцена, которая никогда не становится idle, повесит тест так, что его не прервёт даже `testTimeout`. Отсюда: по умолчанию только кадр «после»; один захват на операцию делить с `AttachOnOperationListener` (сейчас при `OPERATION_FAILURE` получится два `captureToImage` подряд); `RecordFrameListener` обязан ловить все исключения — `AbstractOperationLifecycle` исключения листенеров не перехватывает, и исключение в `before` оставит открытый Allure-step.
- Стоимость (а) **[спайк + ревью]**: ≈1.4 мс на захват (мерилось без двух копий пикселей, которые делает текущий `captureRootPng`: `captureToImage().asSkiaBitmap()` → `Image.makeFromBitmap` — реальная цифра выше, нужен замер **[не проверено]**), 2–24 мс на кодирование. При немедленном кодировании и 50 операциях — ≈1.1–1.3 с на тест для *каждого* теста при `TEST_FAILURE`, вдвое больше с кадром «до». Ленивый вариант «держать сырые `Bitmap` по 3 MiB и кодировать в `afterTest`» нереалистичен: 100 операций × 2 кадра = 600 MiB нативной памяти, освобождаемой только на GC/`close()`; на wasm это недопустимо. Поэтому: **кодировать сразу, держать кольцевой буфер последних N закодированных кадров, явно `close()` для `Bitmap`/`Image`**. Дедупликация обязательна: кадр «после» операции N почти всегда совпадает с кадром «до» N+1 и с кадром после «пустых» assert-операций — сравнивать байты закодированного кадра или хеш пикселей и вместо нового кадра удлинять предыдущий.

Важно (уже учтено в архитектуре): кадры падения нужно снимать в `onTestFailed` (сцена жива), а финальную сборку контейнера можно делать и в `afterTest` — там сцена не нужна, только байты.

### 3.2 Кодеки без внешних бинарей

skiko **[исходники `Image.kt`, `Image.cc`; спайк]**: `Image.encodeToData(format, quality)` — только PNG/JPEG/WEBP; на JVM `EncodedImageFormat.GIF` бросает `RuntimeException("Only PNG, JPEG and WEBP formats are supported")` **[спайк]**, а в `nativeJsMain/cpp/Image.cc` та же ветка — `default: break` и возврат `null` без исключения **[ревью, исходники]** — код должен обрабатывать оба случая. Skia имеет `SkWebpEncoder::EncodeAnimated`, но skiko его не биндит (в sources 0.9.37.3 `Codec.kt` умеет только декодирование; enum `EncodedImageFormat` содержит `GIF`, кодировщика нет) **[исходники, подтверждено ревью]**. Версия skiko в репозитории — 0.9.37.3 (транзитивно от Compose 1.10.0).

Варианты контейнера, одинаковые для всех skiko-таргетов (чистый Kotlin в `commonMain`):

1. **Animated WebP — мультиплексирование still-WebP от skiko** **[спайк + ревью]**. Спайк собрал simple-format WebP (`RIFF…WEBP` + один чанк `VP8 `) в `ANMF` с `VP8X`+`ANIM` muxer'ом ≈60 строк: 60 кадров 1024×768 → 123 KiB за 1.3 с. **Но на реальных тестах simple-format не гарантирован**: skiko для WEBP задаёт только `fQuality` (lossy по умолчанию, `Image.cc`), а libwebp пишет расширенный формат `VP8X` + `ALPH` + `VP8 `, если в кадре есть хоть один прозрачный пиксель. Поверхность ui-test — `Surface.makeRasterN32Premul` (строка 222 `ComposeUiTest.skiko.kt`), и `setContent { Button(...) }` без фонового `Surface` даёт прозрачный фон; Android `Bitmap.compress(WEBP_LOSSLESS)` даёт `VP8L`. Muxer должен: разбирать simple- и extended-входы; переносить `ALPH`+`VP8 ` или `VP8L` внутрь `ANMF`; ставить флаг alpha в `VP8X`; ставить в `ANMF` «do not blend» и подходящий dispose (иначе прозрачные кадры наложатся друг на друга — «призраки»); выравнивать чанки по чётной длине. Реалистичный объём — 120–200 строк плюс тесты, обязательный тест — прозрачная сцена. Самый компактный результат, воспроизводится в Chrome/Firefox/Safari 14+, но Allure-тип `image/webp` показывается только в Allure 2.35+ / Allure 3 (см. 2.4).
2. **APNG — переупаковка PNG от skiko** **[спайк + ревью]**. Спайк писал APNG своим writer'ом с deflate (60 кадров → 1 MiB за 0.57 с при `Deflater(1)` и попиксельном `getColor`), что требует zlib на каждой платформе. Рекомендуемый вариант — без собственного zlib: вынимать `IDAT` из `encodeToData(PNG)` каждого кадра и переупаковывать в `fdAT`. Рецензент оценивает реализуемость как высокую, ≈150 строк, риск низкий, при соблюдении правил: `IHDR` и предшествующие ему ancillary-чанки брать из первого кадра; склеивать все `IDAT` кадра (libpng режет данные на несколько `IDAT`); проверять, что `IHDR` у всех кадров одинаковый, иначе отказываться от анимации; `acTL` писать в конце, когда число кадров известно; номера последовательности общие для `fcTL` и `fdAT`; `blend_op = SOURCE`; задержка `fcTL` — пара u16/u16, при `delay_den = 1000` максимум ≈65 с, нужен clamp. CRC32 — табличная реализация на 256 записей, ≈15 строк. Lossless, Allure-тип `image/png` работает во всех версиях viewer'а; Roborazzi выбрал тот же APNG для `recordRoboVideo`. Размер больше WebP (lossless), что компенсируется дедупликацией кадров и лимитами.
3. **GIF** — только там, где есть платформенный writer: JVM `javax.imageio` (5.4 с на 60 кадров, 256 цветов, дизеринг) **[спайк]**; iOS/macOS `platform.ImageIO.CGImageDestination` с `kUTTypeGIF`/`UTTypeGIF` **[сообщество]**. Не стоит делать базовым.
4. **MP4/H.264**: JVM — JCodec (`org.jcodec:jcodec-javase` 0.2.5, BSD, pure Java, Baseline, медленный и фактически заброшен) **[доки README]**; iOS/macOS — `AVAssetWriter` + `AVAssetWriterInputPixelBufferAdaptor` (см. 3.3); ffmpeg-обёртки (jave2 GPL, humble-video AGPL, JavaCV десятки МБ) — не для тестовой библиотеки. H.264 даёт честный `video/mp4` для Allure, но добавляет платформенный код на каждом таргете. Отложить.

Рекомендация **[ревью]**: формат по умолчанию — **APNG через переупаковку `IDAT` → `fdAT`** (`image/png`, работает в любой версии Allure, без zlib, меньше краевых случаев), **animated WebP** — опция для тех, у кого Allure 2.35+ / Allure 3 и важен размер. Первоначальная рекомендация «WebP по умолчанию» снята из-за совместимости с Allure и сложности muxer'а на прозрачных кадрах.

### 3.3 iOS / macOS

- Общий код из 3.1–3.2 работает как есть (захват и кодирование — skiko; запись файла — уже существующий `AllureResultsSink.native.kt` через okio; на симуляторе файлы идут на хост через `SIMCTL_CHILD_ULTRON_ALLURE_RESULTS_DIR`).
- При необходимости настоящего mp4: Kotlin/Native включает platform-библиотеки `platform.AVFoundation`, `platform.CoreVideo`, `platform.CoreMedia`, `platform.ImageIO`, `platform.UniformTypeIdentifiers` для ios и osx без доп. cinterop **[исходники JetBrains/kotlin platformLibs]**. Путь: `Image.readPixels` → BGRA → `CVPixelBufferCreateWithBytes` → `AVAssetWriterInputPixelBufferAdaptor.appendPixelBuffer(_, withPresentationTime:)` → `finishWriting`. На симуляторе H.264 через VideoToolbox «скорее всего работает» (кодер хоста), первоисточник не найден **[не проверено]**. Память: ImageIO держит кадры до `Finalize` **[сообщество]** — капать число кадров.
- Хостовая альтернатива: `xcrun simctl io <udid> recordVideo --codec=h264 file.mp4` (проверено `xcrun simctl io help` на этой машине) — только из CI-скрипта, не из теста.

## 4. Web (js / wasmJs, Karma + headless Chrome)

- Karma `ChromeHeadless` **[исходники karma-chrome-launcher]** = `--headless --disable-gpu --disable-dev-shm-usage --remote-debugging-port=9222`; с Chrome ≥ 132 `--headless` — это «new headless» (полный Chrome) **[доки developer.chrome.com]**, т.е. `MediaRecorder`/`WebCodecs` доступны, рендер — программный.
- `canvas.captureStream(0)` + `CanvasCaptureMediaStreamTrack.requestFrame()` + `MediaRecorder("video/webm;codecs=vp8")` **[доки/сообщество]**: даёт WebM, который Allure воспроизводит. Но тестовая сцена Compose в браузере рендерится в offscreen растровый `Surface`, не в DOM-canvas — нужно в listener'е делать `captureToImage()` → `putImageData` в вспомогательный `<canvas>` → `requestFrame()`. `ondataavailable` асинхронный; Blob надо выгрузить из браузера — Karma не умеет, потребуется middleware в `karma.config.d` (POST на прокси) либо base64 в консоль (как сейчас делает `UltronAllureResults.dumpToConsole`, но объём WebM в консоли — плохая идея). `getDisplayMedia` в headless не работает; CDP `Page.startScreencast` недоступен странице самой себе.
- `WebCodecs.VideoEncoder` — есть, но muxer в WebM/MP4 пришлось бы писать самим; не стоит.
- Реалистично: **те же покадровые кадры + общий APNG/WebP writer**, результат — ещё один файл в `UltronAllureResults` рядом с PNG-скриншотами, транспорт уже решён в `dumpToConsole`. zlib в браузере не нужен ни для APNG-переупаковки, ни для WebP (см. 3.2).
- **[ревью] Транспорт — узкое место.** `UltronAllureResults` держит все вложения прогона в памяти, а `dumpToConsole` выводит их base64 (+33 %). Анимации всех тестов при `TEST_FINISH` будут копиться десятками мегабайт в памяти браузера и в логе Karma — то же возражение, что документ выдвигал против WebM, относится и к APNG/WebP. Нужны: дефолт `TEST_FAILURE` (вложение только у упавших), `videoMaxFrames` и `videoMaxBytes`, опционально даунскейл кадра.

## 5. Встраивание в архитектуру

### 5.1 Что где живёт

| Слой | Изменения |
|---|---|
| `ultron-common` | Для Compose-пути ничего обязательного. Для Android-пути **[ревью]**: `MimeType` (`commonMain/.../file/MimeType.kt`) уже содержит `WEBP("image/webp")`, но `MP4` там `"audio/mp4"` — нужен `VIDEO_MP4(".mp4", "video/mp4")` либо явная строка в вызове. Кадр на итерацию retry (не в MVP) потребовал бы хука в `OperationExecutor`. |
| `ultron-compose` | Ничего обязательного: `UltronTestRunHooks` (`beforeTest` / `onTestFailed` / `afterTest`) и `ComposeTestContainer.getProvider()` уже дают всё (подтверждено ревью). `UltronLifecycleListener` даёт точки на операцию, не на итерацию retry. Если захотим режим «кадр на тик часов» (3.1 б) — понадобится доступ к `mainClock` (уже есть в `UltronComposeTestEnvironment`). Хуки срабатывают **только из `runUltronUiTest`**: тесты на `createUltronComposeRule` (Android) записи не получат. |
| `ultron-allure-multiplatform` | Новое: `attach/FrameRecorder` (кольцевой буфер последних N **закодированных** кадров + метка «операция/длительность», дедупликация по байтам/хешу), `attach/AnimationEncoder` (commonMain: `ApngRepacker` по умолчанию, `AnimatedWebpMuxer` опционально), `listeners/RecordFrameListener : UltronLifecycleListener` (кадр `after` по умолчанию, `before` опционально; захват делить с `AttachOnOperationListener`; все исключения ловить), расширение `AllureTestRunHook`: `beforeTest` → `recorder.start()`, `onTestFailed` → финальный кадр, `afterTest` → `encoder.encode(frames)` → вложение **на уровень теста и до `stopTest`** **[ревью]**: текущий `lifecycle.attach(name, bytes, ext, type)` прикрепит к последнему открытому step, если такой остался, нужен явный `attachToTest`. Конфиг: `AllureMultiplatformConfigParams.videoPolicy: Set<AllureAttachStrategy>` (`NONE` по умолчанию; `TEST_FAILURE` — писать всегда, прикладывать только при падении; **новое** значение `TEST_FINISH` — всегда, добавить в оба enum), `videoFormat: APNG|WEBP`, `videoQuality`, `videoMaxFrames`, `videoMaxBytes`, `videoFrameBefore: Boolean = false`. Регистрация/очистка — в `modify()`/`clearRegistrations()` по образцу `AttachOnOperationListener`. |
| `ultron-allure` (Android) | Новое: `runner/ScreenRecordRunListener : UltronRunListener` (start в `testStarted`, stop + attach в `testFinished`/`testFailure` по политике; PID через `pidof`/`ps` с разбором в Kotlin, никаких shell-подстановок), `screenrecord/ScreenRecorder` (команды, ожидание EOF, перенос файла), `AllureConfigParams.recordVideoPolicy`, `screenRecordOptions` (size/bitrate/timeLimit). Проверить порядок `testFinished` относительно Allure-листенера и `UltronAllureResultsTransferListener`. |
| Документация | `docs/docs/common/allure.md`: новые параметры в «Custom config» и «Compose Multiplatform → Configuration», раздел про ограничения (180 с, параллельность, эмулятор, кадры-по-операциям). |

### 5.2 Потоки данных

Compose (skiko-таргеты; Android Compose — см. ограничения ниже):

```
runUltronUiTest
  └─ UltronTestRunHooks.around
       ├─ beforeTest      → AllureTestRunHook → FrameRecorder.start()
       ├─ <операции>      → RecordFrameListener.after → captureRoot → encode → dedup → FrameRecorder.add(bytes, label)
       ├─ onTestFailed    → FrameRecorder.add(finalFrame)   (сцена жива)
       └─ afterTest       → encoder.encode(frames) → lifecycle.attachToTest(...) → stopTest   (сцена закрыта — байты уже есть)
```

Android Espresso:

```
UltronRunInformer.testStarted  → ScreenRecordRunListener → executeShellCommand("screenrecord … /data/local/tmp/ultron_<id>.mp4"), фоновое чтение stdout
UltronRunInformer.testFailure  → пометить "attach"
UltronRunInformer.testFinished → executeShellCommand("pidof screenrecord") → выбрать PID по <id> в Kotlin → executeShellCommand("kill -2 <pid>")
                                 → ждать EOF stdout → executeShellCommand("cat …") → AttachUtil.attachFile("video/mp4") (если политика разрешает) → rm
```

**[ревью] Ограничения, общие для Compose-пути:**

- `UltronTestRunHooks`, `AllureLifecycle.currentTest` и `ComposeTestContainer` — глобальные синглтоны без синхронизации; весь `ultron-allure-multiplatform` рассчитан на один тест за раз в процессе, recorder это наследует.
- Android Compose: хуки срабатывают только из `runUltronUiTest`, тесты на `createUltronComposeRule` записи не получат; `captureToImage` на Android требует API 26+, а `onRoot()` падает при нескольких корнях (открытый диалог). Для Android Compose основной путь — `screenrecord` из `ultron-allure`; `captureRootWebp.android` в MVP не нужен.

### 5.3 Стоимость для пользователя

- Compose **[ревью]**: при `videoPolicy = TEST_FAILURE` все тесты платят захват + немедленное кодирование ≈20–25 мс/операция (PNG ≈24 мс по спайку на почти пустой сцене; на реальных экранах больше): при 50 операциях ≈1.1–1.3 с на тест, с кадром «до» — вдвое. Ленивое кодирование сырых кадров исключено (память). Память — `videoMaxFrames` × размер закодированного кадра (кольцевой буфер), плюс `videoMaxBytes` как верхняя граница вложения. Дедупликация одинаковых кадров заметно снижает и время сборки, и размер.
- Android: отдельный процесс-кодер, нагрузка на тестовый поток нулевая; добавляются 1–3 с на тест на остановку/перенос файла.

## 6. Рекомендуемый первый шаг (MVP)

**MVP = Compose Multiplatform, кадр после каждой операции с дедупликацией, APNG по умолчанию (WebP опционально), desktop + iOS + wasmJs sample-тесты.** Пересмотрено по итогам ревью.

1. `ultron-allure-multiplatform/commonMain`: `ApngRepacker` (переупаковка `IDAT` → `fdAT` из `encodeToData(PNG)`; табличный CRC32; проверка одинаковых `IHDR`; clamp задержки; unit-тесты парсят свой же PNG-поток и сравнивают с golden), `FrameRecorder` (кольцевой буфер закодированных кадров, дедупликация по байтам/хешу, длительность кадра из реального времени между операциями с clamp), `RecordFrameListener` (кадр `after`, все исключения перехвачены, захват общий с `AttachOnOperationListener`).
2. `AnimatedWebpMuxer` — как опция, только с поддержкой `VP8X`/`ALPH`/`VP8L`, флагов blend/dispose и выравнивания; обязательный тест на прозрачную сцену; проверка файлов `webpmux`/`anim_dump` и в Chrome + Safari, не только `ffprobe`. Если не укладывается в срок — выносится из MVP без потери функциональности.
3. `AllureTestRunHook`: start/finalize; вложение на уровень теста до `stopTest` (`attachToTest`); `AllureAttachStrategy.TEST_FINISH` в оба enum; `AllureMultiplatformConfigParams.videoPolicy`/`videoFormat`/`videoMaxFrames`/`videoMaxBytes`/`videoFrameBefore`.
4. `captureRootPng` уже есть в `ComposeArtifactProviders.skiko.kt`; при WebP-опции — `captureRootWebp(provider, quality)` только для skiko. Android-реализацию не делать (см. 5.2).
5. Sample-тесты: desktop (`AllureResultsDesktopTest`: вложение `video` с типом `image/png`, сигнатура PNG + `acTL`, ≥2 кадра, дедупликация одинаковых кадров), wasm (файл в `UltronAllureResults`, проверка лимитов), iOS (на хосте). Учесть, что в CI (`ci-pipeline.yml`) ни `desktopTest`, ни `iosSimulatorArm64Test`, ни `wasmJsTest` сейчас не запускаются — эти тесты будут локальными, пока CI не расширен.
6. Документация в `allure.md`, включая явные ограничения MVP: кадр на операцию, а не на итерацию retry; один тест за раз в процессе; только `runUltronUiTest`; Android Compose — не в этом модуле; `image/webp` требует Allure 2.35+.
7. Отдельным шагом — Android `screenrecord` в `ultron-allure` (`ScreenRecordRunListener`): PID через `pidof`/`ps` с разбором в Kotlin, `"video/mp4"` или новый `MimeType.VIDEO_MP4`, проверка порядка `testFinished`, замер задержки старта. Проверка на физическом устройстве локально; для CI нужна отдельная задача на эмулятор или instrumented-джоб — его сейчас нет.

Оценка **[ревью, экспертная, не измерение]**: MVP (п. 1–6) — 5–8 дней (вместо 3–5: кодек со всеми форматами кадров, дедупликация и буфер, три платформы sample-тестов без CI, документация); Android (п. 7) — 4–6 дней плюс отдельная задача на instrumented-джоб в CI.

## 7. Открытые вопросы

Закрыто ревью: viewer анимирует APNG/GIF/WebP через `<img>` (но `image/webp` только с Allure 2.35); остановка `screenrecord` и параллельные прогоны решаются выбором PID по имени файла в Kotlin; бесконечные анимации при `autoAdvance` снимает `InfiniteAnimationPolicy`.

Остаются открытыми:

1. **Длительность кадра**: на skiko нет реального времени; что писать в задержку кадра — фиксированные 500 мс, реальное время между операциями (информативно, но ожидание операции N+1 «приписывается» кадру после операции N, и подписи расходятся с картинкой; тест с 10-секундным ожиданием даёт 10-секундный кадр) или `mainClock.currentTime`-дельту? Нужны правило и clamp (для APNG максимум ≈65 с при `delay_den = 1000`).
2. **Переупаковка PNG `IDAT` в APNG `fdAT`**: структурно корректна, но на практике не проверена — libpng режет данные на несколько `IDAT`, их нужно склеивать; проверить на выходе skiko всех таргетов **[не проверено]**.
3. **Android Compose через `ultron-allure-multiplatform`** (`enabled = true`): покадровый вариант не покрывает `createUltronComposeRule` и дублирует `screenrecord` — в MVP Android-реализацию не делать; решить, нужна ли она вообще.
4. **`screenrecord` на эмуляторах** и поведение `--time-limit 0`: по исходникам `screenrecord.cpp` подтверждены лимит 180 с и семантика `0 = без лимита`, но с какого API значение `0` принимается, не установлено («ориентир API 34» не проверен). Эмулятора в CI нет — проверка возможна только локально или после отдельной задачи на instrumented-джоб.
5. **Порядок `testFinished`** между `ScreenRecordRunListener`, Allure-листенером и `UltronAllureResultsTransferListener` **[не проверено]**.
6. **Задержка старта `screenrecord`** и потеря первых кадров теста — замерить **[не проверено]**.
7. **Реальная стоимость захвата** с учётом копий пикселей в `captureRootPng` и размеры кадров на реальных экранах — замерить на `composeApp`, выбрать дефолты `videoMaxFrames`/`videoMaxBytes` (ориентир: 200 кадров, кодировать сразу, не хранить `Bitmap`).
8. **Кадр на итерацию retry**: нужен ли, и если да — какой хук добавлять в `OperationExecutor` (`ultron-common`).
9. Нужен ли отдельный публичный API «записать композабл в анимацию» вне теста (`ImageComposeScene.render(nanoTime)`, как Camcorder) — это другая фича, вне скоупа.

## 8. Источники

- AOSP `UiAutomation.java`: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/core/java/android/app/UiAutomation.java ; `UiAutomationConnection.java` (`Runtime.exec`, cleanup-поток): https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/core/java/android/app/UiAutomationConnection.java
- allure2 `attachmentType.js` (поддержка `image/webp` с 2.35.0, PR #2978): https://github.com/allure-framework/allure2/blob/master/allure-generator/src/main/javascript/utils/attachmentType.js ; Allure 3 `attachments.ts`: https://github.com/allure-framework/allure3/blob/main/packages/web-commons/src/attachments.ts
- APNG spec: https://wiki.mozilla.org/APNG_Specification ; WebP container (RIFF, `VP8X`/`ANIM`/`ANMF`/`ALPH`): https://developers.google.com/speed/webp/docs/riff_container
- AOSP `screenrecord.cpp`: https://android.googlesource.com/platform/frameworks/av/+/refs/heads/main/cmds/screenrecord/screenrecord.cpp
- Остановка screenrecord из кода (SO): https://stackoverflow.com/questions/25185746/stop-adb-screenrecord-from-java ; SELinux при `Runtime.exec`: https://stackoverflow.com/questions/53806119/screenrecord-doesn-t-work-when-invoked-from-code
- Лимит 180 с: https://www.maestro.dev/blog/fixing-androids-3-minute-screen-recording-limitation ; Marathon: https://marathonlabs.github.io/marathon/next/android/configure/
- MediaProjection: https://developer.android.com/media/grow/media-projection ; FGS types: https://developer.android.com/develop/background-work/services/fg-service-types
- TestStorage: https://developer.android.com/reference/androidx/test/services/storage/TestStorage ; UTP-плагины: https://android.googlesource.com/platform/tools/base/+/refs/heads/mirror-goog-studio-main/utp/
- GMD / FTL видео: https://developer.android.com/studio/test/gradle-managed-devices ; https://firebase.google.com/docs/test-lab/command-line
- Allure attachments: https://allurereport.org/docs/attachments/ ; allure2#1358: https://github.com/allure-framework/allure2/issues/1358 ; allure-kotlin `Allure.kt`: https://github.com/allure-framework/allure-kotlin/blob/master/allure-kotlin-commons/src/main/kotlin/io/qameta/allure/kotlin/Allure.kt
- skiko `Image.kt` / `Image.cc` / `Codec.kt`: https://github.com/JetBrains/skiko/tree/master/skiko/src ; Skia `SkWebpEncoder.h`: https://skia.googlesource.com/skia/+/main/include/encode/SkWebpEncoder.h
- Compose ui-test skiko: https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/ui/ui-test/src/skikoMain/kotlin/androidx/compose/ui/test/ComposeUiTest.skiko.kt ; `ImageComposeScene`: https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/ui/ui/src/skikoMain/kotlin/androidx/compose/ui/ImageComposeScene.skiko.kt ; тестирование анимаций: https://developer.android.com/develop/ui/compose/animation/testing
- Roborazzi (`captureRoboGif`, `recordRoboVideo`, APNG): https://github.com/takahirom/roborazzi
- JCodec: https://github.com/jcodec/jcodec ; jave2: https://github.com/a-schild/jave2 ; humble-video: https://github.com/artclarke/humble-video
- Kotlin/Native platform libs: https://kotlinlang.org/docs/native-platform-libs.html ; defs: https://github.com/JetBrains/kotlin/tree/master/kotlin-native/platformLibs/src/platform/ios
- Animated GIF via ImageIO (Apple): https://gist.github.com/mayoff/4969104 ; APNG via ImageIO: https://developer.apple.com/forums/thread/743170
- karma-chrome-launcher: https://github.com/karma-runner/karma-chrome-launcher/blob/master/index.js ; new headless: https://developer.chrome.com/docs/chromium/new-headless ; https://developer.chrome.com/blog/removing-headless-old-from-chrome ; canvas captureStream: https://developer.chrome.com/blog/capture-stream ; WebCodecs: https://developer.mozilla.org/docs/Web/API/VideoEncoder
