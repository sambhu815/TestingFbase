package com.fastbase.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fastbase.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.fastbase.support.AppConstant;
import com.fastbase.support.NonScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Swapnil.Patel on 30-11-2017.
 */

public class VisitorAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> visitorList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> pageList;

    JSONArray pages;
    PageAdapter pageAdapter;

    public VisitorAdapter(Activity activity, ArrayList<HashMap<String, String>> visitorList) {
        this.activity = activity;
        this.visitorList = visitorList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return visitorList.size();
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
        view = inflater.inflate(R.layout.row_visitor, viewGroup, false);

        NonScrollListView list_pages = (NonScrollListView) view.findViewById(R.id.list_pages);
        pageList = new ArrayList<>();

        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_day = (TextView) view.findViewById(R.id.tv_day);
        TextView tv_year = (TextView) view.findViewById(R.id.tv_year);

        TextView tv_refer = (TextView) view.findViewById(R.id.tv_refer);
        TextView tv_pageview = (TextView) view.findViewById(R.id.tv_pageview);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);

        final ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);

        setListViewHeightBasedOnChildren(list_pages);
        resultp = visitorList.get(i);

        String str = resultp.get(AppConstant.Tag_Date);
        String[] array = str.split(",");

        try {
            tv_date.setText(array[0]);
            tv_day.setText(array[1]);
            tv_year.setText(array[2] + " " + array[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_refer.setText(resultp.get(AppConstant.Tag_Referrer));
        tv_pageview.setText(resultp.get(AppConstant.Tag_PageViews));
        tv_time.setText(resultp.get(AppConstant.Tag_TimeSpent));

        final String str_img = resultp.get(AppConstant.Tag_ClientImg);
        if (str_img.equals("null") || str_img == null || str_img.isEmpty()) {
            iv_img.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(activity)
                    .load(str_img)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(iv_img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(str_img)
                                    .fit()
                                    .centerCrop()
                                    .error(R.drawable.ic_error)
                                    .into(iv_img);
                        }
                    });

            try {
                pages = new JSONArray(resultp.get("VisitedPages"));
                for (int j = 0; j < pages.length(); j++) {
                    JSONObject object = pages.getJSONObject(j);

                    HashMap<String, String> visitor = new HashMap<String, String>();
                    visitor.put(AppConstant.Tag_Time, object.getString(AppConstant.Tag_Time));
                    visitor.put(AppConstant.Tag_Url, object.getString(AppConstant.Tag_Url));

                    pageList.add(visitor);
                }
                pageAdapter = new PageAdapter(activity, pageList);
                list_pages.setAdapter(pageAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
