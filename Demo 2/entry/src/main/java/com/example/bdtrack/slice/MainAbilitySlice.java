package com.example.bdtrack.slice;

import com.amap.adapter.content.Context;
import com.amap.adapter.graphics.PointF;
import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.example.bdtrack.Model.EFBPoint;
import com.example.bdtrack.ResourceTable;
import com.example.bdtrack.View.RightItemView;
import com.example.bdtrack.View.RightToolView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.IBundleManager;
import ohos.rpc.RemoteException;

import java.io.IOException;
import java.net.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static ohos.agp.utils.Color.*;

public class MainAbilitySlice extends AbilitySlice implements OfflineMapManager.OfflineMapDownloadListener, OfflineMapManager.OfflineLoadedListener {

    //定位权限匹配值
    public static final int MY_LOCATION_PERMISSION = 6;

    private MapView mapView;

    private RightToolView timeView;

    private RightToolView angleView;

    private RightItemView heightView;

    private RightItemView spiteView;

    private RightItemView longtituteView;

    private RightItemView latitueView;

    private RightItemView horiView;

    private RightItemView vertiView;

    private Text tipsText;

    private ArrayList<EFBPoint> gpsPoints = new ArrayList<>();

    private ArrayList<EFBPoint> bdPoints = new ArrayList<>();

    private Marker bdBeginMarker;

    private Marker bdFlightMarker;

    private Marker gpsBeginMarker;

    private Marker gpsFlightMarker;

    private ArrayList<Polyline> bdPolylines = new ArrayList<>();

    private ArrayList<Polyline> gpsPolylines = new ArrayList<>();

    private LoadingPopupView loadingPopup;

    private OfflineMapManager mapManager;

    public void requestLocationPermission(){
        if (verifySelfPermission("ohos.permission.LOCATION") != IBundleManager.PERMISSION_GRANTED){

            if (canRequestPermission("ohos.permission.LOCATION")){
                //是否可以申请"位置"的动态弹框授权(首次申请或者用户未选择禁止且不再提示)
                requestPermissionsFromUser(
                        new String[]{"ohos.permission.LOCATION","ohos.permission.LOCATION_IN_BACKGROUND"}, MY_LOCATION_PERMISSION
                );
            } else {
                //提示用户进入设置界面进行授权
                new ToastDialog(getContext()).setText("请进入手机系统【设置】中，重新开启应用的定位权限").show();
            }
        } else {
            //此鸿蒙地图APP应用已被授予“位置”权限
            new ToastDialog(getContext()).setText("此应用已授权,可进行【定位】功能的初始化").show();
        }
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        requestLocationPermission();

        String appId = null;
        try {
            appId = getBundleManager().getBundleInfo(getBundleName(), 0).getAppId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.setUIContent(ResourceTable.Layout_ability_main);


        mapView = findComponentById(ResourceTable.Id_mapview);
        mapView.onCreate(null);
        mapView.onResume();

        AMap map = mapView.getMap();
        map.setMapType(1);
        UiSettings settings = map.getUiSettings();
        settings.setZoomControlsEnabled(false);
        settings.setRotateGesturesEnabled(false);
        map.addMarker(new MarkerOptions().position(new LatLng(39.90748,116.41279)));
        map.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition position) {
            }
        });


        MapsInitializer.setExceptionLogger(new ExceptionLogger() {
            @Override
            public void onException(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onDownloaderException(int errorCode, int responseCode) {

            }
        });

//        mapView.getMap().setLoadOfflineData(true);
        mapManager = new OfflineMapManager( new Context(this.getContext()), this, map);
        mapManager.setOnOfflineLoadedListener(this);
//        ArrayList<OfflineMapProvince> provinces = mapManager.getOfflineMapProvinceList();
        loadingPopup = (LoadingPopupView) new XPopup.Builder(getContext())
                .dismissOnBackPressed(false)
                .asLoading("正在加载离线地图数据")
                .show();
//        ArrayList<OfflineMapCity> list = mapManager.getOfflineMapCityList();
//        for (int k = 0; k < provinces.size(); k++) {
//            OfflineMapProvince province = provinces.get(k);
//            ArrayList<OfflineMapCity> cities = province.getCityList();
//            for (int i = 0; i < cities.size(); i++) {
//                OfflineMapCity city = cities.get(i);
//                try {
//                    mapManager.updateOfflineCityByName(city.getCity());
//                } catch (AMapException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

//        loadingPopup.delayDismissWith(6000, new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

        Image typeImage = findComponentById(ResourceTable.Id_image_type);
        typeImage.setTag(0);
        typeImage.setClickedListener(any -> {
            int tag = (int) typeImage.getTag();
            if (tag == 0) {
                typeImage.setTag(1);
                map.setMapType(2);
                typeImage.setImageAndDecodeBounds(ResourceTable.Media_realtime_map_type_selected);
            } else {
                typeImage.setTag(0);
                map.setMapType(1);
                typeImage.setImageAndDecodeBounds(ResourceTable.Media_realtime_map_type_normal);
            }
        });

        Button topBtn = findComponentById(ResourceTable.Id_button_plus);
        ShapeElement element = new ShapeElement(getContext(), ResourceTable.Graphic_top_button);
        element.setRgbColor(new RgbColor(255,255,255));
        element.setCornerRadiiArray(new float[]{
                20f, 20f,
                20f, 20f,
                0f, 0f,
                0f, 0f
        });
        topBtn.setBackground(element);
        topBtn.setClickedListener(topListener -> {
            map.animateCamera(CameraUpdateFactory.zoomIn());
        });

        Button bottomBtn = findComponentById(ResourceTable.Id_button_minus);
        ShapeElement element2 = new ShapeElement(getContext(), ResourceTable.Graphic_top_button);
        element2.setRgbColor(new RgbColor(255,255,255));
        element2.setCornerRadiiArray(new float[]{
                0f, 0f,
                0f, 0f,
                20f, 20f,
                20f, 20f
        });
        bottomBtn.setBackground(element2);
        bottomBtn.setClickedListener(bottomListener -> {
            map.animateCamera(CameraUpdateFactory.zoomOut());
        });

//        try {
//            receiveSocket();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void onDownload(int i, int i1, String s) {

    }

    @Override
    public void onCheckUpdate(boolean b, String s) {

    }

    @Override
    public void onRemove(boolean b, String s, String s1) {

    }

    @Override
    public void onVerifyComplete() {
        List<OfflineMapProvince> provinces = mapManager.getOfflineMapProvinceList();
        loadingPopup.delayDismissWith(6000, new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
