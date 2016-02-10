package com.dario.tiempo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dario.tiempo.models.NextDaysForecast;
import com.dario.tiempo.R;
import com.dario.tiempo.helper.formater;

import java.util.List;

/**
 * Created by dario on 2016-01-18.
 */
public class ListViewAdapter extends ArrayAdapter<NextDaysForecast> {

    private static final String TAG = "==>" + ListViewAdapter.class.getSimpleName();

    private Context mContext;
    private List<NextDaysForecast> mForecastList;

    public ListViewAdapter(Context context, int resource, List<NextDaysForecast> objects) {
        super(context, resource, objects);

        mContext = context;
        mForecastList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View rowView = inflater.inflate(R.layout.list_item_forecast, parent, false);
        NextDaysForecast dayForecast = mForecastList.get(position);
        View rowView = convertView;
        ViewHolder holder = null;
        if (rowView == null) {
            holder = new ViewHolder();
            rowView = inflater.inflate(R.layout.list_item_forecast, parent, false);
            holder.date = (TextView) rowView.findViewById(R.id.date_next_days);
            holder.wind = (TextView) rowView.findViewById(R.id.wind_value_next_days);
            holder.pressure = (TextView) rowView.findViewById(R.id.pressure_next_days);
            holder.description = (TextView) rowView.findViewById(R.id.description_next_days);
            holder.max = (TextView) rowView.findViewById(R.id.max_temp_next_days);
            holder.min = (TextView) rowView.findViewById(R.id.min_temp_next_days);
            holder.clouds = (TextView) rowView.findViewById(R.id.clouds_value_next_days);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder)rowView.getTag();
        }
        holder.date.setText(dayForecast.getDay());
        holder.clouds.setText(formater.formatClouds(dayForecast.getClouds()));
        holder.wind.setText(formater.formatWind(dayForecast.getWind()));
        holder.pressure.setText(formater.formatPressure(dayForecast.getPressure()));
        holder.description.setText(dayForecast.getDescription());
        holder.max.setText(formater.formatTemperature(dayForecast.getMax(),dayForecast.getUnits()));
        holder.min.setText(formater.formatTemperature(dayForecast.getMin(),dayForecast.getUnits()));
        return rowView;
    }

    static class ViewHolder {
        TextView date;
        TextView wind;
        TextView pressure;
        TextView description;
        TextView max;
        TextView min;
        TextView clouds;
    }


}
