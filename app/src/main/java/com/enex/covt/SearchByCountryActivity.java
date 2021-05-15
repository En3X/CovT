package com.enex.covt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.media.session.MediaButtonReceiver;
import androidx.viewpager.widget.ViewPager;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SearchByCountryActivity extends SecondaryTitleBar {
    private Fragment stats,graph;
    private ViewPager vp;
    private TabLayout tabLayout;
    private String countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbycountry);
        getSearchTerm(getIntent());
        countryName = getIntent().getExtras().getString("countryname");


        Log.d("countryname","Name: "+countryName);
        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        vp = findViewById(R.id.searchPageViewPager);
        tabLayout = findViewById(R.id.mainTabSearchPage);
        // Load stats and graph fragments
        stats = new countrystats(countryName,this);
        graph = new countrygraph(countryName,this);

        // Setting up view pager
        tabLayout.setupWithViewPager(vp);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(),0);
        vpAdapter.addFragment(stats,"Stats",countryName);
        vpAdapter.addFragment(graph,"Graph",countryName);
        vp.setAdapter(vpAdapter);

    }
    @Override
    protected void onNewIntent(Intent intent) {
        getSearchTerm(intent);
        super.onNewIntent(intent);
    }

    private void getSearchTerm(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchTerm = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(),"Country search: "+searchTerm,Toast.LENGTH_SHORT).show();
            getSharedPreferences("PREFERENCES",MODE_PRIVATE).edit().putString("countryName",searchTerm).apply();
        }
    }
    private class VPAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();
        private List<String> countrySearch = new ArrayList<>();
        public VPAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragmentLayout,String title,String countrySearchName){
            fragments.add(fragmentLayout);
            fragmentTitle.add(title);
        }
        @NotNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}