package com.link.cloud.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.sdk.android.push.AliyunMessageIntentService;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import com.link.cloud.BaseApplication;

import com.link.cloud.R;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.Person;
import com.link.cloud.view.ProgressHUD;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.Bind;
import md.com.sdk.MicroFingerVein;

import static com.alibaba.sdk.android.ams.common.util.HexUtil.hexStringToByte;
import static com.link.cloud.component.MyMessageReceiver.REC_TAG;


/**
 * Created by Administrator on 2017/8/16.
 */
/**
 * Created by liyazhou on 17/8/22.
 * 为避免推送广播被系统拦截的小概率事件,我们推荐用户通过IntentService处理消息互调,接入步骤:
 * 1. 创建IntentService并继承AliyunMessageIntentService
 * 2. 覆写相关方法,并在Manifest的注册该Service
 * 3. 调用接口CloudPushService.setPushIntentService
 * 详细用户可参考:https://help.aliyun.com/document_detail/30066.html#h2-2-messagereceiver-aliyunmessageintentservice
 */
public class WorkService extends AliyunMessageIntentService implements SyncUserFeature.syncUser{
    @Bind(R.id.edit_code)
    EditText edit_code;
//    MatchVeinTaskContract matchVeinTaskContract;
    boolean ret=false;
    Handler handler=new Handler();
    static Activity activity=null;
   static MicroFingerVein microFingerVein;
    private ConnectivityManager connectivityManager;//用于判断是否有网络
    private Toast mToast=null;
   static PersonDao personDao;
   byte[] fingermodle=null;
    String device;
    private boolean isNetWork;
    private SyncUserFeature syncUserFeature;
    @Override
    public void onCreate() {
        super.onCreate();
        syncUserFeature=new SyncUserFeature();
        syncUserFeature.attachView(this);
        com.orhanobut.logger.Logger.e("WorkService"+"==========");
//        matchVeinTaskContract = new MatchVeinTaskContract();
    }
    public static void setActactivity(Activity actactivity) {
        activity = actactivity;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new TimeThread().start();
//  isNetworkConnected(WorkService.this);
//        if (ret!=true){
//            ret= MicroFingerVein.fvdevOpen();
//        }
        return Service.START_STICKY;
    }

    public void setFingermodle(byte[] fingermodle) {
        this.fingermodle = fingermodle;
    }
    public byte[] getFingermodle() {
        return fingermodle;
    }

    private void checkMD(){
        if (activity!=null&&ret!=true) {
            microFingerVein = MicroFingerVein.getInstance(activity);
            int devicecount=microFingerVein.fvdev_get_count();
            if (devicecount!=0) {
                ret = microFingerVein.fvdev_open();
//               Logger.e("WorkService"+"devicecount"+devicecount + "=====================" + ret);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
//        matchVeinTaskContract.getVerifyTemplate();
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        SharedPreferences user=activity.getSharedPreferences("user_info",0);
        device= user.getString("deviceId","");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void network(){
        connectivityManager =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
        if (info == null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
            showPromptToast("网络已断开");
//            Toast.makeText(WorkService.this, "检查网络连接是否打开", Toast.LENGTH_SHORT).show();
            isNetWork=false;
        } else { //当前有已激活的网络连接
            if (isNetWork!=true&&device!=null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        syncUserFeature.syncUser(device);
                    }
                }).start();
                isNetWork=true;
            }
        }
    }
    public void showPromptToast(String promptWord) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), promptWord,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(promptWord);
        }
        mToast.show();
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                checkMD();
                try {
                    Thread.sleep(1000);
//                    isNetworkConnected(WorkService.this);
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        network();
                        final Intent intent = new Intent();
                        intent.setAction(LockActivity.ACTION_UPDATEUI);
                        intent.putExtra("timeStr",getTime());
                        intent.putExtra("timeData",getData());
                        sendBroadcast(intent);
                        break;
                    default:
                        break;
                }
            }
        };
        //获得当前年月日时分秒星期
        public String getData(){
            String timeStr=null;
            String mMonth=null;
            String mDay=null;
            final Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
            if ((c.get(Calendar.MONTH) + 1)<10){
                mMonth = "0"+String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            }else {
                mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            }
            if(c.get(Calendar.DAY_OF_MONTH)<10){
                mDay = "0"+String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
            }else {
                mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
            }
            String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
            int mtime=c.get(Calendar.HOUR_OF_DAY);
            int mHour = c.get(Calendar.HOUR);//时
            int mMinute = c.get(Calendar.MINUTE);//分
            int seconds=c.get(Calendar.SECOND);
            if (mtime>=0&&mtime<=5){
                timeStr="凌晨";
            }else if (mtime>5&&mtime<8){
                timeStr="早晨";
            }else if(mtime>8&&mtime<12){
                timeStr="上午";
            }else if(mtime>=12&&mtime<14){
                timeStr="中午";
            }else if(mtime>=14&&mtime<18){
                timeStr="下午";
            }else if(mtime>=18&&mtime<19){
                timeStr="傍晚";
            }else if(mtime>=19&&mtime<=22){
                timeStr="晚上";
            }else if(mtime>22){
                timeStr="深夜";
            }
            if("1".equals(mWay)){
                mWay ="天";
            }else if("2".equals(mWay)){
                mWay ="一";
            }else if("3".equals(mWay)){
                mWay ="二";
            }else if("4".equals(mWay)){
                mWay ="三";
            }else if("5".equals(mWay)){
                mWay ="四";
            }else if("6".equals(mWay)){
                mWay ="五";
            }else if("7".equals(mWay)){
                mWay ="六";
            }
            return mYear+"-"+mMonth + "-" + mDay;
        }
        public String getTime(){
            String timeStr=null;
            final Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            int mtime=c.get(Calendar.HOUR_OF_DAY);
            int mHour = c.get(Calendar.HOUR);//时
            int mMinute = c.get(Calendar.MINUTE);//分
            int seconds=c.get(Calendar.SECOND);
            if (mtime>=0&&mtime<=5){
                timeStr="凌晨";
            }else if (mtime>5&&mtime<8){
                timeStr="早晨";
            }else if(mtime>8&&mtime<12){
                timeStr="上午";
            }else if(mtime>=12&&mtime<14){
                timeStr="中午";
            }else if(mtime>=14&&mtime<18){
                timeStr="下午";
            }else if(mtime>=18&&mtime<19){
                timeStr="傍晚";
            }else if(mtime>=19&&mtime<=22){
                timeStr="晚上";
            }else if(mtime>22){
                timeStr="深夜";
            }
            return checknum(mtime)+":"+checknum(mMinute)+":"+checknum(seconds);
        }
        private String checknum(int num){
            String strnum=null;
            if (num<10){
                strnum="0"+num;
            }else {
                strnum=num+"";
            }
            return strnum;
        }
    }

    @Override
    public void syncUserSuccess(DownLoadData resultResponse) {
        PersonDao personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
        personDao.deleteAll();
        Person person = new Person();
        if (resultResponse.getDown_userInfo().length >= 0) {
            for (int i = 0; i < resultResponse.getDown_userInfo().length; i++) {
                person.setId((long)i+1);
                person.setUid(resultResponse.getDown_userInfo()[i].getUid());
                person.setNumber(resultResponse.getDown_userInfo()[i].getUserName());
                person.setPos(i+"");
                person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
                personDao.insert(person);
            }
        }
        showPromptToast("同步成功");
    }

    @Override
    public void onPermissionError(ApiException e) {

    }

    @Override
    public void onError(ApiException e) {

    }

    @Override
    public void onResultError(ApiException e) {

    }
    //    public boolean isNetworkConnected(Context context) {
//        Runnable runnable=new Runnable() {
//            @Override
//            public void run() {
//                ProgressHUD.dismissProgress(WorkService.this,false,"网络不可用");
//            }
//        };
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//            if (mNetworkInfo != null) {
//                return mNetworkInfo.isAvailable();
//            }else {
//                handler.postDelayed(runnable,1000);
//            }
//        }
//        return false;
//    }

    /**
     * 推送通知的回调方法
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    protected void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        Log.i(REC_TAG,"收到一条推送通知 ： " + title + ", summary:" + summary);
//        BaseApplication.setConsoleText("收到一条推送通知 ： " + title + ", summary:" + summary);
    }

    /**
     * 推送消息的回调方法
     * @param context
     * @param cPushMessage
     */
    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        Log.i(REC_TAG,"收到一条推送消息 ： " + cPushMessage.getTitle()+cPushMessage.getTraceInfo()+", content:" + cPushMessage.getContent());
        BaseApplication.setConsoleText(cPushMessage.getContent());
    }

    /**
     * 从通知栏打开通知的扩展处理
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    protected void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG,"onNotificationOpened ： " + " : " + title + " : " + summary + " : " + extraMap);
        BaseApplication.setConsoleText("onNotificationOpened ： " + " : " + title + " : " + summary + " : " + extraMap);
    }

    /**
     * 无动作通知点击回调。当在后台或阿里云控制台指定的通知动作为无逻辑跳转时,通知点击回调为onNotificationClickedWithNoAction而不是onNotificationOpened
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG,"onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
        BaseApplication.setConsoleText("onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
    }

    /**
     * 通知删除回调
     * @param context
     * @param messageId
     */
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.i(REC_TAG, "onNotificationRemoved ： " + messageId);
        BaseApplication.setConsoleText("onNotificationRemoved ： " + messageId);
    }

    /**
     * 应用处于前台时通知到达回调。注意:该方法仅对自定义样式通知有效,相关详情请参考https://help.aliyun.com/document_detail/30066.html#h3-3-4-basiccustompushnotification-api
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     * @param openType
     * @param openActivity
     * @param openUrl
     */
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.i(REC_TAG,"onNotificationReceivedInApp ： " + " : " + title + " : " + summary + "  " + extraMap + " : " + openType + " : " + openActivity + " : " + openUrl);
        BaseApplication.setConsoleText("onNotificationReceivedInApp ： " + " : " + title + " : " + summary);
    }
}