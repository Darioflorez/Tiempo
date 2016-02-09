package com.dario.tiempo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dario.tiempo.models.NextDaysForecast;
import com.dario.tiempo.R;
import com.dario.tiempo.adapters.ListViewAdapter;
import com.dario.tiempo.helper.formater;

import java.util.Date;
import java.util.List;

/**
 * Created by dario on 2015-12-22.
 */
public class NextDaysTab extends Fragment {

    private static final String TAG = "==> " + NextDaysTab.class.getSimpleName();
    private ListView listView;
    private TextView location;
    private TextView date;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "NextDayTab created!");
        context = getActivity();
        View view = inflater.inflate(R.layout.tab_netx_days,container,false);
        listView = (ListView)view.findViewById(R.id.listview_forecast);
        date = (TextView) view.findViewById(R.id.date_value_next_days);
        location = (TextView) view.findViewById(R.id.location_next_days_value);
        return view;
    }

    public void updateUI(List<NextDaysForecast> nextDays){
        NextDaysForecast day = nextDays.get(0);
        location.setText(day.getLocation());
        date.setText(formater.formatDate(new Date()));

        ListViewAdapter adapter = new ListViewAdapter(context, R.layout.list_item_forecast, nextDays);
        listView.setAdapter(adapter);
        //Log.d(TAG, "UPDATE NEXT DAYS ADAPTER!");
    }
}
