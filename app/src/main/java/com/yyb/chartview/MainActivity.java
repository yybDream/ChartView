package com.yyb.chartview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Point> points=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Random random = new Random();
        ChartView chartView = findViewById(R.id.cv);
        for (int i = 0; i < 40; i++) {
            float y = random.nextInt(80);
            points.add(new Point(i,y ,false,false));
        }
        chartView.setData(points);
    }
}
