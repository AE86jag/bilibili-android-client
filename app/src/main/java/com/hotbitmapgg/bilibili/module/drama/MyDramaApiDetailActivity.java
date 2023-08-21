package com.hotbitmapgg.bilibili.module.drama;

import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_CURRENT_DURATION;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_CUSTOM_REPORT_ENABLED;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_FREE_SET;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_HIDE_LEFT_TOP_TIPS;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_INFINITE_SCROLL_ENABLED;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_LOCK_SET;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_MODE;
import static com.hotbitmapgg.bilibili.adapter.section.HomeRecommendedSection.KEY_DRAMA_UNLOCK_INDEX;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bytedance.sdk.dp.DPDrama;
import com.bytedance.sdk.dp.DPDramaDetailConfig;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPSecondaryPageType;
import com.bytedance.sdk.dp.DPWidgetDramaDetailParams;
import com.bytedance.sdk.dp.IDPAdListener;
import com.bytedance.sdk.dp.IDPDramaListener;
import com.bytedance.sdk.dp.IDPWidget;
import com.hotbitmapgg.bilibili.base.RxBaseActivity;
import com.hotbitmapgg.ohmybilibili.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MyDramaApiDetailActivity extends AppCompatActivity {
    private static final String TAG = "DramaApiDetailActivity";

    private static final String IS_FROM_CARD = "is_from_card";
    private static final String FROM_GID = "from_gid";

    int tipsTopMargin = -1;

    public static DPDrama outerDrama;
    public static DPWidgetDramaDetailParams.DPDramaEnterFrom enterFrom  = DPWidgetDramaDetailParams.DPDramaEnterFrom.DEFAULT;


    private IDPWidget dpWidget = null;
    private boolean isInited = false;
    private DPDrama drama = null;

    private int mInitUnlockIndex = 0;

    // 可免费观看的最大集数
    private Map<Long, Integer> mUnlockIndexMap = new HashMap<>();

    // 已经解锁的集数
    private Map<Long, List<Integer>> mHasUnlockIndexMap = new HashMap<>();

    //解锁时的全局阴影
    View blockView;
    //解锁按钮
    Button unlockBtn;
    //解锁时离开按钮
    Button leaveBtn;

    //private var sp = BaseApplication.instance.getSharedPreferences("pangrowth_demo", Context.MODE_PRIVATE)

    private int lastIndex = 1;

    private int currentDuration = 0;

    private boolean enableInfiniteScroll = true;
    private boolean enableCustomReport = false;

    private boolean hideLeftTopTips = false;

    private boolean hideMore = false;

    private int freeSet = 5; // 仅封装模式生效

    private int lockSet = 2; // 仅封装模式生效

    private String mode = DPDramaDetailConfig.SPECIFIC_DETAIL;

    //private val isFromCard by lazy { getIntent()?.getBooleanExtra(IS_FROM_CARD, false) ?: false }
    //private Long fromGid = getIntent() != null ? getIntent().getLongExtra(FROM_GID, -1L) : null;

    public Long getFromGid() {
        Intent intent = getIntent();
        return intent != null ? intent.getLongExtra(FROM_GID, -1L) : null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drama_activity_api_detail);
        blockView = findViewById(R.id.block_view);
        if (blockView != null) {
            blockView.setClickable(true);
            blockView.setVisibility(View.GONE);
        }
        unlockBtn = findViewById(R.id.unlock);
        leaveBtn = findViewById(R.id.leave);
        //outerDrama在列表页，点击单个短剧进入详情时已经赋值
        drama = outerDrama;
        //设置每个短剧已解锁的集数序号
        Intent intent = getIntent();
        mInitUnlockIndex = intent.getIntExtra(KEY_DRAMA_UNLOCK_INDEX, 1);
        mUnlockIndexMap.put(drama.id, mInitUnlockIndex);

        //
        currentDuration = intent.getIntExtra(KEY_DRAMA_CURRENT_DURATION, 0);
        enableInfiniteScroll = intent.getBooleanExtra(KEY_DRAMA_INFINITE_SCROLL_ENABLED, true);
        enableCustomReport = intent.getBooleanExtra(KEY_DRAMA_CUSTOM_REPORT_ENABLED, false);
        String dramaMode = intent.getStringExtra(KEY_DRAMA_MODE);
        mode = dramaMode == null ? DPDramaDetailConfig.COMMON_DETAIL : dramaMode;
        hideLeftTopTips = intent.getBooleanExtra(KEY_DRAMA_HIDE_LEFT_TOP_TIPS, false);
        freeSet = intent.getIntExtra(KEY_DRAMA_FREE_SET, -1);
        lockSet = intent.getIntExtra(KEY_DRAMA_LOCK_SET, -1);

        if (DPSdk.isStartSuccess()) {
            init();
        }
    }

    private void init() {
        if (isInited) {
            return;
        }
        initWidget();

        if (dpWidget != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, dpWidget.getFragment()).commit();

            if (leaveBtn != null) {
                //解锁当前短剧，需要查看广告，点击不看广告执行当前逻辑
                leaveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        }
        isInited = true;
    }

    private void initWidget() {
        if (drama != null) {
            dpWidget = DPSdk.factory().createDramaDetail(
                    DPWidgetDramaDetailParams.obtain()
                            .detailConfig(
                                    DPDramaDetailConfig.obtain(mode)
                                            .bottomOffset(20)
                                            .infiniteScrollEnabled(enableInfiniteScroll)
                                            .scriptTipsTopMargin(tipsTopMargin)
                                            .hideLeftTopTips(hideLeftTopTips, null)
                                            .showCellularToast(true)
                                            .hideMore(hideMore)
                                            .freeSet(freeSet)
                                            .lockSet(lockSet)
                                            .listener(dramaListener)
                                            .adListener(dramaAdListener)
                                            .setCustomReport(enableCustomReport, this::enterCustomReport)
                            )
                            .id(drama.id)
                            .index(drama.index)
                            .currentDuration(currentDuration)
                            .fromGid(getFromGid().toString()) // 必传，否则影响推荐效果
                            .from(enterFrom) // 必传，否则影响推荐效果
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dpWidget != null) {
            dpWidget.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO 什么作用
        /*drama?.let {
            val json = JSONObject()
            json.put("id", drama?.id)
            json.put("status", drama?.status)
            json.put("total", drama?.total)
            json.put("title", drama?.title)
            json.put("cover_image", drama?.coverImage)
            json.put("index", lastIndex)
            json.put("class", drama?.type)
            json.put("desc", drama?.desc)
            sp.edit().putString(DramaApiActivity.KEY_DRAMA_HISTORY, json.toString()).apply()
        }*/
    }

    /*内流短剧详情页的回调通过IDPDramaListener回调，其余沉浸式场景回调通过IDPDrawListener回传给开发者*/
    private IDPDramaListener dramaListener = new IDPDramaListener() {
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
            super.onDPVideoPlay(map);
            Log.d(TAG, "视频播放时回调, onDPVideoPlay(), 附加参数: " + map);
            if (map != null) {
                lastIndex = (Integer) map.get("index");
            }
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
            if (dpDrama == null) {
                return false;
            }
            int unlockIndex = mUnlockIndexMap.get(dpDrama.id) == null ? mInitUnlockIndex : mUnlockIndexMap.get(dpDrama.id);
            List<Integer> hasUnlockList = mHasUnlockIndexMap.get(dpDrama.id) == null ? new ArrayList<>() : mHasUnlockIndexMap.get(dpDrama.id);
            return i > unlockIndex && !hasUnlockList.contains(i);

        }

        @Override
        public void showAdIfNeeded(DPDrama dpDrama, Callback callback, @Nullable Map<String, Object> map) {
            super.showAdIfNeeded(dpDrama, callback, map);
            if (dpDrama == null) {
                return;
            }
            Log.d(TAG, "showAdIfNeeded:" + map);
            if (blockView != null) {
                blockView.setVisibility(View.VISIBLE);
            }

            if (unlockBtn != null) {
                unlockBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<Integer> hasUnlockList = mHasUnlockIndexMap.get(dpDrama.id) == null ? new ArrayList<>() : mHasUnlockIndexMap.get(dpDrama.id);
                        hasUnlockList.add(dpWidget.getCurrentDramaIndex());
                        mHasUnlockIndexMap.put(dpDrama.id, hasUnlockList);
                        if (blockView != null) {
                            blockView.setVisibility(View.GONE);
                        }
                        if (callback != null) {
                            callback.onDramaRewardArrived(); // 解锁当前集
                        }
                    }
                });
            }
        }

        @Override
        public void onDramaSwitch(@Nullable Map<String, Object> map) {
            Log.d(TAG, "当前播放页短剧切换时回调, onDramaSwitch(), 附加参数: " + map);
            super.onDramaSwitch(map);
        }

        @Override
        public void onDramaGalleryShow(@Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧详情页选集面板展现, onDramaGalleryShow(), 附加参数: " + map);
            super.onDramaGalleryShow(map);
        }

        @Override
        public void onDramaGalleryClick(@Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧详情页选集面板点击, onDramaGalleryClick(), 附加参数: " + map);
            super.onDramaGalleryClick(map);
        }

        @Override
        public void onRewardDialogShow(@Nullable Map<String, Object> map) {
            Log.d(TAG, "短剧解锁弹窗展示, onRewardDialogShow(), 附加参数: " + map);
            super.onRewardDialogShow(map);
        }

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

    private IDPAdListener dramaAdListener = new IDPAdListener() {
        @Override
        public void onDPAdRequest(Map<String, Object> map) {
            Log.d(TAG, "广告请求, onDPAdRequest: 附加参数:" + map);
            super.onDPAdRequest(map);
        }

        @Override
        public void onDPAdRequestSuccess(Map<String, Object> map) {
            Log.d(TAG, "广告请求成功, onDPAdRequestSuccess: 附加参数:" + map);
            super.onDPAdRequestSuccess(map);
        }

        @Override
        public void onDPAdRequestFail(int i, String s, Map<String, Object> map) {
            Log.d(TAG, "广告请求失败, onDPAdRequestFail: 错误码:" + i + ",错误信息:" + s + " 附加参数:" + map);
            super.onDPAdRequestFail(i, s, map);
        }

        @Override
        public void onDPAdFillFail(Map<String, Object> map) {
            Log.d(TAG, "广告填充失败, onDPAdFillFail: 附加参数:" + map);
            super.onDPAdFillFail(map);
        }

        @Override
        public void onDPAdShow(Map<String, Object> map) {
            Log.d(TAG, "广告曝光, onDPAdShow: 附加参数:" + map);
            super.onDPAdShow(map);
        }

        @Override
        public void onDPAdPlayStart(Map<String, Object> map) {
            Log.d(TAG, "广告开始播放, onDPAdPlayStart: 附加参数:" + map);
            super.onDPAdPlayStart(map);
        }

        @Override
        public void onDPAdPlayPause(Map<String, Object> map) {
            Log.d(TAG, "广告暂停播放, onDPAdPlayPause: 附加参数:" + map);
            super.onDPAdPlayPause(map);
        }

        @Override
        public void onDPAdPlayContinue(Map<String, Object> map) {
            Log.d(TAG, "广告继续播放, onDPAdPlayContinue: 附加参数:" + map);
            super.onDPAdPlayContinue(map);
        }

        @Override
        public void onDPAdPlayComplete(Map<String, Object> map) {
            Log.d(TAG, "广告播放结束, onDPAdPlayComplete: 附加参数:" + map);
            super.onDPAdPlayComplete(map);
        }

        @Override
        public void onDPAdClicked(Map<String, Object> map) {
            Log.d(TAG, "广告点击, onDPAdClicked: 附加参数:" + map);
            super.onDPAdClicked(map);
        }

        @Override
        public void onRewardVerify(Map<String, Object> map) {
            Log.d(TAG, "激励视频广告确认回调, onRewardVerify: 附加参数:" + map);
            super.onRewardVerify(map);
        }

        @Override
        public void onSkippedVideo(Map<String, Object> map) {
            Log.d(TAG, "激励视频广告跳过回调, onSkippedVideo: 附加参数:" + map);
            super.onSkippedVideo(map);
        }
    };

    private void enterCustomReport(Context context, Long gid) {
        //跳转自定义举报页
        /*drama?.let {
            val intent = Intent(context, DramaCustomReportActivity::class.java)
            intent.putExtra(DramaCustomReportActivity.DRAMA_GROUP_ID, gid)
            this.startActivity(intent)
        }*/
    }
}
