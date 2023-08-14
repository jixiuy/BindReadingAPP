package com.android.bindreading;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.bindreading.enentbus.DataEvent;
import com.android.bindreading.gson.JsonResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoActivity extends AppCompatActivity {
    private ExecutorService cameraExecutor;
    private static final String TAG = "CameraXApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        ImageButton takePicture = findViewById(R.id.image_capture_button);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        cameraExecutor = Executors.newSingleThreadExecutor();
    }
    private ImageCapture imageCapture = null;
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private void takePhoto() {
        // Get a stable reference of the modifiable image capture use case

        if (imageCapture == null) {
            return;
        }

        // Create time stamped name and MediaStore entry
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults output) {
                        String msg = "Photo capture succeeded: " + output.getSavedUri();
//                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, msg);
                        dealWithImage(output.getSavedUri());
                    }
                }
        );
    }

    private void dealWithImage(Uri savedUri) {
        String path = FileUtils.getPathFromUri(getApplicationContext(), savedUri);
        String url = "https://api.textin.com/ai/service/v1/crop_enhance_image";
        String appId = "ac6d3377747e0712475f1bec56df83c1";
        String secretCode = "6db1d23727f4e4ad1c2c4268636b79eb";

        byte[] imgData = readfile(path); // image data

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");

        // Build URL with query parameters
        HttpUrl httpUrl = HttpUrl.parse(url)
                .newBuilder()
                .addQueryParameter("enhance_mode", "5")
                .addQueryParameter("crop_image","1")
                .addQueryParameter("crop_scene","0")
                .addQueryParameter("correct_direction","1")
                .build();

        // Create the request body with image data
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), imgData);

        // Create the request with headers, URL, and request body
        Request request = new Request.Builder()
                .url(httpUrl)
                .addHeader("x-ti-app-id", appId)
                .addHeader("x-ti-secret-code", secretCode)
                .post(requestBody)
                .build();

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE); // 显示进度条
        // 执行加载任务
        View overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE); // 显示半透明背景
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        overlay.setVisibility(View.GONE); // 隐藏半透明背景
                        progressBar.setVisibility(View.GONE); // 隐藏进度条

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JsonResponse response2 = new Gson().fromJson(response.body().string(), JsonResponse.class);
                    result = response2.getResult().getImageList()[0].getImage();
                    EventBus.getDefault().post(new DataEvent(result));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            overlay.setVisibility(View.GONE); // 隐藏半透明背景
                            progressBar.setVisibility(View.GONE); // 隐藏进度条
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            finish();
                            overridePendingTransition(0, R.anim.slide_out_to_right);
                        }
                    });


                } else {
                    Log.d(TAG, "Request failed: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            overlay.setVisibility(View.GONE); // 隐藏半透明背景
                            progressBar.setVisibility(View.GONE); // 隐藏进度条
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
                }
            }
        });

    }
    String result = null;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new DataEvent(result)); // 发送事件
        // 在需要退出活动的地方调用以下代码
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_right);
    }


    public static byte[] readfile(String path)
    {
        String imgFile = path;
        InputStream in = null;
        byte[] data = null;
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        PreviewView viewFinder = findViewById(R.id.viewFinder);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // Used to bind the lifecycle of cameras to the lifecycle owner
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    // Preview
                    Preview preview = new Preview.Builder()
                            .build();
                    preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                    imageCapture = new ImageCapture.Builder()
                            .build();

                    // Select back camera as a default
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    try {
                        // Unbind use cases before rebinding
                        cameraProvider.unbindAll();

                        // Bind use cases to camera
                        cameraProvider.bindToLifecycle(
                                PhotoActivity.this, cameraSelector, preview, imageCapture);

                    } catch (Exception exc) {
                        Log.e(TAG, "Use case binding failed", exc);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private String[] REQUIRED_PERMISSIONS = new ArrayList<String>() {{
        add(Manifest.permission.CAMERA);
        add(Manifest.permission.RECORD_AUDIO);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }}.toArray(new String[0]);

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}