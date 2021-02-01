package com.example.android.bluetooth_prototype;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.bluetooth_prototype.ui.BottomNavigationViewHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChartAcitivity extends AppCompatActivity {

    private static final String TAG = "CHART_ACTIVITY";
    private final int ACTIVITY_NUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        setupBottomNavigationView();
    }

    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        BottomNavigationViewHolder.enableNavigation(ChartAcitivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}