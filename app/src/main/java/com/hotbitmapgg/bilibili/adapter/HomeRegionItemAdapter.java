package com.hotbitmapgg.bilibili.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.bilibili.adapter.helper.AbsRecyclerViewAdapter;
import com.spmystery.episode.R;

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
            R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press,
            R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press, R.drawable.drama_press
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
