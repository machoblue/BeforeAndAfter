package org.macho.beforeandafter;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yuukimatsushima on 2017/08/12.
 */

public class RecordDto extends RealmObject {
    @PrimaryKey
    private long date;
    private float weight;
    private float rate;
    private String frontImagePath;
    private String sideImagePath;
    private String memo;
    public RecordDto() {
    }

    public RecordDto(long date, float weight, float rate, String frontImagePath, String sideImagePath, String memo) {
        this.date = date;
        this.weight = weight;
        this.rate = rate;
        this.frontImagePath = frontImagePath;
        this.sideImagePath = sideImagePath;
        this.memo = memo;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
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


}
