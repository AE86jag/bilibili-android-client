package com.spmystery.episode.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.spmystery.episode.adapter.helper.AbsRecyclerViewAdapter;
import com.spmystery.episode.entity.user.UserInterestQuanInfo;
import com.spmystery.episode.R;

import java.util.List;

/**
 * Created by hcc on 2016/10/12 20:02
 * 100332338@qq.com
 * <p>
 * 用户详情兴趣圈adapter
 */

public class UserInterestQuanAdapter extends AbsRecyclerViewAdapter {
    private List<UserInterestQuanInfo.DataBean.ResultBean> userInterestQuans;

    public UserInterestQuanAdapter(RecyclerView recyclerView, List<UserInterestQuanInfo.DataBean.ResultBean> userInterestQuans) {
        super(recyclerView);
        this.userInterestQuans = userInterestQuans;
    }


    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_interest_quan, parent, false));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            UserInterestQuanInfo.DataBean.ResultBean resultBean = userInterestQuans.get(position);

            Glide.with(getContext())
                    .load(resultBean.getThumb())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.bili_default_image_tv)
                    .into(itemViewHolder.mImage);

            itemViewHolder.mTitle.setText(resultBean.getName());
            itemViewHolder.mDesc.setText(resultBean.getDesc());
            itemViewHolder.mPostNickName.setText(resultBean.getPost_nickname() + ":");
            itemViewHolder.mPostCount.setText(String.valueOf(resultBean.getPost_count()));
            itemViewHolder.mMemberNickName.setText(resultBean.getMember_nickname() + ":");
            itemViewHolder.mMemberCount.setText(String.valueOf(resultBean.getMember_count()));
        }
        super.onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return userInterestQuans.size();
    }


    private class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {

        ImageView mImage;
        TextView mTitle;
        TextView mDesc;
        TextView mPostNickName;
        TextView mPostCount;
        TextView mMemberNickName;
        TextView mMemberCount;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImage = $(R.id.item_img);
            mTitle = $(R.id.item_title);
            mDesc = $(R.id.item_desc);
            mPostNickName = $(R.id.item_post_nickname);
            mPostCount = $(R.id.item_post_count);
            mMemberNickName = $(R.id.item_member_nickname);
            mMemberCount = $(R.id.item_member_count);
        }
    }
}
