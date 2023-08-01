package com.hotbitmapgg.bilibili;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

import android.app.Application;
import android.util.Log;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.ILogger;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.UriConfig;
import com.bytedance.applog.util.UriConstants;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPSdkConfig;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by hcc on 16/8/7 21:18
 * 100332338@qq.com
 * <p/>
 * 哔哩哔哩动画App
 */
public class BilibiliApp extends Application {

    public static BilibiliApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
        //initAppLog();
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        /*TTAdSdk.init(this,
                new TTAdConfig.Builder()
                        .appId("5413164")//xxxxxxx为穿山甲媒体平台注册的应用ID
                        .useTextureView(true) //默认使用SurfaceView播放视频广告,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName("APP测试媒体")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)//落地页主题
                        .allowShowNotify(true) //是否允许sdk展示通知栏提示,若设置为false则会导致通知栏不显示下载进度
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        //.customController()隐私信息控制开关
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                        .supportMultiProcess(false) //是否支持多进程，true支持
                        .asyncInit(true) //是否异步初始化sdk,设置为true可以减少SDK初始化耗时。3450版本开始废弃~~
                        //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                        .build(), new TTAdSdk.InitCallback() {
                    @Override
                    public void success() {
                        Log.i(TAG, "success: ad adk init success");
                    }

                    @Override
                    public void fail(int i, String s) {
                        Log.e(TAG, "fail: ad adk init failed, error code is: " + i + ", message is: " + s);
                    }
                });*/
        //随便哪里可以调用了
        //TTAdManager ttAdManager = TTAdSdk.getAdManager();

        //短剧SDK
        //DPSdkConfig.Builder configBuilder = new DPSdkConfig.Builder().debug(true);
        //TODO JSON文件需要从平台下载
        //DPSdk.init(this, "SDK_Setting_5175152.json", configBuilder.build());

        /*DPSdk.start(new DPSdk.StartListener() {
            @Override
            public void onStartComplete(boolean isSuccess, String s) {

                        //请确保使用时Sdk已经成功启动
                        //isSuccess=true表示启动成功
                        //启动失败，可以再次调用启动接口（建议最多不要超过3次)
                        //isDPStarted = isSuccess;
                Log.e(TAG, "start result=$isSuccess, msg=$s");
                //Bus.getInstance().sendEvent(DPStartEvent(isSuccess))
            }
        });*/
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

        InitConfig config = new InitConfig("appid", "hhhh"); //TODO 赋能平台的appid，自定义渠道号
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
