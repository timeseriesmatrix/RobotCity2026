/*
 * Decompiled with CFR 0.151.
 */
package com.mercurypay.ws.sdk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class MercuryWebRequest {
    private URL mWebServiceURL;
    private String mWebMethodName = "";
    private final String mXMLNamespace = "http://www.mercurypay.com";
    private int mTimeout;
    private HashMap<String, String> mWSParameters;
    private final String mSOAPWrapper = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><%2$s xmlns=\"%1$s\">%3$s</%2$s></soap:Body></soap:Envelope>";
    private final String mMPSExceptionString = "MPSWebRequest Error: %1$s";

    public MercuryWebRequest(String webServiceURL) throws Exception {
        this.setWebServiceURL(webServiceURL);
        this.mWebMethodName = "";
        this.mTimeout = 10000;
        this.mWSParameters = new HashMap();
    }

    public void setWebMethodName(String webMethodName) {
        this.mWebMethodName = webMethodName.trim();
    }

    private void setWebServiceURL(String webServiceURL) throws Exception {
        URL tempURL = new URL(webServiceURL = webServiceURL.trim());
        if (!tempURL.getProtocol().equals("https")) {
            throw new Exception(String.format("MPSWebRequest Error: %1$s", "WebService URL value must use SSL"));
        }
        this.mWebServiceURL = tempURL;
    }

    public void setTimeout(int timeout) throws Exception {
        if (timeout <= 0) {
            throw new Exception(String.format("MPSWebRequest Error: %1$s", "Timeout value must be greater than 0"));
        }
        this.mTimeout = timeout * 1000;
    }

    private String buildSOAPRequest() throws Exception {
        if (!this.mWSParameters.isEmpty()) {
            StringBuilder parameters = new StringBuilder();
            for (Map.Entry<String, String> element : this.mWSParameters.entrySet()) {
                parameters.append(String.format("<%1$s>%2$s</%1$s>", element.getKey(), element.getValue()));
            }
            return String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><%2$s xmlns=\"%1$s\">%3$s</%2$s></soap:Body></soap:Envelope>", "http://www.mercurypay.com", this.mWebMethodName, parameters.toString());
        }
        throw new Exception(String.format("MPSWebRequest Error: %1$s", "Cannot build SOAP request with no parameters"));
    }

    public void addParameter(String paramName, String paramValue) throws Exception {
        paramName = paramName.trim();
        paramValue = paramValue.trim();
        if (paramName.equals("tran")) {
            paramValue = paramValue.replace("<", "&lt;").replace(">", "&gt;").replace("\t", "").replace("\n", "").replace("\r", "");
        }
        this.mWSParameters.put(paramName, paramValue);
    }

    public String sendRequest() throws Exception {
        this.validateRequiredParameters();
        String responseData = "";
        boolean error = false;
        String soap = this.buildSOAPRequest();
        HttpsURLConnection conn = (HttpsURLConnection)this.mWebServiceURL.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setReadTimeout(this.mTimeout);
        conn.setConnectTimeout(this.mTimeout);
        conn.setUseCaches(false);
        conn.setDefaultUseCaches(false);
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("Content-Length", String.valueOf(soap.length()));
        conn.setRequestProperty("SOAPAction", "\"http://www.mercurypay.com/" + this.mWebMethodName + "\"");
        conn.setRequestProperty("Connection", "Close");
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(soap);
        wr.flush();
        wr.close();
        int httpResponseCode = conn.getResponseCode();
        BufferedReader rd = httpResponseCode != 200 ? new BufferedReader(new InputStreamReader(conn.getErrorStream())) : new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String responseBuffer = "";
        while ((responseBuffer = rd.readLine()) != null) {
            responseData = responseData + responseBuffer;
        }
        rd.close();
        int start = 0;
        int end = 0;
        if (httpResponseCode != 200) {
            error = true;
            String returnparam = "faultstring";
            if (responseData.contains(returnparam)) {
                start = responseData.indexOf("<" + returnparam + ">") + returnparam.length() + 2;
                end = responseData.indexOf("</" + returnparam + ">");
            }
        } else {
            String returnparam = this.mWebMethodName + "Result";
            start = responseData.indexOf("<" + returnparam + ">") + returnparam.length() + 2;
            end = responseData.indexOf("</" + returnparam + ">");
        }
        responseData = responseData.substring(start, end).replace("&lt;", "<").replace("&gt;", ">");
        if (error) {
            throw new Exception(String.format("MPSWebRequest Error: %1$s", responseData));
        }
        return responseData;
    }

    private void validateRequiredParameters() throws Exception {
        if (this.mWebMethodName.equals("")) {
            throw new Exception(String.format("MPSWebRequest Error: %1$s", "WebMethodName is required"));
        }
        if (!this.mWSParameters.containsKey("pw")) {
            throw new Exception(String.format("MPSWebRequest Error: %1$s", "WebServices password parameter (\"pw\") is required"));
        }
    }
}

