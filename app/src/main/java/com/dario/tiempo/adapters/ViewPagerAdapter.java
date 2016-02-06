package com.dario.tiempo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.dario.tiempo.NextDaysForecast;
import com.dario.tiempo.R;
import com.dario.tiempo.TodayForecast;
import com.dario.tiempo.fragments.TodayTab;
import com.dario.tiempo.fragments.NextDaysTab;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dario on 2015-12-22.
 */
public class ViewPagerAdapter  extends FragmentPagerAdapter{

    private static final String TAG = "==> " + ViewPagerAdapter.class.getSimpleName();

    private TodayTab todayFragment;
    private NextDaysTab nextDaysFragment;

    private CharSequence mTitles[]; //Store the Titles of the Tabs
    private int mNumbOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int numbOfTabs) {
        super(fm);
        mTitles = titles;
        mNumbOfTabs = numbOfTabs;
        todayFragment = new TodayTab(); //Create a fragment (page)
        nextDaysFragment = new NextDaysTab(); //Create a fragment (page)
        Log.d(TAG, "VIewPagerAdapter Created!");
    }

    //return the fragment for the position passed
    @Override
    public Fragment getItem(int position) {

        Log.d(TAG, "TAB POSITION: " + position);
        switch (position){
            case 0:
                return todayFragment; //return the page.
            case 1:
                return nextDaysFragment; //return the page.
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }

    public void updateFragments(HashMap<Integer, Object> weatherData){
        if (weatherData == null){
            Log.wtf(TAG, "Weather data null!!!");
            return;
        }

        if(todayFragment == null){
            Log.wtf(TAG, "today fragment null!!!");
            return;
        }
        todayFragment.updateUI((TodayForecast)weatherData.get(R.string.today));
        nextDaysFragment.updateUI((List<NextDaysForecast>) weatherData.get(R.string.next_days));
    }
}
