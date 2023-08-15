package com.android.bindreading;

import static com.theokanning.openai.service.OpenAiService.defaultClient;
import static com.theokanning.openai.service.OpenAiService.defaultObjectMapper;
import static com.theokanning.openai.service.OpenAiService.defaultRetrofit;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.bindreading.enentbus.DataEvent;
import com.android.bindreading.okhttp.HttpLoggingInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.noties.markwon.Markwon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view = null;
    TextView notify2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_photo, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        notify2 = view.findViewById(R.id.notify2);
        Resources resources = getResources();
        Drawable camara = resources.getDrawable(R.drawable.baseline_photo_camera_24);
        fab.setImageDrawable(camara);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PhotoActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, 0);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataEvent(DataEvent event) {
        // 在这里处理返回的数据
        String data = event.getData();
//        ImageView imageView = view.findViewById(R.id.image_view);
        if (!data.isEmpty()) {
            byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);
            overlay = view.findViewById(R.id.overlay);
            progressBar = view.findViewById(R.id.progress_bar);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE); // 显示进度条
                    // 执行加载任务

                    overlay.setVisibility(View.VISIBLE); // 显示半透明背景
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }
            });
            post(decodedBytes);
//            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//            imageView.setImageBitmap(decodedBitmap);

        } else {
            // 处理Base64图片为空的情况
        }
    }
    View overlay = null;
    ProgressBar progressBar = null;
    private void post(byte[] imgData) {
        String url = "https://api.textin.com/ai/service/v2/recognize";
        String appId = "ac6d3377747e0712475f1bec56df83c1";
        String secretCode = "6db1d23727f4e4ad1c2c4268636b79eb";
        String imagePath = "example.jpg"; // 图片路径

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody requestBody = RequestBody.create(mediaType, imgData);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-ti-app-id", appId)
                .addHeader("x-ti-secret-code", secretCode)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    String jsonResponse = result; // 将实际的 JSON 响应数据赋值给 jsonResponse

                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    String paragraph = null;
                    if (jsonObject.has("result")) {
                        JsonObject resultObject = jsonObject.getAsJsonObject("result");

                        if (resultObject.has("lines")) {
                            JsonArray linesArray = resultObject.getAsJsonArray("lines");

                            StringBuilder paragraphBuilder = new StringBuilder();

                            for (JsonElement lineElement : linesArray) {
                                JsonObject lineObject = lineElement.getAsJsonObject();

                                if (lineObject.has("text")) {
                                    String text = lineObject.get("text").getAsString();
                                    paragraphBuilder.append(text).append(" "); // 拼接文字和空格
                                }
                            }

                            paragraph = paragraphBuilder.toString();
                            System.out.println("Paragraph: " + paragraph);
                        }
                    }
                    Log.d("HEEELO", "onResponse: "+paragraph);
                    gptDealWith(paragraph);
                } else {
                    System.out.println("Request failed with code: " + response.code());
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void gptDealWith(String str){
//        String token = "sk-f2DdTuGI3Wy84oejHW2DdJ82KCCxbQJXvZQvYG5XP9rknT6b";
//        int timeout = 10; // 请求超时时间（秒）
//
//        OkHttpClient client = defaultClient(token, Duration.ofDays(timeout))
//                .newBuilder()
//                .addInterceptor(new HttpLoggingInterceptor())
//                .build();
//
//        ObjectMapper mapper = defaultObjectMapper(); // 这是您自定义的 ObjectMapper
//
//        Retrofit retrofit = defaultRetrofit(client, mapper);
//
//        Retrofit customBaseUrlRetrofit = retrofit.newBuilder()
//                .baseUrl("https://api.f2gpt.com/v1/chat/completions/") // 替换为您的自定义 baseURL
//                .build();
//
//        OpenAiApi api = customBaseUrlRetrofit.create(OpenAiApi.class);
//        OpenAiService service = new OpenAiService(api);
//        CompletionRequest completionRequest = CompletionRequest.builder()
//                .prompt("将以下段落进行整理"+str)
//                .model("curie")
//                .echo(true)
//                .build();
//        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        String apiUrl = "https://api.f2gpt.com/v1/chat/completions";

        // Request payload
        String payload = "{\"model\": \"gpt-3.5-turbo\",\"messages\": [{\"role\": \"user\", \"content\": \"把下面的话整理一下"+str+"返回屏幕阅读器可以连续阅读的文本，不要换行\"}],\"temperature\": 0.7}";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload);

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer sk-f2DdTuGI3Wy84oejHW2DdJ82KCCxbQJXvZQvYG5XP9rknT6b")
                .post(requestBody)
                .build();
        TextView notify = view.findViewById(R.id.notify);

         client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        overlay.setVisibility(View.GONE); // 隐藏半透明背景
                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                        notify2.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

                Log.d("JXY", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    String responseBody = response.body().string();
                    String jsonResponse = responseBody; // Replace with your JSON response

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

                    if (jsonObject.has("choices")) {
                        JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
                        StringBuilder contentBuilder = new StringBuilder();

                        for (JsonElement choiceElement : choicesArray) {
                            JsonObject choiceObject = choiceElement.getAsJsonObject();
                            JsonObject messageObject = choiceObject.getAsJsonObject("message");
                            if (messageObject.has("content")) {
                                String content = messageObject.get("content").getAsString();
                                contentBuilder.append(content); // 拼接消息内容
                            }
                        }

                        final String allContent = contentBuilder.toString();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String allContent2 = allContent.replaceAll("\n","");
                                overlay.setVisibility(View.GONE); // 隐藏半透明背景
                                progressBar.setVisibility(View.GONE); // 隐藏进度条

                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                notify.setText(allContent2);
                                Markwon markwon = Markwon.create(getActivity());
                                markwon.setMarkdown(notify, allContent2);
                            }
                        });

                    }
                    System.out.println("Response Body: " + responseBody);
                    Log.d("JXY", "onResponse: "+ responseBody);
                } else {
                    System.out.println("Request failed with code: " + response.code());
                    Log.d("JXY", "onResponse: "+ response.code());
                }
            }
        });

    }

}