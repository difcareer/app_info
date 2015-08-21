package com.andr0day.appinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andr0day
 * on 2015/7/27.
 */
public class CommonAdaptor<T> extends BaseAdapter {
    private List<T> data;
    private LayoutInflater mLayoutInflater;
    private View.OnClickListener clickListener;


    public CommonAdaptor(List<T> data, Context context, View.OnClickListener clickListener) {
        this.data = data;
        mLayoutInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return data != null ? data.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.commonitem, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) convertView.findViewById(R.id.common_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position < data.size()) {
            viewHolder.content.setText(data.get(position).toString());
            viewHolder.content.setOnClickListener(clickListener);
        }
        convertView.setOnClickListener(clickListener);
        return convertView;
    }


    public final class ViewHolder {
        public TextView content;
    }
}
