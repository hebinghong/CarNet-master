package com.rtech.carnet.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;
import com.rtech.carnet.adapter.CarAdapter;

import java.util.ArrayList;
import java.util.List;

public class CarListActivity extends AppCompatActivity {

    private CarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        RecyclerView list = ((RecyclerView) findViewById(R.id.car_list));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(true);
        adapter = new CarAdapter(this, new ArrayList<AVObject>());
        list.setAdapter(adapter);

        ((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCars();
            }
        });
        loadCars();

        findViewById(R.id.add_car).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarInfoActivity.car = null;
                startActivity(new Intent(CarListActivity.this, CarInfoActivity.class));
            }
        });
    }

    private void loadCars() {
        ((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setRefreshing(true);
        ((TextView) findViewById(R.id.car_message)).setText("");
        AVQuery<AVObject> query = AVQuery.getQuery("car");
        query.whereEqualTo("owner", MainApplication.user);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                ((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setRefreshing(false);
                if (e == null) {
                    ArrayList<AVObject> cars = new ArrayList<>();
                    int i = 0;
                    while (i < list.size()) {
                        cars.add(list.get(i));
                        i++;
                    }
                    adapter.cars = cars;
                    adapter.notifyDataSetChanged();
                    if (cars.size() == 0) ((TextView) findViewById(R.id.car_message)).setText("你没有添加任何车辆");
                    else ((TextView) findViewById(R.id.car_message)).setText("");
                }
                else if (adapter.getItemCount() == 0) ((TextView) findViewById(R.id.car_message)).setText("无法连接上网络");
            }
        });
    }
}
