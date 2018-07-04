package com.chuangber.verify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chuangber.verify.R;
import com.chuangber.verify.bean.Machine;

import java.util.List;

/**
 * Created by jinyh on 2018/2/10.
 */

public class MachineAdapter extends BaseAdapter {

    List<Machine> arrayList;
    Context context;
    public MachineAdapter(Context context,List<Machine> arrayList){
        this.arrayList = arrayList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_machine,null);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.item_machine_name);
            viewHolder.textViewOnline = (TextView) convertView.findViewById(R.id.item_machine_online);
            convertView.setTag(viewHolder);
        }else {
           viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewName.setText(arrayList.get(position).getMachine_name());
        boolean online = arrayList.get(position).isMachine_login();
        if (online){
            viewHolder.textViewOnline.setText("在线");
        }

        else viewHolder.textViewOnline.setText("离线");


        return convertView;
    }

    class ViewHolder{
        public TextView textViewName;
        public TextView textViewOnline;
    }

}
