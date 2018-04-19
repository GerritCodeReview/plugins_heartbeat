Plugin that sends heartbeat stream event.

Main use case for this plugin is to always keep stream events alive so Jenkins
Gerrit-Trigger connection watchdog doesn't force reconnection when it detects no
stream events activity.

By default, send one heartbeat event every 15 seconds.

To configure heartbeat, refer to [config](config.html) documentation.
