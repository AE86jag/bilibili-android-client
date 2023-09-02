package com.hotbitmapgg.bilibili.module.drama;

import static com.hotbitmapgg.bilibili.module.home.region.HomeRegionFragment.DRAMA_TYPE_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bytedance.sdk.dp.DPDrama;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.IDPWidgetFactory;
import com.hotbitmapgg.bilibili.base.RxBaseActivity;
import com.hotbitmapgg.bilibili.utils.SnackbarUtil;
import com.hotbitmapgg.bilibili.widget.CustomEmptyView;
import com.hotbitmapgg.bilibili.widget.drama.DramaListAdapter;
import com.spmystery.drama.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class DramaListActivity extends RxBaseActivity {

    public static final String TAG = "DramaListActivity";

    @BindView(R.id.drama_list)
    RecyclerView mRvMain;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_drama_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mRvMain.setLayoutManager(new GridLayoutManager(this, 2));

        Intent intent = getIntent();
        String name = intent.getStringExtra(DRAMA_TYPE_NAME);

        if (DPSdk.isStartSuccess()) {
            DPSdk.factory().requestDramaByCategory(name, 1, Integer.MAX_VALUE, new IDPWidgetFactory.DramaCallback() {
                @Override
                public void onError(int i, String s) {
                    Toast.makeText(getApplicationContext(), "查询失败, 请重试", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "drama category: request failed, code = " + i + ", msg = " + s);
                }

                @Override
                public void onSuccess(List<? extends DPDrama> list, Map<String, Object> map) {
                    if (list == null || list.size() == 0) {
                        showEmpty();
                        return;
                    }
                    DramaListAdapter dramaListAdapter = new DramaListAdapter(getApplicationContext(), list);
                    mRvMain.setAdapter(dramaListAdapter);
                }
            });
        }
    }

    @Override
    public void initToolBar() {

    }

    public void showEmpty() {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRvMain.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("查询结果为空，请换关键词重试~(≧▽≦)~");
        //SnackbarUtil.showMessage(mRvMain, "数据加载失败,请重新加载或者检查网络是否链接");
    }
}
