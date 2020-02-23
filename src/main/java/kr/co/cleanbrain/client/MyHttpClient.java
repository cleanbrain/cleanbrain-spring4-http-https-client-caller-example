package kr.co.cleanbrain.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * Company: NANDSOFT
 * User: 노상현
 * Date: 2020-02-17
 * Time: 오후 1:03
 */
public class MyHttpClient {
    private static final String testUrl = "http://localhost:8080/getJsonData";

    public static void main(String[] args) throws IOException {
        // testWithApacheCommon();
        testWithJavaNet();
    }

    public static void testWithApacheCommon() {
        HttpClient httpclient = new HttpClient();
        PostMethod httppost = new PostMethod(testUrl);
        JSONParser parser = new JSONParser();
        JSONObject responseObj = new JSONObject();
        try {
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
        String result = "";

        OutputStream out = null;
        BufferedReader in = null;
        HttpURLConnection httpConn = null;

        try {
            URL url = new URL(testUrl);
            URLConnection connection = url.openConnection();

            httpConn = (HttpURLConnection) connection;
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Content-Type", "application/json");
            // httpConn.setRequestProperty("Accept", "");
            httpConn.setRequestMethod("POST");
            if (!httpConn.getDoOutput()) {
                httpConn.setDoOutput(true);
            }
            if (!httpConn.getDoInput()) {
                httpConn.setDoInput(true);
            }
            httpConn.setConnectTimeout(100000);
            httpConn.connect();

            out = httpConn.getOutputStream();
            // out.write(data.getBytes("UTF-8"));
            out.flush();

            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String inputLine = null;

            if ((inputLine = in.readLine()) != null) {
                result = inputLine;
            }

            System.out.println(result);
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }
}
