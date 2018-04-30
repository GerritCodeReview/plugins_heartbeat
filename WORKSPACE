workspace(name = "heartbeat")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "e9ba0bf5dd4a356d5a6edea3ac2b3c8dfc3ee03e",
    #    local_path = "/home/<user>/projects/bazlets",
)

#Snapshot Plugin API
#load(
#    "@com_googlesource_gerrit_bazlets//:gerrit_api_maven_local.bzl",
#    "gerrit_api_maven_local",
#)

# Load snapshot Plugin API
#gerrit_api_maven_local()

# Release Plugin API
load(
    "@com_googlesource_gerrit_bazlets//:gerrit_api.bzl",
    "gerrit_api",
)

# Load release Plugin API
gerrit_api()
