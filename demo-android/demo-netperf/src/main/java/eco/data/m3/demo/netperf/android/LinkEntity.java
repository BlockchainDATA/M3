package eco.data.m3.demo.netperf.android;

import eco.data.m3.net.p2p.channel.PeerLink;

/**
 * author: dai
 * date:   2019-3-12
 * des: 连接实体
 */
public class LinkEntity {

    private String remoteId;
    private int type;
    private String time;
    private String state;
    private String name;
    private int delay;
    private int max;
    private int min;
    private String address;
    private String failedReason = "";

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    //发送并收到回复所需时间
    private long deltaTime;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(long deltaTime) {
        this.deltaTime = deltaTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private int count;

    private PeerLink link;

    public PeerLink getLink() {
        return link;
    }

    public void setLink(PeerLink link) {
        this.link = link;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }
}
