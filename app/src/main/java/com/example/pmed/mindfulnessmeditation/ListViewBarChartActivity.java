package com.example.pmed.mindfulnessmeditation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pmed.graphmanager.DemoBase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListViewBarChartActivity extends DemoBase {

    BarChart chart1, chart2, chart3, chart4;
    String userId;

    final String TAG_SECTION = "section";
    final String TAG_POSITIVE = "positive";
    final String TAG_NEGATIVE = "negative";
    final String TAG_HEART_RATE = "heart_rate";
    final String TAG_HRV = "heart_rate_variability";
    Integer numOfDays;
    String posT, negT, hrT, hrvT;
    HashMap<String, HashMap<String, HashMap<String, String>>> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w("charts", "made it to charts");
        this.userId = getIntent().getStringExtra("com.example.pmed.USER_ID");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);

        /*
        ListView lv = (ListView) findViewById(R.id.listView1);
        ArrayList<BarData> list = new ArrayList<BarData>();
        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
        */
        new GetData().execute();
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
                holder.chart1 = (BarChart) convertView.findViewById(R.id.chart1);
                //holder.chart2 = (BarChart) convertView.findViewById(R.id.chart2);
                //holder.chart3 = (BarChart) convertView.findViewById(R.id.chart3);
                //holder.chart4 = (BarChart) convertView.findViewById(R.id.chart4);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


//////////////////////CHART 1///////////////////////////////////////
            // apply styling
            //data1.setValueTextColor(Color.RED);
            holder.chart1.setDescription("");
            holder.chart1.setDrawGridBackground(false);

            XAxis xAxis1 = holder.chart1.getXAxis();
            xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis1.setDrawGridLines(false);

            YAxis leftAxis1 = holder.chart1.getAxisLeft();
            leftAxis1.setLabelCount(5, false);
            leftAxis1.setSpaceTop(15f);
            leftAxis1.setSpaceBottom(0f);

            YAxis rightAxis1 = holder.chart1.getAxisRight();
            rightAxis1.setLabelCount(5, false);
            rightAxis1.setSpaceTop(15f);
            rightAxis1.setSpaceBottom(0f);

            // set data
            holder.chart1.setData(data);

            // do not forget to refresh the chart
            //holder.chart.invalidate();
            holder.chart1.animateY(700, Easing.EasingOption.EaseInCubic);

////////////////////CHART 2///////////////////////////////////////////
            // apply styling
            //data1.setValueTextColor(Color.RED);
   /*         holder.chart2.setDescription("");
            holder.chart2.setDrawGridBackground(false);

            XAxis xAxis2 = holder.chart2.getXAxis();
            xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis2.setDrawGridLines(false);

            YAxis leftAxis2 = holder.chart2.getAxisLeft();
            leftAxis2.setLabelCount(5, false);
            leftAxis2.setSpaceTop(15f);
            leftAxis2.setSpaceBottom(0f);

            YAxis rightAxis2 = holder.chart2.getAxisRight();
            rightAxis2.setLabelCount(5, false);
            rightAxis2.setSpaceTop(15f);
            rightAxis2.setSpaceBottom(0f);

            // set data
            holder.chart2.setData(data);

            // do not forget to refresh the chart
            //holder.chart.invalidate();
            holder.chart2.animateY(700, Easing.EasingOption.EaseInCubic);


////////////////////CHART 3///////////////////////////////////////////
            // apply styling
            //data1.setValueTextColor(Color.RED);
            holder.chart3.setDescription("");
            holder.chart3.setDrawGridBackground(false);

            XAxis xAxis3 = holder.chart3.getXAxis();
            xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis3.setDrawGridLines(false);

            YAxis leftAxis3 = holder.chart3.getAxisLeft();
            leftAxis3.setLabelCount(5, false);
            leftAxis3.setSpaceTop(15f);
            leftAxis3.setSpaceBottom(0f);

            YAxis rightAxis3 = holder.chart3.getAxisRight();
            rightAxis3.setLabelCount(5, false);
            rightAxis3.setSpaceTop(15f);
            rightAxis3.setSpaceBottom(0f);

            // set data
            holder.chart3.setData(data);

            // do not forget to refresh the chart
            //holder.chart.invalidate();
            holder.chart3.animateY(700, Easing.EasingOption.EaseInCubic);

////////////////////CHART 4///////////////////////////////////////////
            // apply styling
            //data.setValueTextColor(Color.BLACK);
            holder.chart4.setDescription("");
            //holder.chart.setDescriptionPosition(data.getXValMaximumLength(),data.getYMin()); //HELP
            holder.chart4.setDrawGridBackground(false);

            XAxis xAxis4 = holder.chart4.getXAxis();
            xAxis4.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis4.setDrawGridLines(false);

            YAxis leftAxis4 = holder.chart4.getAxisLeft();
            leftAxis4.setLabelCount(5, false);
            leftAxis4.setSpaceTop(15f);
            leftAxis4.setSpaceBottom(0f);

            YAxis rightAxis4 = holder.chart4.getAxisRight();
            rightAxis4.setLabelCount(5, false);
            rightAxis4.setSpaceTop(15f);
            rightAxis4.setSpaceBottom(0f);

            // set data
            holder.chart4.setData(data);

            // do not forget to refresh the chart
            //holder.chart.invalidate();
            holder.chart4.animateY(700, Easing.EasingOption.EaseInCubic);*/

            return convertView;
        }

        private class ViewHolder {

            BarChart chart1, chart2, chart3, chart4;
        }
    }

    private ArrayList<String> getDays(Integer number_of_days) {

        ArrayList<String> m = new ArrayList<String>();
        for(int i = 1; i <= number_of_days; i++) {
            m.add("Day" + Integer.toString(i));
        }
        return m;
    }

    public void onClickButton(View v) {

        if (v.getId() == R.id.button_logout) {
            setResult(1);
            finish();
        }
    }


    class GetData extends AsyncTask<String, String, String> {

        JSONParser jsParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("id", userId));
            // getting JSON string from URL
            JSONObject json = jsParser.makeHttpRequest("http://meagherlab.co/read_results_for_user.php", "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");

                if (success == 1) {
                    //negT = json.getString("negative");
                    numOfDays = Integer.parseInt(json.getString("number_of_days"));


                    values = new HashMap<String,HashMap<String, HashMap<String,String>>>();

                    JSONArray results = json.getJSONArray("results");

                    for(int i =0; i < results.length(); i++)
                    {
                        JSONObject result = results.getJSONObject(i);

                        String day = result.getString("day");

                        JSONArray res = result.getJSONArray("results");

                        for(int j=0; j<res.length(); j++) {
                            JSONObject currentResult = res.getJSONObject(j);
                            String section = currentResult.getString(TAG_SECTION); // either A or B
                            String pos = currentResult.getString(TAG_POSITIVE);
                            String neg = currentResult.getString(TAG_NEGATIVE);
                            String hr = currentResult.getString(TAG_HEART_RATE);
                            String hrv = currentResult.getString(TAG_HRV);

                            HashMap<String, String> inner = new HashMap<String, String>();
                            inner.put(TAG_POSITIVE, pos);
                            inner.put(TAG_NEGATIVE, neg);
                            inner.put(TAG_HEART_RATE, hr);
                            inner.put(TAG_HRV, hrv);

                            if (values.get(day) == null) {
                                values.put(day, new HashMap<String, HashMap<String, String>>());
                            }
                            values.get(day).put(section, inner);
                        }

                    }

                } else {
                    /*
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    //BuildFormFromDatabase();

                    //TextView tv = (TextView) findViewById(R.id.chart_title);
                    ListView lv = (ListView) findViewById(R.id.listView1);

                    ArrayList<BarData> list = new ArrayList<BarData>();

                    ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yVals4 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yVals5 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yVals6 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yVals7 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yVals8 = new ArrayList<BarEntry>();

                    //generates the # of days of data on the charts

                    for(int i = 1; i <= values.size(); i++)
                    {
                        HashMap<String, HashMap<String, String>> day = values.get(Integer.toString(i));

                        HashMap<String, String> pre = day.get("Pre");
                        String preHR = pre.get(TAG_HEART_RATE);
                        yVals1.add(new BarEntry(Integer.parseInt(preHR), i-1));
                        String preHRV = pre.get(TAG_HRV);
                        yVals3.add(new BarEntry(Integer.parseInt(preHRV), i-1));
                        String prePos = pre.get(TAG_POSITIVE);
                        yVals5.add(new BarEntry(Integer.parseInt(prePos), i-1));
                        String preNeg = pre.get(TAG_NEGATIVE);
                        yVals7.add(new BarEntry(Integer.parseInt(preNeg), i - 1));

                        HashMap<String, String> post = day.get("Post");
                        String postHR = post.get(TAG_HEART_RATE);
                        yVals2.add(new BarEntry(Integer.parseInt(postHR), i-1));
                        String postHRV = post.get(TAG_HRV);
                        yVals4.add(new BarEntry(Integer.parseInt(postHRV), i-1));
                        String postPos = post.get(TAG_POSITIVE);
                        yVals6.add(new BarEntry(Integer.parseInt(postPos), i-1));
                        String postNeg = post.get(TAG_NEGATIVE);
                        yVals8.add(new BarEntry(Integer.parseInt(postNeg), i - 1));

                    }

                    //final boolean negative = title1.add(new BarEntry("negative", negT));
                    //String m = getString(TAG_NEGATIVE);
                    //setTitle(TAG_NEGATIVE);

                    //****pre and post bars****//
                    BarDataSet a = new BarDataSet(yVals1, "Pre HR");
                    a.setBarSpacePercent(20f);
                    a.setBarShadowColor(Color.rgb(203, 203, 203));
                    a.setColor(Color.rgb(58, 79, 156));
                    BarDataSet b = new BarDataSet(yVals2, "Post HR");
                    b.setBarSpacePercent(20f);
                    b.setBarShadowColor(Color.rgb(203, 203, 203));
                    b.setColor(Color.rgb(255, 164, 1));

                    //****pre and post bars****//
                    BarDataSet c = new BarDataSet(yVals3, "Pre HRV");
                    c.setBarSpacePercent(20f);
                    c.setBarShadowColor(Color.rgb(203, 203, 203));
                    c.setColor(Color.rgb(58, 79, 156));
                    BarDataSet d = new BarDataSet(yVals4, "Post HRV");
                    d.setBarSpacePercent(20f);
                    d.setBarShadowColor(Color.rgb(203, 203, 203));
                    d.setColor(Color.rgb(255, 164, 1));

                    //****pre and post bars****//
                    BarDataSet e = new BarDataSet(yVals5, "Pre Positive Emotions");
                    e.setBarSpacePercent(20f);
                    e.setBarShadowColor(Color.rgb(203, 203, 203));
                    e.setColor(Color.rgb(58, 79, 156));
                    BarDataSet f = new BarDataSet(yVals6, "Post Positive Emotions");
                    f.setBarSpacePercent(20f);
                    f.setBarShadowColor(Color.rgb(203, 203, 203));
                    f.setColor(Color.rgb(255, 164, 1));

                    //****pre and post bars****//
                    BarDataSet g = new BarDataSet(yVals7, "Pre Negative Emotions");
                    g.setBarSpacePercent(20f);
                    g.setBarShadowColor(Color.rgb(203, 203, 203));
                    g.setColor(Color.rgb(58, 79, 156));
                    BarDataSet h = new BarDataSet(yVals8, "Post Negative Emotions");
                    h.setBarSpacePercent(20f);
                    h.setBarShadowColor(Color.rgb(203, 203, 203));
                    h.setColor(Color.rgb(255, 164, 1));

                    //BarDataSet i = new BarDataSet(title1, "NEGATIVE");



                    ArrayList<IBarDataSet> sets1 = new ArrayList<IBarDataSet>();
                    sets1.add(a);
                    sets1.add(b);

                    ArrayList<IBarDataSet> sets2 = new ArrayList<IBarDataSet>();
                    sets2.add(c);
                    sets2.add(d);

                    ArrayList<IBarDataSet> sets3 = new ArrayList<IBarDataSet>();
                    sets3.add(e);
                    sets3.add(f);

                    ArrayList<IBarDataSet> sets4 = new ArrayList<IBarDataSet>();
                    sets4.add(g);
                    sets4.add(h);



                    BarData data1 = new BarData(getDays(numOfDays), sets1);
                    //tv.setText(negT);
                    list.add(data1);
                    BarData data2 = new BarData(getDays(numOfDays), sets2);
                    list.add(data2);
                    BarData data3 = new BarData(getDays(numOfDays), sets3);
                    list.add(data3);
                    BarData data4 = new BarData(getDays(numOfDays), sets4);
                    list.add(data4);

                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);


                }
            });

        }

    }
}
