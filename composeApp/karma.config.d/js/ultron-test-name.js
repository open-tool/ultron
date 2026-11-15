// Loads karma-adapters/ultron-test-name-adapter.js first, so that kotlin.test names reach Ultron's Allure
// results (see docs/docs/common/allure-multiplatform.md, "Test names in the browser").
//
// Wrapped in a function: every fragment of this directory is concatenated into one function body of the
// generated karma.conf.js, so a top-level `const path` here would clash with the one in config.js.
(function () {
    const path = require("path");
    // basePath is <root>/build/<js|wasm>/packages/<root>-composeApp-test; four levels up is the root project.
    const adapter = path.resolve(config.basePath, "..", "..", "..", "..", "composeApp", "karma-adapters", "ultron-test-name-adapter.js");
    config.files.unshift({pattern: adapter, included: true, served: true, watched: false});
})();
