package com.gjmetal.app.model.alphametal;
/**
 *  Description:  行权价格
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  9:59
 *
 */
public class Rate {

    private String optionsDirection; //方向 0:看涨 1:看跌
    private String optionsType; //期权类型   0:上期所  1.LME
    private String contract; // 合约Code
    private Long dueDate;// LME到期时间

    public String getOptionsDirection() {
        return optionsDirection;
    }

    public void setOptionsDirection(String optionsDirection) {
        this.optionsDirection = optionsDirection;
    }

    public String getOptionsType() {
        return optionsType;
    }

    public void setOptionsType(String optionsType) {
        this.optionsType = optionsType;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }
}
