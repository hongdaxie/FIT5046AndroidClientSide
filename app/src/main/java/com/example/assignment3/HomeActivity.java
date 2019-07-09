package com.example.assignment3;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mapquest.mapping.MapQuest;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapQuest.start(getApplicationContext());

        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
//        getSupportActionBar().setTitle("Navigation Drawer");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new DisplayHomeFragment()).commit();


        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String fname = intent.getStringExtra("fname");
        String userid = intent.getStringExtra("userid");
        SharedPreferences spUsername = getSharedPreferences("names",Context.MODE_PRIVATE);
        SharedPreferences.Editor eMyUsername = spUsername.edit();
        eMyUsername.putString("username",username);
        eMyUsername.putString("fname",fname);
        eMyUsername.putString("userid",userid);
        eMyUsername.apply();

        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, ScheduledIntentService.class);
        PendingIntent pendingIntent =PendingIntent.getService(this,100,alarmIntent,0);

        //set timer you want alarm to work 23:59:00
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000,pendingIntent);


//
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment nextFragment = null;
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_home){
            nextFragment = new DisplayHomeFragment();

            fragmentManager.beginTransaction().replace(R.id.content_frame,nextFragment).commit();
        }if (id == R.id.nav_report){
            nextFragment = new DisplayReportFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame,nextFragment).commit();
        }if (id == R.id.nav_daily_diet){
            nextFragment = new DisplayDailyDietFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame,nextFragment).commit();
        }if (id == R.id.nav_steps){
            nextFragment = new DisplayStepsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame,nextFragment).commit();
        }if (id == R.id.nav_go_to_map){
            nextFragment = new DisplayGoToMapFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame,nextFragment).commit();
        }if (id == R.id.nav_logout){
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





}
