package com.lezurex.ap20bklassenportal;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.loader.content.AsyncTaskLoader;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lezurex.ap20bklassenportal.async.PostRequest;
import com.lezurex.ap20bklassenportal.login.LoginError;
import com.lezurex.ap20bklassenportal.tasks.Task;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String EMAIL_KEY = "email";
    private static final String API_BASE_URL = "https://ap20b.lezurex.com/app/";

    private static Utils instance;
    private SharedPreferences sharedPreferences;
    private Context context;

    private ArrayList<Task> tasks = new ArrayList<>();

    private Utils(Context context) {
        sharedPreferences = context.getSharedPreferences("ap20b_data", Context.MODE_PRIVATE);
        this.context = context;
        initData();
    }

    public static Utils getInstance(Context context) {
        if (null != instance) {
            return instance;
        } else {
            instance = new Utils(context);
            return instance;
        }
    }

    private void initData() {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public boolean validateToken() {
        Log.d("Request", "Validate");
        String token;
        if ((token = getAccessToken()) != null) {
            Map<String, String> args = new HashMap<>();
            Log.d("Token", getAccessToken());
            Log.d("Email", getEmail());
            args.put("token", getAccessToken());
            args.put("email", getEmail());
            JSONObject jsonObject = query(API_BASE_URL + "testToken.php", args);
            Log.d("JSON", jsonObject.toString());
            if (jsonObject.has("error")) {
                return false;
            } else if (jsonObject.has("success")) {
                return true;
            }
        }
        return false;
    }

    public LoginError login(String email, String password) {
        Map<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("password", password);
        Log.d("Request", "Login");
        JSONObject jsonObject = query(API_BASE_URL + "login.php", args);
        try {
            if (jsonObject.has("error")) {
                switch (jsonObject.getString("error")) {
                    case "Wrong Login":
                        return LoginError.INVALID_CREDENTIALS;
                    case "Not Permitted":
                        return LoginError.NOT_PERMITTED;
                }
            } else if (jsonObject.has("success")) {
                setAccessToken(jsonObject.getString("success"));
                return LoginError.SUCCESS;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return LoginError.OTHER;
    }

    public ArrayList<Task> refreshTasks() {
        Map<String, String> args = new HashMap<>();
        args.put("token", getAccessToken());
        args.put("email", getEmail());
        Log.d("Request", "Tasks");
        JSONObject jsonObject = query(API_BASE_URL + "tasks.php", args);
        if (jsonObject.has("error")) {
            return null;
        } else {
            try {
                ArrayList<Task> newTasks = new ArrayList<>();
                for (int i=0; i < jsonObject.getJSONArray("data").length(); i++) {
                    System.out.println("Iterate " + i);
                    JSONObject item = jsonObject.getJSONArray("data").getJSONObject(i);
                    newTasks.add(new Task(item.getInt("id"), item.getString("title"), item.getString("subject"), item.getString("date"), item.getString("description")));
                }
                this.tasks = newTasks;
                System.out.println(newTasks.toString());
                return newTasks;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private JSONObject query(String dest, Map<String, String> postFields) {
        if (isNetworkAvailablable()) {
            PostRequest postRequest = new PostRequest();
            try {
                String result = postRequest.execute(dest, new Gson().toJson(postFields)).get();
                if (result != null) {
                    return new JSONObject(result);
                }
            } catch (JSONException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Toast.makeText(context, context.getResources().getString(R.string.err_no_internet), Toast.LENGTH_LONG).show();
                return new JSONObject("{\"error\":'NoNetwork'}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }

    public boolean isNetworkAvailablable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL_KEY, null);
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(EMAIL_KEY);
        editor.putString(EMAIL_KEY, email);
        editor.commit();
    }

    public void setAccessToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.commit();
    }

}
