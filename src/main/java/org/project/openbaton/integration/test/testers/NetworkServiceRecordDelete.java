package org.project.openbaton.integration.test.testers;

import org.project.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.project.openbaton.integration.test.utils.Tester;

import java.util.Properties;

/**
 * Created by mob on 28.07.15.
 */
public class NetworkServiceRecordDelete extends Tester<NetworkServiceRecord> {

    public NetworkServiceRecordDelete(Properties properties) {
        super(properties, NetworkServiceRecord.class, "", "/ns-records");
    }

    @Override
    protected NetworkServiceRecord prepareObject() {
        return null;
    }

    @Override
    protected Object doWork() throws Exception {
        NetworkServiceRecord nsr = (NetworkServiceRecord) param;
        delete(nsr.getId());
        //TODO Send back id of NSD father
        return null;
    }

    @Override
    protected void handleException(Exception e) {
        e.printStackTrace();
        log.error("there was an exception: " + e.getMessage());
    }
}