package com.soonvein.cloud.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.TestActivityManager;
import com.soonvein.cloud.activity.CallBackValue;
import com.soonvein.cloud.activity.EliminateActivity;
import com.soonvein.cloud.activity.NewMainActivity;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.CodeInfo;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.UserResponse;
import com.soonvein.cloud.bean.UserUID;
import com.soonvein.cloud.contract.GetVerifyTemplate;
import com.soonvein.cloud.contract.UserLessonContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.ui.HorizontalListView;
import com.soonvein.cloud.ui.HorizontalListViewAdapter;
import com.soonvein.cloud.ui.RollListView;
import com.soonvein.cloud.utils.Utils;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/7/31.
 */

public class LessonFragment_test extends BaseFragment implements UserLessonContract.UserLesson,GetVerifyTemplate.VerifyTemplate{

    @Bind(R.id.failureImage)
    ImageView failureImage;
    @Bind(R.id.em_lesson_img)
    ImageView tipImageView;
    @Bind(R.id.em_lesson_tv)
    TextView tvTips;
    @Bind(R.id.eliminatetemp)
    LinearLayout eliminatetemp;
    @Bind(R.id.elm_member_message)
    LinearLayout eliminateuser;
    @Bind(R.id.lesson_menber_name)
    TextView tvMemberName;
    @Bind(R.id.lesson_menber_phone)
    TextView tvMemberPhone;
    @Bind(R.id.lesson_menber_person)
    TextView tvuserTyper;
    @Bind(R.id.lesson_menber_sex)
    TextView tvmemberSex;
    @Bind(R.id.lesson_menber_btn)
    Button btnext;
    @Bind(R.id.successeliminate)
    LinearLayout successeliminate;
    @Bind(R.id.elm_menber_name)
    TextView successName;
    @Bind(R.id.elm_menber_phone)
    TextView successPhone;
    @Bind(R.id.elm_coach_nam)
    TextView successCoach;
    @Bind(R.id.elm_lesson_name)
    TextView successLesson;
    @Bind(R.id.elm_lesson_time)
    TextView successDate;
    @Bind(R.id.lesson_bt_next)
    Button lesson_bt_next;
    @Bind(R.id.lessonmessage)
    LinearLayout lessonmessage;
    @Bind(R.id.lessonmessageInfo)
    RollListView horizontalListView;
    @Bind(R.id.lessonLayout)
    LinearLayout lessonLayout;
    @Bind(R.id.selectLesson)
    LinearLayout selectLesson;
    @Bind(R.id.up_lesson)
    Button up_lesson;
    @Bind(R.id.next_lesson)
    Button next_lesson;
    private HorizontalListViewAdapter hListViewAdapter;
    private int selectPosition = 0;//用于记录用户选择的变量
    public Context mContext;
    int needclerk;
    public int usernum=1,userID;
    private String memberUID,coachUID,checkUID;
    private String TAG="EliminateLessonFragment" ;
    private MediaPlayer mediaPlayer;
    UserResponse userResponse;
    public Handler mHandler1;
    public Runnable runnable,runnable2;
    public UserLessonContract presenter;
    private String phoneNum, deviceID, veinFingerID;
    public static CallBackValue callBackValue;
    private GetVerifyTemplate getVerifyTemplate;
    View.OnClickListener clickListener;
    public  String lessonnum=new String();
    private LessonResponse lessonResponse;
    private SharedPreferences userInfo;
    private String type=null;
    public int indext=0,num=0;
    EliminateActivity activity;
    String[] lessonId,lessonName,lessonDate;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(EliminateActivity) activity;
        callBackValue=(CallBackValue)activity;
    }
    public  LessonFragment_test(){

    }

    public static LessonFragment_test newInstance(){
        LessonFragment_test fragment= new LessonFragment_test();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public void setUsernum(int usernum) {
        this.usernum = usernum;
    }

    public int getUsernum() {
        return usernum;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                userInfo = activity.getSharedPreferences("user_info", 0);
                deviceID = userInfo.getString("DeviceID", "");
                phoneNum = "6991";
                veinFingerID = "IuqINyc2RVzlFoN50OAtont7aMLHK7dUDZFw2iALkiHUl8p2yna+MpLdF0KChyevTbJk8zXAnL3yBkdzhGZ39pW20x9AIcTncN1840KuCJU7aL4My1Yj25RuEZ1PTVMRZD+zDWzZ6ts0oWP+WvirmtpY5KVatmvUokJsmIkeoZl7cvQA/USyYrLFv43n3e5E5zOQN7nbd6nMqwkjSsVO1jNroaoRE9Bgj/uTECU1uhaHQMcrCHuJ1wcSPky4/yOaryPVnMZVxTVcy2y1dzZgo6O7C1cucIegLk+LQUYciGCZyNt1p43tJzpDKmKpmSYOdPvTcuxFVQBmyZvszbf9DtfJ6m9rasBkSqJ895HXZ6ZxrLa+UCrmBUIKUtLex2AbVhOGXPEO3sL1nb5lU9xz2wiRiB5G5JSbma3Wv5F+xhZOtWw857JZHCcE4QFF8N4DywtUt8rboUsac5fD0YKTqO/7RozgU0fvCRT/lKPfJcOG54EL7Hi26nbJwMI1lFs6bM0IBnD8+Gpzt2Zyema2m7ohCVioI+xqfrcWgy0K21i1lVsHV9kWO8GPeeLlObQTwrTgzCRnnbLfZe6PpK6TapkvygpZLa3/XLH+YV7BRri7F99yRZQ4iLfgk8rQh62uEaet0Y2FybDPnlXe2poQxMPkMHw+O8saMwVQcWwiA5o=";
                LessonFragment_test.this.showProgress(true, false, "请稍后");
                //签到需要查找会员信息
//                SignInFragment.this.presenter.getMemberInfoByVein(phoneNum, deviceID, veinFingerID);
            } else if (msg.what == 1) {
                if (getContext() != null) {
                    String errorMsg = (String) msg.obj;
                    tvTips.setText(errorMsg);
                    mediaPlayer.start();
                    failureImage.setVisibility(View.VISIBLE);
                    runnable=new Runnable() {
                        @Override
                        public void run() {
                            failureImage.setVisibility(View.GONE);
                            tvTips.setText("请按图示放置手指");
                            if (getVerifyTemplate.getMvpView()!=null) {
                                getVerifyTemplate.getVerifyTemplate();
                            }
                        }
                    };
                    if (Utils.isEmpty(errorMsg)) {
                        //失败页面展示
                    } else {
                        mHandler.postDelayed(runnable, 2000);
                    }
                }
            }else if (msg.what==2){
                Logger.e("LessonFragment"+"handle"+lessonResponse.toString());
                hListViewAdapter=new HorizontalListViewAdapter(getContext(),indext,lessonResponse.getCoach(),lessonResponse.getMembername(),lessonResponse.getMemberphone(),lessonId[indext],lessonName[indext],lessonDate[indext]);
                horizontalListView.setAdapter(hListViewAdapter);
                hListViewAdapter.notifyDataSetChanged();
                horizontalListView.invalidate();
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this.getContext();
        mediaPlayer=MediaPlayer.create(activity,R.raw.failure_sign);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    protected void initData() {
        presenter = new UserLessonContract();
        this.presenter.attachView(this);
        getVerifyTemplate=new GetVerifyTemplate();
        this.getVerifyTemplate.attachView(this);
        this.getVerifyTemplate.getVerifyTemplate();
        setUserID(1);
    }

    @Override
    protected void initListeners() {
        Logger.e("LessonFragment_test============initListeners");
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Logger.e("LessonFragment_test============initViews");
    }

    @Override
    protected void onInvisible() {
        Logger.e("LessonFragment_test============onInvisible");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_eliminatelesson_test;
    }
    @Override
    protected void onVisible() {
        Logger.e("LessonFragment_test============onVisible");
    }
    @Override
    public void verifyUserEliminateSuccess(UserResponse userResponse) {
        eliminatetemp.setVisibility(View.GONE);
        eliminateuser.setVisibility(View.VISIBLE);
        this.showProgress(false);
        Logger.e("EliminateLessonFragment---userResponse.getUserTyper"+userResponse.getUserTyper());
        if (userResponse.getUserTyper()==1){
            tvuserTyper.setText("教练");
            coachUID=userResponse.getUid();
        }else if (userResponse.getUserTyper()==2){
            tvuserTyper.setText("会员");
            memberUID=userResponse.getUid();
        }else if (userResponse.getUserTyper()==3){
            tvuserTyper.setText("员工");
            checkUID=userResponse.getUid();
        }
        btnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvuserTyper.getText()=="教练"){
                    eliminateuser.setVisibility(View.GONE);
                    eliminatetemp.setVisibility(View.VISIBLE);
                    setUsernum(2);
                    getVerifyTemplate.getVerifyTemplate();
                    tvTips.setText("请会员放置手指");
                    if (needclerk==0){
                        btnext.setText("确定");
                    }
                    callBackValue.setActivtyChange("2");
                }else if (tvuserTyper.getText()=="会员"){
                    if (needclerk==0){
                        userInfo = activity.getSharedPreferences("user_info", 0);
                        deviceID = userInfo.getString("DeviceID", "");
                        getVerifyTemplate.eliminateLesson(deviceID,"1",memberUID,coachUID,checkUID);
                        setUsernum(1);
//                        Logger.e("EliminateLessonFragment"+"type"+type+"DeviceID():"+Utils.getCurrentDeviceID()+"memberUID:"+memberUID+"coachUID:"+coachUID);
                    }else if (needclerk==1){
                        tvTips.setText("请员工放置手指");
                        setUsernum(3);
                        getVerifyTemplate.getVerifyTemplate();
                        callBackValue.setActivtyChange("3");
                    }
                }else if (tvuserTyper.getText()=="员工"){
                    callBackValue.setActivtyChange("4");
                    setUsernum(1);
                    userInfo = activity.getSharedPreferences("user_info", 0);
                    deviceID = userInfo.getString("DeviceID", "");
                    getVerifyTemplate.eliminateLesson(deviceID,"1",memberUID,coachUID,checkUID);
                    Logger.e("EliminateLessonFragment"+"type"+type+"memberUID"+memberUID+"coachUID"+coachUID+"checkUID"+checkUID);
                }
            }
        });
        tvMemberName.setText(userResponse.getName());
        tvMemberPhone.setText(userResponse.getPhone());
        tvmemberSex.setText(userResponse.getSex());
        needclerk=userResponse.getNeedclerk();
//        ((EliminateLessonMainFragment) getParentFragment()).viewPager.setCurrentItem(0);
        Logger.e("EliminateLessonFragment:verifyUserEliminateSuccess"+userResponse.toString());
    }
    @Override
    public void eliminateSuccess(LessonResponse lessonResponse) {
        callBackValue.setActivtyChange("3");
        if (getVerifyTemplate.getMvpView()!=null) {
            getVerifyTemplate.detachView();
        }
        eliminatetemp.setVisibility(View.GONE);
        eliminateuser.setVisibility(View.GONE);
        successeliminate.setVisibility(View.VISIBLE);
        this.lessonResponse=lessonResponse;
        num= lessonResponse.lessonInfo.length;
        lessonmessage.setVisibility(View.GONE);
        lesson_bt_next.setVisibility(View.VISIBLE);
        lessonId=new String[num];
        lessonName=new String[num];
        lessonDate=new String[num];
            for (int i=0;i<num;i++) {
                lessonId[i]=lessonResponse.lessonInfo[i].getLessonId();
                lessonName[i]=lessonResponse.lessonInfo[i].getLessonName();
                lessonDate[i]=lessonResponse.lessonInfo[i].getLessonDate();
            }
            lessonnum=lessonResponse.lessonInfo[0].getLessonId();
            hListViewAdapter=new HorizontalListViewAdapter(getContext(),selectPosition,lessonResponse.getCoach(),lessonResponse.getMembername(),lessonResponse.getMemberphone(),lessonId[indext],lessonName[indext],lessonDate[indext]);
            horizontalListView.setAdapter(hListViewAdapter);
            horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    hListViewAdapter.setSelectIndex(position);
                    selectPosition = position;
                    lessonnum=lessonResponse.lessonInfo[position].getLessonId();
                    hListViewAdapter.notifyDataSetChanged();
                }
            });
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.up_lesson:
                        uplesson();
                        Logger.e("eliminateSuccess========="+"R.id.up_lesson");
                        break;
                    case R.id.next_lesson:
                        nextlesson();
                        Logger.e("eliminateSuccess========="+"next_lesson");
                        break;
                }
            }
        };
        up_lesson.setOnClickListener(clickListener);
        next_lesson.setOnClickListener(clickListener);
         checkButton();
            Logger.e("eliminateSuccess========="+lessonResponse.toString());
    }
    public void uplesson(){
        indext--;
        lessonnum=lessonResponse.lessonInfo[indext].getLessonId();
        Logger.e("eliminateSuccess========="+"uplesson:"+(indext));
//        hListViewAdapter=new HorizontalListViewAdapter(getContext(),indext-1,lessonResponse.getCoach(),lessonResponse.getMembername(),lessonResponse.getMemberphone(),lessonId[indext],lessonName[indext],lessonDate[indext]);
//        horizontalListView.setAdapter(hListViewAdapter);
//        hListViewAdapter.notifyDataSetChanged();
        Logger.e("eliminateSuccess========="+"uplesson"+lessonResponse.toString());
        Message msg=mHandler.obtainMessage();
        msg.what=2;
        mHandler.sendMessage(msg);
        checkButton();
    }
    public void nextlesson(){
        indext++;
        lessonnum=lessonResponse.lessonInfo[indext].getLessonId();
        Logger.e("eliminateSuccess========="+"nextlesson"+"nextlesson:"+indext);
//        hListViewAdapter=new HorizontalListViewAdapter(getContext(),indext+1,lessonResponse.getCoach(),lessonResponse.getMembername(),lessonResponse.getMemberphone(),lessonId[indext],lessonName[indext],lessonDate[indext]);
//        horizontalListView.setAdapter(hListViewAdapter);
//        hListViewAdapter.notifyDataSetChanged();
        Logger.e("eliminateSuccess========="+lessonResponse.toString());
        Message msg=mHandler.obtainMessage();
        msg.what=2;
        mHandler.sendMessage(msg);
        checkButton();
    }
    public void checkButton(){
        Logger.e("eliminateSuccess========="+"checkButton"+indext+ "num:"+num);
        if (num>1){
            if (indext==0){
                Logger.e("eliminateSuccess=indext==0="+"checkButton"+indext+ "num:"+num);
                up_lesson.setEnabled(false);
                up_lesson.setBackgroundResource(R.drawable.no_up);
                next_lesson.setBackgroundResource(R.drawable.next_btn_bge);
                next_lesson.setEnabled(true);
            }else if (indext<num-1&&indext!=0){
                Logger.e("eliminateSuccess=indext<num-1&&indext!=0="+"checkButton"+indext+ "num:"+num);
                up_lesson.setBackgroundResource(R.drawable.up_btn_bg);
                up_lesson.setEnabled(true);
                next_lesson.setBackgroundResource(R.drawable.next_btn_bge);
                next_lesson.setEnabled(true);
            }else if (indext==num-1&indext!=0){
                Logger.e("eliminateSuccess=indext==num-1&indext!=0="+"checkButton"+indext+ "num:"+num);
                up_lesson.setBackgroundResource(R.drawable.up_btn_bg);
                up_lesson.setEnabled(true);
                next_lesson.setEnabled(false);
                next_lesson.setBackgroundResource(R.drawable.no_next);
            }
        }else if (num==1){
            up_lesson.setBackgroundResource(R.drawable.no_up);
            next_lesson.setBackgroundResource(R.drawable.no_next);
            next_lesson.setEnabled(false);
            up_lesson.setEnabled(false);
        }
    }
    @Override
    public void selectLessonSuccess(LessonResponse lessonResponse) {
        callBackValue.setActivtyChange("4");
        successeliminate.setVisibility(View.GONE);
        selectLesson.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent();
                intent.setClass(activity, NewMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        },3000);
    }
    @Override
    public void onPermissionError(ApiException e) {
        Logger.e("EliminateLessonFragment:--onPermissionError");
        onError(e);
    }
    @Override
    public void onResultError(ApiException e) {
        this.showProgress(true, false, e.getDisplayMessage());
        Logger.e("EliminateLessonFragment:--onResultError"+e.getDisplayMessage());
        onError(e);
    }
    //获取验证模板成功
    @Override
    public void verifyTemplateSuccess(String veinFingerID) {
        userInfo = activity.getSharedPreferences("user_info", 0);
        deviceID = userInfo.getString("DeviceID", "");
        this.showProgress(true, false, "请稍后");
        this.presenter.verifyUserEliminateLesson(deviceID,getUsernum(), veinFingerID);
        Logger.e("EliminateLessonFragment:--verifyTemplateSuccess"+"deviceID"+deviceID+"getUsernum"+getUsernum()+"veinFingerID"+veinFingerID);
    }
    @Override
    public void verifyTemplateFailure(ApiException e) {
        Logger.e("EliminateLessonFragment:--verifyTemplateFailure");
        onError(e);
    }
    @Override
    public void signedCodeInfo(CodeInfo codeInfo) {
    }
    @Override
    public void onError(ApiException error) {
        Logger.e("EliminateLessonFragment:--onError"+error.getDisplayMessage());
        this.showToast(error.getDisplayMessage());
        super.onError(error);
//        this.showProgress(true, true, error.getDisplayMessage());
        if (BaseApplication.DEBUG) {
            Logger.e(error.getMessage());
            this.showToast(error.getMessage());
        }
        this.showProgress(false);
        Message msg=mHandler.obtainMessage();
        msg.what=1;
        if (!Utils.isEmpty(error.getDisplayMessage())) {
            msg.obj = error.getDisplayMessage();
        }
        mHandler.sendMessage(msg);
    }
    @Override
    public void onStop() {
        Logger.e("LessonFragment_test============onStop");
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Logger.e("LessonFragment_test============onDestroy");
        if (runnable!=null) {
            mHandler.removeCallbacks(runnable);
        }
        if (runnable2!=null){
            mHandler.removeCallbacks(runnable2);
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    @OnClick({R.id.failureImage,R.id.lesson_menber_btn,R.id.lesson_bt_next})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.lesson_bt_next:
                userInfo = activity.getSharedPreferences("user_info", 0);
                deviceID = userInfo.getString("DeviceID", "");
                this.presenter.selectLesson(deviceID,"1",lessonnum,memberUID,coachUID,checkUID);
            case R.id.lesson_menber_btn:
//                Logger.e("EliminateLessonFragment---tvuserTyper"+tvuserTyper.getText().toString());
                userInfo =activity.getSharedPreferences("user_info", 0);
                deviceID = userInfo.getString("DeviceID", "");
                if (tvuserTyper.getText()=="教练"){
                    eliminateuser.setVisibility(View.GONE);
                    eliminatetemp.setVisibility(View.VISIBLE);
                    setUsernum(2);
                    tvTips.setText("请会员放置手指");
                    getVerifyTemplate.getVerifyTemplate();
                }else if (tvuserTyper.getText()=="会员"&&getVerifyTemplate.getMvpView()!=null){
                    getVerifyTemplate.detachView();
                    if (needclerk==0){
                        TestActivityManager.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //此时已在主线程中，可以更新UI了
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                            getVerifyTemplate.eliminateLesson(deviceID, "1", memberUID, coachUID, checkUID);
                                    }
                                }).start();
                            }
                        });
                        eliminateuser.setVisibility(View.GONE);
                        Logger.e("EliminateLessonFragment"+"type"+type+"DeviceID():"+deviceID+"memberUID:"+memberUID+"coachUID:"+coachUID);
                    }else {
                        tvTips.setText("请员工放置手指");
                        setUsernum(3);
                        getVerifyTemplate.getVerifyTemplate();
                    }
                }else if (tvuserTyper.getText()=="员工"){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            presenter.eliminateLesson(deviceID,type,memberUID,coachUID,checkUID);
                        }
                    }).start();
                    Logger.e("EliminateLessonFragment"+"type"+"1"+"memberUID"+memberUID+"coachUID"+coachUID+"checkUID"+checkUID);
                }
                break;
        }
    }
}