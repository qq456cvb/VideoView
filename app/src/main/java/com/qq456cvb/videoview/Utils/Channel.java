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
    public String hdnType;

    public Channel(int category, String name, String IP, String multicastIP) {
        this.category = category;
        this.name = name;
        this.IP = IP;
        this.multicastIP = multicastIP;
    }
    public String getMulticastIP() {
        return multicastIP;
    }

    public void setMulticastIP(String ip) {
        this.multicastIP = ip;
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

    public void setName(String name) {
        this.name = name;
    }

    public void resetChannel() {
        this.setcategory(1);
        this.setName("CCTV-1");
        this.setMulticastIP("http://220.250.58.250:7000/D*5005*");
        this.id = "1";
        this.hdnDyass = "0";
        this.hdnType = "3";
    }
    // TODO: too lazy to write getters and setters.
}
