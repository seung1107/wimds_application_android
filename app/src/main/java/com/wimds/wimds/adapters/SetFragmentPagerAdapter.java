package com.wimds.wimds.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wimds.wimds.activities.Fragment_home;
import com.wimds.wimds.activities.Fragment_map;
import com.wimds.wimds.activities.Fragment_setting;



public class SetFragmentPagerAdapter extends SmartFragmentStatePagerAdapter {
    final int PAGE_COUNT = 3; //탭 개수
    private String tabTitles[] = new String[] { "홈", "지도 보기", "설정" }; //탭 이름
    Fragment[] fragments = new Fragment[3];

    public SetFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments[0] = new Fragment_home();
        fragments[1] = new Fragment_map();
        fragments[2] = new Fragment_setting();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
