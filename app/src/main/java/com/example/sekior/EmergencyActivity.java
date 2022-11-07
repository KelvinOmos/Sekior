package com.example.sekior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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

public class EmergencyActivity extends AppCompatActivity {

    LinearLayout abuseLayout,rapeLayout, mobLayout, kidnapLayout, floodLayout, fireLayout, burglaryLayout, accidentLayout;
    String email, phoneNumber, phoneuId,longitude, latitude;
    AppCompatButton panicMode, buttonOthers;
    LocationTrack locationTrack;
    Logg logger;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        getSupportActionBar().hide();

        logger = new Logg();

        sharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
        longitude = sharedPreferences.getString("longitude", null);
        latitude = sharedPreferences.getString("latitude", null);

        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        phoneuId = getIntent().getStringExtra("phoneuId");

        panicMode = findViewById(R.id.space2);
        panicMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarning(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new MyTask().execute();
                    }
                });
            }
        });

        buttonOthers = findViewById(R.id.buttonOthers);
        buttonOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otherIntent = new Intent(EmergencyActivity.this, OtherActivity.class);
                otherIntent.putExtra("phoneNumber", phoneNumber);
                otherIntent.putExtra("email", email);
                startActivity(otherIntent);
            }
        });

        abuseLayout = findViewById(R.id.abuseLayout);
        abuseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "1");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("email", email);

                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });

        accidentLayout = findViewById(R.id.accidentLayout);
        accidentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "2");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("email", email);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });

        burglaryLayout = findViewById(R.id.burglaryLayout);
        burglaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "3");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("email", email);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });

        fireLayout = findViewById(R.id.fireLayout);
        fireLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "4");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("email", email);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });

        floodLayout = findViewById(R.id.floodLayout);
        floodLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "5");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("email", email);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });

        kidnapLayout = findViewById(R.id.kidnapLayout);
        kidnapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "6");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("email", email);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });

        mobLayout = findViewById(R.id.mobLayout);
        mobLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "7");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("email", email);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });

        rapeLayout = findViewById(R.id.rapeLayout);
        rapeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(EmergencyActivity.this, ImageActivity.class);
                imageIntent.putExtra("serviceTypeId", "8");
                imageIntent.putExtra("phoneNumber", phoneNumber);
                imageIntent.putExtra("phoneuId", phoneuId);
                imageIntent.putExtra("longitude", longitude);
                imageIntent.putExtra("latitude", latitude);
                imageIntent.putExtra("email", email);
                startActivity(imageIntent);
                overridePendingTransition(0,0);
            }
        });
    }

    private void showWarning(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EmergencyActivity.this)
                .setMessage("It is a criminal offense to send false information, do you want to proceed?")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
    }

    private HttpResponse callApi() throws JSONException, UnirestException {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(EmergencyActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            Toast.makeText(EmergencyActivity.this, "Please enable location", Toast.LENGTH_SHORT).show();

        }

        String address = addresses.get(0).getAddressLine(0);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sendermobile", phoneNumber);
        jsonObject.put("sendername", "");
        jsonObject.put("senderemail", email);
        jsonObject.put("currentaddress", address == null || address.equals("") ? "" : address);
        jsonObject.put("currentcoordinate",latitude + "," + longitude);
        String url = "http://102.223.37.26:7833/api/Duress/panicmsg";
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
                Toast.makeText(getApplicationContext(), "Service not available", Toast.LENGTH_LONG).show();

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
                    Intent intent = new Intent(EmergencyActivity.this, SuccessActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Service not available", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Service not available", Toast.LENGTH_LONG).show();

            }
        }
    }
}