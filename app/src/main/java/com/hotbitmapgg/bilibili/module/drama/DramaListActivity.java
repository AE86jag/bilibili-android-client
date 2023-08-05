package com.hotbitmapgg.bilibili.module.drama;

import static com.hotbitmapgg.bilibili.module.home.region.HomeRegionFragment.DRAMA_TYPE_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.dp.DPDrama;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.IDPWidgetFactory;
import com.hotbitmapgg.bilibili.base.RxBaseActivity;
import com.hotbitmapgg.bilibili.widget.drama.DramaListAdapter;
import com.hotbitmapgg.ohmybilibili.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class DramaListActivity extends RxBaseActivity {

    public static final String TAG = "DramaListActivity";

    @BindView(R.id.drama_list)
    RecyclerView mRvMain;

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
                    DramaListAdapter dramaListAdapter = new DramaListAdapter(getApplicationContext(), list);
                    mRvMain.setAdapter(dramaListAdapter);
                }
            });
        }
    }

    @Override
    public void initToolBar() {

    }
}
