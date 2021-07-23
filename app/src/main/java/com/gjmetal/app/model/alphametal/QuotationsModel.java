package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

import java.util.List;
/**
 *  Description: 上期所合约
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  9:59
 *
 */
public class QuotationsModel extends BaseModel {

    private String contract;
    private String contractName;
    private List<NameListBean> nameList;

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public List<NameListBean> getNameList() {
        return nameList;
    }

    public void setNameList(List<NameListBean> nameList) {
        this.nameList = nameList;
    }

    public static class NameListBean {

        private String optionsCode;
        private String name;
        private Long endDate;

        public String getOptionsCode() {
            return optionsCode;
        }

        public void setOptionsCode(String optionsCode) {
            this.optionsCode = optionsCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getEndDate() {
            return endDate;
        }

        public void setEndDate(Long endDate) {
            this.endDate = endDate;
        }
    }
}



