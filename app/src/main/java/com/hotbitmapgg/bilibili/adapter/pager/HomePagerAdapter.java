package com.hotbitmapgg.bilibili.adapter.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hotbitmapgg.bilibili.module.home.attention.HomeAttentionFragment;
import com.hotbitmapgg.bilibili.module.home.bangumi.HomeBangumiFragment;
import com.hotbitmapgg.bilibili.module.home.discover.HomeDiscoverFragment;
import com.hotbitmapgg.bilibili.module.home.drama.DramaTypeListFragment;
import com.hotbitmapgg.bilibili.module.home.live.HomeLiveFragment;
import com.hotbitmapgg.bilibili.module.home.recommend.HomeRecommendedFragment;
import com.hotbitmapgg.bilibili.module.home.region.HomeRegionFragment;
import com.hotbitmapgg.ohmybilibili.R;

/**
 * Created by hcc on 16/8/4 14:12
 * 100332338@qq.com
 * <p/>
 * 主界面Fragment模块Adapter
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

  private final String[] TITLES;
  private final Fragment[] fragments;

  public HomePagerAdapter(FragmentManager fm, Context context) {
    super(fm);
    TITLES = context.getResources().getStringArray(R.array.sections);
    fragments = new Fragment[TITLES.length];
  }


  @Override
  public Fragment getItem(int position) {
    if (fragments[position] == null) {
      if (position == 0) {
        fragments[0] = HomeRecommendedFragment.newInstance();
        return fragments[0];
      }

      if (position == 6) {
        fragments[6] = HomeRegionFragment.newInstance();
        return fragments[6];
      }

      fragments[position] = DramaTypeListFragment.newInstance(TITLES[position]);
      return fragments[position];
    }
    return fragments[position];
  }


  @Override
  public int getCount() {
    return TITLES.length;
  }


  @Override
  public CharSequence getPageTitle(int position) {
    return TITLES[position];
  }
}
