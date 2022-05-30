package com.example.bdtrack;

import com.amap.adapter.util.Log;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import ohos.aafwk.ability.AbilityPackage;
import com.amap.api.maps.MapsInitializer;

public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        String key = "d9b89216f084bd19a4f471af0a093d9b";
        MapsInitializer.setApiKey(key);
        super.onInitialize();
    }
}
