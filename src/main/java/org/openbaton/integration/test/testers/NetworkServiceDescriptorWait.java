package org.openbaton.integration.test.testers;

import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.nfvo.Action;
import org.openbaton.catalogue.nfvo.EndpointType;
import org.openbaton.catalogue.nfvo.EventEndpoint;
import org.openbaton.integration.test.exceptions.IntegrationTestException;
import org.openbaton.integration.test.exceptions.SubscriptionException;
import org.openbaton.integration.test.interfaces.Waiter;
import org.openbaton.sdk.api.exception.SDKException;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by mob on 31.07.15.
 */
public class NetworkServiceDescriptorWait extends Waiter {

    private static final String name="NetworkServiceDescriptorWait";
    private int nsrCreated=0;

    public NetworkServiceDescriptorWait(Properties properties) {
        super(properties, NetworkServiceRecordWait.class, "", "");
    }

    @Override
    protected Serializable prepareObject() {
        return null;
    }

    @Override
    protected Object doWork() throws SubscriptionException, SDKException, InterruptedException {

        EventEndpoint eventEndpoint = createEventEndpoint(name,EndpointType.REST);
        eventEndpoint.setEvent(Action.RELEASE_RESOURCES_FINISH);
        NetworkServiceDescriptor nsd = (NetworkServiceDescriptor) param;
        //The eventEndpoint param of EventEndpoint will be set in the RestWaiter or JMSWaiter

        try {
            subscribe(eventEndpoint);
            log.debug(name + ": --- Registration complete, start waiting for deleting the nsd with id:" + nsd.getId());
            waitForEvent();
            this.unSubscribe();
            log.debug(name + ": --- unSubscription complete it's time to delete the nsd with id :" + nsd.getId());
        } catch (SubscriptionException e) {
            log.error("Subscription failed for the event " + eventEndpoint.getEvent().toString() + " nsd id: " + nsd.getId() + " nsd name: " + nsd.getName());
            throw e;
        } catch (SDKException e) {
            log.error("Wait failed for the event " + eventEndpoint.getEvent().toString() + " nsd id: " + nsd.getId() + " nsd name: " + nsd.getName());
            throw e;
        } catch (InterruptedException e) {
            log.error("Wait failed for the event " + eventEndpoint.getEvent().toString() + " nsd id: " + nsd.getId() + " nsd name: " + nsd.getName());
            throw e;
        }
        return nsd.getId();
    }
}
