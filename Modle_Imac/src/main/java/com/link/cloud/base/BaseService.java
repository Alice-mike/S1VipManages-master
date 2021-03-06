package com.link.cloud.base;

import com.google.gson.JsonObject;
import com.link.cloud.bean.CodeInfo;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.LessonResponse;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.bean.RetrunLessons;
import com.link.cloud.bean.ReturnBean;
import com.link.cloud.bean.SignUserdata;
import com.link.cloud.bean.SignedResponse;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.bean.UserResponse;
import com.link.cloud.bean.Voucher;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Shaozy on 2016/8/10.
 */
public interface BaseService {

    /**
     * 1.根据设备ID和手机号查询会员信息
     *
     * @param params REQUEST BODY请求体
     * @return Observable<Member>
     */
//    @POST("getMemInfoByVeinDeviceIDPhone")
//    Observable<Member> getMemInfo( @Body JsonObject params);
    @POST("validationUser")
    Observable<Member> getMemInfo( @Body JsonObject params);
    /**
     * 2.指静脉设备绑定会员接口
     *
     * @param params REQUEST BODY请求体
     * @return Observable<ReturnBean>
     */
    @POST("bindUserFinger")
    Observable<Member> bindVeinMemeber(@Body JsonObject params);

    /**
     * 3.通过指静脉查询会员信息
     *
     * @param params REQUEST BODY请求体
     * @return Observable<Member>
     */
    @POST("queryMemInfoByVein")
    Observable<Member> getMemberInfoByVein(@Body JsonObject params);

    /**
     * 4通过会员ID查询会员信息
     *
     * @param params REQUEST BODY请求体
     * @return Observable<Member>
     */
    @POST("queryMemInfoByMemID")
    Observable<Member> getMemInfoByMemID(@Body JsonObject params);

    /**
     * 5.查询会员卡信息
     *
     * @param params REQUEST BODY请求体
     * @return Observable<CardInfo>
     */
    @POST("queryMemCardByMemID")
    Observable<ReturnBean> getCardInfo(@Body JsonObject params);


    /**
     * 6.会员的上次签到时间
     *
     * @param params REQUEST BODY请求体
     * @return Observable<ReturnBean>
     */
    @POST("getLastSignedTime")
    Observable<ReturnBean> getLastSignedTime(@Body JsonObject params);


    /**
     * 7.会员签到
     *
     * @param params REQUEST BODY请求体
     * @return Observable<ReturnBean>
     */
    @POST("checkIn")
    Observable<ReturnBean> signedMember(@Body JsonObject params);

    /**
     * 8.指静脉消费接口
     *
     * @param params REQUEST BODY请求体
     * @return Observable<ReturnBean>
     */
    @POST("consumeRecord")
    Observable<Voucher> consumeRecord(@Body JsonObject params);

    /**
     * 9.新增签到接口 员工&会员签到
     *
     * @param params REQUEST BODY请求体
     * @return Observable<ReturnBean>
     */
    @POST("checkIn")
    Observable<SignUserdata> signMember(@Body JsonObject params);

    /**
     * 10.用户消课指静脉验证接口
     */
    @POST("verifyUserEliminateLesson")
    Observable<UserResponse>verifyUserEliminateLesson(@Body JsonObject params);
    /**
     *  消课接口
     */
    @POST("eliminateLesson")
    Observable<RetrunLessons>eliminateLesson(@Body JsonObject params);
    /**
     * 选择消课接口
     */
    @POST("selectLesson")
    Observable<RetrunLessons>selectLesson(@Body JsonObject params);
    /**
     * 11.获取签到二维码接口
     */
    @POST("signedCodeInfo")
    Observable<CodeInfo>singnedCodeInfo(@Body JsonObject params);
    /**
     * 12.检测软件版本
     */
    @POST("deviceUpgrade")
    Observable<UpdateMessage>deviceUpgrade(@Body JsonObject params);

    //2018
    @POST("deviceRegister")
    //13.获取设备id
    Observable<DeviceData>getdeviceId(@Body JsonObject params);

    /**
     * 同步接口
     * @param params
     * @return
     */
    @POST("syncUserFeature")
    Observable<DownLoadData>syncUserFeature(@Body JsonObject params);
    /**
     * 下载指静脉数据
     * @param params
     * @return
     */
    @POST("downloadFeature")
    Observable<DownLoadData>downloadFeature(@Body JsonObject params);
}
