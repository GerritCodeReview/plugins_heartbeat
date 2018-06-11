// Copyright (C) 2015 The Android Open Source Project
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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.events.EventDispatcher;
import com.google.gwtorm.client.KeyUtil;
import com.google.gwtorm.server.SchemaFactory;
import com.google.gwtorm.server.StandardKeyEncoder;
import org.junit.Before;
import org.junit.Test;

public class HeartbeatDaemonTest {

  static {
    KeyUtil.setEncoderImpl(new StandardKeyEncoder());
  }

  private EventDispatcher eventDispatcherMock;
  private HeartbeatDaemon heartbeatDaemon;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    eventDispatcherMock = createNiceMock(EventDispatcher.class);
    replay(eventDispatcherMock);
    DynamicItem<EventDispatcher> dynamicEventDispatcherMock = createNiceMock(DynamicItem.class);
    expect(dynamicEventDispatcherMock.get()).andReturn(eventDispatcherMock).anyTimes();
    replay(dynamicEventDispatcherMock);
    HeartbeatConfig heartbeatConfigMock = createMock(HeartbeatConfig.class);
    expect(heartbeatConfigMock.getDelay()).andReturn(1).anyTimes();
    replay(heartbeatConfigMock);
    SchemaFactory<ReviewDb> schemaFactoryMock = createNiceMock(SchemaFactory.class);
    expect(schemaFactoryMock.open()).andReturn(createNiceMock(ReviewDb.class)).anyTimes();
    replay(schemaFactoryMock);
    heartbeatDaemon = new HeartbeatDaemon(dynamicEventDispatcherMock, heartbeatConfigMock);
  }

  @Test
  public void thatDaemonSendsHeartbeatEvents() throws Exception {
    reset(eventDispatcherMock);
    eventDispatcherMock.postEvent(isA(HeartbeatEvent.class));
    expectLastCall().atLeastOnce();
    replay(eventDispatcherMock);

    heartbeatDaemon.start();
    Thread.sleep(500);
    verify(eventDispatcherMock);
    heartbeatDaemon.stop();
  }
}
