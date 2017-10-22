package org.macho.beforeandafter.record;

/**
 * Created by yuukimatsushima on 2017/09/18.
 */

public class Record {
    private long date;
    private float weight;
    private float rate;
    private String frontImagePath;
    private String sideImagePath;
    private String memo;
    public Record() {
    }
    public Record(long date, float weight, float rate, String frontImagePath, String sideImagePath, String memo) {
        this.date = date;
        this.weight = weight;
        this.rate = rate;
        this.frontImagePath = frontImagePath;
        this.sideImagePath = sideImagePath;
        this.memo = memo;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getFrontImagePath() {
        return frontImagePath;
    }

    public void setFrontImagePath(String frontImagePath) {
        this.frontImagePath = frontImagePath;
    }

    public String getSideImagePath() {
        return sideImagePath;
    }

    public void setSideImagePath(String sideImagePath) {
        this.sideImagePath = sideImagePath;
    }
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String toString() {
        return date + ", " + weight + ", " + rate + ", " +  frontImagePath + ", " + sideImagePath + ", " + memo;
    }

}
