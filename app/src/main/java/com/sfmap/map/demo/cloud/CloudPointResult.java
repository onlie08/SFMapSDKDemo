package com.sfmap.map.demo.cloud;

import java.io.Serializable;

/**
 * 主要用于activity之间传递数据，序列化
 */
public class CloudPointResult implements Serializable{
    public double mLon;
    public double mLat;
}
