package com.example.chart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LineChart mMainChart;
    private ScrollBlockView mSubScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }, 50);
    }

    private void initView() {
        mMainChart = findViewById(R.id.main_chart);
        mSubScroll = findViewById(R.id.sub_scroll);
    }

    private void initData() {
        MainChartConfig chartConfig = new MainChartConfig(mMainChart, mSubScroll, this);
        chartConfig.setData(getDefaultData());
    }

    /**
     * 获取模拟数据
     */
    private List<DataModel> getDefaultData() {
        Random random = new Random();
        List<DataModel> dataModels = new ArrayList<>();
        for (int i = 0; i < 24; i ++) {
            int y = random.nextInt(20);
            DataModel model = new DataModel(i, y);
            dataModels.add(model);
        }
        return dataModels;
    }
}
