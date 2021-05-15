package com.enex.covt;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class countrygraph extends Fragment{
    String countryName;
    Context context;
    private final Handler handleData = new Handler();
    RequestQueue queue;
    BarData data;
    BarChart graph;
    XAxis xaxis;

    // Pie Chart
    PieChart pieChart;
    private static final String TAG = "countryGraph";

    public countrygraph(String countryName,Context context) {
        this.countryName = countryName;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(context);
        fetchCountryData();

    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView indicator = view.findViewById(R.id.graphIndicator);
        indicator.setText("Graph for "+capitalize(countryName));
        setBarGraph(view);
        setPieChart(view);

    }
    public void setPieChart(View view){
        pieChart = view.findViewById(R.id.pieChartGraph);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Showing cases for "+capitalize(countryName));
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setEnabled(false);
        // legend setup
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextColor(Color.rgb(194, 194, 194));
        l.setDrawInside(false);
        l.setEnabled(true);

    }

    public void loadPieChart(ArrayList<PieEntry> entries){
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries, ""+capitalize(countryName));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }
    public void setBarGraph(View view){
        graph = view.findViewById(R.id.graphicalComparision);
        graph.setDrawBarShadow(false);
        graph.setDrawValueAboveBar(true);
        graph.setPinchZoom(false);
        graph.setDrawGridBackground(false);
        /* Colors used
            - Bar graph background -> 18, 18, 18
            - Grayish Text - > 194, 194, 194
         */
        graph.setGridBackgroundColor(Color.rgb(18, 18, 18));
        Description description = new Description();
        description.setText("Stats for last 7 days");
        graph.setNoDataText("Data not found! Try again by clicking here.");
        graph.setNoDataTextColor(Color.rgb(194, 194, 194));
        graph.setDescription(description);

        graph.getDescription().setTextColor(Color.rgb(194, 194, 194));
        xaxis = graph.getXAxis();

        String[] neededDate = new String[7];
        for (int dateCounter = 0;dateCounter<7;dateCounter++){
            neededDate[dateCounter] = "Day "+ (dateCounter+1);
        }
        xaxis.setTextColor(Color.rgb(194, 194, 194));
        graph.setBackgroundColor(Color.rgb(18, 18, 18));
        graph.getAxisLeft().setTextColor(Color.rgb(194, 194, 194));
        graph.getAxisRight().setTextColor(Color.rgb(194, 194, 194));

        xaxis.setValueFormatter(new IndexAxisValueFormatter(neededDate));
        xaxis.setPosition(XAxis.XAxisPosition.TOP);

//        xaxis.setGranularity(1);
//        xaxis.setGranularityEnabled(true);

        graph.setDragEnabled(true);
        graph.getXAxis().setAxisMinimum(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_countrygraph, container, false);
    }
    public void fetchCountryData(){
        CountryDataForGraph countryDataForGraph = new CountryDataForGraph();
        new Thread(countryDataForGraph).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDateForGraph(int sub){
        LocalDate date = LocalDate.now();
        int d = date.getDayOfMonth() - sub;
        int m = date.getMonthValue();
        String finalDateGraph = m+"/"+d;

        return finalDateGraph;
    }
    public void setGraphData(ArrayList<BarEntry> confirmedEntry,
                             ArrayList<BarEntry> recoveredEntry,
                             ArrayList<BarEntry> deadEntry){
        /*
            @Annotations
            cset = Confirmed Cases Set
            rset = Recovered Case Set
            aset = Active Cases Set
            dset = Death Case Set

         */
        BarDataSet cset = new BarDataSet(confirmedEntry,"Confirmed");
        BarDataSet rset = new BarDataSet(recoveredEntry,"Recovered");
        BarDataSet dset = new BarDataSet(deadEntry,"Death");

        cset.setColor(Color.MAGENTA);
        rset.setColor(Color.GREEN);
        dset.setColor(Color.RED);


        data = new BarData(cset,rset,dset);
        data.setBarWidth(0.15f);
        data.setValueTextColor(Color.rgb(194, 194, 194));
        graph.setData(data);
        graph.getLegend().setTextColor(Color.rgb(194, 194, 194));
        graph.groupBars(0,0.3f,0.1f);
        graph.invalidate();
    }

    public class CountryDataForGraph implements Runnable{
        ArrayList<BarEntry> confirmedEntries = new ArrayList<>();
        ArrayList<BarEntry> deathEntries = new ArrayList<>();
        ArrayList<BarEntry> activeEntries = new ArrayList<>();
        ArrayList<BarEntry> recoveredEntries = new ArrayList<>();

        ArrayList<PieEntry> pieFetchEntry = new ArrayList<>();

        long c=0, r=0, a=0, d=0;
        JSONObject object;
        @RequiresApi(api = Build.VERSION_CODES.O)
        public String getDate(){
            LocalDate date = LocalDate.now();
            int y = date.getYear();
            int d = date.getDayOfMonth() - 1;
            int fromD=d-6;
            int m = date.getMonthValue();
            String finalDate = y+"-"+m+"-"+d;
            String startDate = y+"-"+m+"-"+fromD;

            return "from="+startDate+"T00:00:00Z&to="+finalDate+"T00:00:00Z";
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            String urlAPI = "https://api.covid19api.com/total/country/"+countryName+"?"+getDate();
            Log.d("apiurl","API Fetch URL: "+urlAPI);
            JsonArrayRequest jsonData = new JsonArrayRequest(Request.Method.GET, urlAPI, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (response.length() < 1){
                                toast("Could not find data for "+countryName+"!");
                            }else {
                                try {
                                    for (int i=0;i<response.length();i++){
                                        object = response.getJSONObject(i);
                                           c = object.getInt("Confirmed");
//                                           Log.d("Confirmed","Confirmed Cases: "+c);
                                           d = object.getInt("Deaths");
//                                        Log.d("death","Death Cases: "+d);

                                        r = object.getInt("Recovered");
                                           a = object.getInt("Active");
                                           confirmedEntries.add(new BarEntry(i+1,c));
                                           recoveredEntries.add(new BarEntry(i+1,r));
                                           deathEntries.add(new BarEntry(i+1,d));
                                           activeEntries.add(new BarEntry(i+1,a));

                                           pieFetchEntry.clear();
                                           pieFetchEntry.add(new PieEntry(c,"Confirmed Cases"));
                                            pieFetchEntry.add(new PieEntry(r,"Recovered Cases"));
                                            pieFetchEntry.add(new PieEntry(d,"Death Cases"));

                                    }
                                    setGraphData(confirmedEntries,recoveredEntries,deathEntries);
                                    loadPieChart(pieFetchEntry);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "Error: " + error);
                    if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                        toast("It seems like you do not have internet connection!");
                    } else {
                        toast("Error: " + error.getMessage());
                    }
                }
            });
            queue.add(jsonData);
        }
        protected void toast(String s) {
            Toast.makeText(context, s,Toast.LENGTH_SHORT).show();
        }

    }
    public static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}