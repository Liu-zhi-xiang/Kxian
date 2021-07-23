package com.gjmetal.app.model.alphametal;

import android.os.Parcel;
import android.os.Parcelable;
/**
 *  Description:  上期所合约计算结果
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  9:59
 *
 */
public class RateComputerModel implements Parcelable {

    /**
     * 期权类型 0:上期所  1.LME
     */
    private String optionsType;

    /** 方向 0:看涨 1:看跌 */
    private String optionsDirection;

    /** 合约Code */
    private String contract;

    /** 行权价格 */
    private String exercisePrice;

    /** Shibor利率 */
    private String rate;

    /** 浮动数值 */
    private String floatNum;

    /** LME到期时间 */
    private Long dueDate;

    public RateComputerModel() {
    }

    protected RateComputerModel(Parcel in) {
        optionsType = in.readString();
        optionsDirection = in.readString();
        contract = in.readString();
        exercisePrice = in.readString();
        rate = in.readString();
        floatNum = in.readString();
        if (in.readByte() == 0) {
            dueDate = null;
        } else {
            dueDate = in.readLong();
        }
    }

    public static final Creator<RateComputerModel> CREATOR = new Creator<RateComputerModel>() {
        @Override
        public RateComputerModel createFromParcel(Parcel in) {
            return new RateComputerModel(in);
        }

        @Override
        public RateComputerModel[] newArray(int size) {
            return new RateComputerModel[size];
        }
    };

    public String getOptionsType() {
        return optionsType;
    }

    public void setOptionsType(String optionsType) {
        this.optionsType = optionsType;
    }

    public String getOptionsDirection() {
        return optionsDirection;
    }

    public void setOptionsDirection(String optionsDirection) {
        this.optionsDirection = optionsDirection;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getExercisePrice() {
        return exercisePrice;
    }

    public void setExercisePrice(String exercisePrice) {
        this.exercisePrice = exercisePrice;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getFloatNum() {
        return floatNum;
    }

    public void setFloatNum(String floatNum) {
        this.floatNum = floatNum;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(optionsType);
        dest.writeString(optionsDirection);
        dest.writeString(contract);
        dest.writeString(exercisePrice);
        dest.writeString(rate);
        dest.writeString(floatNum);
        if (dueDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dueDate);
        }
    }
}










