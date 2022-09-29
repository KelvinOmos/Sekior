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

    Logg logger;
    Button signInButton;
    EditText editTextNumber, emailEditText;
    AppCompatTextView registerButton;
    String email, phoneNumber;

    private static final int RC_SIGN_IN = 1;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<String>();
    private ArrayList<String> permissions = new ArrayList<String>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    String phoneuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        phoneuId =  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        editTextNumber = findViewById(R.id.editTextNumber);
        emailEditText = findViewById(R.id.emailEditText);

        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = editTextNumber.getText().toString();
                email = emailEditText.getText().toString();
                if(phoneNumber.isEmpty() || email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                } else {
                    new MyTask().execute();
                }
            }
        });

        registerButton = findViewById(R.id.registerTextView);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.putExtra("phoneuId", phoneuId);
                startActivity(intent);
                finish();
            }
        });
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
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ERROR MESSAGE:::" + e.getMessage());
                logger.addRecordToLog("STACK TRACE:::" + e.getStackTrace().toString());
            }
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("These permissions are mandatory for the application. Please allow access.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
    }

}