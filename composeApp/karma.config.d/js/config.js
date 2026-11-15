// Karma configuration for the Kotlin/JS (`js`) browser tests of composeApp.
//
// On the `js` target Compose reaches Skia through global functions
// (`org_jetbrains_skia_*` on `window`). Those globals are assigned by
// `js-reexport-symbols.mjs` from the Skiko web runtime, which the Compose
// Gradle plugin copies into the test resources (basePath/kotlin) but which
// nothing imports. Without it every Compose-rendering test fails with
// "ReferenceError: org_jetbrains_skia_Surface__1nMakeRasterN32Premul is not defined".
//
// The wasmJs target does not need this: it imports skiko.mjs as an ES module
// (see karma.config.d/wasm/config.js).

const fs = require("fs");
const path = require("path");

const basePath = config.basePath;
const projectPath = path.resolve(basePath, "..", "..", "..", "..");
const kotlinDir = path.resolve(basePath, "kotlin");

// Browser console goes to a file as well: Ultron Allure results have no file system in the browser and
// are printed as `ULTRON_ALLURE_RESULT <file> <base64>` lines, which `collectJsAllureResults` decodes.
config.browserConsoleLogOptions = {
    level: "debug",
    terminal: false,
    path: path.resolve(projectPath, "build", "karma-console-js.log"),
};
const skikoReexportPath = path.resolve(kotlinDir, "js-reexport-symbols.mjs");

const debug = message => console.log(`[karma-config] ${message}`);
debug(`karma basePath: ${basePath}`);

if (!fs.existsSync(skikoReexportPath)) {
    throw new Error(`[karma-config] ${skikoReexportPath} not found: Skiko web runtime was not copied into the js test resources`);
}

// skiko.wasm is instantiated asynchronously by js-reexport-symbols.mjs. A root-level
// mocha `before` hook gates the whole run on that promise, otherwise the first tests
// race the load and fail with "TypeError: org_jetbrains_skia_... is not a function".
const skikoAwaitSetupPath = path.resolve(kotlinDir, "skiko-await-setup.mjs");
fs.writeFileSync(
    skikoAwaitSetupPath,
    'import { api } from "./js-reexport-symbols.mjs";\n' +
    "before(function () {\n" +
    "  this.timeout(60000);\n" +
    "  return api.awaitSkiko;\n" +
    "});\n"
);

// Compose resources (painterResource & co.) are fetched from /composeResources/... at run time. Without serving them
// the fetch is a 404, and the MissingResourceException it raises in a coroutine reaches mocha as a global error after
// the UI block has finished: the test fails in Karma while its Allure result is already written as passed.
const composeResourcesPath = path.resolve(kotlinDir, "composeResources");
config.proxies = Object.assign({}, config.proxies, {"/composeResources/": "/absolute" + composeResourcesPath + "/"});

config.files = [
    skikoReexportPath,
    skikoAwaitSetupPath,
    {pattern: path.resolve(composeResourcesPath, "**/*"), included: false, served: true, watched: false},
].concat(config.files);
config.preprocessors = Object.assign({}, config.preprocessors, {
    [skikoReexportPath]: ["webpack", "sourcemap"],
    [skikoAwaitSetupPath]: ["webpack", "sourcemap"],
});

// Mocha's default 2000 ms per-test timeout is too tight for a Compose/Skia render
// when the whole suite shares one headless browser tab.
config.client = Object.assign({}, config.client, {
    mocha: Object.assign({}, (config.client && config.client.mocha) || {}, {timeout: 10000}),
});
