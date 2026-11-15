// Publishes the running kotlin.test test into `window.__ultronCurrentTest` (the method) and
// `window.__ultronCurrentSuite` (the enclosing suites joined with ".": package and class), which Ultron's
// Allure module reads on js/wasmJs for tests that pass no explicit name to `runUltronUiTest`.
//
// Loaded by Karma as a plain script before `kotlin-test-karma-runner.js` (see karma.config.d/<target>/
// ultron-test-name.js). Not in karma.config.d itself: every *.js there is concatenated into the generated
// karma.conf.js and run as Node configuration, where `window` does not exist.
window.kotlinTest = window.kotlinTest || {};
window.kotlinTest.adapterTransformer = function (adapter) {
    var suite = adapter.suite;
    var test = adapter.test;
    var suites = []; // the suite stack while the runner registers the tests
    adapter.suite = function (name, ignored, fn) {
        return suite.call(adapter, name, ignored, function () {
            suites.push(name);
            try {
                return fn();
            } finally {
                suites.pop();
            }
        });
    };
    adapter.test = function (name, ignored, fn) {
        var suitePath = suites.filter(Boolean).join("."); // captured at registration, the stack is gone when the test runs
        return test.call(adapter, name, ignored, function () {
            window.__ultronCurrentTest = name;
            window.__ultronCurrentSuite = suitePath;
            return fn();
        });
    };
    return adapter;
};
