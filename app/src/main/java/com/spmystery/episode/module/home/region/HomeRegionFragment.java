package com.spmystery.episode.module.home.region;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.IDPWidgetFactory;
import com.spmystery.episode.adapter.HomeRegionItemAdapter;
import com.spmystery.episode.base.RxLazyFragment;
import com.spmystery.episode.module.drama.DramaListActivity;
import com.spmystery.episode.utils.SnackbarUtil;
import com.spmystery.episode.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * 首页顶部更多TAB页
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
        Log.i(TAG, "finishCreateView");
        loadData();
    }

    @Override
    protected void loadData() {
        if (DPSdk.isStartSuccess()) {
            Log.i(TAG, "start load data");
            DPSdk.factory().requestDramaCategoryList(new IDPWidgetFactory.DramaCategoryCallback() {
                @Override
                public void onError(int i, String s) {
                    Log.d(TAG, "request failed, code = " + i +", msg = " + s);
                    SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
                }

                @Override
                public void onSuccess(List<String> list) {
                    Log.d(TAG, "get data callback success, request dramas size is:" + list.size());
                    regionTypes.clear();
                    regionTypes.addAll(list);
                    //异步回调，在获取成功后初始化adapt
                    initRecyclerView();
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
