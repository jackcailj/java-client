package com.dangdang.reader.client.core;

/**
 * Created by cailianjie on 2016-6-27.
 */
public enum DeviceOrientation {
    UP("向上"),
    DOWN("向下"),
    LEFT("向左"),
    RIGHT("向右"),
    CENTER("中间");

    String content="";

    DeviceOrientation(String orientation){
        content=orientation;
    }
}
