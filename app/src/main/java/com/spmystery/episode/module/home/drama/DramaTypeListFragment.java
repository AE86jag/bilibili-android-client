package com.spmystery.episode.module.home.drama;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bytedance.sdk.dp.DPDrama;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.IDPWidgetFactory;
import com.spmystery.episode.adapter.helper.EndlessRecyclerOnScrollListener;
import com.spmystery.episode.adapter.section.HomeRecommendedSection;
import com.spmystery.episode.base.RxLazyFragment;
import com.spmystery.episode.utils.SnackbarUtil;
import com.spmystery.episode.widget.CustomEmptyView;
import com.spmystery.episode.widget.sectioned.SectionedRecyclerViewAdapter;
import com.spmystery.episode.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by hcc on 16/8/4 11:58
 * 100332338@qq.com
 * <p/>
 * 首页短剧具体类型列表
 */
public class DramaTypeListFragment extends RxLazyFragment {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    private boolean mIsRefreshing = false;

    private int pageNum = 1;
    private final int pageSize = 10;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    //private View loadMoreView;

    private SectionedRecyclerViewAdapter mSectionedAdapter;
    //private List<BannerEntity> banners = new ArrayList<>();
    private List<DPDrama> results = new ArrayList<>();
    //private List<RecommendBannerInfo.DataBean> recommendBanners = new ArrayList<>();

    private static final String TAG = "DramaTypeListFragment";

    private String typeName;

    public DramaTypeListFragment() {
    }

    public static DramaTypeListFragment newInstance(String typeName) {
        DramaTypeListFragment fragment = new DramaTypeListFragment();
        fragment.typeName = typeName;
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_recommended;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }

    private void createLoadMoreView() {
        //loadMoreView = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.layout_load_more, mRecyclerView, false);
        //mSectionedAdapter.addFooterView(loadMoreView);
        //loadMoreView.setVisibility(View.GONE);
    }

    @Override
    protected void initRecyclerView() {
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSectionedAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        //顶部栏占两列，相当于就是居中显示，总共才两列
                        return 2;
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER:
                        //底部栏占两列，相当于就是居中显示，总共才两列
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSectionedAdapter);


        createLoadMoreView();
        setRecycleNoScroll();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.i("hhhh", "onLoadMore: ");
                pageNum++;
                loadData();
                //loadMoreView.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            pageNum = 1;
            clearData();
            loadData();
        });
    }

    @Override
    protected void loadData() {
        if (DPSdk.isStartSuccess()) {
            DPSdk.factory().requestDramaByCategory(typeName, pageNum, pageSize, new IDPWidgetFactory.DramaCallback() {
                @Override
                public void onError(int i, String s) {
                    initEmptyView();
                    Log.d(TAG, "request failed, code = $code, msg = $msg");
                }

                @Override
                public void onSuccess(List<? extends DPDrama> list, Map<String, Object> map) {
                    Log.i(TAG, "request success, drama size = " + (list == null ? 0 : list.size()));
                    if (list != null && list.size() > 0) {
                        results.addAll(list);
                    }
                    finishTask();
                }
            });
        } else {
            initEmptyView();
            Log.e(TAG, "sdk is not init");
        }
    }


    /**
     * 设置轮播banners
     */
    /*private void convertBanner() {
        Observable.from(recommendBanners)
                .compose(bindToLifecycle())
                .forEach(dataBean -> banners.add(new BannerEntity(dataBean.getValue(),
                        dataBean.getTitle(), dataBean.getImage())));
    }*/


    @Override
    protected void finishTask() {

        mSwipeRefreshLayout.setRefreshing(false);

        mIsRefreshing = false;
        hideEmptyView();
        //loadMoreView.setVisibility(View.GONE);
        //convertBanner();
        //mSectionedAdapter.addSection(new HomeRecommendBannerSection(banners));

        mSectionedAdapter.addSection(new HomeRecommendedSection(
                getActivity(), null, "type",0, results));

        /*int size = results.size();
        for (int i = 0; i < size; i++) {
            String type = results.get(i).getType();
            if (!TextUtils.isEmpty(type)) {
                switch (type) {
                    case ConstantUtil.TYPE_TOPIC:
                        //话题
                        mSectionedAdapter.addSection(new HomeRecommendTopicSection(getActivity(),
                                results.get(i).getBody().get(0).getCover(),
                                results.get(i).getBody().get(0).getTitle(),
                                results.get(i).getBody().get(0).getParam()));
                        break;
                    case ConstantUtil.TYPE_ACTIVITY_CENTER:
                        //活动中心
                        mSectionedAdapter.addSection(new HomeRecommendActivityCenterSection(
                                getActivity(),
                                results.get(i).getBody()));
                        break;
                    default:
                        //修改后都走这里
                        mSectionedAdapter.addSection(new HomeRecommendedSection(
                                getActivity(),
                                results.get(i).getHead().getTitle(),
                                results.get(i).getType(),
                                results.get(i).getHead().getCount(),
                                results.get(i).getBody()));
                        break;
                }
            }
            String style = results.get(i).getHead().getStyle();
            if (style.equals(ConstantUtil.STYLE_PIC)) {
                mSectionedAdapter.addSection(new HomeRecommendPicSection(getActivity(),
                        results.get(i).getBody().get(0).getCover(),
                        results.get(i).getBody().get(0).getParam()));
            }
        }*/

        if (pageNum * pageSize - pageSize - 1 > 0) {
            mSectionedAdapter.notifyItemRangeChanged(pageNum * pageSize - pageSize - 1, pageSize);
        } else {
            mSectionedAdapter.notifyDataSetChanged();
        }
    }


    public void initEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void clearData() {
        /*banners.clear();
        recommendBanners.clear();*/
        results.clear();
        mIsRefreshing = true;
        mSectionedAdapter.removeAllSections();
    }


    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}
