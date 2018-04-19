include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//bucklets/java_sources.bucklet')

SOURCES = glob(['src/main/java/**/*.java'])
RESOURCES = glob(['src/main/resources/**/*'])
TEST_DEPS = GERRIT_PLUGIN_API + [
  ':heartbeat__plugin',
  '//lib:junit',
  '//lib/easymock:easymock',
]

gerrit_plugin(
  name = 'heartbeat',
  srcs = SOURCES,
  resources = RESOURCES,
  manifest_entries = [
    'Gerrit-PluginName: heartbeat',
    'Gerrit-Module: com.ericsson.gerrit.plugins.heartbeat.HeartbeatModule',
  ],
)

java_library(
  name = 'classpath',
  deps = TEST_DEPS,
)

java_test(
  name = 'heartbeat_tests',
  labels = ['heartbeat'],
  srcs = glob(['src/test/java/**/*.java']),
  deps = TEST_DEPS,
)

java_sources(
  name = 'heartbeat-sources',
  srcs = SOURCES + RESOURCES,
)
