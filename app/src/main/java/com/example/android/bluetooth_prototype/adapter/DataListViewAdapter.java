package com.example.android.bluetooth_prototype.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.android.bluetooth_prototype.R;
import com.example.android.bluetooth_prototype.model.TemperatureData;
import com.example.android.bluetooth_prototype.util.DateUtil;

import org.w3c.dom.Text;

import java.util.List;

public class DataListViewAdapter extends BaseAdapter {

    private List<TemperatureData> dataList;

    public DataListViewAdapter(List<TemperatureData> dataList){
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Context context = parent.getContext();
        if(convertView == null){
            LayoutInflater inflter = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflter.inflate(R.layout.data_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTempData = (TextView) convertView.findViewById(R.id.tvTempData);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TemperatureData temperatureData = dataList.get(i);

        viewHolder.tvTempData.setText("날짜:" + temperatureData.getDateTime() +" 현재온도: " + temperatureData.getCurrentTemp() + " 설정온도: " + temperatureData.getRefTemp());
        return convertView;
    }

    static class ViewHolder {
        TextView tvTempData;
    }
}
