package com.markmao.pulltorefresh.slidetablayout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 简单实例
 * @author fyales
 */
public class TabViewPagerAdapter extends FragmentPagerAdapter {

    private String mTabTitle[] = new String[]{"Latest Ideas", "Most Voted Ideas"};
    private Context mContext;

    public TabViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return BaseFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitle[position];
    }
}
