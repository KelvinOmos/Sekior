package com.example.sekior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import java.util.List;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    AppCompatButton registerButton;
    EditText number,email;
    String phoneNumber, emailAddress, phoneuId;
    LocationTrack locationTrack;
    double longitude , latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        phoneuId = getIntent().getStringExtra("phoneuId");

        locationTrack = new LocationTrack(RegistrationActivity.this);
        if(locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();
        }

        number = findViewById(R.id.editTextNumberReg);
        email = findViewById(R.id.emailEditTextReg);


        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = number.getText().toString();
                emailAddress = email.getText().toString();
                if (phoneNumber.isEmpty() && emailAddress.isEmpty()) {
                    showWarning(null);
                } else {
                    new MyTask().execute();
                }
            }
        });
    }

    private void showWarning(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(RegistrationActivity.this)
                .setMessage("Please fill in one or more details")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private HttpResponse callApi() throws JSONException, UnirestException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("useremail", emailAddress);
        jsonObject.put("usermobile", phoneNumber);
        jsonObject.put("username", "");
        jsonObject.put("phonebrand", "");
        jsonObject.put("phonemodel", "");
        jsonObject.put("phoneuid", phoneuId);
        String url = "http://102.223.37.26:7833/api/User/newuser";
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
                RegistrationActivity.this.runOnUiThread(()-> {
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
                    Intent intent = new Intent(RegistrationActivity.this, EmergencyActivity.class);
                    intent.putExtra("email", emailAddress);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("phoneuId", phoneuId.isEmpty() ? "" : phoneuId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Registration error", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e) {
                RegistrationActivity.this.runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), "An error has occured, please try again", Toast.LENGTH_LONG).show();
                });

            }
        }
    }

    @Override
    public void onBackPressed() {
    }

}