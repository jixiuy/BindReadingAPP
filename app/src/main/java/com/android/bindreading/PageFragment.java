package com.android.bindreading;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private PageBean pageBean;

    private Message message;

    private List<PageBean.ResultDTO.NewslistDTO> list;
    //感兴趣的关键词
    private String keyword = "北京";
    private String url = "https://apis.tianapi.com/travel/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";


    public PageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PageFragment newInstance(String param1, String param2) {
        PageFragment fragment = new PageFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_article);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        message = new Message();
        if (mParam1.equals("旅游资讯")){
            url = "https://apis.tianapi.com/travel/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }else if (mParam1.equals("娱乐新闻")){
            url = "https://apis.tianapi.com/huabian/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 2;
        }else if (mParam1.equals("社会新闻")){
            url = "https://apis.tianapi.com/social/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 2;
        }else if (mParam1.equals("动漫资讯")){
            url = "https://apis.tianapi.com/dongman/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }else if (mParam1.equals("互联网资讯")){
            url = "https://apis.tianapi.com/internet/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }else if (mParam1.equals("健康知识")){
            url = "https://apis.tianapi.com/health/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }

        Log.d("TAG123q", ""+url+"   "+mParam1+"     1");


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
                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 1){
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                PageAdapter pageAdapter = new PageAdapter(pageBean.getResult().getNewslist(), getContext());
                recyclerView.setAdapter(pageAdapter);
                Log.d("TAG123q", ""+pageBean.getResult().getNewslist());
            }else if (message.what == 2){
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                PageTwoAdapter pagetwoAdapter = new PageTwoAdapter(pageBean.getResult().getNewslist(), getContext());
                recyclerView.setAdapter(pagetwoAdapter);
            }
            return false;
        }
    });
}