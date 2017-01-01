package eldeveloper13.weatherapp.weatherinfo.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eldeveloper13.weatherapp.R;
import eldeveloper13.weatherapp.WeatherAppApplication;
import eldeveloper13.weatherapp.services.StatusUpdateService;
import eldeveloper13.weatherapp.services.UpdateReceiver;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    public static final double LATITUDE = 43.6532;
    public static final double LONGITUDE = -79.3832;
    @BindView(R.id.weather_tabs)
    View mWeatherTabs;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @Inject
    MainContract.Presenter mPresenter;

    Fragment mFragment;
    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ((WeatherAppApplication)getApplication()).getAppComponent().inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        mFragment = new CurrentWeatherFragment();
        final FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.navigation_main_content, mFragment).commit();

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_current:
                        mFragment = CurrentWeatherFragment.newInstance();
                        break;
                    case R.id.action_short_term:
                        break;
                    case R.id.action_long_term:
                        break;
                }
                final FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.replace(R.id.navigation_main_content, mFragment).commit();
                return false;
            }
        });

        scheduleAlarm();
    }

    private void scheduleAlarm() {
        Intent intent = UpdateReceiver.getIntent(getApplicationContext(), LATITUDE, LONGITUDE, DarkSkyService.CA);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UpdateReceiver.REQUEST_CODE, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 5*60*1000, pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        mPresenter.getCity();
        mPresenter.getWeather();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.detachView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_cancel_update) {
            // Handle the camera action
//            mPresenter.getWeather();
            Intent intent = UpdateReceiver.getIntent(getApplicationContext(), LATITUDE, LONGITUDE, DarkSkyService.CA);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UpdateReceiver.REQUEST_CODE, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //region MainContract.View
    @Override
    public void setWeatherTabsVisible(boolean visible) {
        mWeatherTabs.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showSpinner() {
        // TODO
    }

    @Override
    public void showCurrentWeather(CurrentWeatherModel model) {
        if (mFragment instanceof CurrentWeatherFragment) {
            ((CurrentWeatherFragment) mFragment).showCurrentWeather(model);
        }
    }

    @Override
    public void showError() {

    }
    //endregion
}
