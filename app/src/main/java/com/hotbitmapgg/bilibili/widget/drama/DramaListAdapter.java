package com.hotbitmapgg.bilibili.widget.drama;

import static com.bytedance.sdk.dp.DPDrama.STATUS_DRAMA_FINISHED;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_CURRENT_DURATION;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_CUSTOM_REPORT_ENABLED;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_FREE_SET;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_HIDE_LEFT_TOP_TIPS;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_INFINITE_SCROLL_ENABLED;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_LOCK_SET;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_MODE;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_UNLOCK_INDEX;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bytedance.sdk.dp.DPDrama;
import com.bytedance.sdk.dp.DPDramaDetailConfig;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPWidgetDramaDetailParams;
import com.bytedance.sdk.dp.IDPWidgetFactory;
import com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection;
import com.hotbitmapgg.bilibili.module.drama.MyDramaApiDetailActivity;
import com.spmystery.drama.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DramaListAdapter extends RecyclerView.Adapter<HomeRecommendedSection.ItemViewHolder>{

    public static final String TAG = "DramaListAdapter";

    private Context mContext;

    private List<? extends DPDrama> datas = new ArrayList<>();

    public DramaListAdapter(Context mContext, List<? extends DPDrama> datas) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HomeRecommendedSection.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_home_recommend_boby, viewGroup,false);
        return new HomeRecommendedSection.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecommendedSection.ItemViewHolder itemViewHolder, int i) {
        DPDrama drama = datas.get(i);
        Glide.with(mContext)
                .load(Uri.parse(drama.coverImage))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(itemViewHolder.mVideoImg);
        itemViewHolder.mVideoTitle.setText(drama.title);
        itemViewHolder.mCardView.setOnClickListener(v -> {
            List<Long> ids = new ArrayList<>();
            ids.add(drama.id);
            Log.i(TAG, "onBindItemViewHolder: " + ids);
            if (DPSdk.isStartSuccess()) {
                int index = 1;
                int unlockIndex = 1;
                int duration = 0;
                int freeSet = -1;
                int lockSet = -1;
                DPSdk.factory().requestDrama(ids, new IDPWidgetFactory.DramaCallback() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(mContext, "请求失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "request failed, code = " + i + ", msg = " + s);
                    }

                    @Override
                    public void onSuccess(List<? extends DPDrama> list, Map<String, Object> map) {
                        if (list == null || list.size() == 0) {
                            return;
                        }
                        DPDrama dramaItem = list.get(0);
                        dramaItem.index = index;

                        //TODO mContext设置是否合理
                        Intent intent = new Intent(mContext, MyDramaApiDetailActivity.class);
                        MyDramaApiDetailActivity.outerDrama = dramaItem;
                        MyDramaApiDetailActivity.enterFrom = DPWidgetDramaDetailParams.DPDramaEnterFrom.DEFAULT;
                        intent.putExtra(KEY_DRAMA_UNLOCK_INDEX, unlockIndex);
                        //封装模式，弹出激励视频、上拉查看选择集数
                        intent.putExtra(KEY_DRAMA_MODE, DPDramaDetailConfig.COMMON_DETAIL);
                        intent.putExtra(KEY_DRAMA_FREE_SET, freeSet);
                        intent.putExtra(KEY_DRAMA_LOCK_SET, lockSet);
                        intent.putExtra(KEY_DRAMA_CURRENT_DURATION, duration);
                        intent.putExtra(KEY_DRAMA_INFINITE_SCROLL_ENABLED, true);
                        intent.putExtra(KEY_DRAMA_CUSTOM_REPORT_ENABLED, false);
                        intent.putExtra(KEY_DRAMA_HIDE_LEFT_TOP_TIPS, false);
                        mContext.startActivity(intent);
                    }
                });
            }
        });

        itemViewHolder.mLiveLayout.setVisibility(View.GONE);
        itemViewHolder.mBangumiLayout.setVisibility(View.GONE);
        itemViewHolder.mVideoLayout.setVisibility(View.VISIBLE);
        //itemViewHolder.mVideoPlayNum.setText(bodyBean.getPlay());
        //根据总集数total，完结状态status，拼接
        String description = (drama.status == STATUS_DRAMA_FINISHED ? "已完结" : "未完结") + "共" + drama.total + "集";
        itemViewHolder.mVideoReviewCount.setText(description);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
