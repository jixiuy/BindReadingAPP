package com.android.bindreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.bindreading.adapter.PageAdapter;
import com.android.bindreading.adapter.PageTwoAdapter;
import com.android.bindreading.bean.PageBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PageBean pageBean;

    private Message message;
    private TextView textView;

    private List<PageBean.ResultDTO.NewslistDTO> list;
    private String keyword = "北京";

    private String url = "https://apis.tianapi.com/travel/index?key=a91afee29bb010a296c554e1647ea058&num=10&rand=1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        textView = findViewById(R.id.text1);
        recyclerView = findViewById(R.id.recyclerView_search);
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");

        message = new Message();

        url = "https://apis.tianapi.com/travel/index?key=a91afee29bb010a296c554e1647ea058&word="+keyword;
        message.what = 1;


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                Gson gson = new Gson();
                pageBean = gson.fromJson(data,PageBean.class);
                if (pageBean.getCode().equals(200)){
                    list = pageBean.getResult().getNewslist();
                    textView.setVisibility(View.INVISIBLE);
                    handler.sendMessage(message);
                }

            }
        });


    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 1){
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                PageAdapter pageAdapter = new PageAdapter(list, SearchActivity.this);
                recyclerView.setAdapter(pageAdapter);
            }
            return false;
        }
    });
}