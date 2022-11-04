package com.example.sekior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity {

    AppCompatButton btnNext;
    Button btnSelectImage;

    byte[] imageBytes;
    Bitmap myBitmap;
    Uri picUri;

    Logg logger;

    LocationTrack locationTrack;
    String email, phoneNumber, serviceTypeId,longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();

        logger = new Logg();

        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        serviceTypeId = getIntent().getStringExtra("serviceTypeId");

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivityForResult(getPickImageChooserIntent(), 200);
                }catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                    logger.addRecordToLog("ERROR MESSAGE:::" + e.getMessage());
                    logger.addRecordToLog("STACK TRACE:::" + e.getStackTrace().toString());
                }
            }
        });

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarning(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            locationTrack = new LocationTrack(ImageActivity.this);
                            if (locationTrack.canGetLocation()) {
                                new MyTask().execute();
                            } else {
                                locationTrack.showSettingsAlert();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                            logger.addRecordToLog("ERROR MESSAGE:::" + e.getMessage());
                            logger.addRecordToLog("STACK TRACE:::" + e.getStackTrace().toString());
                        }
                    }
                });
            }
        });
    }


    private HttpResponse callApi() throws JSONException, UnirestException {
        JSONObject jsonObject = new JSONObject();
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(ImageActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ImageActivity.this, "Please enable location and try again", Toast.LENGTH_SHORT).show();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        String imageString = imageBytes != null ? android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT) : "";

        jsonObject.put("sendermobile", phoneNumber);
        jsonObject.put("serviceTypeId", serviceTypeId);
        jsonObject.put("senderemail", email);
        jsonObject.put("sendername", "");
        jsonObject.put("imagereport", imageString);
        jsonObject.put("currentaddress", address == null || address.equals("") ? "" : address);
        jsonObject.put("currentcoordinate", latitude + "," + longitude);

        String url = "http://102.223.37.26:7833/api/Duress/sendmsg";
        HttpResponse<String> response = Unirest.post(url)
                .header("accept", "application/json")
                .header("Content-Type", "application/json").body(jsonObject.toString())
                .asString();
        return response;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        HttpResponse response;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                response = callApi();
            } catch (Exception e) {
                ImageActivity.this.runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), "An error has occured, please try again", Toast.LENGTH_LONG).show();
                });

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Gson gc = new Gson();
            try {
                ResponseModel responseModel = gc.fromJson(response.getBody().toString(), ResponseModel.class);
                if (responseModel.getSucceeded()) {
                    Intent intent = new Intent(ImageActivity.this, SuccessActivity.class);
                    if (!email.isEmpty()) {
                        intent.putExtra("email", email);
                    }
                    if (!phoneNumber.isEmpty()) {
                        intent.putExtra("phoneNumber", phoneNumber);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Service not available", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ImageActivity.this.runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), "Service not available", Toast.LENGTH_LONG).show();
                });
            }
        }
    }

    private void showWarning(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ImageActivity.this)
                .setMessage("It is a criminal offense to send false information, do you want to proceed?")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public Intent getPickImageChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = (Intent) allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            InputStream iStream;
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    imageView.setImageBitmap(myBitmap);
                    iStream = getContentResolver().openInputStream(picUri);
                    imageBytes = getBytes(iStream);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap = bitmap;
                imageView.setImageBitmap(myBitmap);
                try {
                    iStream = getContentResolver().openInputStream(picUri);
                    imageBytes = getBytes(iStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}