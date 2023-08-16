package com.android.bindreading;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.bindreading.adapter.TablayoutAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private TablayoutAdapter tablayoutAdapter;

    public ReadFragment() {
        // Required empty public constructor
    }


    public static ReadFragment newInstance(String param1, String param2) {
        ReadFragment fragment = new ReadFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.read_page);
        initData();
        tablayoutAdapter = new TablayoutAdapter(getChildFragmentManager(),fragmentList,titleList);
        viewPager.setAdapter(tablayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initData() {
        fragmentList = new ArrayList<>();
        PageFragment tabFragment0 = PageFragment.newInstance("旅游咨讯","");
        PageFragment tabFragment1 = PageFragment.newInstance("娱乐新闻","");
        PageFragment tabFragment2 = PageFragment.newInstance("社会新闻","");
        PageFragment tabFragment3 = PageFragment.newInstance("动漫资讯","");
        PageFragment tabFragment4 = PageFragment.newInstance("互联网资讯","");
        PageFragment tabFragment5 = PageFragment.newInstance("健康知识","");

//        TabFragment tabFragment0 = TabFragment.newInstance("旅游咨讯");
//        TabFragment tabFragment1 = TabFragment.newInstance("娱乐新闻");
//        TabFragment tabFragment2 = TabFragment.newInstance("社会新闻");
//        TabFragment tabFragment3 = TabFragment.newInstance("动漫资讯");
//        TabFragment tabFragment4 = TabFragment.newInstance("互联网资讯");
//        TabFragment tabFragment5 = TabFragment.newInstance("健康知识");
        fragmentList.add(tabFragment0);
        fragmentList.add(tabFragment1);
        fragmentList.add(tabFragment2);
        fragmentList.add(tabFragment3);
        fragmentList.add(tabFragment4);
        fragmentList.add(tabFragment5);

        titleList = new ArrayList<>();
        titleList.add("旅游资讯");
        titleList.add("娱乐新闻");
        titleList.add("社会新闻");
        titleList.add("动漫资讯");
        titleList.add("互联网资讯");
        titleList.add("健康知识");

    }
}