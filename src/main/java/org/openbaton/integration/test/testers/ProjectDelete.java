/*
 * Copyright (c) 2016 Open Baton (http://www.openbaton.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openbaton.integration.test.testers;

import java.io.FileNotFoundException;
import java.util.Properties;
import org.ini4j.Profile;
import org.openbaton.catalogue.security.Project;
import org.openbaton.integration.test.exceptions.IntegrationTestException;
import org.openbaton.integration.test.utils.Tester;
import org.openbaton.integration.test.utils.Utils;
import org.openbaton.sdk.api.exception.SDKException;
import org.openbaton.sdk.api.rest.ProjectAgent;

/**
 * Created by tbr on 02.08.16.
 *
 * <p>Class used to delete a project.
 */
public class ProjectDelete extends Tester<Project> {

  private boolean expectedToFail =
      false; // if the creating of the user is expected to fail this field should be set to true
  private String
      asUser; // if another user than specified in the integration-tests.properties file should try to delete the project
  private String userPassword;
  private String projectToDelete;

  private Properties properties;

  public ProjectDelete(Properties p) throws FileNotFoundException {
    super(p, Project.class);
    this.properties = p;
  }

  @Override
  protected Project prepareObject() {
    return null;
  }

  @Override
  protected Object doWork() throws SDKException, IntegrationTestException, FileNotFoundException {
    this.setAbstractRestAgent(requestor.getProjectAgent());

    if (asUser != null && !"".equals(asUser))
      log.info("Try to delete project " + projectToDelete + " while logged in as " + asUser);
    else log.info("Try to delete project " + projectToDelete);

    try {
      String projectId = Utils.getProjectIdByName(requestor, projectToDelete);

      ProjectAgent projectAgent;
      if (asUser != null && !"".equals(asUser))
        projectAgent =
            new ProjectAgent(
                asUser,
                userPassword,
                projectId,
                Boolean.parseBoolean(properties.getProperty("nfvo-ssl-enabled")),
                properties.getProperty("nfvo-ip"),
                properties.getProperty("nfvo-port"),
                "1");
      else projectAgent = requestor.getProjectAgent();
      projectAgent.delete(projectId);
    } catch (SDKException e) {
      if (expectedToFail) {
        log.info("Deletion of project " + projectToDelete + " failed as expected");
        return param;
      } else {
        log.error("Deletion of project " + projectToDelete + " failed");
        throw e;
      }
    }
    if (expectedToFail)
      throw new IntegrationTestException(
          "The deletion of project " + projectToDelete + " was expected to fail but it did not.");
    log.info("Successfully deleted project " + projectToDelete);
    return param;
  }

  @Override
  public void configureSubTask(Profile.Section currentSection) {
    this.setExpectedToFail(currentSection.get("expected-to-fail"));
    this.setAsUser(currentSection.get("as-user-name"));
    this.setUserPassword(currentSection.get("as-user-password"));
    this.setProjectToDelete(currentSection.get("project-name"));
  }

  public boolean isExpectedToFail() {
    return expectedToFail;
  }

  public void setExpectedToFail(String expectedToFail) {
    this.expectedToFail = Boolean.parseBoolean(expectedToFail);
  }

  public String getAsUser() {
    return asUser;
  }

  public void setAsUser(String asUser) {
    this.asUser = asUser;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getProjectToDelete() {
    return projectToDelete;
  }

  public void setProjectToDelete(String projectToDelete) {
    this.projectToDelete = projectToDelete;
  }
}
