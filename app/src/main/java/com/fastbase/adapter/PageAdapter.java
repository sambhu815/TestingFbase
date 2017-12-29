package com.fastbase.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fastbase.R;
import com.fastbase.support.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Swapnil.Patel on 30-11-2017.
 */

public class PageAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> pageList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public PageAdapter(Activity activity, ArrayList<HashMap<String, String>> pageList) {
        this.activity = activity;
        this.pageList = pageList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pageList.size();
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
        view = inflater.inflate(R.layout.row_page_visited, viewGroup, false);

        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_url = (TextView) view.findViewById(R.id.tv_url);

        resultp = pageList.get(i);

        tv_time.setText(resultp.get(AppConstant.Tag_Time));
        tv_url.setText(resultp.get(AppConstant.Tag_Url));

        return view;
    }
}
