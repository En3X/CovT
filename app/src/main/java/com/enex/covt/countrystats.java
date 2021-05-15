package com.enex.covt;

import android.app.Activity;
import android.content.Context;
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
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class countrystats extends Fragment {
    private final Handler handleData = new Handler();
    TextView confirmed, recovered, death, active,indicator;
    RequestQueue queue;
    SearchByCountryActivity activity = (SearchByCountryActivity) getActivity();
    String countryName;
    Context context;

    public countrystats(String countryName,Context context) {
        this.countryName = countryName;
        this.context = context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(context);

    }
    protected void toast(String s) {
        Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        indicator = view.findViewById(R.id.countryStatsIndicator);
        indicator.setText("Stats for "+capitalize(countryName));
        confirmed = view.findViewById(R.id.countryConfirmedNumber);
        active = view.findViewById(R.id.countryActiveNumber);
        death = view.findViewById(R.id.countryDeathNumber);
        recovered = view.findViewById(R.id.countryRecoveredNumber);
        getCountryData();
    }

    public void getCountryData(){
        CountryData countryData = new CountryData();
        new Thread(countryData).start();
    }
    public class CountryData implements Runnable{
        long c=0, r=0, a=0, d=0;
        JSONObject object;
        @RequiresApi(api = Build.VERSION_CODES.O)
        public String getDate(){
            LocalDate date = LocalDate.now();
            int y = date.getYear();
            int d = date.getDayOfMonth() - 1;
            int m = date.getMonthValue();
            String finalDate = y+"-"+m+"-"+d;
            return finalDate;
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            String urlAPI = "https://api.covid19api.com/live/country/"+countryName+"/status/confirmed/date/"+getDate();
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
                                        c += object.getInt("Confirmed");
                                        d += object.getInt("Deaths");
                                        r += object.getInt("Recovered");
                                        a+=object.getInt("Active");
                                    }
                                    handleData.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            confirmed.setText("" + getNotation(c));
                                            recovered.setText("" + getNotation(r));
                                            active.setText("" + getNotation(a));
                                            death.setText("" + getNotation(d));
                                        }
                                    });
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

        protected String getNotation(long data) {
            String notation = "";
            int pre;
            if (data < 1000) {
                notation += data;
            } else if (data < 1000000) {
                pre = (int) data / 1000;
                notation = pre + "K";
            } else if (data < 1000000000) {
                pre = (int) data / 1000000;
                notation = pre + "M";
            } else if (data < 1000000000000L) {
                pre = (int) data / 1000000000;
                notation = pre + "B";
            } else {
                notation = "" + data;
            }
            return notation;
        }
        protected void toast(String s) {
            Toast.makeText(context, s,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countrystats, container, false);
    }

    public static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}