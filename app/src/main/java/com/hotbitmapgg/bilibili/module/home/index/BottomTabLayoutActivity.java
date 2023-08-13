package com.hotbitmapgg.bilibili.module.home.index;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.dp.DPDrama;
import com.bytedance.sdk.dp.DPDramaDetailConfig;
import com.bytedance.sdk.dp.DPPageState;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPSecondaryPageType;
import com.bytedance.sdk.dp.DPWidgetDrawParams;
import com.bytedance.sdk.dp.IDPAdListener;
import com.bytedance.sdk.dp.IDPDramaListener;
import com.bytedance.sdk.dp.IDPDrawListener;
import com.bytedance.sdk.dp.IDPQuizHandler;
import com.bytedance.sdk.dp.IDPWidget;
import com.hotbitmapgg.bilibili.module.common.MainActivity;
import com.hotbitmapgg.bilibili.module.entry.AttentionPeopleFragment;
import com.hotbitmapgg.bilibili.module.entry.ConsumeHistoryFragment;
import com.hotbitmapgg.bilibili.module.entry.HistoryFragment;
import com.hotbitmapgg.bilibili.module.entry.IFavoritesFragment;
import com.hotbitmapgg.bilibili.module.entry.OffLineDownloadActivity;
import com.hotbitmapgg.bilibili.module.entry.SettingFragment;
import com.hotbitmapgg.bilibili.module.entry.VipActivity;
import com.hotbitmapgg.bilibili.module.home.HomePageFragment;
import com.hotbitmapgg.bilibili.module.home.attention.HomeAttentionFragment;
import com.hotbitmapgg.bilibili.utils.ConstantUtil;
import com.hotbitmapgg.bilibili.utils.PreferenceUtil;
import com.hotbitmapgg.bilibili.utils.SnackbarUtil;
import com.hotbitmapgg.bilibili.utils.ToastUtil;
import com.hotbitmapgg.bilibili.widget.CircleImageView;
import com.hotbitmapgg.ohmybilibili.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class BottomTabLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout mTabLayout;
    private Fragment[] mFragmensts;
    private IDPWidget idpWidget;

    public static final String TAG = "BottomTabLayoutActivity";
    private static final int FREE_SET = -1;
    private static final int LOCK_SET = -1;
    private int index;
    private int currentTabIndex;
    private DrawerLayout mDrawerLayout;

    private NavigationView mNavigationView;

    private static String[] tabNames = new String[]{
            "短剧", "推荐"
    };

    private static int[] tabIconsPress = new int[]{
            R.drawable.drama_press, R.drawable.recommend_press
    };

    private static int[] tabIcons = new int[]{
            R.drawable.drama, R.drawable.recommend
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_bottom_navigation);
        //请求短剧的
        if (!DPSdk.isStartSuccess()) {
            Toast.makeText(getApplicationContext(), "SDK未初始化, 请重试", Toast.LENGTH_SHORT).show();
        }

        DPDramaDetailConfig dramaDetailConfig = DPDramaDetailConfig.obtain(DPDramaDetailConfig.COMMON_DETAIL)
                .freeSet(FREE_SET)
                .lockSet(LOCK_SET)
                .hideMore(false)
                .hideLeftTopTips(false, null)
                .listener(dramaListener) // 短剧详情页视频播放回调
                .adListener(dramaAdListener);

        idpWidget = DPSdk.factory().createDraw(DPWidgetDrawParams.obtain()
                        .adOffset(0) //单位 dp，为 0 时可以不设置
                        .drawContentType(DPWidgetDrawParams.DRAW_CONTENT_TYPE_ONLY_DRAMA)
                        .drawChannelType(DPWidgetDrawParams.DRAW_CHANNEL_TYPE_RECOMMEND)
                        .hideClose(true, null)
                        .hideChannelName(true)
                        .dramaDetailConfig(dramaDetailConfig)
                        .listener(drawListener) // 混排流内视频监听
                        .adListener(drawAdListener) // 混排流内广告监听
                );

        mFragmensts = new Fragment[] {
                HomePageFragment.newInstance(), idpWidget.getFragment()
        };

        initView();
        initNavigationView();

    }

    private void initNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        CircleImageView mUserAvatarView = (CircleImageView) headerView.findViewById(R.id.user_avatar_view);
        TextView mUserName = (TextView) headerView.findViewById(R.id.user_name);
//        TextView mUserSign = (TextView) headerView.findViewById(R.id.user_other_info);
        ImageView mSwitchMode = (ImageView) headerView.findViewById(R.id.iv_head_switch_mode);
        //设置头像
        mUserAvatarView.setImageResource(R.drawable.ic_hotbitmapgg_avatar);
        //设置用户名 签名
        mUserName.setText(getResources().getText(R.string.hotbitmapgg));
        //mUserSign.setText(getResources().getText(R.string.about_user_head_layout));
        //设置日夜间模式切换
        mSwitchMode.setOnClickListener(v -> switchNightMode());
        boolean flag = PreferenceUtil.getBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        if (flag) {
            mSwitchMode.setImageResource(R.drawable.ic_switch_daily);
        } else {
            mSwitchMode.setImageResource(R.drawable.ic_switch_night);
        }
    }

    private void switchNightMode() {
        boolean isNight = PreferenceUtil.getBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        if (isNight) {
            // 日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            PreferenceUtil.putBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        } else {
            // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            PreferenceUtil.putBoolean(ConstantUtil.SWITCH_MODE_KEY, true);
        }
        recreate();
    }

    /**
     * DrawerLayout侧滑菜单开关
     */
    public void toggleDrawer() {
        if (mDrawerLayout == null) {
            return;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }


    private void initView() {
        mTabLayout = findViewById(R.id.bottom_tab_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabItemSelected(tab.getPosition());

                //改变Tab 状态
                for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                    //View view = mTabLayout.getTabAt(i).getCustomView();


                    TabLayout.Tab tabAt = mTabLayout.getTabAt(i);
                    if (tabAt == null) {
                        break;
                    }
                    View view = tabAt.getCustomView();

                    ImageView icon = view.findViewById(R.id.tab_content_image);
                    TextView text = view.findViewById(R.id.tab_content_text);

                    if (i == tab.getPosition()) {
                        icon.setImageResource(tabIconsPress[i]);
                        text.setTextColor(getResources().getColor(android.R.color.black));
                    } else {
                        icon.setImageResource(tabIcons[i]);
                        text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for (int i = 0; i < tabNames.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView(this, i)));
        }
        /*mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.ic_category_t4)).setText("推荐"));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.ic_category_t36)).setText("短剧"));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.ic_category_t160)).setText("我的"));*/

    }

    public static View getTabView(Context context, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.view_bottom_navigation_icon_text,null);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
        tabIcon.setImageResource(tabIcons[position]);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
        tabText.setText(tabNames[position]);
        return view;
    }

    private void onTabItemSelected(int position){
        if (position > mFragmensts.length) {
            Log.e(TAG, "onTabItemSelected: position is invalid, position is:" + position + "fragment length is:" + mFragmensts.length);
            return;
        }
        Fragment fragment = mFragmensts[position];
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_container, fragment).commit();
        }
    }
    private final IDPAdListener dramaAdListener = new IDPAdListener() {
        @Override
        public void onDPAdRequest(Map<String, Object> map) {
            super.onDPAdRequest(map);
        }

        @Override
        public void onDPAdRequestSuccess(Map<String, Object> map) {
            super.onDPAdRequestSuccess(map);
        }

        @Override
        public void onDPAdRequestFail(int i, String s, Map<String, Object> map) {
            super.onDPAdRequestFail(i, s, map);
        }

        @Override
        public void onDPAdFillFail(Map<String, Object> map) {
            super.onDPAdFillFail(map);
        }

        @Override
        public void onDPAdShow(Map<String, Object> map) {
            super.onDPAdShow(map);
        }

        @Override
        public void onDPAdPlayStart(Map<String, Object> map) {
            super.onDPAdPlayStart(map);
        }

        @Override
        public void onDPAdPlayPause(Map<String, Object> map) {
            super.onDPAdPlayPause(map);
        }

        @Override
        public void onDPAdPlayContinue(Map<String, Object> map) {
            super.onDPAdPlayContinue(map);
        }

        @Override
        public void onDPAdPlayComplete(Map<String, Object> map) {
            super.onDPAdPlayComplete(map);
        }

        @Override
        public void onDPAdClicked(Map<String, Object> map) {
            super.onDPAdClicked(map);
        }

        @Override
        public void onRewardVerify(Map<String, Object> map) {
            super.onRewardVerify(map);
        }

        @Override
        public void onSkippedVideo(Map<String, Object> map) {
            super.onSkippedVideo(map);
        }
    };

    private final IDPDramaListener dramaListener = new IDPDramaListener() {
        @Override
        public void onDPSeekTo(int i, long l) {
            super.onDPSeekTo(i, l);
        }

        @Override
        public void onDPPageChange(int i, Map<String, Object> map) {
            super.onDPPageChange(i, map);
        }

        @Override
        public void onDPVideoPlay(Map<String, Object> map) {
            super.onDPVideoPlay(map);
        }

        @Override
        public void onDPVideoPause(Map<String, Object> map) {
            super.onDPVideoPause(map);
        }

        @Override
        public void onDPVideoContinue(Map<String, Object> map) {
            super.onDPVideoContinue(map);
        }

        @Override
        public void onDPVideoCompletion(Map<String, Object> map) {
            super.onDPVideoCompletion(map);
        }

        @Override
        public void onDPVideoOver(Map<String, Object> map) {
            super.onDPVideoOver(map);
        }

        @Override
        public void onDPClose() {
            super.onDPClose();
        }

        @Override
        public void onDPRequestStart(@Nullable Map<String, Object> map) {
            super.onDPRequestStart(map);
        }

        @Override
        public void onDPRequestFail(int i, String s, @Nullable Map<String, Object> map) {
            super.onDPRequestFail(i, s, map);
        }

        @Override
        public void onDPRequestSuccess(List<Map<String, Object>> list) {
            super.onDPRequestSuccess(list);
        }

        @Override
        public boolean isNeedBlock(DPDrama dpDrama, int i, @Nullable Map<String, Object> map) {
            return super.isNeedBlock(dpDrama, i, map);
        }

        @Override
        public void showAdIfNeeded(DPDrama dpDrama, Callback callback, @Nullable Map<String, Object> map) {
            super.showAdIfNeeded(dpDrama, callback, map);
        }

        @Override
        public void onDramaSwitch(@Nullable Map<String, Object> map) {
            super.onDramaSwitch(map);
        }

        @Override
        public void onDramaGalleryShow(@Nullable Map<String, Object> map) {
            super.onDramaGalleryShow(map);
        }

        @Override
        public void onDramaGalleryClick(@Nullable Map<String, Object> map) {
            super.onDramaGalleryClick(map);
        }

        @Override
        public void onRewardDialogShow(@Nullable Map<String, Object> map) {
            super.onRewardDialogShow(map);
        }

        @Override
        public void onUnlockDialogAction(String s, @Nullable Map<String, Object> map) {
            super.onUnlockDialogAction(s, map);
        }

        @Override
        public View onDPOtherView(DPSecondaryPageType dpSecondaryPageType, Map<String, Object> map) {
            return super.onDPOtherView(dpSecondaryPageType, map);
        }
    };

    private final IDPDrawListener drawListener = new IDPDrawListener() {
        @Override
        public void onDPRefreshFinish() {
            super.onDPRefreshFinish();
            Log.d(TAG, "onDPRefreshFinish");
        }

        @Override
        public void onDPListDataChange(Map<String, Object> map) {
            super.onDPListDataChange(map);
        }

        @Override
        public void onDPSeekTo(int i, long l) {
            super.onDPSeekTo(i, l);
        }

        @Override
        public void onDPPageChange(int i) {
            super.onDPPageChange(i);
        }

        @Override
        public void onDPPageChange(int i, Map<String, Object> map) {
            super.onDPPageChange(i, map);
        }

        @Override
        public void onDPVideoPlay(Map<String, Object> map) {
            super.onDPVideoPlay(map);
        }

        @Override
        public void onDPVideoPause(Map<String, Object> map) {
            super.onDPVideoPause(map);
        }

        @Override
        public void onDPVideoContinue(Map<String, Object> map) {
            super.onDPVideoContinue(map);
        }

        @Override
        public void onDPVideoCompletion(Map<String, Object> map) {
            super.onDPVideoCompletion(map);
        }

        @Override
        public void onDPVideoOver(Map<String, Object> map) {
            super.onDPVideoOver(map);
        }

        @Override
        public void onDPClose() {
            super.onDPClose();
        }

        @Override
        public void onDPReportResult(boolean b) {
            super.onDPReportResult(b);
        }

        @Override
        public void onDPReportResult(boolean b, Map<String, Object> map) {
            super.onDPReportResult(b, map);
        }

        @Override
        public void onDPRequestStart(@Nullable Map<String, Object> map) {
            super.onDPRequestStart(map);
        }

        @Override
        public void onDPRequestFail(int i, String s, @Nullable Map<String, Object> map) {
            super.onDPRequestFail(i, s, map);
        }

        @Override
        public void onDPRequestSuccess(List<Map<String, Object>> list) {
            super.onDPRequestSuccess(list);
        }

        @Override
        public void onDPClickAvatar(Map<String, Object> map) {
            super.onDPClickAvatar(map);
        }

        @Override
        public void onDPClickAuthorName(Map<String, Object> map) {
            super.onDPClickAuthorName(map);
        }

        @Override
        public void onDPClickComment(Map<String, Object> map) {
            super.onDPClickComment(map);
        }

        @Override
        public void onDPClickLike(boolean b, Map<String, Object> map) {
            super.onDPClickLike(b, map);
        }

        @Override
        public void onDPClickShare(Map<String, Object> map) {
            super.onDPClickShare(map);
        }

        @Override
        public void onDPPageStateChanged(DPPageState dpPageState) {
            super.onDPPageStateChanged(dpPageState);
        }

        @Override
        public View onCreateQuizView(ViewGroup viewGroup) {
            return super.onCreateQuizView(viewGroup);
        }

        @Override
        public void onQuizBindData(View view, List<String> list, int i, int i1, IDPQuizHandler idpQuizHandler, Map<String, Object> map) {
            super.onQuizBindData(view, list, i, i1, idpQuizHandler, map);
        }

        @Override
        public void onChannelTabChange(int i) {
            super.onChannelTabChange(i);
        }

        @Override
        public View onDPOtherView(DPSecondaryPageType dpSecondaryPageType, Map<String, Object> map) {
            return super.onDPOtherView(dpSecondaryPageType, map);
        }
    };

    private final IDPAdListener drawAdListener = new IDPAdListener() {
        @Override
        public void onDPAdRequest(Map<String, Object> map) {
            super.onDPAdRequest(map);
        }

        @Override
        public void onDPAdRequestSuccess(Map<String, Object> map) {
            super.onDPAdRequestSuccess(map);
        }

        @Override
        public void onDPAdRequestFail(int i, String s, Map<String, Object> map) {
            super.onDPAdRequestFail(i, s, map);
        }

        @Override
        public void onDPAdFillFail(Map<String, Object> map) {
            super.onDPAdFillFail(map);
        }

        @Override
        public void onDPAdShow(Map<String, Object> map) {
            super.onDPAdShow(map);
        }

        @Override
        public void onDPAdPlayStart(Map<String, Object> map) {
            super.onDPAdPlayStart(map);
        }

        @Override
        public void onDPAdPlayPause(Map<String, Object> map) {
            super.onDPAdPlayPause(map);
        }

        @Override
        public void onDPAdPlayContinue(Map<String, Object> map) {
            super.onDPAdPlayContinue(map);
        }

        @Override
        public void onDPAdPlayComplete(Map<String, Object> map) {
            super.onDPAdPlayComplete(map);
        }

        @Override
        public void onDPAdClicked(Map<String, Object> map) {
            super.onDPAdClicked(map);
        }

        @Override
        public void onRewardVerify(Map<String, Object> map) {
            super.onRewardVerify(map);
        }

        @Override
        public void onSkippedVideo(Map<String, Object> map) {
            super.onSkippedVideo(map);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (mDrawerLayout == null) {
            return true;
        }
        // TODO 功能开放后打开
        // mDrawerLayout.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.item_home) {
            // 主页
            changeFragmentIndex(item, 0);
            return true;
        }
        ToastUtil.showShort(getApplicationContext(), "功能暂未开放");
        return true;
    }

    /**
     * 切换Fragment的下标
     */
    private void changeFragmentIndex(MenuItem item, int currentIndex) {
        index = currentIndex;
        switchFragment();
        item.setChecked(true);
    }

    /**
     * Fragment切换
     */
    private void switchFragment() {
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(mFragmensts[currentTabIndex]);
        if (!mFragmensts[index].isAdded()) {
            trx.add(R.id.container, mFragmensts[index]);
        }
        trx.show(mFragmensts[index]).commit();
        currentTabIndex = index;
    }
}
