package com.hotbitmapgg.bilibili.module.home.region;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bytedance.sdk.dp.DPDramaDetailConfig;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.IDPWidgetFactory;
import com.google.gson.Gson;
import com.hotbitmapgg.bilibili.adapter.HomeRegionItemAdapter;
import com.hotbitmapgg.bilibili.base.RxLazyFragment;
import com.hotbitmapgg.bilibili.entity.region.RegionTypesInfo;
import com.hotbitmapgg.bilibili.module.drama.DramaListActivity;
import com.hotbitmapgg.bilibili.module.drama.MyDramaApiDetailActivity;
import com.hotbitmapgg.bilibili.module.entry.GameCentreActivity;
import com.hotbitmapgg.bilibili.utils.SnackbarUtil;
import com.hotbitmapgg.ohmybilibili.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * 首页分区界面
 */
public class HomeRegionFragment extends RxLazyFragment {
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private List<String> regionTypes = new ArrayList<>();

    public static final String TAG = "HomeRegionFragment";

    public static final String DRAMA_TYPE_NAME = "drama_type_name";

    public static HomeRegionFragment newInstance() {
        return new HomeRegionFragment();
    }

    @Override
    public
    @LayoutRes
    int getLayoutResId() {
        return R.layout.fragment_home_region;
    }


    @Override
    public void finishCreateView(Bundle state) {
        loadData();
        initRecyclerView();
    }

    @Override
    protected void loadData() {
        if (DPSdk.isStartSuccess()) {
            DPSdk.factory().requestDramaCategoryList(new IDPWidgetFactory.DramaCategoryCallback() {
                @Override
                public void onError(int i, String s) {
                    Log.d(TAG, "request failed, code = " + i +", msg = " + s);
                    SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
                }

                @Override
                public void onSuccess(List<String> list) {
                    regionTypes.addAll(list);
                }
            });
        }
    }

    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        HomeRegionItemAdapter mAdapter = new HomeRegionItemAdapter(mRecyclerView, regionTypes);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, holder) -> {
            String typeName = regionTypes.get(position);
            Context context = getApplicationContext();
            Intent intent = new Intent(context, DramaListActivity.class);
            intent.putExtra(DRAMA_TYPE_NAME, typeName);
            //不加会报这个错 Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
}
