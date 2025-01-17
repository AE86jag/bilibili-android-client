package com.spmystery.episode.module.home.discover;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.spmystery.episode.adapter.AllAreasRankAdapter;
import com.spmystery.episode.base.RxLazyFragment;
import com.spmystery.episode.entity.discover.AllareasRankInfo;
import com.spmystery.episode.module.video.VideoDetailsActivity;
import com.spmystery.episode.network.RetrofitHelper;
import com.spmystery.episode.utils.ConstantUtil;
import com.spmystery.episode.utils.ToastUtil;
import com.spmystery.episode.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/11 20:23
 * 100332338@qq.com
 * <p/>
 * 全区排行榜界面
 */
public class AllAreasRankFragment extends RxLazyFragment {
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private String type;
    private AllAreasRankAdapter mAdapter;
    private List<AllareasRankInfo.RankBean.ListBean> allRanks = new ArrayList<>();

    public static AllAreasRankFragment newInstance(String type) {
        AllAreasRankFragment mFragment = new AllAreasRankFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(ConstantUtil.EXTRA_KEY, type);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_all_areas_rank;
    }

    @Override
    public void finishCreateView(Bundle state) {
        type = getArguments().getString(ConstantUtil.EXTRA_KEY);
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getRankAPI()
                .getAllareasRanks(type)
                .compose(bindToLifecycle())
                .map(allareasRankInfo -> allareasRankInfo.getRank().getList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBeans -> {
                    allRanks.addAll(listBeans.subList(0, 20));
                    finishTask();
                }, throwable -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastUtil.ShortToast("加载失败啦,请重新加载~");
                });
    }


    @Override
    protected void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void initRecyclerView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AllAreasRankAdapter(mRecyclerView, allRanks);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, holder) -> VideoDetailsActivity.launch(getActivity(),
                allRanks.get(position).getAid(),
                allRanks.get(position).getPic()));
    }
}
