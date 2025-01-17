package com.spmystery.episode.module.home;

import static com.spmystery.episode.module.home.region.HomeRegionFragment.DRAMA_TYPE_NAME;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.flyco.tablayout.SlidingTabLayout;
import com.spmystery.episode.adapter.pager.HomePagerAdapter;
import com.spmystery.episode.base.RxLazyFragment;
import com.spmystery.episode.module.common.MainActivity;
import com.spmystery.episode.module.drama.DramaListActivity;
import com.spmystery.episode.module.home.index.BottomTabLayoutActivity;
import com.spmystery.episode.widget.CircleImageView;
import com.spmystery.episode.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * 首页模块主界面
 */
public class HomePageFragment extends RxLazyFragment {
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTab;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.search_view)
    MaterialSearchView mSearchView;
    @BindView(R.id.toolbar_user_avatar)
    CircleImageView mCircleImageView;

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        /* setHasOptionsMenu(true) 可告知系统您的 fragment 想要接收与菜单相关的回调。
        当发生与菜单相关的事件（例如点击）时，系统会先对 activity 调用事件处理方法，
        然后再对 fragment 调用该方法。*/
        setHasOptionsMenu(true);
        initToolBar();
        initSearchView();
        initViewPager();
    }

    private void initToolBar() {
        mToolbar.setTitle("");
        FragmentActivity activity = getActivity();
        //现在HomePageFragment嵌入到BottomTabLayoutActivity，以前是在MainActivity里面
        if (activity instanceof MainActivity) {
            ((MainActivity)activity).setSupportActionBar(mToolbar);
        } else if (activity instanceof BottomTabLayoutActivity) {
            ((BottomTabLayoutActivity)activity).setSupportActionBar(mToolbar);
        }

        mCircleImageView.setImageResource(R.drawable.ic_hotbitmapgg_avatar);
    }

    private void initSearchView() {
        //初始化SearchBar
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        mSearchView.setEllipsize(true);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, DramaListActivity.class);
                intent.putExtra(DRAMA_TYPE_NAME, query);
                //不加会报这个错 Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //TotalStationSearchActivity.launch(getActivity(), query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initViewPager() {
        HomePagerAdapter mHomeAdapter = new HomePagerAdapter(getChildFragmentManager(), getApplicationContext());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mHomeAdapter);
        mSlidingTab.setViewPager(mViewPager);
        //首页顶部TAB初始位置
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        // 设置SearchViewItemMenu
        MenuItem item = menu.findItem(R.id.id_action_search);
        mSearchView.setMenuItem(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*switch (id) {
            case R.id.id_action_game:
                //游戏中心
                startActivity(new Intent(getActivity(), GameCentreActivity.class));
                break;
            case R.id.id_action_download:
                //离线缓存
                startActivity(new Intent(getActivity(), OffLineDownloadActivity.class));
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.navigation_layout)
    void toggleDrawer() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).toggleDrawer();
        }
        if (activity instanceof BottomTabLayoutActivity) {
            ((BottomTabLayoutActivity)activity).toggleDrawer();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public boolean isOpenSearchView() {
        return mSearchView.isSearchOpen();
    }


    public void closeSearchView() {
        mSearchView.closeSearch();
    }
}
