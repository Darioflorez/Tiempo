package com.dario.tiempo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dario.tiempo.R;
import com.dario.tiempo.helper.formater;
import com.dario.tiempo.models.TodayForecast;

import java.util.List;

/**
 * Created by dario on 2016-03-02.
 */
public class SearchResultListAdapter extends ArrayAdapter<TodayForecast> {

    private static final String TAG = "==>" + SearchResultListAdapter.class.getSimpleName();

    private Context mContext;
    private List<TodayForecast> mList;

    public SearchResultListAdapter(Context context, int resource, List<TodayForecast> objects) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View rowView = inflater.inflate(R.layout.list_item_forecast, parent, false);
        TodayForecast city = mList.get(position);
        View rowView = convertView;
        ViewHolder holder = null;
        if (rowView == null) {
            holder = new ViewHolder();
            rowView = inflater.inflate(R.layout.search_item, parent, false);
            holder.city = (TextView) rowView.findViewById(R.id.search_item_city);
            holder.temp = (TextView) rowView.findViewById(R.id.search_item_temp);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder)rowView.getTag();
        }
        holder.city.setText(city.getLocation());
        holder.temp.setText(formater.formatTemperature(city.getTemp(), city.getUnits()));
        return rowView;
    }


    static class ViewHolder {
        TextView city;
        TextView temp;
    }

}
