package com.enex.covt;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class SecondaryTitleBar extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        Log.d("clickMenu","ID: "+item.getItemId());
        switch(item.getItemId()){
            case 16908332:
            case R.id.home:
                finish();
                break;

            case 2131296565:
            case R.id.refreshSecondary:
                toast("Refreshing! Please wait.");
                finish();
                startActivity(getIntent());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void toast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}
