package com.rtech.carnet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.rtech.carnet.R;
import com.rtech.carnet.activity.CarInfoActivity;

import java.util.ArrayList;

/**
 *
 * Created by zhimeng on 2016/5/31.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View view;
        public TextView addr;
        public View button;

        public ViewHolder(View v) {
            super(v);
            view = v;
            addr = (TextView)v.findViewById(R.id.text);
            button = v.findViewById(R.id.button);
        }

    }

    private Context context;
    public ArrayList<AVObject> orders;

    public OrderAdapter(Context context, ArrayList<AVObject> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.addr.setText(orders.get(position).getString("addr"));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orders.get(position).deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            notifyItemRemoved(position);
                            orders.remove(position);
                        }
                        else Toast.makeText(context, "联网失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

}
