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
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;
import org.openbaton.integration.test.exceptions.IntegrationTestException;
import org.openbaton.integration.test.utils.Tester;
import org.openbaton.integration.test.utils.Utils;
import org.openbaton.sdk.api.exception.SDKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lto on 24/06/15.
 *
 * <p>Tester to create a NetworkServiceDescriptor.
 */
public class NetworkServiceDescriptorCreate extends Tester<NetworkServiceDescriptor> {
  private static Logger log = LoggerFactory.getLogger(NetworkServiceDescriptor.class);

  private String fileName;
  private boolean expectedToFail =
      false; // if the creating of the NSD is expected to fail this field should be set to true

  public NetworkServiceDescriptorCreate(Properties p) throws FileNotFoundException {
    super(p, NetworkServiceDescriptor.class);
  }

  @Override
  protected Object doWork() throws SDKException, IntegrationTestException, FileNotFoundException {
    log.info("Upload NSD " + fileName);
    this.setAbstractRestAgent(requestor.getNetworkServiceDescriptorAgent());
    Object received = null;
    BaseVimInstance vimInstance = (BaseVimInstance) param;
    try {
      received = create();
    } catch (SDKException sdkEx) {
      if (!expectedToFail) {
        log.error(
            "Exception during the on-boarding of NetworkServiceDescriptor from vim with name: "
                + vimInstance.getName()
                + " and id: "
                + vimInstance.getId());
        throw sdkEx;
      } else {
        log.info("As expected the creation of NSD " + fileName + " failed. Everything is fine.");
        return received;
      }
    }
    if (expectedToFail) {
      log.error(
          "The NSD "
              + fileName
              + " was expected to throw an error while onboarding to the NFVO but it did not.");
      throw new IntegrationTestException(
          "The NSD "
              + fileName
              + " was expected to throw an error while onboarding to the NFVO but it did not.");
    }
    NetworkServiceDescriptor nsd = (NetworkServiceDescriptor) received;
    log.debug("Stored NSD with id: " + nsd.getId());
    return received;
  }

  @Override
  public void configureSubTask(Profile.Section currentSection) {
    this.setFileName(currentSection.get("name-file"));
    this.setExpectedToFail(currentSection.get("expected-to-fail"));
  }

  @Override
  protected NetworkServiceDescriptor prepareObject() throws FileNotFoundException {
    String fileAbsoluteName = properties.getProperty("nsd-path") + fileName;
    String body = Utils.getContent(fileAbsoluteName);
    return mapper.fromJson(body, aClass);
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setExpectedToFail(String expectedToFailString) {
    expectedToFail = Boolean.parseBoolean(expectedToFailString);
    log.trace("Set expectedToFail to " + expectedToFail);
  }
}
