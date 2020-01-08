// Copyright (C) 2018 The Android Open Source Project
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

import static com.google.common.truth.Truth.assertWithMessage;

import com.google.gerrit.acceptance.config.GlobalPluginConfig;
import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.NoHttpd;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.acceptance.UseLocalDisk;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.extensions.registration.RegistrationHandle;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.events.Event;
import com.google.gerrit.server.events.UserScopedEventListener;
import com.google.inject.Inject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

@NoHttpd
@TestPlugin(name = "heartbeat", sysModule = "com.ericsson.gerrit.plugins.heartbeat.HeartbeatModule")
public class HeartbeatIT extends LightweightPluginDaemonTest {

  @Inject private DynamicSet<UserScopedEventListener> eventListeners;

  @Test
  @UseLocalDisk
  @GlobalPluginConfig(pluginName = "heartbeat", name = "heartbeat.delay", value = "1000")
  public void heartbeat() throws Exception {
    CountDownLatch expectedEventLatch = new CountDownLatch(1);
    RegistrationHandle handle =
        eventListeners.add(
            "heartbeat",
            new UserScopedEventListener() {
              @Override
              public void onEvent(Event event) {
                if (event instanceof HeartbeatEvent) {
                  expectedEventLatch.countDown();
                }
              }

              @Override
              public CurrentUser getUser() {
                return identifiedUserFactory.create(user.id());
              }
            });
    assertWithMessage("heartbeat event received")
        .that(expectedEventLatch.await(5, TimeUnit.SECONDS))
        .isTrue();
    handle.remove();
  }
}
