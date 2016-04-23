package com.oulu.daussy.broommate.Configuration;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oulu.daussy.broommate.Model.GoogleCloudMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by daussy on 25/03/16.
 * Following the explanations of https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/
 * to be able to query a external database
 */
public class RequestHandler {


    public String sendGoogleRequest(GoogleCloudMessage message) {
        StringBuffer response = null;
        try{

            // 1. URL
            URL url = new URL(Config.URL_GOOGLE_CLOUD_MSG);

            // 2. Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 3. Specify POST method
            conn.setRequestMethod("POST");

            // 4. Set the headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key="+Config.API_KEY);

            conn.setDoOutput(true);

            // 5. Add JSON data into POST request body

            //`5.1 Use Jackson object mapper to convert Contnet object into JSON
            ObjectMapper mapper = new ObjectMapper();

            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            // 5.3 Copy Content "JSON" into
            mapper.writeValue(wr, message);

            // 5.4 Send the request
            wr.flush();

            // 5.5 close
            wr.close();

            // 6. Get the response
            int responseCode = conn.getResponseCode();
            Log.d("GCM", " Sending 'POST' request to URL : " + url);
            Log.d("GCM", " Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 7. Print result
            Log.d("GCM response ",response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    /**
     * Method to send HttpPostRequest
     * This method takes two arguments
     * 1 : URL of the script to which we will send the request
     * 2 : HashMap with name value pairs containing the data to be send with the request
     */
    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        //Creating a URL
        URL url;

        //StringBuilder object to store the message retrieved from the server
        StringBuilder sb = new StringBuilder("Error");
        HttpURLConnection conn = null;
        try {
            //Initializing Url
            url = new URL(requestURL);

            //Creating connection
            conn = (HttpURLConnection) url.openConnection();

            //Configuring connection properties
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //Creating an output stream
            OutputStream os = conn.getOutputStream();

            //Writing parameters to the request
            //We are using a method getPostDataString which is defined below
            BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(os, "UTF-8")
                                    );
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            //Log.d("HTTP Response : ", "Before");
            int responseCode = conn.getResponseCode();
            //Log.d("HTTP Response : ", ""+ responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                //Reading server response
                while ((response = br.readLine()) != null){
                    sb.append(response);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String sendGetRequest(String requestURL){
        StringBuilder sb =new StringBuilder();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s+"\n");
            }
        }catch(Exception e){
        }
        return sb.toString();
    }



    public String sendGetRequestParam(String requestURL, String id){
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(requestURL+id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s+"\n");
            }
        }catch(Exception e){
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
