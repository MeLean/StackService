package com.meline.stackservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//google ad
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etUrlInput;
    private EditText etTimeInput;
    private EditText etTimeClose;
    private boolean isServiceRunning = false;
    private SharedPreferencesUtils utilsSP;

    private AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnStartStopService = (Button) findViewById(R.id.btnStartStopService);
        btnStartStopService.setOnClickListener(this);
        etUrlInput = (EditText) findViewById(R.id.etUrlInput);
        etTimeInput = (EditText) findViewById(R.id.etTimeInput);
        etTimeClose = (EditText) findViewById(R.id.etTimeClose);
        utilsSP = new SharedPreferencesUtils(this, getString(R.string.sh_name));

        //ad loader
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //set url if any in sharedPreferences
        String shUrl = utilsSP.getStringFromSharedPreferences(getString(R.string.sh_url));
        if (shUrl != null){
            etUrlInput.setText(shUrl);
        }
        //set time if any in sharedPreferences
        int shTime = utilsSP.getIntFromSharedPreferences(getString(R.string.sh_time));
        if (shTime != 0){
            etTimeInput.setText(String.valueOf(shTime));
        }

        //set close time if any in sharedPreferences
        int shCloseTime = utilsSP.getIntFromSharedPreferences(getString(R.string.sh_close_time));
        etTimeClose.setText(String.valueOf(shCloseTime));


        isServiceRunning = utilsSP.getBooleanFromSharedPreferences(getString(R.string.sh_isServerRunning));
        if (isServiceRunning){
            btnStartStopService.setText(getString(R.string.btn_stop_service));
            TextView twLoadDate = (TextView) findViewById(R.id.twLoadDate);
            twLoadDate.setText(utilsSP.getStringFromSharedPreferences(getString(R.string.sh_nextDate)));
            etUrlInput.setFocusable(false);
            etTimeInput.setFocusable(false);
            etTimeClose.setFocusable(false);
        }else{
            btnStartStopService.setText(getString(R.string.btn_start_service));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.btnStartStopService:
                if (isServiceRunning){
                    alarm.CancelAlarm(this);
                    utilsSP.putStringInSharedPreferences(getString(R.string.sh_nextDate), null);
                    utilsSP.putBooleanInSharedPreferences(getString(R.string.sh_isServerRunning), false);
                    Toast.makeText(MainActivity.this, R.string.service_stopped, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String url_input = etUrlInput.getText().toString();
                    String url = url_input.matches("") ? getString(R.string.default_url) : url_input;
                    url = checkForHTTP(url);
                    utilsSP.putStringInSharedPreferences(getString(R.string.sh_url), url);

                    int timeNum = checkIntInputOnField(etTimeInput, getString(R.string.default_time));
                    int timeCloseNum = checkIntInputOnField(etTimeClose, getString(R.string.zero_string));

                    if(timeNum * 60  < timeCloseNum){
                        Toast.makeText(MainActivity.this,
                                String.format(getString(R.string.greater_value), timeNum, timeCloseNum), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    utilsSP.putIntInSharedPreferences(getString(R.string.sh_time), timeNum);
                    utilsSP.putIntInSharedPreferences(getString(R.string.sh_close_time), timeCloseNum);
                    Date nextDate = CalendarUtils.addTimeToCurrentTime(timeNum);
                    utilsSP.putStringInSharedPreferences(getString(R.string.sh_nextDate), CalendarUtils.stringifyDateInFormat(nextDate));
                    alarm.setRepeatedlyAlarm(this,alarm.manageWaitingTime(this));
                    utilsSP.putBooleanInSharedPreferences(getString(R.string.sh_isServerRunning), true);
                    Toast.makeText(getApplicationContext(), R.string.service_started, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                Toast.makeText(MainActivity.this, R.string.dont_know_what_to_do, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String checkForHTTP(String url) {
        String httpStr = "http://";
        if(url.startsWith(httpStr) || url.startsWith("https://")){
            return url;
        }

        return httpStr + url;
    }

    private int checkIntInputOnField(EditText text, String defaultValue) {
        String timeInput = text.getText().toString();
        String timeStr = timeInput.matches("") ? defaultValue : timeInput;
        int timeNum = 0;
        try{

            timeNum = Integer.parseInt(timeStr);
            if(!defaultValue.equals(getString(R.string.zero_string))){
                if(timeNum <= 0){
                    throw new NumberFormatException();
                }
            }

        }catch(NumberFormatException e){
            Toast.makeText(this, String.format(this.getString(R.string.invalid_time_format), timeInput), Toast.LENGTH_SHORT)
                    .show();
        }

        return timeNum;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
