package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Description：期货
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-29 10:50
 */
public class Future extends BaseModel implements Serializable {
    //socket
    private int id;
    private String type;
    private String roomCode;
    private boolean gated;
    private boolean leaf;
    private List<SubItem> subItem;
    private List<RoomItem> roomItem;
    //socket end
    private String name;
    private String typeCode;
    private String describe;
    private boolean hasListDetail;
    private boolean selected;
    private Integer state;//刷新状态
    private String groupType;
    private int reloadInterval;

    public int getReloadInterval() {
        return reloadInterval;
    }

    public Future setReloadInterval(int reloadInterval) {
        this.reloadInterval = reloadInterval;
        return this;
    }

    //自选标记
    private boolean isEnd;
    private boolean check;
    private String contract;
    public Future() {

    }

    public List<RoomItem> getRoomItem() {
        return roomItem;
    }

    public void setRoomItem(List<RoomItem> roomItem) {
        this.roomItem = roomItem;
    }

    public Future(int id, String name, String describe) {
        this.id = id;
        this.name = name;
        this.describe = describe;
    }

    public String getGroupType() {
        return groupType;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }


    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDescribe() {
        return describe;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public boolean isGated() {
        return gated;
    }

    public void setGated(boolean gated) {
        this.gated = gated;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }


    public List<SubItem> getSubItem() {
        return subItem;
    }

    public void setSubItem(List<SubItem> subItem) {
        this.subItem = subItem;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }


    public boolean isHasListDetail() {
        return hasListDetail;
    }

    public void setHasListDetail(boolean hasListDetail) {
        this.hasListDetail = hasListDetail;
    }


    public static class SubItem implements Serializable {
        //socket
        private int id;
        private String groupType;
        private String roomCode;
        private int position;//锚点位置
        // end

        private String type;
        private String name;
        private String describe;
        private boolean hasListDetail;
        private List<RoomItem> roomItem;
        private List<SubItem> subItem;


        public List<SubItem> getSubItem() {
            return subItem;
        }

        public void setSubItem(List<SubItem> subItem) {
            this.subItem = subItem;
        }

        public List<RoomItem> getRoomItem() {
            return roomItem;
        }

        public void setRoomItem(List<RoomItem> roomItem) {
            this.roomItem = roomItem;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGroupType() {
            return groupType;
        }

        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }

        public String getRoomCode() {
            return roomCode;
        }

        public void setRoomCode(String roomCode) {
            this.roomCode = roomCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public boolean isHasListDetail() {
            return hasListDetail;
        }

        public void setHasListDetail(boolean hasListDetail) {
            this.hasListDetail = hasListDetail;
        }
    }
}
