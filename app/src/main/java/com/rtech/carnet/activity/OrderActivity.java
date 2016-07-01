package com.rtech.carnet.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;
import com.rtech.carnet.adapter.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        RecyclerView list = ((RecyclerView) findViewById(R.id.order_list));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(true);
        adapter = new OrderAdapter(this, new ArrayList<AVObject>());
        list.setAdapter(adapter);
        load();
    }

    private void load() {
        ((TextView) findViewById(R.id.msg)).setText("");
        AVQuery<AVObject> query = AVQuery.getQuery("order");
        query.whereEqualTo("user", MainApplication.user);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    ArrayList<AVObject> cars = new ArrayList<>();
                    int i = 0;
                    while (i < list.size()) {
                        cars.add(list.get(i));
                        i++;
                    }
                    adapter.orders = cars;
                    adapter.notifyItemRangeInserted(0, cars.size());
                    if (cars.size() == 0) ((TextView) findViewById(R.id.msg)).setText("你没有任何预约信息");
                    else ((TextView) findViewById(R.id.msg)).setText("");
                }
                else if (adapter.getItemCount() == 0) ((TextView) findViewById(R.id.msg)).setText("无法连接上网络");
            }
        });
    }
}
