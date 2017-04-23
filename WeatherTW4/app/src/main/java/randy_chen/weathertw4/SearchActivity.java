package randy_chen.weathertw4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SearchActivity extends AppCompatActivity {

    private String newLine = "\n";// "&#10;H";
    private SearchView searchView;
    private Button passButton;
    private Button futureButton;
    private Button targetButton;
    private Button debugPastSiteButton;
    private Button debugFutureSiteButton;
    private String targetName = "";
    private String targetAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();

        handleIntent();

        setSearchView();
        setClickEvent();

        //updatePastInfoButton("04/07 10:00", "23.4", "90", "1.0", "0");
        //updateFutureInfoButton("04/07 10:00", "23.4", "90", "40%", R.drawable.icon_target);
        //updateTargetButton("彰化大佛", "[23.11, 120.11]");
    }



    private void handleIntent()
    {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if ("text/plain".equals(type)) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            String title = intent.getStringExtra(Intent.EXTRA_TITLE);

            Common.DP("TITLE:" + title);
            Common.DP("TEXT:" + text);

            initAllStationData();

            String name = text.split("\\s+")[0];

            startSearch(name);
        }
    }

    private void init() {
        searchView = (SearchView) findViewById(R.id.location_search_view);
        passButton = (Button) findViewById(R.id.pass_button);
        futureButton = (Button) findViewById(R.id.future_button);
        targetButton = (Button) findViewById(R.id.target_button);
        debugFutureSiteButton = (Button) findViewById(R.id.debug_future_site_button);
        debugPastSiteButton = (Button) findViewById(R.id.debug_past_site_button);

        passButton.setVisibility(View.GONE);
        futureButton.setVisibility(View.GONE);
        targetButton.setVisibility(View.GONE);
        debugPastSiteButton.setVisibility(View.GONE);
        debugFutureSiteButton.setVisibility(View.GONE);
    }

    public void updatePastInfoButton(String date, String temp, String humidity,
                                     String rainfallOneDay, String rainfallOneHour) {

        passButton.setText(date + newLine +
                getResources().getString(R.string.temperature) + ":" + temp + "  " +
                getResources().getString(R.string.relative_humidity) + humidity + newLine +
                getResources().getString(R.string.rainfall_in_one_day) + rainfallOneDay + newLine +
                getResources().getString(R.string.rainfall_in_one_hour) + rainfallOneHour);

        passButton.setVisibility(View.VISIBLE);
    }

    public void updateFutureInfoButton(String date, String temp, String humidity, String rainChance, int icon) {

        Drawable img = getApplicationContext().getResources().getDrawable(icon);
        futureButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null); // left, top, right, bottom


        futureButton.setText(date + newLine +
                getResources().getString(R.string.temperature) + ":" + temp + "  " +
                getResources().getString(R.string.relative_humidity) + humidity + newLine +
                getResources().getString(R.string.rain_chance) + ":" + rainChance);

        futureButton.setVisibility(View.VISIBLE);
    }


    public void updateTargetButton(String address, String coordinate) {

        //Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.icon_target);
        //targetButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null); // left, top, right, bottom

        targetButton.setText(
                getResources().getString(R.string.target_name) + targetName + newLine +
                        getResources().getString(R.string.target_address) + address + newLine +
                        getResources().getString(R.string.target_coordinate) + coordinate);

        targetButton.setVisibility(View.VISIBLE);
    }

    public void updateDebugPastSiteButton(String name, String address) {
        debugPastSiteButton.setText(
                name + newLine + address);

        debugPastSiteButton.setVisibility(View.VISIBLE);
    }

    public void updateDebugFutureSiteButton(String name, String address) {
        debugFutureSiteButton.setText(
                name + newLine + address);

        debugFutureSiteButton.setVisibility(View.VISIBLE);
    }

    public void setClickEvent() {

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, PastInfoActivity.class);
                startActivity(intent);
            }
        });
        futureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, FutureInfoActivity.class);
                startActivity(intent);
            }
        });

        targetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!targetName.equals("")) {
                    String url = "https://www.google.com.tw/maps/place/" + targetName;
                    Common.DP("goto " + url);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else {
                    Common.DP("cannot open map cause no target");
                }


            }
        });




    }

    private void startSearch(String query)
    {
        targetName = query;
        String sUrl = Common.getGmapSearchLatLngUrl(query);
        Common.DP("URL:" + sUrl);

        new DownloadAndParse(SearchActivity.this, Common.SEARCH_ACTIVITY).execute(sUrl, Common.DATA_GOOGLEAPIS_JSON);


        SharedPreferences sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);

        //sharedPreferences.edit().putInt("score" , 100).apply();
        //sharedPreferences.getInt("score" , 0);
    }

    private void setSearchView() {
        searchView.setIconifiedByDefault(false);

        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // your text view here
                        //textView.setText(newText);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        startSearch(query);

                        View view = SearchActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }


                        return true;
                    }
                }
        );
    }

    private void parseLatLng() {

    }

    public String getResourceString(int iResourceID)
    {
        StringBuffer sb = new StringBuffer( "" );
        InputStream myFile = this.getResources().openRawResource(iResourceID);

        try
        {
            InputStreamReader inputStreamReader = new InputStreamReader( myFile, "UTF8" );

            int ch = 0;
            while ( (ch = inputStreamReader.read()) != -1 )
            {
                sb.append( ( char ) ch );
            }

            myFile.close();
        }
        catch ( IOException e ) {
            Common.DP("無法讀入Resource: " + iResourceID);
            e.printStackTrace();
        }

        return sb.toString();
    }

    public void initAllStationData()
    {
        GovData.PAST_STATION = GovData.initStationData(getResourceString(R.raw.station_past));
        GovData.PAST_24HR_STATION = GovData.initStationData(getResourceString(R.raw.station_past24hr));
        GovData.PAST_RAIN_STATION = GovData.initStationData(getResourceString(R.raw.station_past_rain));
        GovData.FUTURE_STATION = GovData.initStationData(getResourceString(R.raw.station_future));

        Common.DP("All Station Data Init Done");
    }

}
