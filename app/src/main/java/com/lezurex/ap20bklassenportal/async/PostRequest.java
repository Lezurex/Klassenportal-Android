package com.lezurex.ap20bklassenportal.async;

import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

import javax.net.ssl.HttpsURLConnection;

public class PostRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> postFields = gson.fromJson(params[1], type);

        try {
            URL url = new URL(params[0]);
            URLConnection con = url.openConnection();
            HttpsURLConnection http = (HttpsURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : postFields.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            Log.d("Request", sj.toString());

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            Log.d("Request", "sent");

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String response = null;
            while ((response = br.readLine()) != null) {
                Log.d("Response", response);
                return response;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
