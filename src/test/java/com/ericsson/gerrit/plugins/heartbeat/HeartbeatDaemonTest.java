// Copyright (C) 2015 Ericsson
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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import com.google.gerrit.common.EventDispatcher;
import com.google.gerrit.reviewdb.client.Branch;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gwtorm.client.KeyUtil;
import com.google.gwtorm.server.StandardKeyEncoder;

public class HeartbeatDaemonTest {

  static {
    KeyUtil.setEncoderImpl(new StandardKeyEncoder());
  }

  private EventDispatcher eventDispatcherMock;
  private HeartbeatConfig heartbeatConfigMock;
  private HeartbeatDaemon heartbeatDaemon;

  @Before
  public void setUp() throws Exception {
    eventDispatcherMock = createNiceMock(EventDispatcher.class);
    replay(eventDispatcherMock);
    heartbeatConfigMock = createMock(HeartbeatConfig.class);
    expect(heartbeatConfigMock.getDelay()).andReturn(1).anyTimes();
    expect(heartbeatConfigMock.getProject()).andReturn("someProject").anyTimes();
    expect(heartbeatConfigMock.getRef()).andReturn("someRef").anyTimes();
    replay(heartbeatConfigMock);
    heartbeatDaemon = new HeartbeatDaemon(eventDispatcherMock, heartbeatConfigMock);
  }

  @Test
  public void thatDaemonSendsHeartbeatEvents() throws InterruptedException {
    reset(eventDispatcherMock);
    eventDispatcherMock.postEvent(
        eq(new Branch.NameKey(Project.NameKey.parse(heartbeatConfigMock
            .getProject()), heartbeatConfigMock.getRef())),
        isA(HeartbeatEvent.class));
    expectLastCall().atLeastOnce();
    replay(eventDispatcherMock);

    heartbeatDaemon.start();
    Thread.sleep(500);
    verify(eventDispatcherMock);
    heartbeatDaemon.stop();
  }
}
