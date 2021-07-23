package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.alphametal.CrossMetalModel;
import com.gjmetal.app.model.my.AppVersion;
import com.gjmetal.app.model.my.ApplyforModel;
import com.gjmetal.app.model.my.Company;
import com.gjmetal.app.model.my.PhotoFileModel;
import com.gjmetal.app.model.my.MessageBean;
import com.gjmetal.app.model.my.MessageStatusBean;
import com.gjmetal.app.model.my.UpLoadBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.my.About;
import com.gjmetal.app.model.my.UserWarnListModel;
import com.gjmetal.app.model.my.VipService;
import com.gjmetal.app.model.my.WarnConfigModel;

import java.util.List;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Description：我的模块接口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  18:12
 */
public interface MyApi {

    //绑定设备id
    @GET("secure/rest/user/bindThirdClient")
    Flowable<BaseModel> bindThirdClient(@Query("appVersion") String appVersion, @Query("clientId") String clientId);

    //获取消息状态
    @GET("secure/rest/getNotify")
    Flowable<BaseModel<MessageStatusBean>> checkMsgStatus();

    //修改消息状态
    @GET("secure/rest/saveNotify")
    Flowable<BaseModel<String>> modifyMsgStatus(@Query("status") String status);


    //修改手机号
    @GET("secure/rest/user/updateMobile")
    Flowable<BaseModel> checkPhone(@Query("mobile") String mobile, @Query("code") String code);

    //获取验证码
    @GET("secure/rest/user/sendMobile2")
    Flowable<BaseModel> getMessage(@Query("mobile") String mobile,@Query("signKey") String signKey,
                                           @Query("rondomCode") String rondomCode,
                                           @Query("time") String time,
                                           @Query("captchaCode") String captchaCode,
                                           @Query("deviceId") String deviceId);

    //消息数量
    @GET("secure/rest/countMsg")
    Flowable<BaseModel<Integer>> msgCount();


    //删除消息
    @POST("secure/rest/clearMsg")
    Flowable<BaseModel<String>> deleteMsg(@Body List<String> params);

    //清空消息
    @GET("secure/rest/clearAllMsg")
    Flowable<BaseModel<String>> clearmsg();

    //读取消息
    @GET("secure/rest/readMsg")
    Flowable<BaseModel<String>> readMsg(@Query("msgId") String msgId);

    //获取消息列表
    @GET("secure/rest/listMsg")
    Flowable<BaseModel<MessageBean>> getMessageList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);


    //修改名字
    @GET("secure/rest/user/updateNickName")
    Flowable<BaseModel<String>> upDateNickName(@Query("nickName") String nickName);


    //修改密码
    @GET("secure/rest/user/updatepwd")
    Flowable<BaseModel> updatepwd(@Query("password") String password, @Query("newPassword") String newPassword, @Query("confirmPassword") String confirmPassword);

    //提交头像
    @GET("secure/rest/user/updateAvatarUrl")
    Flowable<BaseModel<Object>> upLoadHead(@Query("avatarUrl") String avatarUrl, @Query("fileName") String fileName);

    //上传图片
    @POST("rest/upload/file")
    @Multipart
    Flowable<BaseModel<UpLoadBean>> upLoadFile(@Part("file\"; filename=\"photo.png\" ") RequestBody file, @Part("type") RequestBody type);

    //用户信息
    @GET("secure/rest/account")
    Flowable<BaseModel<User>> getUserInfo();

    //关于我们
    @GET("rest/companyInfo")
    Flowable<BaseModel<About>> company();

    //版本更新
    @GET("rest/version")
    Flowable<BaseModel<AppVersion>> appUpdate(@Query("channel") String channel, @Query("code") String code);

    //获取已经认证的企业成员
    @GET("secure/rest/company/getCompanyList")
    Flowable<BaseModel<Company>> getCompanyList(
            @Query("p") int p,
            @Query("size") int size
    );


    //发送企业邀请码
    @GET("secure/rest/company/sendMobile")
    Flowable<BaseModel> sendMobile(
            @Query("mobile") String mobile,
            @Query("name") String name,
            @Query("id") int id
    );

    //加入企业
    @GET("secure/rest/company/check")
    Flowable<BaseModel> checkCompanyCode(
            @Query("code") String code
    );

    //退出登录
    @GET("rest/logout")
    Flowable<BaseModel> loginOut();


    // 获取预警配置项
    @GET("secure/rest/monitor/config")
    Flowable<BaseModel<WarnConfigModel>> getWarningConfig(@Query("monitorType") String monitorType);


    //添加预警
    @POST("secure/rest/monitor/addMonitor")
    Flowable<BaseModel<Integer>> addMonitor(@Body RequestBody body);

    //添加预警
    @POST("secure/rest/monitor/editorMonitor")
    Flowable<BaseModel<Integer>> editorMonitor(@Body RequestBody body);

    //删除预警
    @POST("secure/rest/monitor/deleteMonitor")
    Flowable<BaseModel<Integer>> deleteMonitor(@Body RequestBody body);


    //预警列表
    @GET("secure/rest/monitor/userMonitorList")
    Flowable<BaseModel<List<UserWarnListModel>>> userMonitorList();

    //预期失效检测
    @GET("secure/rest/monitor/effectiveMonitor")
    Flowable<BaseModel> userMonitorPermission(@Query("indicatorRefCode") String indicatorRefCode, @Query(" indicatorType") String indicatorType);


    //申请订阅权限
    @POST("secure/rest/user/applyRead")
    Flowable<BaseModel<String>> goApplyForRead(@Body RequestBody body);

    //申请订阅权限
    @GET("rest/getApplyMessage")
    Flowable<BaseModel<ApplyforModel>> getApplyForReadMsg();

    //投诉建议(批量上传图片)
    @POST("rest/upload/batchUploadFile")
    @Multipart
    Flowable<BaseModel<List<PhotoFileModel>>> goUploadPictures(@Part List<MultipartBody.Part> files);

    //投诉建议
    @POST("rest/suggest")
    Flowable<BaseModel<String>> complaintAndAdvice(@Body RequestBody body);

    //查询申请的订阅权限
    @GET("secure/rest/user/applyReadStatus")
    Flowable<BaseModel<String>> getApplyForRead(@Query("function") String function);

    //检测权限
    @GET("rest/checkPower")
    Flowable<BaseModel<String>> readCheckPower(@Query("module") String module, @Query("code") String code, @Query("type") String type, @Query("url") String url, @Query("extend") String extend);

    //检测自定义跨月基差权限
    @GET("rest/checkApiPower")
    Flowable<BaseModel<List<CrossMetalModel>>> readCheckPowerTwo(@Query("urls") String urls);


    //Vip服务列表
    @GET("secure/rest/user/queryPermissionByUserId")
    Flowable<BaseModel<List<VipService>>> queryPermissionByUserId();

}









