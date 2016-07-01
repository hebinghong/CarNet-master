package com.rtech.carnet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.rtech.carnet.R;
import com.rtech.carnet.activity.CarInfoActivity;

import java.util.ArrayList;

/**
 *
 * Created by zhimeng on 2016/4/26.
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View view;
        public TextView carName, bad;

        public ViewHolder(View v) {
            super(v);
            view = v;
            carName = (TextView)v.findViewById(R.id.car_name);
            bad = (TextView)v.findViewById(R.id.bad);
        }

    }

    private Context context;
    public ArrayList<AVObject> cars;

    public CarAdapter(Context context, ArrayList<AVObject> cars) {
        this.context = context;
        this.cars = cars;
    }

    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CarAdapter.ViewHolder(LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_car, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.carName.setText(cars.get(position).getString("cph"));
        holder.bad.setText("");
        if (cars.get(position).getInt("qyl") < 20) holder.bad.setText("油量太少\n");
        if (cars.get(position).getInt("lcs") > 15000) holder.bad.setText(holder.bad.getText() + "里程数过大，需要检查汽车\n");
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarInfoActivity.car = cars.get(position);
                CarInfoActivity.position = position;
                context.startActivity(new Intent(context, CarInfoActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

}
