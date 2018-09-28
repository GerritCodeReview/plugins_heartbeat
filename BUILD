load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "heartbeat",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: heartbeat",
        "Gerrit-Module: com.ericsson.gerrit.plugins.heartbeat.HeartbeatModule",
        "Implementation-Title: heartbeat plugin",
        "Implementation-URL: https://gerrit-review.googlesource.com/#/admin/projects/plugins/heartbeat",
        "Implementation-Vendor: Ericsson",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

junit_tests(
    name = "heartbeat_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    tags = ["heartbeat"],
    deps = [
        ":heartbeat__plugin_test_deps",
    ],
)

java_library(
    name = "heartbeat__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":heartbeat__plugin",
    ],
)
