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

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gerrit.reviewdb.client.RefNames;
import com.google.gerrit.server.config.AllProjectsNameProvider;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Plugin-specific config file data loader and holder.
 */
@Singleton
public class HeartbeatConfig {
  private static final Logger logger = LoggerFactory.getLogger(HeartbeatConfig.class);

  public static final int DEFAULT_DELAY = 15000;
  public static final String DEFAULT_REF = RefNames.REFS_CONFIG;
  public static final String DELAY_KEY = "delay";
  public static final String FILENAME = "heartbeat.config";
  public static final String HEARTBEAT_SECTION = "heartbeat";
  public static final String PROJECT_KEY = "project";
  public static final String REF_KEY = "ref";

  private int delay = DEFAULT_DELAY;
  private String project;
  private String ref = DEFAULT_REF;

  /**
   * Constructor that loads the config from the plugin file.
   *
   * @param site the site path
   * @param allProjects the provided name for All-Projects project
   * @throws ConfigInvalidException If loaded config from plugin file is invalid
   * @throws IOException If there is an issue with loading the config from plugin file
   */
  @Inject
  public HeartbeatConfig(SitePaths site, AllProjectsNameProvider allProjects)
      throws ConfigInvalidException, IOException {
    project = allProjects.get().toString();
    load(site.etc_dir.resolve(FILENAME).toFile());
  }

  private void load(File configPath) throws ConfigInvalidException, IOException {
    FileBasedConfig cfg = new FileBasedConfig(configPath, FS.DETECTED);
    if (!cfg.getFile().exists() || cfg.getFile().length() == 0) {
      logger.debug("No " + cfg.getFile() + " or empty; using all default values");
      return;
    }

    try {
      cfg.load();
    } catch (ConfigInvalidException e) {
      throw new ConfigInvalidException(String.format(
          "Config file %s is invalid: %s", cfg.getFile(), e.getMessage()), e);
    } catch (IOException e) {
      throw new IOException(String.format(
          "Cannot read %s: %s", cfg.getFile(),  e.getMessage()), e);
    }
    delay = cfg.getInt(HEARTBEAT_SECTION, DELAY_KEY, DEFAULT_DELAY);
    String configuredProject = cfg.getString(HEARTBEAT_SECTION, null, PROJECT_KEY);
    if (!Strings.isNullOrEmpty(configuredProject)) {
      project = configuredProject;
    }
    String configuredRef = cfg.getString(HEARTBEAT_SECTION, null, REF_KEY);
    if (!Strings.isNullOrEmpty(configuredRef)) {
      ref = configuredRef;
    }
  }

  public int getDelay() {
    return delay;
  }

  public String getProject() {
    return project;
  }

  public String getRef() {
    return ref;
  }
}
