package com.rtech.carnet.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;
import com.rtech.carnet.dialog.DialogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapActivity extends AppCompatActivity {

    private Marker me;
    private HashMap<Marker, String> markers;

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(MapActivity.this, "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(MapActivity.this, "网络不同导致定位失败，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(MapActivity.this, "" + location.getLocType() + "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机", Toast.LENGTH_SHORT).show();
            }
            double v = location.getLatitude(), v1 = location.getLongitude();
            if (me == null) {
                me = createBaiduMarker(v, v1, location.getAddrStr(), "我", R.drawable.icon_mark);
                local(v, v1);
                searchPOI(v, v1);
            }
        }
    }

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);

        markers = new HashMap<>();
        initLocation();
        //mLocationClient.start();

        //对 marker 添加点击相应事件
        BaiduMap mBaidumap = ((MapView) findViewById(R.id.bmapView)).getMap();
        mBaidumap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker arg0) {
                DialogHelper.showCommonDialog(MapActivity.this, "预约消息", "你希望预约到 " + markers.get(arg0) + " 加油吗", "预约并导航至加油站", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AVObject object = new AVObject("order");
                        object.put("user", MainApplication.user);
                        object.put("addr", markers.get(arg0));
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) Toast.makeText(MapActivity.this, "预约信息已经发出", Toast.LENGTH_SHORT).show();
                                else Toast.makeText(MapActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                return false;
            }
        });
    }

    private void initLocation(){

        BaiduMap mBaiduMap = ((MapView) findViewById(R.id.bmapView)).getMap();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);


        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
        mLocationClient.start();


    }

    public void searchPOI(double v, double v1) {

        //poi
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                //获取POI检索结果
                if (result == null || result.getAllPoi() == null) return;
                int i = 0;
                int size = result.getAllPoi().size();
                while (i < size) {
                    LatLng point = result.getAllPoi().get(i).location;
//构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_focus_mark);
//构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
//在地图上添加Marker，并显示
                    Marker marker = (Marker)((MapView) findViewById(R.id.bmapView)).getMap().addOverlay(option);
                    markers.put(marker, result.getAllPoi().get(i).address + "的 " + result.getAllPoi().get(i).name);
                    i++;
                }
            }
            public void onGetPoiDetailResult(PoiDetailResult result){
            }
        };
        PoiSearch mPoiSearch;
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearch.searchNearby(new PoiNearbySearchOption().radius(20000).location(new LatLng(v, v1)).keyword("加油站").sortType(PoiSortType.distance_from_near_to_far));
        //mPoiSearch.searchInCity(new PoiCitySearchOption().city("南宁").keyword("加油站").pageNum(2));
    }

    @Override
    protected void onDestroy() {
        MapView view = ((MapView) findViewById(R.id.bmapView));
        if (view != null) view.onDestroy();
        super.onDestroy();
        mLocationClient.stop();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        ((MapView) findViewById(R.id.bmapView)).onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        ((MapView) findViewById(R.id.bmapView)).onPause();
    }

    private void local(double v, double v1) {
        BaiduMap mBaidumap = ((MapView) findViewById(R.id.bmapView)).getMap();
        LatLng cenpt = new LatLng(v,v1);
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaidumap.setMapStatus(mMapStatusUpdate);
    }

    private Marker createBaiduMarker(double v, double v1, String address, String title, int bitmap) {
        LatLng point = new LatLng(v, v1);
//构建Marker图标
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(bitmap);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmapDescriptor).title(title);
//在地图上添加Marker，并显示
        Marker marker = (Marker)((MapView) findViewById(R.id.bmapView)).getMap().addOverlay(option);
        markers.put(marker, address);
        return marker;
    }

    private void initNavi() {
        BaiduNaviManager.getInstance().init(this, Environment.getExternalStorageDirectory().getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath()+"/carnet",
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                    }

                    public void initSuccess() {
                        Toast.makeText(MapActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                    }

                    public void initStart() {
                        Toast.makeText(MapActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    public void initFailed() {
                        Toast.makeText(MapActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }
                }, null /*mTTSCallback*/);
    }


}
