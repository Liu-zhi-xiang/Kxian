package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 权限
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/24  19:53
 */
public class ApplyforModel  extends BaseModel {
    private static volatile ApplyforModel instance = null;

    private ApplyforModel applyforModel;
    private boolean isLoginIng;//是否登录
    public static ApplyforModel getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new ApplyforModel();
                }
            }
        }
        return instance;
    }

    public ApplyforModel getApplyforModel() {
        if (ValueUtil.isEmpty(applyforModel)) {
            // 从本地里查找
            Gson gson = new Gson();
            try {
                applyforModel = gson.fromJson(SharedUtil.get(Constant.APPLYFORMODEL), ApplyforModel.class);
            } catch (JsonSyntaxException e) {
            }
        }
        return applyforModel;
    }
    //
    public void setApplyforModel(ApplyforModel applyforModel) {
        this.applyforModel = applyforModel;
        if (ValueUtil.isNotEmpty(applyforModel)) {
            Gson gson = new Gson();
            SharedUtil.put(Constant.APPLYFORMODEL, gson.toJson(applyforModel));
        }
    }
    private String company;
    private String concerned;
    private String function;
    private String mobile;
    private String name;
    private String phone;
    private String platform;
    private int userId;
    private List<String> deindMsg;
    private List<String> expiredMsg;
    private List<String> phones;
    private List<String> trialTimeMsg;
    private String applyUrl;
    private String bookUrl;

    public List<String> getDeindMsg() {
        if (deindMsg == null) {
            return new ArrayList<>();
        }
        return deindMsg;
    }

    public ApplyforModel setDeindMsg(List<String> deindMsg) {
        this.deindMsg = deindMsg;
        return this;
    }

    public List<String> getExpiredMsg() {
        if (expiredMsg == null) {
            return new ArrayList<>();
        }
        return expiredMsg;
    }

    public ApplyforModel setExpiredMsg(List<String> expiredMsg) {
        this.expiredMsg = expiredMsg;
        return this;
    }

    public List<String> getPhones() {
        if (phones == null) {
            return new ArrayList<>();
        }
        return phones;
    }

    public ApplyforModel setPhones(List<String> phones) {
        this.phones = phones;
        return this;
    }

    public List<String> getTrialTimeMsg() {
        if (trialTimeMsg == null) {
            return new ArrayList<>();
        }
        return trialTimeMsg;
    }

    public ApplyforModel setTrialTimeMsg(List<String> trialTimeMsg) {
        this.trialTimeMsg = trialTimeMsg;
        return this;
    }

    public String getApplyUrl() {
        return applyUrl == null ? "" : applyUrl;
    }

    public ApplyforModel setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
        return this;
    }

    public String getBookUrl() {
        return bookUrl == null ? "" : bookUrl;
    }

    public ApplyforModel setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
        return this;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getConcerned() {
        return concerned;
    }

    public void setConcerned(String concerned) {
        this.concerned = concerned;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
