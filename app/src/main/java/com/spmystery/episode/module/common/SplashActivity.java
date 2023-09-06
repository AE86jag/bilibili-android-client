package com.spmystery.episode.module.common;

import android.content.Intent;
import android.os.Bundle;

import com.spmystery.episode.module.home.index.BottomTabLayoutActivity;
import com.spmystery.episode.utils.SystemUiVisibilityUtil;
import com.spmystery.episode.R;
import com.trello.rxlifecycle.components.RxActivity;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by hcc on 16/8/7 14:12
 * 100332338@qq.com
 * <p/>
 * 启动页界面
 */
public class SplashActivity extends RxActivity {
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bind = ButterKnife.bind(this);
        SystemUiVisibilityUtil.hideStatusBar(getWindow(), true);
        setUpSplash();
    }


    private void setUpSplash() {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> finishTask());
    }


    private void finishTask() {
        startActivity(new Intent(SplashActivity.this, BottomTabLayoutActivity.class));
        /*
        boolean isLogin = PreferenceUtil.getBoolean(ConstantUtil.KEY, false);
        if (isLogin) {
            startActivity(new Intent(SplashActivity.this, BottomTabLayoutActivity.class));
        } else {
            //TODO 从登录页跳转到首页不会有底部TAB
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }*/
        SplashActivity.this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
