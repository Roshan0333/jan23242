package com.darkdiamond.jan23242;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.Date;
import java.util.Locale;
import android.content.ContentResolver;
import android.content.ContentValues;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openDialer(View view) {
        Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
        startActivity(dialerIntent);
    }

    public void openMaps(View view) {
        Uri gmmIntentUri = Uri.parse("geo:28.5426,77.3323?z=15&q=Amity+University+Noida");
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapsIntent.setPackage("com.google.android.apps.maps");
        if (mapsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapsIntent);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=28.5426,77.3323"));
            startActivity(browserIntent);
        }
    }
    public void setAlarm(View view) {
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        if (alarmIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(alarmIntent);
        } else {
            Toast.makeText(this, "No clock app found to set alarm", Toast.LENGTH_SHORT).show();
        }
    }

    public void addContact(View view){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivity(intent);

    }

    public void composeEmail(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void searchWebTab(View view) {
        Intent intent = new Intent(MainActivity.this, SearchInput.class);
        startActivity(intent);
        finish();
    }


    public void openAirplaneModeSettings(View view) {
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        startActivity(intent);
    }

    public void openCamera(View view) {
        if (checkCameraPermission()) {
            startCameraIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void startCameraIntent() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, "com.darkdiamond.jan23242.fileprovider", photoFile);
        } else {
        }

    }
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        File imageFile = new File(imageUri.getPath());

        Log.d("ImageFilePath", "Image file path: " + imageFile.getAbsolutePath());

        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            MediaScannerConnection.scanFile(this,
                    new String[]{imageUri.getPath()},
                    null,
                    (path, uri) -> {
                        Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestStoragePermission() {
        if (!checkStoragePermission()) {
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, REQUEST_STORAGE_PERMISSION);
        }
    }
}