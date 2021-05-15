package com.enex.covt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private final Handler handleData = new Handler();
    TextView confirmed, recovered, death, active;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Toolbar setting
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        confirmed = findViewById(R.id.worldConfirmedNumber);
        recovered = findViewById(R.id.worldRecoveredNumber);
        death = findViewById(R.id.worldDeathNumber);
        active = findViewById(R.id.worldActiveNumber);
        queue = Volley.newRequestQueue(this);

        // First time check
//        sendWelcomeMessage();
        fetchData();
        ImageView searchByCountry = findViewById(R.id.viewByContryHolder);
        searchByCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchPage = new Intent(MainActivity.this,SearchPageActivity.class);
                startActivity(searchPage);
            }
        });

    }

    protected void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.title_bar_menu,menu);
        // Make search possible
        SearchView searchView = (SearchView) menu.findItem(R.id.search_main).getActionView();
        searchView.setQueryHint("Country Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query!=null || !query.equals("")){
                    Intent intent = new Intent(MainActivity.this,SearchByCountryActivity.class);
                    intent.putExtra("countryname",query);
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.safetyTips:
                Intent whoPage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.who.int/health-topics/coronavirus#tab=tab_1"));
                startActivity(whoPage);
                break;
//            case R.id.searchByCountryMenuItem:
//                Intent searchPage = new Intent(getApplicationContext(),SearchByCountryActivity.class);
//                searchPage.putExtra("searchCountryName","Nepal");
//                startActivity(searchPage);
//                break;
            case R.id.refresh:
                fetchData();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public void fetchData() {
        WorldDataFetch fetch = new WorldDataFetch();
        new Thread(fetch).start();
    }

    public class WorldDataFetch implements Runnable {
        long c, r, a, d;

        @Override
        public void run() {
            String urlAPI = "https://api.covid19api.com/world/total";
            JsonObjectRequest jsonData = new JsonObjectRequest(Request.Method.GET, urlAPI, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                c = response.getInt("TotalConfirmed");
                                d = response.getInt("TotalDeaths");
                                r = response.getInt("TotalRecovered");
                                a = c - (d + r);
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
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "Error: " + error);
                    if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                        showToast("It seems like you do not have internet connection!");
                    } else {
                        showToast("Error: " + error.getMessage());
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
    }
}