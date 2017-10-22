package org.macho.beforeandafter.graphe;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuukimatsushima on 2017/10/04.
 */

public class DateLongValueFloat {
    private long date;
    private float value;

    public DateLongValueFloat(long date, float value) {
        this.date = date;
        this.value = value;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String toString() {
        return "(" + new SimpleDateFormat("yy-MM-dd hh:mm:ss").format(new Date(date)) + ", " + value + ")";
    }
}
