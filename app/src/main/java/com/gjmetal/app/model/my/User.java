package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;

/**
 * Description：用户单例模式
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  18:19
 */
public class User extends BaseModel implements Serializable {
    private static volatile User instance = null;
    private User user;
    private boolean isLoginIng;//是否登录
    public static User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User();
                }
            }
        }
        return instance;
    }

    public User getUser() {
        if (ValueUtil.isEmpty(user)) {
            // 从本地里查找
            Gson gson = new Gson();
            try {
                user = gson.fromJson(SharedUtil.get(Constant.USER), User.class);
            } catch (JsonSyntaxException e) {
            }
        }
        return user;
    }
//
    public void setUser(User user) {
        this.user = user;
        if (ValueUtil.isNotEmpty(user)) {
            Gson gson = new Gson();
            SharedUtil.put(Constant.USER, gson.toJson(user));
        }
    }
    /**
     * p : 0
     * size : 20
     * id : 1051
     * customId : 0
     * username : null
     * password : 96aa87e6c2a1a391d1bfd52f0bc72b4d
     * salt : SgIlr1MhXy
     * mobile : 13641809635
     * source : 0
     * type : exterior
     * gender : 0
     * activated : null
     * realName : null
     * nickName : 桂明星
     * avatarUrl : null
     * phone : null
     * email : null
     * registedAt : 1522386994000
     * lastLoginAt : 1522727133000
     * customRealName : null
     * customNickName : null
     * remark : null
     * membersId : 480000
     * securityLevel : null
     */

    private int p;
    private int size;
    private int id;
    private int customId;
    private String username;
    private String password;
    private String salt;
    private String mobile;
    private String source;
    private String type;
    private String gender;
    private String activated;
    private String realName;
    private String nickName;
    private String avatarUrl;
    private String phone;
    private String email;
    private long registedAt;
    private long lastLoginAt;
    private String customRealName;
    private String customNickName;
    private String remark;
    private int membersId;
    private String securityLevel;
    private String expireDate;//到期日

    public boolean isLoginIng() {
        return ValueUtil.isStrNotEmpty(SharedUtil.get(Constant.TOKEN));
    }

    public void LoginOut(){
        SharedUtil.put(Constant.TOKEN, "");//清除本地缓存token
    }

    public void setLoginIng(boolean loginIng) {
        isLoginIng = loginIng;
    }
    public static void setInstance(User instance) {
        User.instance = instance;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomId() {
        return customId;
    }

    public void setCustomId(int customId) {
        this.customId = customId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getMobile() {
        return mobile == null ? "" : mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getRegistedAt() {
        return registedAt;
    }

    public void setRegistedAt(long registedAt) {
        this.registedAt = registedAt;
    }

    public long getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getCustomRealName() {
        return customRealName;
    }

    public void setCustomRealName(String customRealName) {
        this.customRealName = customRealName;
    }

    public String getCustomNickName() {
        return customNickName;
    }

    public void setCustomNickName(String customNickName) {
        this.customNickName = customNickName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getMembersId() {
        return membersId;
    }

    public void setMembersId(int membersId) {
        this.membersId = membersId;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
}
