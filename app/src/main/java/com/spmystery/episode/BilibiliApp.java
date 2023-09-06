package com.spmystery.episode;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import com.bytedance.applog.AppLog;
import com.bytedance.applog.ILogger;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.UriConfig;
import com.bytedance.applog.util.UriConstants;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPSdkConfig;
import com.bytedance.sdk.dp.IDPPrivacyController;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.common.adapter.util.Logger;
import io.dcloud.common.util.RuningAcitvityUtil;
import io.dcloud.feature.sdk.DCSDKInitConfig;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.Interface.IDCUniMPPreInitCallback;
import io.dcloud.feature.sdk.Interface.IMenuButtonClickCallBack;
import io.dcloud.feature.sdk.MenuActionSheetItem;

/**
 * Created by hcc on 16/8/7 21:18
 * 100332338@qq.com
 * <p/>
 * 哔哩哔哩动画App
 */
public class BilibiliApp extends Application {

    public static BilibiliApp mInstance;

    public static final String TAG = "BilibiliApp";

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private String getCurrentProcessName() {
        int pid = Process.myPid();
        String processName = "";
        ActivityManager manager =
                (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();

        // 非小程序进程（这里的unimp 关键字 可以根据宿主的具体情况进行调整）
        if(!RuningAcitvityUtil.getAppName(getBaseContext()).contains("unimp")) {
            //请在此处初始化其他三方SDK
            initAppLog();

            boolean isMainProcess = getApplicationContext().getPackageName().equals(getCurrentProcessName());
            initADSDK(this, "5413164", isMainProcess);
        }

        //initDCloudPm();
    }

    private void initDCloudPm() {
        MenuActionSheetItem item = new MenuActionSheetItem("关于", "gy");

        MenuActionSheetItem item1 = new MenuActionSheetItem("获取当前页面url", "hqdqym");
        MenuActionSheetItem item2 = new MenuActionSheetItem("跳转到宿主原生测试页面", "gotoTestPage");
        List<MenuActionSheetItem> sheetItems = new ArrayList<>();
        sheetItems.add(item);
        sheetItems.add(item1);
        sheetItems.add(item2);
        Log.i("unimp","onCreate----");
        DCSDKInitConfig config = new DCSDKInitConfig.Builder()
                .setCapsule(false)
                .setMenuDefFontSize("16px")
                .setMenuDefFontColor("#ff00ff")
                .setMenuDefFontWeight("normal")
                .setMenuActionSheetItems(sheetItems)
                .setEnableBackground(true)//开启后台运行
                .setUniMPFromRecents(false)
                .build();
        DCUniMPSDK instance = DCUniMPSDK.getInstance();
        //设置setDefMenuButtonClickCallBack监听默认菜单按钮点击事件。
        //uni小程序运行在独立进程中。所以小程序中的内存与宿主是不会共享的。在点击事件处理上请注意！！！
        instance.setDefMenuButtonClickCallBack(new IMenuButtonClickCallBack() {
                    @Override
                    public void onClick(String appid, String id) {
                        switch (id) {
                            case "gy":{
                                Logger.e(appid+"用户点击了关于");
                            }
                        }
                    }
                });
        instance.initialize(this, config, new IDCUniMPPreInitCallback() {
            @Override
            public void onInitFinished(boolean b) {
                Log.d("unimpaa","onInitFinished----"+b);
            }
        });
        //初始化 uni小程序SDK ----end----------
    }



    private void initADSDK(Context context, String siteId, Boolean isMainProcess) {
        initAdSdk(context, siteId, isMainProcess);
        /*if (isOppo()) {
            initOppoAdSdk(context, siteId, isMainProcess);
        } else {
            initAdSdk(context, siteId, isMainProcess);
        }*/
    }

    /**
     * 初始化广告sdk
     */
    private void initAdSdk(Context context, String siteId, Boolean isMainProcess) {
        /**初始化穿山甲sdk, 这里不能仅在主进程中初始化，还需要在miniapp进程中初始化 */
        TTAdConfig build = new TTAdConfig.Builder().appId(siteId) //穿山甲媒体id
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName("pangolin_demo")
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(
                        TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G
                ) //允许直接下载的网络状态集合
                /**设置支持多进程 */
                .supportMultiProcess(true) //是否支持多进程，此处必须为true
                .needClearTaskReset() //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build();
        TTAdSdk.init(context, build,  new TTAdSdk.InitCallback() {
            @Override
            public void success() {
                //穿山甲SDK是异步初始化，必须在、初始化成功后初始化广告SDK
                initDPCallback(isMainProcess);
                Log.i(TAG, "success: ad adk init success");
            }

            @Override
            public void fail(int i, String s) {
                Log.e(TAG, "fail: ad adk init failed, error code is: " + i + ", message is: " + s);
            }
        });
    }

    private void initDPCallback(boolean isMainProcess) {
        if (isMainProcess) {
            //dp
            initDp(this);
//                initNovel() HomeActivity中初始化
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void initDp(Application application) {

        SharedPreferences sp_dpsdk = application.getSharedPreferences("sp_dpsdk", Context.MODE_PRIVATE);
        boolean isXlFont = sp_dpsdk != null && sp_dpsdk.getBoolean("sp_key_xl_font", false);

        //1. 初始化，最好放到application.onCreate()执行
        DPSdkConfig.Builder configBuilder = new DPSdkConfig.Builder()
                .debug(true)
                .disableABTest(false)
                .newUser(false)
                .aliveSeconds(0)
//            .interestType(10)
                .fontStyle(isXlFont ? DPSdkConfig.ArticleDetailListTextStyle.FONT_XL : DPSdkConfig.ArticleDetailListTextStyle.FONT_NORMAL);
        configBuilder.luckConfig(new DPSdkConfig.LuckConfig().application(application).enableLuck(false));

        DPSdkConfig config = configBuilder.build();

        // 配置青少年模式，可选
        config.setPrivacyController(new IDPPrivacyController() {
            @Override
            public boolean isTeenagerMode() {
                return false;
            }
        });
        DPSdk.init(application, "SDK_Setting_5413164.json", config);
        DPSdk.start(new DPSdk.StartListener() {
            @Override
            public void onStartComplete(boolean isSuccess, String s) {

                //请确保使用时Sdk已经成功启动
                //isSuccess=true表示启动成功
                //启动失败，可以再次调用启动接口（建议最多不要超过3次)
                //isDPStarted = isSuccess;
                Log.e(TAG, "start result=" + isSuccess + ", msg=" + s);
                //Bus.getInstance().sendEvent(DPStartEvent(isSuccess))
            }
        });
    }

    /**
     * 初始化oppo商店版广告sdk
     */
    /*private void initOppoAdSdk(Context context, String siteId, Boolean isMainProcess) {
        *//**初始化穿山甲sdk, 这里不能仅在主进程中初始化，还需要在miniapp进程中初始化 *//*
        TTVfConfig build = new TTVfConfig.Builder().appId(siteId) //穿山甲媒体id
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName("pangolin_demo").titleBarTheme(TTVfConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(
                        TTVfConstant.NETWORK_STATE_WIFI, TTVfConstant.NETWORK_STATE_3G
                ) //允许直接下载的网络状态集合
                *//**设置支持多进程 *//*
                .supportMultiProcess(true) //是否支持多进程，此处必须为true
                .needClearTaskReset() //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build();
        TTVfSdk.init(context, build, new TTVfSdk.InitCallback() {
            @Override
            public void success() {
                //穿山甲SDK是异步初始化，必须在、初始化成功后初始化广告SDK
                initDPCallback(isMainProcess);
                Log.i(TAG, "success: ad adk init success");
            }

            @Override
            public void fail(int i, String s) {
                Log.e(TAG, "fail: ad adk init failed, error code is: " + i + ", message is: " + s);
            }
        });
    }*/

    private boolean isOppo() {
        String className = "com.bykv.vk.openvk.TTVfNative";
        try { // 反射判断是否存在oppo版本穿山甲 SDK
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    private void init() {
        //初始化Leak内存泄露检测工具
        LeakCanary.install(this);
        //初始化Stetho调试工具
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    private void initAppLog() {
        /**初始化Applog */

        InitConfig config = new InitConfig("516510", "hhhh"); //TODO 赋能平台的appid，自定义渠道号
        UriConfig uriConfig = UriConfig.createUriConfig(UriConstants.DEFAULT);
        config.setUriConfig(uriConfig);

        config.setLogger(new ILogger() {
            @Override
            public void log(String s, Throwable throwable) {

            }
        });

        config.setAbEnable(true);// 开启ABTest
        config.setAutoStart(true);
        //字节tob applog
        AppLog.init(this, config);
    }

    public static BilibiliApp getInstance() {
        return mInstance;
    }

}
