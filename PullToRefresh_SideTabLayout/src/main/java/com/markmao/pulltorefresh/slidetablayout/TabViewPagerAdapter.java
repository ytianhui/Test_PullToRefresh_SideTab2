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

    private String mTabTitle[] = new String[]{"Latest Ideas", "Most Voted Ideas","My Upload Ideas"};
    private Context mContext;

    public TabViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                return Fragment_Latest.newInstance(position);
            case 1:
                return Fragment_Good.newInstance(position);
            case 2:
                return Fragment_Mine.newInstance(position);
            default:
                return Fragment_Latest.newInstance(position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitle[position];
    }
}
