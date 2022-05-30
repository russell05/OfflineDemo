package com.example.bdtrack;

import com.amap.adapter.util.Log;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.example.bdtrack.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.ToastDialog;
import ohos.agp.window.service.WindowManager;
import ohos.bundle.IBundleManager;

public class MainAbility extends Ability {

    public static final int LOCATION_PEEMISSION_CODE = MainAbilitySlice.MY_LOCATION_PERMISSION;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());

        // 隐藏状态栏、设置状态栏和导航栏透明
        getWindow().addFlags(WindowManager.LayoutConfig.MARK_FULL_SCREEN|
                WindowManager.LayoutConfig.MARK_TRANSLUCENT_STATUS|
                WindowManager.LayoutConfig.MARK_TRANSLUCENT_NAVIGATION);

        // 隐藏导航栏（部分系统无效）
        getWindow().setStatusBarVisibility(Component.HIDE);
    }
    //调用requestPermissionsFromUser后的权限申请应答
    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case LOCATION_PEEMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == IBundleManager.PERMISSION_GRANTED){
                    new ToastDialog(getContext()).setText("权限被授予，可进行地图【定位】的显示和初始化").show();

                }else{
                    new ToastDialog(getContext()).setText("权限被拒绝").show();
                }
                return;
            }
        }
    }
}
