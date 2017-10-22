package org.macho.beforeandafter.graphe;

/**
 * Created by yuukimatsushima on 2017/10/04.
 */

public class Point {
//    private float x;
    private double x; // 日付なので、doubleにした
    private float y;
    public Point() {

    }

    public Point(double x, float y){
        this.x = x;
        this.y = y;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
