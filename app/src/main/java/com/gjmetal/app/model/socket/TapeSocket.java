package com.gjmetal.app.model.socket;

import com.gjmetal.app.base.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:  盘口
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/9/20  15:13
 */
public class TapeSocket  extends BaseModel {

    private String type;
    private String contract;
    private String name;
    private List<BlocksBean> blocks;


    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public String getContract() {
        return contract == null ? "" : contract;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public List<BlocksBean> getBlocks() {
        if (blocks == null) {
            return new ArrayList<>();
        }
        return blocks;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setBlocks(List<BlocksBean> blocks) {
        this.blocks = blocks;
    }

    public static class BlocksBean {
        private String location;
        private LightDependeOnBean lightDependeOn;
        private List<ItemsBean> items;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public LightDependeOnBean getLightDependeOn() {
            return lightDependeOn;
        }

        public void setLightDependeOn(LightDependeOnBean lightDependeOn) {
            this.lightDependeOn = lightDependeOn;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class LightDependeOnBean {

            private String contract;
            private String attr;
            private String name;
            private String value;

            public String getContract() {
                return contract == null ? "" : contract;
            }

            public String getAttr() {
                return attr == null ? "" : attr;
            }

            public String getName() {
                return name == null ? "" : name;
            }

            public String getValue() {
                return value == null ? "" : value;
            }

            public void setContract(String contract) {
                this.contract = contract;
            }



            public void setAttr(String attr) {
                this.attr = attr;
            }



            public void setName(String name) {
                this.name = name;
            }



            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class ItemsBean {

            private String contract;
            private Object attr;
            private String name;
            private String value;
            private boolean add;//多加的
            private int isColor=0;//0=白色，1红色，2绿色

            public int getIsColor() {
                return isColor;
            }

            public ItemsBean setIsColor(int isColor) {
                this.isColor = isColor;
                return this;
            }

            public ItemsBean(String name, String value, boolean add) {
                this.name = name;
                this.value = value;
                this.add = add;
            }

            public boolean isAdd() {
                return add;
            }

            public ItemsBean setAdd(boolean add) {
                this.add = add;
                return this;
            }

            public String getContract() {
                return contract == null ? "" : contract;
            }

            public Object getAttr() {
                return attr;
            }

            public String getName() {
                return name == null ? "" : name;
            }

            public String getValue() {
                return value == null ? "- -" : value;
            }

            public void setContract(String contract) {
                this.contract = contract;
            }


            public void setAttr(Object attr) {
                this.attr = attr;
            }


            public void setName(String name) {
                this.name = name;
            }


            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
