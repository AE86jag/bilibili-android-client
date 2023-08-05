package com.hotbitmapgg.bilibili.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.bilibili.adapter.helper.AbsRecyclerViewAdapter;
import com.hotbitmapgg.ohmybilibili.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcc on 2016/10/23 10:23
 * 100332338@qq.com
 * <p>
 * 首页分区adapter
 */

public class HomeRegionItemAdapter extends AbsRecyclerViewAdapter {
    private List<String> itemNames = new ArrayList<>();

    private int[] itemIcons = new int[]{
            R.drawable.ic_category_live, R.drawable.ic_category_t13,
            R.drawable.ic_category_t1, R.drawable.ic_category_t3,
            R.drawable.ic_category_t129, R.drawable.ic_category_t4,
            R.drawable.ic_category_t36, R.drawable.ic_category_t160,
            R.drawable.ic_category_t119, R.drawable.ic_category_t155,
            R.drawable.ic_category_t165, R.drawable.ic_category_t5,
            R.drawable.ic_category_t23, R.drawable.ic_category_t11,
            R.drawable.ic_category_game_center, R.drawable.ic_category_t15, R.drawable.ic_category_t17,
            R.drawable.ic_category_t19, R.drawable.ic_category_t20, R.drawable.ic_category_t21, R.drawable.ic_category_t22,
            R.drawable.ic_category_t23, R.drawable.ic_category_t24, R.drawable.ic_category_t25, R.drawable.ic_category_t26,
            R.drawable.ic_category_t27, R.drawable.ic_category_t28, R.drawable.ic_category_t29, R.drawable.ic_category_t30,
            R.drawable.ic_category_t31, R.drawable.ic_category_t32, R.drawable.ic_category_t33, R.drawable.ic_category_t34
    };


    public HomeRegionItemAdapter(RecyclerView recyclerView, List<String> itemNames) {
        super(recyclerView);
        this.itemNames = itemNames;
    }


    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(
                LayoutInflater.from(getContext()).inflate(R.layout.item_home_region, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (position < itemIcons.length) {
                itemViewHolder.mItemIcon.setImageResource(itemIcons[position]);
            } else {
                //实际类别数量大于图标数量，从第一张开始区
                itemViewHolder.mItemIcon.setImageResource(itemIcons[position % itemIcons.length]);
            }
            itemViewHolder.mItemText.setText(itemNames.get(position));
        }
        super.onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return itemNames.size();
    }


    private class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {

        ImageView mItemIcon;
        TextView mItemText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mItemIcon = $(R.id.item_icon);
            mItemText = $(R.id.item_title);
        }
    }
}
