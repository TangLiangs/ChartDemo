package com.example.chart;

import android.content.Context;
import android.graphics.Matrix;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by tangliang
 * on 2020/5/6
 */
public class MainChartConfig {

    // 图表
    private LineChart mMainChart;
    // 图表控制滑块
    private ScrollBlockView mSubScroll;
    // 表格默认缩放比例
    private float scaleX = 6;

    private Context mContext;
    private Matrix mMatrix;

    private List<Entry> entryList = new ArrayList<>();
    private List<Integer> colorList = new ArrayList<>();


    public MainChartConfig(LineChart mainChart, ScrollBlockView scrollView, Context context) {
        mMainChart = mainChart;
        mSubScroll = scrollView;
        mContext = context;
        mMatrix = mMainChart.getViewPortHandler().getMatrixTouch();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        setMainChart();
        setMainXConfig();
        setMainYConfig();
        setScrollConfig();
        setScrollViewRate();
    }

    /**
     * 图表绘制
     */
    public void setData(List<DataModel> dataModels) {
        parseEntry(dataModels);
        setMainLineData();
        refresh();
    }

    /**
     * 刷新表格
     */
    private void refresh() {
        // 设置自动缩放
        float distance = mMainChart.getContentRect().width() * (scaleX - 1);
        mMatrix.setTranslate(-distance, 0);
        //两个参数分别是x,y轴的缩放比例。
        mMatrix.preScale(scaleX, 1f);
        mMatrix = mMainChart.getViewPortHandler().refresh(mMatrix, mMainChart, true);
        mMainChart.notifyDataSetChanged();
        mMainChart.invalidate();
        mSubScroll.setScrollRate(1.0f, scaleX);
    }

    /**
     * 解析数据
     */
    private void parseEntry(List<DataModel> models) {
        // 清除旧的数据
        entryList.clear();
        colorList.clear();

        if (models != null && models.size() > 0) {
            // 取最值，适应高度
            float max = 0;
            float min = 0;
            for (int i = 0; i < models.size(); i++) {
                // 设置Y轴数据
                DataModel model = models.get(i);
                Entry entry = new Entry(model.x, model.y);
                entryList.add(entry);

                // 根据y值，设置点的颜色
                if (model.y <= 4) {
                    colorList.add(mContext.getResources().getColor(R.color.orange));
                } else if (model.y <= 10) {
                    colorList.add(mContext.getResources().getColor(R.color.blue));
                } else {
                    colorList.add(mContext.getResources().getColor(R.color.red));
                }

                if (max < model.y) {
                    max = model.y;
                }

                if (min > model.y) {
                    min = model.y;
                }
            }
            updateY(max, min);
        } else {
            // 设置一个超出范围的值，保证列表不空，可以解决空数据下，不触发绘制的问题
            entryList.add(new Entry(1000, 1000));
        }
    }

    /**
     * 根据图表中的最值进行自适应Y轴的高度
     */
    private void updateY(float max, float min) {
        if (max <= 10) {
            // 最大值不低于15
            max = 15;
        } else {
            max = max + 5;
        }

        if (min > 2) {
            // 最小值不大于0，为了能让4.0的限制线显示完全
            min = 2;
        } else {
            min = min - 2;
        }

        mMainChart.getAxisLeft().setAxisMaximum(max);
        mMainChart.getAxisLeft().setAxisMinimum(min);
    }

    /**
     * 图表设置
     */
    private void setMainChart() {
        Description description = new Description();
        // 不需要右下角的描述文字
        description.setEnabled(false);
        mMainChart.setDescription(description);
        // 不需要背景
        mMainChart.setBackground(null);
        // 可以拖动，而不影响缩放比例
        mMainChart.setDragEnabled(true);
        // 设置没有数据时候的字体样式
        mMainChart.setNoDataText("");
        mMainChart.setNoDataTextColor(mContext.getResources().getColor(R.color.gray));
        // 不需要网格背景
        mMainChart.setDrawGridBackground(false);
        // 不需要边界
        mMainChart.setDrawBorders(false);
        // X轴可以缩放，Y轴不能缩放
        mMainChart.setScaleXEnabled(true);
        mMainChart.setScaleYEnabled(false);
        // 左右两边预留点空白部分
        mMainChart.setExtraOffsets(20, 0, 20, 0);
        // 不需要展示图例
        mMainChart.getLegend().setEnabled(false);
    }


    /**
     * 图表曲线设置
     */
    private void setMainLineData() {

        LineDataSet lineDataSet = new LineDataSet(entryList, null);
        // 使用贝塞尔曲线
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        // 画过的区域不填充
        lineDataSet.setDrawFilled(false);
        // 不需要展示数据的值
        lineDataSet.setDrawValues(false);
        // 使用圆形标记
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setCircleHoleRadius(3);
        // 使用空心圆标记
        lineDataSet.setDrawCircleHole(true);
        // 设置每个圆形标记的颜色
        lineDataSet.setCircleColors(colorList);
        // 设置曲线的颜色
        lineDataSet.setColor(mContext.getResources().getColor(R.color.white));
        // 设置线宽
        lineDataSet.setLineWidth(3f);
        // 需要高亮的指示器
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighlightLineWidth(1);
        lineDataSet.setHighLightColor(mContext.getResources().getColor(R.color.white));
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.enableDashedHighlightLine(8, 8, 0f);
        LineData lineData = new LineData(lineDataSet);
        mMainChart.setData(lineData);
    }

    /**
     * 图表X坐标轴设置
     */
    private void setMainXConfig() {

        XAxis xAxis = mMainChart.getXAxis();
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置最大值，最小值
        xAxis.setAxisMaximum(24f);
        xAxis.setAxisMinimum(0f);
        //设置标签的数量
        xAxis.setLabelCount(5, false);
        xAxis.setDrawGridLines(false);
        // 设置X轴字体
        xAxis.setTextSize(11);
        xAxis.setTextColor(mContext.getResources().getColor(R.color.black));
        // 不要轴线
        xAxis.setDrawAxisLine(false);
        // 不要网格线
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
    }


    /**
     * 图表Y坐标轴设置
     */
    private void setMainYConfig() {
        YAxis yAxis = mMainChart.getAxisLeft();
        // 不需要右边Y轴
        mMainChart.getAxisRight().setEnabled(false);
        // 添加最高和最低两条限制线
        yAxis.addLimitLine(getLimitLine(10, "10", mContext.getResources().getColor(R.color.white)));
        yAxis.addLimitLine(getLimitLine(4, "4.0", mContext.getResources().getColor(R.color.white)));
        // 设置Y轴最小值
        yAxis.setAxisMinimum(0);
        // 设置Y轴最大值
        yAxis.setAxisMaximum(15);
        // 不要网格背景
        yAxis.setDrawGridLines(false);
        yAxis.setGranularityEnabled(false);
        yAxis.setDrawAxisLine(false);
        // 不要标签
        yAxis.setDrawLabels(false);
        // 限制线在数据下方
        yAxis.setDrawLimitLinesBehindData(true);
    }

    /**
     * 获取限制线，限制线设置
     */
    private LimitLine getLimitLine(float limit, String label, int color) {
        // 设置限制值，和标签文字，限制值是血糖中的限制值，标签就是限制值
        LimitLine highLimit = new LimitLine(limit, label);
        // 限制线颜色为白色
        highLimit.setLineColor(color);
        // 线宽度为1
        highLimit.setLineWidth(1);
        // 标签位置在左边
        highLimit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        // 标签字体
        highLimit.setTextSize(10);
        // 标签颜色
        highLimit.setTextColor(color);
        // 使用虚线，设置每个短线的长度，和间隔长度
        highLimit.enableDashedLine(15, 15, 0);
        return highLimit;
    }

    /**
     * 滑块滑动时，图表改变相应的位置
     */
    private void setScrollConfig() {
        mSubScroll.setOnScrollListener(new ScrollBlockView.OnScrollListener() {
            @Override
            public void onScroll(float scrollRate) {
                float distance = mMainChart.getContentRect().width() * (scaleX - 1) * scrollRate;
                mMatrix.setTranslate(-distance, 0);
                mMatrix.preScale(scaleX, 1);
                mMainChart.getViewPortHandler().refresh(mMatrix, mMainChart, true);
            }
        });
    }

    /**
     * 图表拖动或缩放的时候，改变滑块的位置和大小
     */
    private void setScrollViewRate() {
        mMainChart.setOnChartGestureListener(new SimpleChartGestureListener() {
            float start = mMainChart.getViewPortHandler().getTransX();

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                updateMoveView(start);
                start = mMainChart.getViewPortHandler().getTransX();
            }

            @Override
            public void onChartScale(MotionEvent me, float sX, float sY) {
                scaleX = mMainChart.getViewPortHandler().getScaleX();
                updateMoveView(start);
                start = mMainChart.getViewPortHandler().getTransX();
            }
        });
    }

    /**
     * 更新滑块的位置
     */
    private void updateMoveView(float start) {
        float distance = mMainChart.getViewPortHandler().getTransX();
        // 两次滑动间隔小于10,不触发滑块的滑动
        if (distance - start < scaleX && distance - start > -scaleX && scaleX > 1.1) {
            return;
        }

        // 基本不缩放的时候直接充满
        if (scaleX < 1.01) {
            scaleX = 1;
        }

        float scrollRate = 1;
        float total = mMainChart.getContentRect().width() * (scaleX - 1);
        if (total != 0) {
            scrollRate = -distance / total;
        }

        mSubScroll.setScrollRate(scrollRate, scaleX);
    }
}
