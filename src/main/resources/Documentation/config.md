Heartbeat Configuration
=========================
File `heartbeat.config`
-------------------------

The optional file `$site_path/etc/heartbeat.config` is a Git-style config file that controls the heartbeat settings for the heartbeat plugin.

The file is composed of one `heartbeat` section with the following optional parameters.

delay: time in milliseconds to send heartbeat (defaults to 15000)

project: project on which to send event (default is All-Projets)

ref: ref on which to send event (default is refs/meta/config)
