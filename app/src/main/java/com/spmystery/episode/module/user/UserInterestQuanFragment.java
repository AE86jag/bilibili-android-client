package com.spmystery.episode.module.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.spmystery.episode.adapter.UserInterestQuanAdapter;
import com.spmystery.episode.adapter.helper.EndlessRecyclerOnScrollListener;
import com.spmystery.episode.adapter.helper.HeaderViewRecyclerAdapter;
import com.spmystery.episode.base.RxLazyFragment;
import com.spmystery.episode.entity.user.UserInterestQuanInfo;
import com.spmystery.episode.network.RetrofitHelper;
import com.spmystery.episode.utils.ConstantUtil;
import com.spmystery.episode.widget.CustomEmptyView;
import com.spmystery.episode.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 2016/10/12 18:17
 * 100332338@qq.com
 * <p>
 * 用户详情界面的兴趣圈
 */

public class UserInterestQuanFragment extends RxLazyFragment {
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    private int mid;
    private int pageNum = 1;
    private int pageSize = 10;
    private View loadMoreView;
    private UserInterestQuanAdapter mAdapter;
    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;
    private List<UserInterestQuanInfo.DataBean.ResultBean> userInterestQuans = new ArrayList<>();

    public static UserInterestQuanFragment newInstance(int mid, UserInterestQuanInfo userInterestQuanInfo) {
        UserInterestQuanFragment mFragment = new UserInterestQuanFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.EXTRA_MID, mid);
        bundle.putParcelable(ConstantUtil.EXTRA_DATA, userInterestQuanInfo);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_user_interest_quan;
    }


    @Override
    public void finishCreateView(Bundle state) {
        mid = getArguments().getInt(ConstantUtil.EXTRA_MID);
        UserInterestQuanInfo userInterestQuanInfo = getArguments().getParcelable(ConstantUtil.EXTRA_DATA);
        if (userInterestQuanInfo != null) {
            userInterestQuans.addAll(userInterestQuanInfo.getData().getResult());
        }
        initRecyclerView();
    }


    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UserInterestQuanAdapter(mRecyclerView, userInterestQuans);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        createLoadMoreView();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int i) {
                pageNum++;
                loadData();
                loadMoreView.setVisibility(View.VISIBLE);
            }
        });
        if (userInterestQuans.isEmpty()) {
            initEmptyLayout();
        }
    }


    @Override
    protected void loadData() {
        RetrofitHelper.getIm9API()
                .getUserInterestQuanData(mid, pageNum, pageSize)
                .compose(bindToLifecycle())
                .map(userInterestQuanInfo -> userInterestQuanInfo.getData().getResult())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(resultBeans -> {
                    if (resultBeans.size() < pageSize) {
                        loadMoreView.setVisibility(View.GONE);
                        mHeaderViewRecyclerAdapter.removeFootView();
                    }
                })
                .subscribe(resultBeans -> {
                    userInterestQuans.addAll(resultBeans);
                    finishTask();
                }, throwable -> loadMoreView.setVisibility(View.GONE));
    }


    @Override
    protected void finishTask() {
        loadMoreView.setVisibility(View.GONE);
        if (pageNum * pageSize - pageSize - 1 > 0) {
            mAdapter.notifyItemRangeChanged(pageNum * pageSize - pageSize - 1, pageSize);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void createLoadMoreView() {
        loadMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_load_more, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addFooterView(loadMoreView);
        loadMoreView.setVisibility(View.GONE);
    }


    private void initEmptyLayout() {
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_space_no_data);
        mCustomEmptyView.setEmptyText("ㄟ( ▔, ▔ )ㄏ 再怎么找也没有啦");
    }
}
