package com.android.bindreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, perform network operations

        }
        bind();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform network operations

            } else {
                // Permission denied, handle accordingly
            }
        }
    }


    private void bind() {
        ViewPager2 viewPager2 = findViewById(R.id.viewpager2bottom);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        ViewPager2Adapter viewPager2Adapter =
                new ViewPager2Adapter(this,initFragmentList());
        viewPager2.setAdapter(viewPager2Adapter);
        //重点 设置 bottomNavigationView 的item 的点击事件 设置viewPager2的联动
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId){
                    case R.id.action_search:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.action_settings:
                        viewPager2.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

        //重点 实现滑动的时候 联动 bottomNavigationView的selectedItem
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.action_search);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.action_settings);
                        break;
                }
            }
        });

    }

    private List<Fragment> initFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ReadFragment());
        fragments.add(new PhotoFragment());
        return fragments;
    }
}