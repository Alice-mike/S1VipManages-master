package com.link.cloud.base;

import com.google.gson.JsonObject;
import com.link.cloud.bean.CabinetNumberData;
import com.link.cloud.bean.CardInfo;
import com.link.cloud.bean.CodeInfo;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.LessonResponse;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.bean.ResultHeartBeat;
import com.link.cloud.bean.ResultResponse;
import com.link.cloud.bean.ReturnBean;
import com.link.cloud.bean.SignedResponse;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.bean.UserResponse;
import com.link.cloud.bean.Voucher;
import com.link.cloud.model.impl.DeviceHelper;
import com.link.cloud.model.impl.HttpClientHelper;
import com.link.cloud.utils.ReservoirUtils;
import com.link.cloud.utils.RxUtils;


import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public class DataManager {
    private static DataManager dataManager;
    private HttpClientHelper httpClientHelper;
    private DeviceHelper deviceHelper;
    public ReservoirUtils reservoirUtils;
    public synchronized static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }
    private DataManager() {
        this.httpClientHelper = HttpClientHelper.getInstance();
        this.deviceHelper = DeviceHelper.getInstance();
        this.reservoirUtils = new ReservoirUtils();
    }
    public Observable<DownLoadData>syncSignUserFeature(String deviceId){
        return this.httpClientHelper.syncSignUserFeature(deviceId).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<ResultHeartBeat>deviceHeartBeat(String deviceId){
        return this.httpClientHelper.deviceHeartBeat(deviceId).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<UpdateMessage>deviceUpgrade(String deviceID){
        return this.httpClientHelper.deviceUpgrade(deviceID).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<RestResponse>sendLogMessage(String deviceId, String uid,String uids,String feature,String time,String scope, String result){
        return this.httpClientHelper.sendLogMessage(deviceId,uid,uids,feature,time,scope,result)
                .compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<DeviceData>getdeviceID(String deviceTargetValue,int deviceType){
        return this.httpClientHelper.getdeviceID(deviceTargetValue,deviceType).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<Lockdata>isOpenCabinet(String deviceId,String uid, String fromType){
        return this.httpClientHelper.isOpenCabinet(deviceId,uid,fromType).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<ResultResponse>clearCabinet(String deviceId, String cabinetNumber){
        return this.httpClientHelper.clearCabinet(deviceId,cabinetNumber).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<ResultResponse>adminiOpenCabinet(String deviceId,String cabinetNumber){
        return this.httpClientHelper.adminiOpenCabinet(deviceId,cabinetNumber).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<DownLoadData>downloadFeature(String messageId, String appid, String shopId, String deviceId, String uid){
        return this.httpClientHelper.downloadFeature(messageId,appid,shopId,deviceId,uid).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<DownLoadData>syncUserFeature(String deviceId){
        return this.httpClientHelper.syncUserFeature(deviceId).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<CabinetNumberData>cabinetNumberList(String deviceId){
        return this.httpClientHelper.cabinetNumberList(deviceId).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<DownLoadData>downloadNotReceiver(String deviceId){
        return this.httpClientHelper.downloadNotReceiver(deviceId).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
    public Observable<UpDateBean>appUpdateInfo(String deviceId){
        return this.httpClientHelper.appUpdateInfo(deviceId).compose(RxUtils.applyIOToMainThreadSchedulers());
    }
}
