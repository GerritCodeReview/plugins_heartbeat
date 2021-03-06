// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License"),
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.ericsson.gerrit.plugins.heartbeat;

import com.google.common.flogger.FluentLogger;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.server.events.EventDispatcher;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;

/** Timer-based daemon doing the actual heartbeat task. */
@Singleton
public class HeartbeatDaemon implements LifecycleListener {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String HEARTBEAT_THREAD_NAME = "StreamEventHeartbeat";
  private final DynamicItem<EventDispatcher> dispatcher;
  private final HeartbeatConfig config;
  private final Timer timer;

  /**
   * Constructor that sets the heartbeat timer.
   *
   * @param dispatcher the event dispatcher
   * @param config the plugin config
   */
  @Inject
  public HeartbeatDaemon(DynamicItem<EventDispatcher> dispatcher, HeartbeatConfig config) {
    this.dispatcher = dispatcher;
    this.config = config;
    timer = new Timer(HEARTBEAT_THREAD_NAME, true);
  }

  @Override
  public void start() {
    timer.schedule(new HeartbeatTask(), 0, config.getDelay());
    logger.atInfo().log(
        "Initialized to send heartbeat event every %d milliseconds", config.getDelay());
  }

  @Override
  public void stop() {
    timer.cancel();
    logger.atInfo().log("Stopped sending heartbeat event");
  }

  private class HeartbeatTask extends TimerTask {
    @Override
    public void run() {
      try {
        dispatcher.get().postEvent(new HeartbeatEvent());
      } catch (PermissionBackendException e) {
        logger.atSevere().withCause(e).log("Failed to post heartbeat event");
      }
    }
  }
}
