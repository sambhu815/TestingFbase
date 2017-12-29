package com.fastbase.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fastbase.R;
import com.fastbase.model.WebList;

import java.util.ArrayList;

/**
 * Created by Swapnil.Patel on 17-11-2017.
 */

public class WebListAdapter extends BaseAdapter implements Filterable {
    Activity activity;
    LayoutInflater inflater;
    ArrayList<WebList> webLists;
    ArrayList<WebList> orig;
    WebList web;

    private int mSelectedItem = 0;
    private int TAG_UNSELECTED = 0;
    private int TAG_SELECTED = 1;

    public WebListAdapter(Activity activity, ArrayList<WebList> webLists) {
        this.activity = activity;
        this.webLists = webLists;
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
        return webLists.size();
    }

    @Override
    public Object getItem(int i) {
        return webLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_peroide, viewGroup, false);

        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_pid = (TextView) view.findViewById(R.id.tv_pid);
        TextView tv_aid = (TextView) view.findViewById(R.id.tv_aid);

        web = webLists.get(i);
        tv_time.setText(web.getUrl());
        tv_pid.setText(web.getProfileId());
        tv_aid.setText(web.getAccountId());

        int type = getItemViewType(i);
        if (type == TAG_SELECTED) {
            tv_time.setTextColor(activity.getResources().getColor(R.color.white));
            tv_time.setBackgroundColor(activity.getResources().getColor(R.color.menu_text_bg));
        } else {
            tv_time.setTextColor(activity.getResources().getColor(R.color.text));
        }

        return view;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<WebList> results = new ArrayList<WebList>();
                if (orig == null)
                    orig = webLists;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final WebList g : orig) {
                            if (g.getUrl().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                webLists = (ArrayList<WebList>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
