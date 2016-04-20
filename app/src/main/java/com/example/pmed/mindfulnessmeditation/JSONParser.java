package com.example.pmed.mindfulnessmeditation;

/**
 * Created by harri on 4/19/2016.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url=
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                Log.w("test", "Made it in here");
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                Log.w("test", "Made it in here1");
                HttpPost httpPost = new HttpPost(url);
                Log.w("test", "Made it in here2");
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                Log.w("test", "Made it in here3");

                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.w("test", "Made it in here4");
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.w("test", "Made it in here5");
                is = httpEntity.getContent();

            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Log.w("test", "Made it in here6");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            Log.w("test", "Made it in here7");
            StringBuilder sb = new StringBuilder();
            Log.w("test", "Made it in here8");
            String line = null;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                Log.w("test", "Made it in here9-" + count);
                count = count + 1;
            }
            Log.w("test", "Made it in here10");
            is.close();
            Log.w("test", "Made it in here11");
            json = sb.toString();
            Log.w("test", "Made it in here12");
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
}
