package com.fastbase.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fastbase.R;

import java.util.List;

/**
 * Created by Swapnil.Patel on 17-11-2017.
 */

public class PeriodeAdapter extends BaseAdapter {
    Activity activity;
    List<String> fruits_list;
    LayoutInflater inflater;

    private int mSelectedItem = 0;
    private int TAG_UNSELECTED = 0;
    private int TAG_SELECTED = 1;

    public PeriodeAdapter(Activity activity, List<String> fruits_list) {
        this.activity = activity;
        this.fruits_list = fruits_list;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    public void selectItem(int position) {
        mSelectedItem = position;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mSelectedItem ? TAG_SELECTED : TAG_UNSELECTED;
    }

    @Override
    public int getCount() {
        return fruits_list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_peroide, viewGroup, false);

        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);

        tv_time.setText(fruits_list.get(i).toString());

        int type = getItemViewType(i);
        if (type == TAG_SELECTED) {
            tv_time.setTextColor(activity.getResources().getColor(R.color.white));
            tv_time.setBackgroundColor(activity.getResources().getColor(R.color.menu_text_bg));
        } else {
            tv_time.setTextColor(activity.getResources().getColor(R.color.text));
        }

        return view;
    }
}
