package com.example.pmed.mindfulnessmeditation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.pmed.graphmanager.DemoBase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ListViewBarChartActivity extends DemoBase {

    BarChart chart, chart1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);

        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayList<BarData> list = new ArrayList<BarData>();


        // 4 items
        for (int i = 0; i < 2; i++) {
            list.add(generateData(i + 1));
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        //private Typeface mTf;

        public ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);

            //mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BarData data = getItem(position); //position
            //BarData data1 = getItem(position); //position

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);
                holder.chart1 = (BarChart) convertView.findViewById(R.id.chart1);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            data.setValueTextColor(Color.BLACK);
            holder.chart.setDescription("CHART 1");
            holder.chart.setDescriptionPosition(data.getXValMaximumLength(),data.getYMin()); //HELP
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            // set data
            holder.chart.setData(data);

            // do not forget to refresh the chart
            //holder.chart.invalidate();
            holder.chart.animateY(700, Easing.EasingOption.EaseInCubic);

            // apply styling
            //data1.setValueTextColor(Color.RED);
            holder.chart1.setDescription("CHART 2");
            holder.chart1.setDrawGridBackground(false);

            XAxis xAxis1 = holder.chart1.getXAxis();
            xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis1.setDrawGridLines(false);

            YAxis leftAxis1 = holder.chart1.getAxisLeft();
            leftAxis1.setLabelCount(5, false);
            leftAxis1.setSpaceTop(15f);

            YAxis rightAxis1 = holder.chart1.getAxisRight();
            rightAxis1.setLabelCount(5, false);
            rightAxis1.setSpaceTop(15f);

            // set data
            holder.chart1.setData(data);

            // do not forget to refresh the chart
//            holder.chart.invalidate();
            holder.chart1.animateY(700, Easing.EasingOption.EaseInCubic);

            return convertView;
        }

        private class ViewHolder {

            BarChart chart, chart1;
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateData(int cnt) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        //generates the # of days of data on the charts
        for (int i = 0; i < 12; i++) {
            yVals1.add(new BarEntry((int) (Math.random() * 70) + 30, i));
            yVals2.add(new BarEntry((int) (Math.random() * 70) + 30, i));
        }

/*
        LineDataSet set1, set2;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet)mChart.getData().getDataSetByIndex(1);
            set1.setYVals(yVals1);
            set2.setYVals(yVals2);
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "DataSet 1");
*/

        //****pre and post bars****//
        BarDataSet a = new BarDataSet(yVals1, "Pre");
        a.setBarSpacePercent(20f);
        a.setBarShadowColor(Color.rgb(203, 203, 203));
        a.setColor(Color.rgb(58, 79, 156));


        BarDataSet d = new BarDataSet(yVals2, "Post");
        d.setBarSpacePercent(20f);
        d.setBarShadowColor(Color.rgb(203, 203, 203));
        d.setColor(Color.rgb(255, 164, 1));


        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();
        sets.add(a);
        sets.add(d);

        //returns the days
        BarData cd = new BarData(getDays(), sets);
        return cd;
    }

    private ArrayList<String> getDays() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Day1");
        m.add("Day2");
        m.add("Day3");
        m.add("Day4");
        m.add("Day5");
        m.add("Day6");
        m.add("Day7");
        m.add("Day8");
        m.add("Day9");
        m.add("Day10");
        m.add("Day11");
        m.add("Day12");

        return m;
    }

    public void onClickButton(View v) {

        if (v.getId() == R.id.button_logout) {
            Intent i = new Intent(ListViewBarChartActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}