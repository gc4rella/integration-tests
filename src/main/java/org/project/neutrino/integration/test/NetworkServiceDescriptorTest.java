package org.project.neutrino.integration.test;

import org.json.JSONObject;
import org.project.neutrino.integration.test.exceptions.IntegrationTestException;
import org.project.neutrino.integration.test.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by lto on 24/06/15.
 */
public class NetworkServiceDescriptorTest {
    private static final String FILE_NAME = "/etc/json_file/network_service_descriptors/NetworkServiceDescriptor-with-dependencies-without-allacation.json";
    private static Logger log = LoggerFactory.getLogger(VimInstanceTest.class);
    private static String path = "ns-descriptors";

    public static String create(String nfvoIp, String nfvoPort) throws URISyntaxException {
        String body = Utils.getStringFromInputStream(NetworkServiceDescriptorTest.class.getResourceAsStream(FILE_NAME));

        JSONObject obtained;
        JSONObject expected = new JSONObject(body);
        String url = "http://" + nfvoIp + ":" + nfvoPort+ "/api/v1/" + path;
        log.info("Sending request create NetworkServiceDescriptor on url: " + url);

        try {
            obtained = Utils.executePostCall(nfvoIp, nfvoPort, body, path);
            log.trace("received: " + obtained.toString());
        	
            Boolean resultEvaluate = Utils.evaluateJSONObject(expected, obtained);
            
            if(resultEvaluate == false)
            	log.debug("NSD RESPONSE - FALSE");
            else
            	log.debug("NSD RESPONSE - TRUE");

            


        } catch (IntegrationTestException e) {
            e.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        /**
         * TODO assert everything is created!
         */

        return obtained.getString("id");
    }

    public static String create() throws IOException, URISyntaxException {
        Properties properties = Utils.getProperties();
        return NetworkServiceDescriptorTest.create(properties.getProperty("nfvo-ip"), properties.getProperty("nfvo-port"));
    }
}
