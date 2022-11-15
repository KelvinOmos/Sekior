package com.example.sekior;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.example.sekior.models.RegistrationRequestModel;
import com.example.sekior.models.ResponseModel;
import com.example.sekior.utils.LocationTrack;
import com.example.sekior.utils.Logg;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;


public class LauncherActivity extends AppCompatActivity {

    String phoneuId;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<String>();
    private ArrayList<String> permissions = new ArrayList<String>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getSupportActionBar().hide();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        try {
            phoneuId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            new MyTask().execute();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No response from server 1", Toast.LENGTH_LONG).show();
        }
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
                LauncherActivity.this.runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), "No response from server 2", Toast.LENGTH_LONG).show();
                });

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
                        LinkedTreeMap map = (LinkedTreeMap) responseModel.getData();
                        String usermobile = "";
                        String useremail = "";
                        if (map != null) {
                            usermobile = Objects.requireNonNull(map.get("usermobile")).toString();
                            useremail = Objects.requireNonNull(map.get("useremail")).toString();
                        }
                        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
                        intent.putExtra("phoneuId", phoneuId);
                        if (!useremail.isEmpty()) {
                            intent.putExtra("email", useremail);
                        }
                        if (!usermobile.isEmpty()) {
                            intent.putExtra("phoneNumber", usermobile);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LauncherActivity.this, RegistrationActivity.class);
                        intent.putExtra("phoneuId", phoneuId);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(LauncherActivity.this, "No response from server 3", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                LauncherActivity.this.runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), "No response from server 4", Toast.LENGTH_LONG).show();
                });
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
        new AlertDialog.Builder(LauncherActivity.this)
                .setMessage("These permissions are mandatory for the application. Please allow access.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}