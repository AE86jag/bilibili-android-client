package com.hotbitmapgg.bilibili.module.home.index;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.hotbitmapgg.bilibili.module.home.HomePageFragment;
import com.hotbitmapgg.bilibili.utils.ConstantUtil;
import com.hotbitmapgg.bilibili.utils.PreferenceUtil;
import com.hotbitmapgg.bilibili.utils.ToastUtil;
import com.hotbitmapgg.bilibili.widget.CircleImageView;
import com.spmystery.drama.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BottomTabLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout mTabLayout;
    private Fragment[] mFragmensts;
    private IDPWidget idpWidget;

    public static final String TAG = "BottomTabLayoutActivity";
    private static final int FREE_SET = -1;
    private static final int LOCK_SET = -1;
    private int index;
    private int currentTabIndex;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    private final List<String> mPermissionList = new ArrayList<>();

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
    protected void onDestroy() {
        super.onDestroy();
        if (idpWidget != null) {
            idpWidget.destroy();
        }
    }

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
        getPermissions();
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
        /**
         * 广告请求
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdRequest(Map<String, Object> map) {
            Log.d(TAG, "广告请求, onDPAdRequest: 附加参数:" + map);
            super.onDPAdRequest(map);
        }
        /**
         * 广告请求成功
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdRequestSuccess(Map<String, Object> map) {
            Log.d(TAG, "广告请求成功, onDPAdRequestSuccess: 附加参数:" + map);
            super.onDPAdRequestSuccess(map);
        }

        /**
         * 广告请求失败
         *
         * @param i 错误码
         * @param s  错误信息
         * @param map  附加参数
         */
        @Override
        public void onDPAdRequestFail(int i, String s, Map<String, Object> map) {
            Log.d(TAG, "广告请求失败, onDPAdRequestFail: 错误码:" + i + ",错误信息:" + s + " 附加参数:" + map);
            super.onDPAdRequestFail(i, s, map);
        }

        /**
         * 广告填充失败
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdFillFail(Map<String, Object> map) {
            Log.d(TAG, "广告填充失败, onDPAdFillFail: 附加参数:" + map);
            super.onDPAdFillFail(map);
        }

        /**
         * 广告曝光
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdShow(Map<String, Object> map) {
            Log.d(TAG, "广告曝光, onDPAdShow: 附加参数:" + map);
            super.onDPAdShow(map);
        }

        /**
         * 广告开始播放
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayStart(Map<String, Object> map) {
            Log.d(TAG, "广告开始播放, onDPAdPlayStart: 附加参数:" + map);
            super.onDPAdPlayStart(map);
        }

        /**
         * 广告暂停播放.
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayPause(Map<String, Object> map) {
            Log.d(TAG, "广告暂停播放, onDPAdPlayPause: 附加参数:" + map);
            super.onDPAdPlayPause(map);
        }

        /**
         * 广告继续播放
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayContinue(Map<String, Object> map) {
            Log.d(TAG, "广告继续播放, onDPAdPlayContinue: 附加参数:" + map);
            super.onDPAdPlayContinue(map);
        }

        /**
         * 广告播放结束.
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayComplete(Map<String, Object> map) {
            Log.d(TAG, "广告播放结束, onDPAdPlayComplete: 附加参数:" + map);
            super.onDPAdPlayComplete(map);
        }

        /**
         * 广告点击
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdClicked(Map<String, Object> map) {
            Log.d(TAG, "广告点击, onDPAdClicked: 附加参数:" + map);
            super.onDPAdClicked(map);
        }

        /**
         * 激励视频广告确认回调
         * 4601新增
         * @param map 附加参数
         */
        @Override
        public void onRewardVerify(Map<String, Object> map) {
            Log.d(TAG, "激励视频广告确认回调, onRewardVerify: 附加参数:" + map);
            super.onRewardVerify(map);
        }
        /**
         * 激励视频广告跳过回调
         * 4601新增
         * @param map 附加参数
         */
        @Override
        public void onSkippedVideo(Map<String, Object> map) {
            Log.d(TAG, "激励视频广告跳过回调, onSkippedVideo: 附加参数:" + map);
            super.onSkippedVideo(map);
        }
    };

    private final IDPDramaListener dramaListener = new IDPDramaListener() {
        @Override
        public void onDPSeekTo(int i, long l) {
            Log.d(TAG, "用户拖动进度条松手时回调, onDPSeekTo(), position: " + i + ", time: " + l);
            super.onDPSeekTo(i, l);
        }

        @Override
        public void onDPPageChange(int i, Map<String, Object> map) {
            Log.d(TAG, "页面切换时回调, onDPPageChange(), 附加参数: " + map + ", 页面索引值: " + i);
            super.onDPPageChange(i, map);
        }

        @Override
        public void onDPVideoPlay(Map<String, Object> map) {
            Log.d(TAG, "视频播放时回调, onDPVideoPlay(), 附加参数: " + map);
            super.onDPVideoPlay(map);
        }

        @Override
        public void onDPVideoPause(Map<String, Object> map) {
            Log.d(TAG, "视频暂停播放时回调（4.3.0.1 添加）, onDPVideoPause(), 附加参数: " + map);
            super.onDPVideoPause(map);
        }

        @Override
        public void onDPVideoContinue(Map<String, Object> map) {
            Log.d(TAG, "视频继续播放时回调（4.3.0.1 添加）, onDPVideoContinue(), 附加参数: " + map);
            super.onDPVideoContinue(map);
        }

        @Override
        public void onDPVideoCompletion(Map<String, Object> map) {
            Log.d(TAG, "视频播放完成时回调(包含重复播放), onDPVideoCompletion(), 附加参数: " + map);
            super.onDPVideoCompletion(map);
        }

        @Override
        public void onDPVideoOver(Map<String, Object> map) {
            Log.d(TAG, "视频播放结束时回调, onDPVideoOver(), 附加参数: " + map);
            super.onDPVideoOver(map);
        }

        @Override
        public void onDPClose() {
            Log.d(TAG, "界面关闭时回调, onDPClose()");
            super.onDPClose();
        }

        @Override
        public void onDPRequestStart(@Nullable Map<String, Object> map) {
            Log.d(TAG, "开始请求回调, onDPRequestStart(), 附加参数: " + map);
            super.onDPRequestStart(map);
        }

        @Override
        public void onDPRequestFail(int i, String s, @Nullable Map<String, Object> map) {
            Log.d(TAG, "请求失败回调, onDPRequestFail(), 错误码：" + i + ",错误信息：" + s + ",附加参数: " + map);
            super.onDPRequestFail(i, s, map);
        }

        @Override
        public void onDPRequestSuccess(List<Map<String, Object>> list) {
            Log.d(TAG, "请求成功回调, onDPRequestSuccess(), 附加参数: " + list);
            super.onDPRequestSuccess(list);
        }

        @Override
        public boolean isNeedBlock(DPDrama dpDrama, int i, @Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧视频是否需要阻塞才能进行, isNeedBlock(), 附加参数: " + map);
            return super.isNeedBlock(dpDrama, i, map);
        }

        /**
         * 开发者自行实现该回调，在短剧阻塞播放时展示广告，等广告展示结束后调用{@link Callback#onDramaRewardArrived()}进行短剧播放
         *
         * @param dpDrama 短剧信息
         * @param callback 回调
         * @param map 附加参数
         * @return
         */
        @Override
        public void showAdIfNeeded(DPDrama dpDrama, Callback callback, @Nullable Map<String, Object> map) {
            super.showAdIfNeeded(dpDrama, callback, map);
        }

        /**
         * 当前播放页短剧切换时回调（4.4.0.0 添加）
         *
         * @param map 附加参数
         * @return
         */
        @Override
        public void onDramaSwitch(@Nullable Map<String, Object> map) {
            Log.d(TAG, "当前播放页短剧切换时回调, onDramaSwitch(), 附加参数: " + map);
            super.onDramaSwitch(map);
        }

        /**
         * 4601新增
         * 短剧详情页选集面板展现，
         * 封装模式
         * @param map 额外参数
         */
        @Override
        public void onDramaGalleryShow(@Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧详情页选集面板展现, onDramaGalleryShow(), 附加参数: " + map);
            super.onDramaGalleryShow(map);
        }

        /**
         * 4601新增
         * 短剧详情页选集面板点击，
         * 封装模式
         * @param map 参数
         */
        @Override
        public void onDramaGalleryClick(@Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧详情页选集面板点击, onDramaGalleryClick(), 附加参数: " + map);
            super.onDramaGalleryClick(map);
        }

        /**
         *4601新增
         * 短剧解锁弹窗展示，
         * 封装模式
         * @param map 参数
         * */
        @Override
        public void onRewardDialogShow(@Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧解锁弹窗展示, onRewardDialogShow(), 附加参数: " + map);
            super.onRewardDialogShow(map);
        }

        /**
         * 4601更新
         * 短剧解锁弹窗点击动作，
         * 封装模式
         * @param s  点击动作
         * @param map     参数
         * */
        @Override
        public void onUnlockDialogAction(String s, @Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧解锁弹窗点击动作, onUnlockDialogAction(), 点击动作：" + s + "附加参数: " + map);
            super.onUnlockDialogAction(s, map);
        }

        @Override
        public View onDPOtherView(DPSecondaryPageType dpSecondaryPageType, Map<String, Object> map) {
            return super.onDPOtherView(dpSecondaryPageType, map);
        }
    };

    private final IDPDrawListener drawListener = new IDPDrawListener() {
        /**
         * 刷新任务完成后回调（数据刷新完成）
         */
        @Override
        public void onDPRefreshFinish() {
            Log.d(TAG, "刷新任务完成后回调, onDPRefreshFinish: ");
            super.onDPRefreshFinish();
        }

        /**
         * 列表数据变化回调
         */
        @Override
        public void onDPListDataChange(Map<String, Object> map) {
            Log.d(TAG, "列表数据变化回调, onDPListDataChange: 附加参数:" + map);
            super.onDPListDataChange(map);
        }

        /**
         * 用户拖动进度条松手时回调
         *
         * @param i 视频位置
         * @param l 毫秒
         */
        @Override
        public void onDPSeekTo(int i, long l) {
            Log.d(TAG, "用户拖动进度条松手时回调, onDPSeekTo(), 视频位置" + i + ",毫秒：" + l);
            super.onDPSeekTo(i, l);
        }

        /**
         * 页面切换时回调
         *
         * @param i 页面索引值
         */
        @Override
        public void onDPPageChange(int i) {
            Log.d(TAG, "页面切换时回调, onDPPageChange(), 页面索引值：" + i);
            super.onDPPageChange(i);
        }

        /**
         * 页面切换时回调
         *
         * @param i 页面索引值
         * @param map      附加参数
         */
        @Override
        public void onDPPageChange(int i, Map<String, Object> map) {
            Log.d(TAG, "页面切换时回调, onDPPageChange: ");
            super.onDPPageChange(i, map);
        }

        /**
         * 视频播放时回调
         *
         * @param map 附加参数
         */
        @Override
        public void onDPVideoPlay(Map<String, Object> map) {
            Log.d(TAG, "视频播放时回调, onDPVideoPlay: 附加参数：" + map);
            super.onDPVideoPlay(map);
        }

        /**
         * 视频暂停播放时回调（2.1.0.0 添加）
         *
         * @param map 附加参数
         */
        @Override
        public void onDPVideoPause(Map<String, Object> map) {
            Log.d(TAG, "视频暂停播放时回调, onDPVideoPause: 附加参数：" + map);
            super.onDPVideoPause(map);
        }

        /**
         * 视频继续播放时回调（2.1.0.0 添加）
         *
         * @param map 附加参数
         */
        @Override
        public void onDPVideoContinue(Map<String, Object> map) {
            Log.d(TAG, "视频继续播放时回调, onDPVideoContinue: 附加参数：" + map);
            super.onDPVideoContinue(map);
        }

        /**
         * 视频播放完成时回调（包含重复播放）。
         * 2000版本添加该接口回调。
         *
         * @param map 附加参数
         */
        @Override
        public void onDPVideoCompletion(Map<String, Object> map) {
            Log.d(TAG, "视频播放完成时回调, onDPVideoCompletion: 附加参数：" + map);
            super.onDPVideoCompletion(map);
        }

        /**
         * 视频播放结束时回调
         *
         * @param map 附加参数
         */
        @Override
        public void onDPVideoOver(Map<String, Object> map) {
            Log.d(TAG, "视频播放结束时回调, onDPVideoOver: 附加参数：" + map);
            super.onDPVideoOver(map);
        }

        /**
         * 界面关闭时回调
         */
        @Override
        public void onDPClose() {
            Log.d(TAG, "界面关闭时回调, onDPClose: ");
            super.onDPClose();
        }

        /**
         * 举报结果回调（根据举报成功失败结果，隐藏、展示举报界面及提示）（1.0.0.0 新增）
         *
         * @param b 举报成功：true 举报失败：false
         */
        @Override
        public void onDPReportResult(boolean b) {
            Log.d(TAG, "举报结果回调, onDPReportResult: 举报结果：" + b);
            super.onDPReportResult(b);
        }

        /**
         * 举报结果回调（根据举报成功失败结果，隐藏、展示举报界面及提示）（2.1.0.0 添加）
         *
         * @param isSucceed 举报成功：true 举报失败：false
         * @param map       附加参数
         */
        @Override
        public void onDPReportResult(boolean isSucceed, Map<String, Object> map) {
            Log.d(TAG, "举报结果回调, onDPReportResult: 举报结果：" + isSucceed + "附加参数:" + map);
            super.onDPReportResult(isSucceed, map);
        }

        /**
         * 开始请求回调（2.1.0.0 添加）
         *
         * @param map 附加参数
         */
        @Override
        public void onDPRequestStart(@Nullable Map<String, Object> map) {
            Log.d(TAG, "开始请求回调, onDPRequestStart: 附加参数:" + map);
            super.onDPRequestStart(map);
        }

        /**
         * 请求失败回调（2.1.0.0 添加）
         *
         * @param code 错误码
         * @param msg  错误信息
         * @param map  附加参数
         */
        @Override
        public void onDPRequestFail(int code, String msg, @Nullable Map<String, Object> map) {
            Log.d(TAG, "请求失败回调, onDPRequestFail(), 错误码:" + code + ",错误信息:" + msg + ",附加参数:" + map);
            super.onDPRequestFail(code, msg, map);
        }

        /**
         * 请求成功回调（2.1.0.0 添加）
         *
         * @param list 附加参数
         */
        @Override
        public void onDPRequestSuccess(List<Map<String, Object>> list) {
            Log.d(TAG, "请求成功回调, onDPRequestSuccess: 附加参数：" + list);
            super.onDPRequestSuccess(list);
        }

        /**
         * 点击作者头像时回调（2.1.0.0 添加）
         *
         * @param map 附加参数
         */
        @Override
        public void onDPClickAvatar(Map<String, Object> map) {
            Log.d(TAG, "点击作者头像时回调, onDPClickAvatar: 附加参数：" + map);
            super.onDPClickAvatar(map);
        }

        /**
         * 点击作者昵称时回调（2.1.0.0 添加）
         *
         * @param map 附加参数
         */
        @Override
        public void onDPClickAuthorName(Map<String, Object> map) {
            Log.d(TAG, "点击作者昵称时回调, onDPClickAuthorName: 附加参数：" + map);
            super.onDPClickAuthorName(map);
        }

        /**
         * 点击评论时回调（2.1.0.0 添加）
         *
         * @param map 附加参数
         */
        @Override
        public void onDPClickComment(Map<String, Object> map) {
            Log.d(TAG, "点击评论时回调, onDPClickComment: 附加参数：" + map);
            super.onDPClickComment(map);
        }

        /**
         * 点赞时回调（2.1.0.0 添加）
         *
         * @param isLike 是否点赞 true 点赞，false 取消点赞
         * @param map    附加参数
         */
        @Override
        public void onDPClickLike(boolean isLike, Map<String, Object> map) {
            Log.d(TAG, "点赞时回调, onDPClickLike:是否点赞" + isLike + " 附加参数：" + map);
            super.onDPClickLike(isLike, map);
        }

        /**
         * 点击更多-分享时的回调，用于宿主侧处理分享链接
         * @param map 附加参数
         */
        @Override
        public void onDPClickShare(Map<String, Object> map) {
            Log.d(TAG, "点击更多-分享时的回调, onDPClickShare: 附加参数：" + map);
            super.onDPClickShare(map);
        }

        /**
         * 页面状态回调
         * @param dpPageState 页面状态{@link DPPageState}
         */
        @Override
        public void onDPPageStateChanged(DPPageState dpPageState) {
            Log.d(TAG, "页面状态回调, onDPPageStateChanged: 附加参数：" + dpPageState);
            super.onDPPageStateChanged(dpPageState);
        }

        /**
         * 创建答题View返回，每次需要返回新的View
         *
         * @param viewGroup 父容器，不要直接向父容器里添加View
         * @return
         */
        @Override
        public View onCreateQuizView(ViewGroup viewGroup) {
            Log.d(TAG, "创建答题View返回, onCreateQuizView: 附加参数：" + viewGroup);
            return super.onCreateQuizView(viewGroup);
        }

        @Override
        public void onQuizBindData(View view, List<String> list, int i, int i1, IDPQuizHandler idpQuizHandler, Map<String, Object> map) {
            Log.d(TAG, "onQuizBindData(), view:" + view + ",list:" + list + ",附加参数：" + map);
            super.onQuizBindData(view, list, i, i1, idpQuizHandler, map);
        }

        /**
         * 频道tab切换回调  4501版本新增
         *
         * @param i 切换后的频道 推荐 - {@link DPWidgetDrawParams#DRAW_CHANNEL_TYPE_RECOMMEND}
         *                关注 - {@link DPWidgetDrawParams#DRAW_CHANNEL_TYPE_FOLLOW}
         *                剧场 - {@link DPWidgetDrawParams#DRAW_CHANNEL_TYPE_THEATER}
         * */
        @Override
        public void onChannelTabChange(int i) {
            Log.d(TAG, "频道tab切换回调, onChannelTabChange: 切换后的频道：" + i);
            super.onChannelTabChange(i);
        }

        @Override
        public View onDPOtherView(DPSecondaryPageType dpSecondaryPageType, Map<String, Object> map) {
            Log.d(TAG, "onDPOtherView: DPSecondaryPageType + " + dpSecondaryPageType + "附加参数：" + map);
            return super.onDPOtherView(dpSecondaryPageType, map);
        }
    };

    private final IDPAdListener drawAdListener = new IDPAdListener() {
        /**
         * 广告请求
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdRequest(Map<String, Object> map) {
            Log.d(TAG, "广告请求, onDPAdRequest: 附加参数:" + map);
            super.onDPAdRequest(map);
        }
        /**
         * 广告请求成功
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdRequestSuccess(Map<String, Object> map) {
            Log.d(TAG, "广告请求成功, onDPAdRequestSuccess: 附加参数:" + map);
            super.onDPAdRequestSuccess(map);
        }
        /**
         * 广告请求失败
         *
         * @param i 错误码
         * @param s  错误信息
         * @param map  附加参数
         */
        @Override
        public void onDPAdRequestFail(int i, String s, Map<String, Object> map) {
            Log.d(TAG, "广告请求失败, onDPAdRequestFail: 错误码:" + i + ",错误信息:" + s + " 附加参数:" + map);
            super.onDPAdRequestFail(i, s, map);
        }
        /**
         * 广告填充失败
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdFillFail(Map<String, Object> map) {
            Log.d(TAG, "广告填充失败, onDPAdFillFail: 附加参数:" + map);
            super.onDPAdFillFail(map);
        }

        /**
         * 广告曝光
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdShow(Map<String, Object> map) {
            Log.d(TAG, "广告曝光, onDPAdShow: 附加参数:" + map);
            super.onDPAdShow(map);
        }

        /**
         * 广告开始播放
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayStart(Map<String, Object> map) {
            Log.d(TAG, "广告开始播放, onDPAdPlayStart: 附加参数:" + map);
            super.onDPAdPlayStart(map);
        }

        /**
         * 广告暂停播放.
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayPause(Map<String, Object> map) {
            Log.d(TAG, "广告暂停播放, onDPAdPlayPause: 附加参数:" + map);
            super.onDPAdPlayPause(map);
        }

        /**
         * 广告继续播放
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayContinue(Map<String, Object> map) {
            Log.d(TAG, "广告继续播放, onDPAdPlayContinue: 附加参数:" + map);
            super.onDPAdPlayContinue(map);
        }

        /**
         * 广告播放结束.
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdPlayComplete(Map<String, Object> map) {
            Log.d(TAG, "广告播放结束, onDPAdPlayComplete: 附加参数:" + map);
            super.onDPAdPlayComplete(map);
        }

        /**
         * 广告点击
         *
         * @param map 附加参数
         */
        @Override
        public void onDPAdClicked(Map<String, Object> map) {
            Log.d(TAG, "广告点击, onDPAdClicked: 附加参数:" + map);
            super.onDPAdClicked(map);
        }

        /**
         * 激励视频广告确认回调
         * 4601新增
         * @param map 附加参数
         */
        @Override
        public void onRewardVerify(Map<String, Object> map) {
            Log.d(TAG, "激励视频广告确认回调, onRewardVerify: 附加参数:" + map);
            super.onRewardVerify(map);
        }
        /**
         * 激励视频广告跳过回调
         * 4601新增
         * @param map 附加参数
         */
        @Override
        public void onSkippedVideo(Map<String, Object> map) {
            Log.d(TAG, "激励视频广告跳过回调, onSkippedVideo: 附加参数:" + map);
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

    private void getPermissions() {
        mPermissionList.clear(); //清空已经允许的没有通过的权限
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i])
                    != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(PERMISSIONS[i]);
            }
        }
        if (mPermissionList.size() > 0) {                           //有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, PERMISSIONS, 10000);
        } else {
        }
    }
}
