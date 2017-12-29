package com.fastbase.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fastbase.R;
import com.fastbase.model.Contact;
import com.fastbase.model.WebList;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Swapnil.Patel on 23-11-2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {
    Context context;
    List<Contact> contactList;
    List<Contact> fileter;
    private static int currentPosition = 0;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        this.context = context;
        this.fileter = contactList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        final Contact contact = fileter.get(position);

        holder.tv_name.setText(contact.getName());
        holder.tv_designation.setText(contact.getDesignation());

        holder.tv_detail_name.setText(contact.getName());
        holder.tv_info.setText(contact.getDesignation());

        holder.tv_current.setText(contact.getCurrent());
        holder.tv_previous.setText(contact.getPrevious());
        holder.tv_education.setText(contact.getEducation());

        final String str_img = contact.getImg();
        if (str_img.equals("null") || str_img == null || str_img.isEmpty()) {
            holder.iv_profile.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context)
                    .load(str_img)
                    .into(holder.iv_profile);
        }

        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = position;
                if (currentPosition == position) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                    holder.lin_details.setVisibility(View.VISIBLE);
                    holder.lin_details.startAnimation(animation);
                }
            }
        });
        holder.iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = position;
                if (currentPosition == position) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                    holder.lin_details.setVisibility(View.GONE);
                    holder.lin_details.startAnimation(animation);
                }
            }
        });

        holder.iv_linkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_VIEW);
                in.setData(Uri.parse(contact.getLinkedInUrl()));
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileter.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_designation, tv_save, tv_send, tv_detail_name, tv_info, tv_current, tv_education, tv_previous;
        ImageView iv_linkin, iv_close;
        CircleImageView iv_profile;
        LinearLayout lin_details;

        public ContactViewHolder(View view) {
            super(view);

            iv_profile = (CircleImageView) view.findViewById(R.id.iv_profile);
            iv_linkin = (ImageView) view.findViewById(R.id.iv_linkin);
            iv_close = (ImageView) view.findViewById(R.id.iv_close);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_designation = (TextView) view.findViewById(R.id.tv_designation);

            tv_save = (TextView) view.findViewById(R.id.tv_save);
            tv_send = (TextView) view.findViewById(R.id.tv_send);
            tv_detail_name = (TextView) view.findViewById(R.id.tv_detail_name);
            tv_info = (TextView) view.findViewById(R.id.tv_info);

            tv_current = (TextView) view.findViewById(R.id.tv_current);
            tv_previous = (TextView) view.findViewById(R.id.tv_previous);
            tv_education = (TextView) view.findViewById(R.id.tv_education);

            lin_details = (LinearLayout) view.findViewById(R.id.lin_details);
        }
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    fileter = contactList;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : contactList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    fileter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = fileter;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                fileter = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
