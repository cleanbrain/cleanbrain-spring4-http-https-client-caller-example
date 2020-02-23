package kr.co.cleanbrain.client;

import kr.co.cleanbrain.client.factory.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;

/**
 * Created with IntelliJ IDEA.
 * Company: NANDSOFT
 * User: 노상현
 * Date: 2020-02-17
 * Time: 오후 1:03
 */
public class MyHttpsClient {

    private static final String testUrl = "https://localhost:8443/getJsonData";

    public static void main(String[] args) throws IOException {
        // testWithApacheCommon();
        testWithJavaNet();
        // systemParamTest();
    }

    // SSL 인증 스킵
    public static void testWithApacheCommon() {
        HttpClient httpclient = new HttpClient();
        PostMethod httppost = new PostMethod(testUrl);
        JSONParser parser = new JSONParser();
        JSONObject responseObj = new JSONObject();
        try {
            Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
            httpclient.executeMethod(httppost);
            responseObj = (JSONObject) parser.parse(httppost.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.releaseConnection();
        }

        System.out.println(responseObj);
    }

    public static void testWithJavaNet() throws IOException {
        final String keyStorePassword = "changeit";

        OutputStream out = null;
        BufferedReader in = null;
        String result = null;
        HttpsURLConnection httpsConn = null;
        SSLContext sc = null;
        try {
            try {
                sc = SSLContext.getInstance("TLSv1.2");
            } catch (Exception e) {
                try {
                    sc = SSLContext.getInstance("TLSv1.1");
                } catch (Exception e1) {
                    try {
                        sc = SSLContext.getInstance("TLS1");
                    } catch (Exception e2) {
                        try {
                            sc = SSLContext.getInstance("TLS");
                        } catch (Exception e3) {
                            System.out.println(e3.getMessage());
                            sc = SSLContext.getInstance("SSLv3");
                        }
                    }
                }
            }

            /* Load keyStore File */
            char SEP = File.separatorChar;
            File keyStoreFile = new File("D:\\work\\example-projcects\\cleanbrain-http-https-client-callee-example\\certification\\keystore");
            InputStream input = new FileInputStream(keyStoreFile);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(input, keyStorePassword.toCharArray());
            input.close();

            /* set cacerts to TrustManager */
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            sc.init(new KeyManager[0], new TrustManager[]{defaultTrustManager}, new java.security.SecureRandom());

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.out.println(sw.toString());
        }

        try {
            URL url = new URL(testUrl);
            URLConnection connection = url.openConnection();
            httpsConn = (HttpsURLConnection) connection;
            httpsConn.setSSLSocketFactory(sc.getSocketFactory());
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            // httpsConn.setRequestProperty("Content-Length", String.valueOf(b.length));
            // httpsConn.setRequestProperty("Accept", Type);
            httpsConn.setRequestProperty("Content-Type", "application/json");
            httpsConn.setRequestMethod("POST");

            if (!httpsConn.getDoOutput()) {
                httpsConn.setDoOutput(true);
            }
            if (!httpsConn.getDoInput()) {
                httpsConn.setDoInput(true);
            }

            httpsConn.setConnectTimeout(10000);
            httpsConn.connect();
            out = httpsConn.getOutputStream();
            // out.write(data.getBytes("UTF-8"));
            out.flush();

            in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "UTF-8"));
            String inputLine = null;
            if ((inputLine = in.readLine()) != null) {
                result = inputLine;
            }
        } catch (Throwable t) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            System.out.println(sw.toString());
        } finally {
            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (httpsConn != null) {
                httpsConn.disconnect();
            }
        }

        System.out.println(result);
    }

    public static void systemParamTest() {
        System.out.println(System.getProperties());
    }
}
