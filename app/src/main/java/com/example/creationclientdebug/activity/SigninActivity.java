package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.debug.ToastUtil;
import com.example.loginregiste.R;
import com.henu.entity.Group;
import com.henu.entity.Signin;
import com.henu.entity.User;
import com.henu.poxy.SigninServicePoxy;
import com.henu.service.SigninService;

public class SigninActivity extends AppCompatActivity {

    public static void startActivity(Context c, User user, Group group){
        
        Intent i = new Intent(c,SigninActivity.class);
        i.putExtra("user",user);
        i.putExtra("group",group);
        i.putExtra("signal",LAUNCHER);
        c.startActivity(i);
    }

    public static void startActivity(Context c,Signin signin){
        Intent i = new Intent(c,SigninActivity.class);
        i.putExtra("signin",signin);
        i.putExtra("signal",SIGNINIER);
        c.startActivity(i);
    }

    public static final int LAUNCHER = 14345;
    public static final int SIGNINIER = 34645;

    private Button bLaunchSignin,bSignin,bRejSignin;

    private MapView mapView;

    private BaiduMap mBaiduMap;

    private boolean isFirst = true;

    private LocationClient mLocationClient;

    private double lat;

    private double lon;

    private User user;

    private Group group;

    private int signal;

    private Signin signinSponser;

    private MyBtnClickListener btnListener = new MyBtnClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_signin);

        mapView = findViewById(R.id.map_view);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        lunchLocation();

        user = (User) getIntent().getSerializableExtra("user");
        group = (Group) getIntent().getSerializableExtra("group");
        signal = getIntent().getIntExtra("signal",0);
        signinSponser = (Signin) getIntent().getSerializableExtra("signin");
        launchLayout();
    }

    private void launchLayout(){
        if(signal == LAUNCHER){
            bLaunchSignin = findViewById(R.id.btn_launch_signin);
            bLaunchSignin.setOnClickListener(btnListener);
            bLaunchSignin.setVisibility(View.VISIBLE);
        }else if(signal == SIGNINIER){
            bSignin = findViewById(R.id.btn_signin);
            bSignin.setOnClickListener(btnListener);
            bSignin.setVisibility(View.VISIBLE);

            bRejSignin = findViewById(R.id.btn_rej_signin);
            bRejSignin.setOnClickListener(btnListener);
            bRejSignin.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 添加其他坐标
     */
    private void addMarker(double lat,double lon){
        LatLng point = new LatLng(lat,lon);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(icon)
                .draggable(true)
                .flat(true)
                .perspective(true);
        Marker m = (Marker) mBaiduMap.addOverlay(option);
        Bundle b = new Bundle();
        b.putInt("listIndex",19878);
        m.setExtraInfo(b);
    }

    /**
     * 设置缩放级别
     */
    private void setZoom(){
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 配置定位
     */
    private void setMapCfg(){
        //LocationMode
        MyLocationConfiguration.LocationMode compass = MyLocationConfiguration.LocationMode.COMPASS;
        MyLocationConfiguration.LocationMode normal = MyLocationConfiguration.LocationMode.NORMAL;
        MyLocationConfiguration.LocationMode following = MyLocationConfiguration.LocationMode.FOLLOWING;
        //精度圈颜色
        int fillColor = 0xAAffff88;
        //精度圈边框颜色
        int strokeColor = 0xaa00ff00;

        MyLocationConfiguration cfg;
        if (!isFirst){
            cfg = new MyLocationConfiguration(normal,true,null,fillColor,strokeColor);
        }else{
            isFirst = false;
            cfg = new MyLocationConfiguration(following,true,null,fillColor,strokeColor);
            setZoom();
        }
        mBaiduMap.setMyLocationConfiguration(cfg);
    }

    /**
     * 发起定位
     */
    public void lunchLocation(){
        mLocationClient = new LocationClient(this);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd0911");
        option.setScanSpan(1000);

        mLocationClient.setLocOption(option);

        MyLocationListener listener = new MyLocationListener();
        mLocationClient.registerLocationListener(listener);
        mLocationClient.start();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    /**
     * 定位监听器
     */
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //mapView 销毁后不再处理新接收的位置
            if(bdLocation == null|| mapView == null){
                return;
            }

            lat = bdLocation.getLatitude();
            lon = bdLocation.getLongitude();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(bdLocation.getDirection())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            setMapCfg();
        }
    }

    /**
     * 配置Marker监听
     */
    public class MyMarkerListener implements BaiduMap.OnMarkerClickListener{

        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle b = marker.getExtraInfo();
            int index = (int)b.get("listIndex");
            System.out.println("listIndex:"+index);
            return true;
        }
    }

    /**
     * 按钮监听
     */

    public class MyBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_launch_signin:
                    launchSignin();
                    break;
                case R.id.btn_signin:
                    signin(true);
                    break;
                case R.id.btn_rej_signin:
                    signin(false);
            }
        }
    }

    /**
     * 签到
     */
    private void signin(boolean b) {
        mLocationClient.stop();
        new Thread(()->{
            SigninService signinService = SigninServicePoxy.getInstance();
            Signin signin = this.signinSponser;
            signin.setRlongitude(lon);
            signin.setRlatitude(lat);
            boolean res = signinService.sendSignin(signin);
            runOnUiThread(()->{
                if (res){
                    ToastUtil.Toast(SigninActivity.this,"提交签到成功，请耐心等待签到结果！");
                    finish();
                }else{
                    ToastUtil.Toast(SigninActivity.this,"提交签到失败，请重试！");
                }
            });
        }).start();
    }

    /**
     * 发起签到
     */
    private void launchSignin(){
        mLocationClient.stop();
        new Thread(()->{
            SigninService signinService = SigninServicePoxy.getInstance();
            Signin signin = new Signin();
            signin.setTime(System.currentTimeMillis());
            signin.setLatitude(lat);
            signin.setLongtitude(lon);
            signin.setGroupid(group.getId());
            signin.setOriginator(user.getId());
            signin.setRegion(0.5);
            boolean res = signinService.sponserSignin(signin);
            runOnUiThread(()->{
                if(res){
                    ToastUtil.Toast(SigninActivity.this,"发起成功！");
                    finish();
                }else{
                    ToastUtil.Toast(SigninActivity.this,"发起失败！");
                }
            });
        }).start();
    }
}
