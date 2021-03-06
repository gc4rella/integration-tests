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
import java.io.Serializable;
import java.util.Properties;
import org.ini4j.Profile;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.Status;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.integration.test.exceptions.IntegrationTestException;
import org.openbaton.integration.test.utils.Tester;

/**
 * Created by tbr on 25.01.16.
 *
 * <p>Class used to check if a VirtualNetworkFunctionRecord's status is correct.
 */
public class VNFRStatusTester extends Tester {
  private String vnfrType = "";
  private Status status = null;

  public VNFRStatusTester(Properties properties) throws FileNotFoundException {
    super(properties, VNFRStatusTester.class);
  }

  @Override
  protected Serializable prepareObject() {
    return null;
  }

  @Override
  protected Object doWork() throws Exception {
    log.info("Start VNFRStatusTester on VNFR type " + vnfrType);
    this.setAbstractRestAgent(requestor.getNetworkServiceRecordAgent());
    NetworkServiceRecord nsr = (NetworkServiceRecord) getParam();
    if (status == null)
      throw new IntegrationTestException(
          "Status to test is not declared. Specify it in the .ini file.");
    boolean found = false;
    for (VirtualNetworkFunctionRecord vnfr : nsr.getVnfr()) {
      if (vnfr.getType().equals(vnfrType)) {
        found = true;
        log.debug("The status of the VNFR of type " + vnfrType + " is " + vnfr.getStatus());
        if (!vnfr.getStatus().equals(status)) {
          log.error(
              "The status of VNFR "
                  + vnfr.getName()
                  + " of type "
                  + vnfr.getType()
                  + " is not "
                  + status
                  + " but "
                  + vnfr.getStatus()
                  + ".");
          throw new IntegrationTestException(
              "The status of VNFR "
                  + vnfr.getName()
                  + " of type "
                  + vnfr.getType()
                  + " is not "
                  + status
                  + " but "
                  + vnfr.getStatus()
                  + ".");
        }
      }
    }

    if (!found) log.warn("did not find a VNFR of type " + vnfrType);

    log.debug("--- VNFRStatusTester finished successfully");
    return param;
  }

  @Override
  public void configureSubTask(Profile.Section currentSection) {
    String status = currentSection.get("status");
    if (status != null) {
      this.setStatus(status);
    }

    String vnfrType = currentSection.get("vnf-type");
    if (vnfrType != null) {
      this.setVnfrType(vnfrType);
    }
  }

  public void setVnfrType(String vnfrType) {
    this.vnfrType = vnfrType;
  }

  public void setStatus(String status) {
    try {
      this.status = Status.valueOf(status);
    } catch (IllegalArgumentException e) {
      log.error("The status " + status + " does not exist. Please change it in the .ini file.");
    }
  }
}
