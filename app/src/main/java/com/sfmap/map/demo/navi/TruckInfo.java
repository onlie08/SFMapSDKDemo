package com.sfmap.map.demo.navi;

public class TruckInfo {
    private String plate;//车牌号

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getPowerType() {
        return powerType;
    }

    public void setPowerType(String powerType) {
        this.powerType = powerType;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCheckWeight() {
        return checkWeight;
    }

    public void setCheckWeight(String checkWeight) {
        this.checkWeight = checkWeight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAxleNum() {
        return axleNum;
    }

    public void setAxleNum(String axleNum) {
        this.axleNum = axleNum;
    }

    private String powerType;//动力类型
    private String truckType;//货车类型 1:小车 4:拖挂车 5:微型货车 6:轻型货车 7:中型货车 8:中型货车 9:危险品运输车
    private String weight;//货车重量
    private String checkWeight;//核载重量
    private String length;
    private String width;
    private String height;
    private String axleNum;//轴数

    public TruckInfo(String plate, String powerType, String truckType,
                     String weight, String checkWeight, String length, String width,
                     String height, String axleNum) {
        this.plate = plate;
        this.powerType = powerType;
        this.truckType = truckType;
        this.weight = weight;
        this.checkWeight = checkWeight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.axleNum = axleNum;
    }

    public TruckInfo() {
    }
}