package com.wqian001.lovereminder.api;

import com.wqian001.lovereminder.data.Beacon;


public interface ApiDataCallback {
    void onBeaconResponse(Beacon beacon);
    void onAttachmentResponse();
}
