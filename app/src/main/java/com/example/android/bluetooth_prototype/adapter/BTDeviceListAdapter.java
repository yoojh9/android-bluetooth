package com.example.android.bluetooth_prototype.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bluetooth_prototype.bluetooth.BTDevice;
import com.example.android.bluetooth_prototype.R;

import java.util.List;

public class BTDeviceListAdapter extends BaseAdapter {

    private List<BTDevice> btDevices;

    private LayoutInflater mInflater;

    public BTDeviceListAdapter(Context context, List<BTDevice> btDevices){
        this.btDevices = btDevices;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return btDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return btDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.device_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(btDevices.get(i).getName());
        viewHolder.tvAddress.setText(btDevices.get(i).getAddress());

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvAddress;
    }
}
