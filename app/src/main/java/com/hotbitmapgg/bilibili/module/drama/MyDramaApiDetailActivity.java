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

import com.bytedance.sdk.dp.DPDrama;
import com.bytedance.sdk.dp.DPDramaDetailConfig;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPSecondaryPageType;
import com.bytedance.sdk.dp.DPWidgetDramaDetailParams;
import com.bytedance.sdk.dp.IDPAdListener;
import com.bytedance.sdk.dp.IDPDramaListener;
import com.bytedance.sdk.dp.IDPWidget;
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
    private  Map<Long, List<Integer>> mHasUnlockIndexMap = new HashMap<>();

    //解锁时的全局阴影
    @BindView(R.id.block_view)
    private View blockView;
    //解锁按钮
    @BindView(R.id.unlock)
    private Button unlockBtn;
    //解锁时离开按钮
    @BindView(R.id.leave)
    private Button leaveBtn;

    //跳转到指定集数输入框
    @BindView(R.id.et_drama_index)
    private EditText indexEt = null;
    //跳转到指定集数的按钮
    @BindView(R.id.btn_go)
    private Button goBtn = null;

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
    private Long fromGid = getIntent() != null ? getIntent().getLongExtra(FROM_GID, -1L) : null;

    public long getFromGid() {
        Intent intent = getIntent();
        return intent != null ? intent.getLongExtra(FROM_GID, -1L) : null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drama_activity_api_detail);

        if (blockView != null) {
            blockView.setClickable(true);
            blockView.setVisibility(View.GONE);
        }
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
                            .fromGid(fromGid.toString()) // 必传，否则影响推荐效果
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

    private IDPDramaListener dramaListener = new IDPDramaListener() {
        @Override
        public void onDPSeekTo(int i, long l) {
            super.onDPSeekTo(i, l);
            Log.d(TAG, "onDPSeekTo:");
        }

        @Override
        public void onDPPageChange(int i, Map<String, Object> map) {
            super.onDPPageChange(i, map);
            Log.d(TAG, "onDPPageChange:" + map);
        }

        @Override
        public void onDPVideoPlay(Map<String, Object> map) {
            super.onDPVideoPlay(map);
            Log.d(TAG, "onDPVideoPlay:" + map);
            if (map != null) {
                lastIndex = (Integer) map.get("index");
            }
        }

        @Override
        public void onDPVideoPause(Map<String, Object> map) {
            super.onDPVideoPause(map);
            Log.d(TAG, "onDPVideoPause:" + map);
        }

        @Override
        public void onDPVideoContinue(Map<String, Object> map) {
            super.onDPVideoContinue(map);
            Log.d(TAG, "onDPVideoContinue:" + map);
        }

        @Override
        public void onDPVideoCompletion(Map<String, Object> map) {
            super.onDPVideoCompletion(map);
            Log.d(TAG, "onDPVideoCompletion:" + map);
        }

        @Override
        public void onDPVideoOver(Map<String, Object> map) {
            super.onDPVideoOver(map);
            Log.d(TAG, "onDPVideoOver:" + map);
        }

        @Override
        public void onDPClose() {
            super.onDPClose();
            Log.d(TAG, "onDPClose");
        }

        @Override
        public void onDPRequestStart(@Nullable Map<String, Object> map) {
            super.onDPRequestStart(map);
            Log.d(TAG, "onDPRequestStart:" + map);
        }

        @Override
        public void onDPRequestFail(int i, String s, @Nullable Map<String, Object> map) {
            super.onDPRequestFail(i, s, map);
            Log.d(TAG, "onDPRequestFail:" + map);
        }

        @Override
        public void onDPRequestSuccess(List<Map<String, Object>> list) {
            super.onDPRequestSuccess(list);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> stringObjectMap : list) {
                    Log.d(TAG, "onDPRequestSuccess:" + stringObjectMap);
                }
            }
        }

        @Override
        public boolean isNeedBlock(DPDrama dpDrama, int i, @Nullable Map<String, Object> map) {
            if (dpDrama == null) {
                return false;
            }
            Log.d(TAG, "isNeedBlock: index = " + i);
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
            super.onDramaSwitch(map);
            Log.d(TAG, "onDramaSwitch:" + map);
        }

        @Override
        public void onDramaGalleryShow(@Nullable Map<String, Object> map) {
            super.onDramaGalleryShow(map);
        }

        @Override
        public void onDramaGalleryClick(@Nullable Map<String, Object> map) {
            super.onDramaGalleryClick(map);
            Log.d(TAG, "onDramaGalleryClick: " + map);
        }

        @Override
        public void onRewardDialogShow(@Nullable Map<String, Object> map) {
            super.onRewardDialogShow(map);
            Log.d(TAG, "onRewardDialogShow: $it" + map);
        }

        @Override
        public void onUnlockDialogAction(String s, @Nullable Map<String, Object> map) {
            super.onUnlockDialogAction(s, map);
            Log.d(TAG, "onUnlockDialogAction: s: " + s + "map: " + map);
        }

        @Override
        public View onDPOtherView(DPSecondaryPageType dpSecondaryPageType, Map<String, Object> map) {
            return super.onDPOtherView(dpSecondaryPageType, map);
        }
    };

    private IDPAdListener dramaAdListener = new IDPAdListener() {
        @Override
        public void onDPAdRequest(Map<String, Object> map) {
            super.onDPAdRequest(map);
            Log.d(TAG, "onDPAdRequest map = " + map);
        }

        @Override
        public void onDPAdRequestSuccess(Map<String, Object> map) {
            super.onDPAdRequestSuccess(map);
            Log.d(TAG, "onDPAdRequestSuccess map = " + map);
        }

        @Override
        public void onDPAdRequestFail(int i, String s, Map<String, Object> map) {
            super.onDPAdRequestFail(i, s, map);
            Log.d(TAG, "onDPAdRequestFail map = " + map);
        }

        @Override
        public void onDPAdFillFail(Map<String, Object> map) {
            super.onDPAdFillFail(map);
            Log.d(TAG, "onDPAdFillFail map = " + map);
        }

        @Override
        public void onDPAdShow(Map<String, Object> map) {
            super.onDPAdShow(map);
            Log.d(TAG, "onDPAdShow map = " + map);
        }

        @Override
        public void onDPAdPlayStart(Map<String, Object> map) {
            super.onDPAdPlayStart(map);
            Log.d(TAG, "onDPAdPlayStart map = " + map);
        }

        @Override
        public void onDPAdPlayPause(Map<String, Object> map) {
            super.onDPAdPlayPause(map);
            Log.d(TAG, "onDPAdPlayPause map = " + map);
        }

        @Override
        public void onDPAdPlayContinue(Map<String, Object> map) {
            super.onDPAdPlayContinue(map);
            Log.d(TAG, "onDPAdPlayContinue map = " + map);
        }

        @Override
        public void onDPAdPlayComplete(Map<String, Object> map) {
            super.onDPAdPlayComplete(map);
            Log.d(TAG, "onDPAdPlayComplete map = " + map);
        }

        @Override
        public void onDPAdClicked(Map<String, Object> map) {
            super.onDPAdClicked(map);
            Log.d(TAG, "onDPAdClicked map = " + map);
        }

        @Override
        public void onRewardVerify(Map<String, Object> map) {
            super.onRewardVerify(map);
            Log.d(TAG, "onRewardVerify map = " + map);
        }

        @Override
        public void onSkippedVideo(Map<String, Object> map) {
            super.onSkippedVideo(map);
            Log.d(TAG, "onSkippedVideo map = " + map);
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
