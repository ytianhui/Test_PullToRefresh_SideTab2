package com.markmao.pulltorefresh.slidetablayout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

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
        Log.e("here4", position + "");
        if(position == 1) {
            return BaseFragment_Good.newInstance(position);
        }
        return BaseFragment_Latest.newInstance(position);
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
