package com.example.sekior;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sekior.models.ResponseModel;
import com.example.sekior.utils.LocationTrack;
import com.example.sekior.utils.Logg;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    Button signInButton;
    EditText editTextNumber, emailEditText;
    String email, phoneNumber;

    private static final int RC_SIGN_IN = 1;

    LocationTrack locationTrack;
    double longitude, latitude;

    String phoneuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        locationTrack = new LocationTrack(LoginActivity.this);
        if(locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();
        } else {
            locationTrack.showSettingsAlert();
            finish();
            System.exit(0);
        }

        SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("location",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("longitude", String.valueOf(longitude));
        editor.putString("latitude", String.valueOf(latitude));
        editor.commit();

        phoneuId =  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        editTextNumber = findViewById(R.id.editTextNumber);
        emailEditText = findViewById(R.id.emailEditText);

        if (email != null && !email.isEmpty()) {
            emailEditText.setText(email);
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            editTextNumber.setText(phoneNumber);
        }

        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNumber.isEmpty() && email.isEmpty()) {
                    showWarning(null);
                } else {
                    new MyTask().execute();
                }
            }
        });
    }

    private void showWarning(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("Please fill in one or more details")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private HttpResponse callApi() throws JSONException, UnirestException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("useremail", email);
        jsonObject.put("usermobile", phoneNumber);
        String url = "http://102.223.37.26:7833/api/User/loginuser";
        HttpResponse<String> response = Unirest.post(url)
                .header("accept", "application/json")
                .header("Content-Type", "application/json").body(jsonObject.toString())
                .asString();
        return response;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        HttpResponse response;
        Logg logger;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                logger = new Logg();
                response = callApi();
            } catch (Exception e) {
                LoginActivity.this.runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), "An error has occured, please try again", Toast.LENGTH_LONG).show();
                });

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                Gson gc = new Gson();
                ResponseModel responseModel = gc.fromJson(response.getBody().toString(), ResponseModel.class);
                System.out.println(responseModel);
                if (responseModel.getSucceeded()) {
                    Intent intent = new Intent(LoginActivity.this, EmergencyActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("phoneuId", phoneuId.isEmpty() ? "" : phoneuId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Sign in cancel", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                LoginActivity.this.runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), "No response from server", Toast.LENGTH_LONG).show();
                });

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
    }

}