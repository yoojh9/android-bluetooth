package com.example.android.bluetooth_prototype.ui;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.android.bluetooth_prototype.ChartAcitivity;
import com.example.android.bluetooth_prototype.DataActivity;
import com.example.android.bluetooth_prototype.MainActivity;
import com.example.android.bluetooth_prototype.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHolder {

    public static void enableNavigation(final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(context, MainActivity.class);
                        context.startActivity(intent1);
                        break;
                    case R.id.navigation_note:
                        Intent intent2 = new Intent(context, DataActivity.class);
                        context.startActivity(intent2);
                        break;
                    case R.id.navigation_chart:
                        Intent intent3 = new Intent(context, ChartAcitivity.class);
                        context.startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }
}
