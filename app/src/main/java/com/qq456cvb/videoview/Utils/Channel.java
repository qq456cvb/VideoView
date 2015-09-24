package com.qq456cvb.videoview.Utils;

/**
 * Created by qq456cvb on 8/28/15.
 */
public class Channel {
    public final static int ZHONGYANG = 1;
    public final static int HUNAN = 2;
    private int category;
    public String id;
    public String hdnDyass;
    private String name;
    private String IP;
    private String multicastIP;

    public Channel(int category, String name, String IP, String multicastIP) {
        this.category = category;
        this.name = name;
        this.IP = IP;
        this.multicastIP = multicastIP;
    }
    public String getMulticastIP() {
        return multicastIP;
    }

    public int getcategory() {
        return category;
    }

    public void setcategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    // TODO: too lazy to write getters and setters.
}
