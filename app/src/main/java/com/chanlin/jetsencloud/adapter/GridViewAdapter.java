package com.chanlin.jetsencloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chanlin.jetsencloud.R;

/**
 * Created by ChanLin on 2018/1/9.
 * jetsenCloud
 * TODO:
 */

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private String[][] courses= null;
    public GridViewAdapter(Context context, String[][] Strs){
        this.mContext = context;
        this.courses = Strs;
    }
    @Override
    public int getCount() {
        return courses.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv_cource_name;
        if (convertView == null){
            tv_cource_name = new TextView(mContext);
            tv_cource_name.setBackgroundResource(R.drawable.course_text);
        }else{
            tv_cource_name = (TextView) convertView;
        }
        tv_cource_name.setText(courses[position][1]);
        return tv_cource_name;
    }

}
