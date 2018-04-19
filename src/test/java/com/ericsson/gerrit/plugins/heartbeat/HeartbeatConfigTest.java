// Copyright (C) 2013 Ericsson
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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.AllProjectsNameProvider;
import com.google.gerrit.server.config.SitePaths;
import com.google.gwtorm.client.KeyUtil;
import com.google.gwtorm.server.StandardKeyEncoder;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class HeartbeatConfigTest {

  static {
    KeyUtil.setEncoderImpl(new StandardKeyEncoder());
  }

  private SitePaths sitePaths;
  private AllProjectsNameProvider allProjectsNameProviderMock;
  private HeartbeatConfig heartbeatConfig;

  @Before
  public void setUp() throws Exception {
    File tmpSiteFolder = File.createTempFile("gerrit-site", null);
    tmpSiteFolder.delete();
    tmpSiteFolder.mkdirs();
    sitePaths = new SitePaths(tmpSiteFolder.toPath());
    allProjectsNameProviderMock = createMock(AllProjectsNameProvider.class);
    expect(allProjectsNameProviderMock.get()).andReturn(
        new AllProjectsName("AllProjectName")).anyTimes();
    replay(allProjectsNameProviderMock);
  }

  @After
  public void tearDown() throws Exception {
    if(sitePaths != null){
      deleteFolder(sitePaths.site_path.toFile());
    }
  }

  @Test
  public void shouldBeDefaultValuesWhenConfigFileDoesNotExist()
      throws ConfigInvalidException, IOException {
    heartbeatConfig = new HeartbeatConfig(sitePaths, allProjectsNameProviderMock);
    assertEquals(HeartbeatConfig.DEFAULT_DELAY, heartbeatConfig.getDelay());
    assertEquals(allProjectsNameProviderMock.get().get(), heartbeatConfig.getProject());
    assertEquals(HeartbeatConfig.DEFAULT_REF, heartbeatConfig.getRef());
  }

  @Test
  public void shouldReturnDelayConfiguredInFile()
      throws ConfigInvalidException, IOException {
    try (PrintWriter writer = getWriterForConfig()) {
      writer.println("[" + HeartbeatConfig.HEARTBEAT_SECTION + "]");
      writer.println(HeartbeatConfig.DELAY_KEY + " = 1234");
    }
    heartbeatConfig = new HeartbeatConfig(sitePaths, allProjectsNameProviderMock);
    assertEquals(1234, heartbeatConfig.getDelay());
    assertEquals(allProjectsNameProviderMock.get().get(), heartbeatConfig.getProject());
    assertEquals(HeartbeatConfig.DEFAULT_REF, heartbeatConfig.getRef());
  }

  @Test
  public void shouldReturnProjectConfiguredInFile()
      throws ConfigInvalidException, IOException {
    try (PrintWriter writer = getWriterForConfig()) {
      writer.println("[" + HeartbeatConfig.HEARTBEAT_SECTION + "]");
      writer.println(HeartbeatConfig.PROJECT_KEY + " = someProject");
    }
    heartbeatConfig = new HeartbeatConfig(sitePaths, allProjectsNameProviderMock);
    assertEquals(HeartbeatConfig.DEFAULT_DELAY, heartbeatConfig.getDelay());
    assertEquals("someProject", heartbeatConfig.getProject());
    assertEquals(HeartbeatConfig.DEFAULT_REF, heartbeatConfig.getRef());
  }

  @Test
  public void shouldReturnRefConfiguredInFile()
      throws ConfigInvalidException, IOException {
    try (PrintWriter writer = getWriterForConfig()) {
      writer.println("["+HeartbeatConfig.HEARTBEAT_SECTION+"]");
      writer.println(HeartbeatConfig.REF_KEY +  "= someRef");
    }
    heartbeatConfig = new HeartbeatConfig(sitePaths, allProjectsNameProviderMock);
    assertEquals(HeartbeatConfig.DEFAULT_DELAY, heartbeatConfig.getDelay());
    assertEquals(allProjectsNameProviderMock.get().get(), heartbeatConfig.getProject());
    assertEquals("someRef", heartbeatConfig.getRef());
  }

  @Test(expected=ConfigInvalidException.class)
  public void shouldThrowExceptionWhenFileHasBadFormat()
      throws ConfigInvalidException, IOException {
    try (PrintWriter writer = getWriterForConfig()) {
      writer.println("[" + HeartbeatConfig.HEARTBEAT_SECTION);
    }
    heartbeatConfig = new HeartbeatConfig(sitePaths, allProjectsNameProviderMock);
  }

  private PrintWriter getWriterForConfig() throws FileNotFoundException{
    sitePaths.etc_dir.toFile().mkdirs();
    return new PrintWriter(sitePaths.etc_dir.resolve(HeartbeatConfig.FILENAME).toFile());
  }

  private void deleteFolder(File folder) {
    File[] files = folder.listFiles();
    if(files!=null) { //some JVMs return null for empty dirs
      for(File f: files) {
        if(f.isDirectory()) {
          deleteFolder(f);
        } else {
          f.delete();
        }
      }
    }
    folder.delete();
  }
}
