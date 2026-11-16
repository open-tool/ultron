// see https://kotlinlang.org/docs/js-project-setup.html#webpack-configuration-file
// This file provides karma.config.d configuration to run tests with k/wasm

const path = require("path");

const basePath = config.basePath;
const projectPath = path.resolve(basePath, "..", "..", "..", "..");

// Browser console goes to a file as well: Ultron Allure results have no file system in the browser and
// are printed as `ULTRON_ALLURE_RESULT <file> <base64>` lines, which `collectWasmAllureResults` decodes.
config.browserConsoleLogOptions = {
    level: "debug",
    terminal: false, // multi-megabyte base64 lines belong in the file, not in Gradle's stdout
    path: path.resolve(projectPath, "build", "karma-console-wasm.log"),
};
// The video sample deliberately runs for several seconds; mocha's default per-test timeout is 2 s.
config.client = config.client || {};
config.client.mocha = Object.assign({}, config.client.mocha, {timeout: 60000});
const generatedAssetsPath = path.resolve(projectPath, "build", "karma-webpack-out")

const debug = message => console.log(`[karma-config] ${message}`);

debug(`karma basePath: ${basePath}`);
debug(`karma generatedAssetsPath: ${generatedAssetsPath}`);

config.proxies["/"] = path.resolve(basePath, "kotlin");

config.files = [
    {pattern: path.resolve(generatedAssetsPath, "**/*"), included: false, served: true, watched: false},
    {pattern: path.resolve(basePath, "kotlin", "**/*.png"), included: false, served: true, watched: false},
    {pattern: path.resolve(basePath, "kotlin", "**/*.gif"), included: false, served: true, watched: false},
    {pattern: path.resolve(basePath, "kotlin", "**/*.ttf"), included: false, served: true, watched: false},
    {pattern: path.resolve(basePath, "kotlin", "**/*.txt"), included: false, served: true, watched: false},
    {pattern: path.resolve(basePath, "kotlin", "**/*.json"), included: false, served: true, watched: false},
    {pattern: path.resolve(basePath, "kotlin", "**/*.xml"), included: false, served: true, watched: false},
].concat(config.files);

function KarmaWebpackOutputFramework(config) {
    // This controller is instantiated and set during the preprocessor phase.
    const controller = config.__karmaWebpackController;

    // only if webpack has instantiated its controller
    if (!controller) {
        console.warn(
            "Webpack has not instantiated controller yet.\n" +
            "Check if you have enabled webpack preprocessor and framework before this framework"
        )
        return
    }

    config.files.push({
        pattern: `${controller.outputPath}/**/*`,
        included: false,
        served: true,
        watched: false
    })
}

const KarmaWebpackOutputPlugin = {
    'framework:webpack-output': ['factory', KarmaWebpackOutputFramework],
};

config.plugins.push(KarmaWebpackOutputPlugin);
config.frameworks.push("webpack-output");