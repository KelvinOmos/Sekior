package com.example.sekior;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.example.sekior.models.ResponseModel;
import com.example.sekior.utils.Logg;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Future;


public class LauncherActivity extends AppCompatActivity {

    Handler handler;

    String phoneuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getSupportActionBar().hide();

        phoneuId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        new MyTask().execute();
    }

    private HttpResponse callApi() throws JSONException, UnirestException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phoneuid", phoneuId);
        String url = "http://102.223.37.26:7833/api/User/isuserregistered";
        HttpResponse<String> response = Unirest.post(url)
                .header("accept", "application/json")
                .header("Content-Type", "application/json").body(jsonObject.toString())
                .asString();
        return response;
    }

    private class MyTask extends AsyncTask<Void, Void, Void>{
        HttpResponse response;
        Logg logger;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                logger = new Logg();
                response = callApi();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ERROR MESSAGE:::" + e.getMessage());
                logger.addRecordToLog("STACK TRACE:::" + e.getStackTrace().toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                Gson gc = new Gson();
                if (response != null) {
                    ResponseModel responseModel = gc.fromJson(response.getBody().toString(), ResponseModel.class);
                    if (responseModel.getSucceeded()) {
                        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
                        intent.putExtra("phoneuId", phoneuId);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LauncherActivity.this, RegistrationActivity.class);
                        intent.putExtra("phoneuId", phoneuId);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(LauncherActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ERROR MESSAGE:::" + e.getMessage());
                logger.addRecordToLog("STACK TRACE:::" + e.getStackTrace().toString());
            }

        }
    }
}