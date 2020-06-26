package com.yyb.chartview;

import java.util.Random;

public class Point {
    //x轴
    public float x;
    //y轴
    public float y;
    //买入点
    public boolean isBuy;
    //卖出点
    public boolean isSale;

    public Point(float x, float y, boolean isBuy, boolean isSale) {
        this.x = x;
        this.y = y;
        this.isBuy = isBuy;
        this.isSale = isSale;
    }
}
