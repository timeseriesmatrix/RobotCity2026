/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.api.client.Client
 *  com.sun.jersey.api.client.ClientResponse
 *  com.sun.jersey.api.client.WebResource
 *  com.sun.jersey.core.util.MultivaluedMapImpl
 */
package com.floreantpos.services;

import com.floreantpos.PosLog;
import com.floreantpos.config.TerminalConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class PosWebService {
    private static final String SERVICE_URL = TerminalConfig.getWebServiceUrl();

    public String getAvailableNewVersions(String terminalKey, String currentPosVersion) {
        try {
            Client client = Client.create();
            client.getProperties();
            MultivaluedMapImpl map = new MultivaluedMapImpl();
            map.add((Object)"terminal_key", (Object)terminalKey);
            map.add((Object)"pos_version", (Object)currentPosVersion);
            WebResource webResource = client.resource(SERVICE_URL + "/public/posuser/update");
            ClientResponse response = (ClientResponse)webResource.accept(new String[]{"application/json"}).post(ClientResponse.class, (Object)map);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String versionInfo = (String)response.getEntity(String.class);
            PosLog.info(this.getClass(), "\n============update info============");
            PosLog.info(this.getClass(), versionInfo);
            return versionInfo;
        }
        catch (Exception exception) {
            return null;
        }
    }
}

