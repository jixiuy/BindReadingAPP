package com.android.bindreading.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bindreading.AgentWebActivity;
import com.android.bindreading.R;
import com.android.bindreading.bean.PageBean;
import com.bumptech.glide.Glide;


import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {

    private List<PageBean.ResultDTO.NewslistDTO> mPageBeanList;

    private Context mContext;

    public PageAdapter(List<PageBean.ResultDTO.NewslistDTO> mPageBeanList, Context mContext) {
        this.mPageBeanList = mPageBeanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PageBean.ResultDTO.NewslistDTO pageBean = mPageBeanList.get(position);
        //写逻辑
        holder.description.setText(pageBean.getDescription());
        holder.title.setText(pageBean.getTitle());
        holder.time.setText(pageBean.getCtime());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(pageBean.getUrl());
                Intent intent = new Intent(mContext, AgentWebActivity.class);
                intent.putExtra("Uri",uri.toString());
                mContext.startActivity(intent);
            }
        });
        Glide.with(mContext).load(pageBean.getPicUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPageBeanList == null ? 0 : mPageBeanList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;
        TextView description;
        TextView time;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativelayout_page);
            imageView = itemView.findViewById(R.id.imageview_page);
            title = itemView.findViewById(R.id.textview_title);
            description = itemView.findViewById(R.id.textview_description);
            time = itemView.findViewById(R.id.page_time);
        }
    }
}
