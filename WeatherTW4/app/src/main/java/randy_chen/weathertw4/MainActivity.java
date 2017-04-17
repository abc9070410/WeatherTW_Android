package randy_chen.weathertw4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setClickEvent();

        //handleIntent();

        initAllStationData();
        //test();
    }

    public void setClickEvent()
    {
        Button searchButton = (Button)findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    public void test()
    {
        Common.DP("Hello !");

        double fLat = 24.822834;//24.839068;//24.081400;
        double fLon = 121.183654;//121.009183;//120.538335;

        int iStationIndex = 0;
        String sStationInfo = "";
        String sUrl = "";
        String sText = "";

        //GovData.initAllStationData();

        iStationIndex = Common.getNearLocationIndex(GovData.TYPE_PAST_STATION, fLat, fLon);

        iStationIndex = Common.getNearLocationIndex(GovData.TYPE_PAST_24HR_STATION, fLat, fLon);
        sUrl = GovData.getStationWeatherUrl(GovData.TYPE_PAST_24HR_STATION, iStationIndex);

        Common.DP("URL:" + sUrl);
        new DownloadAndParse(MainActivity.this, Common.MAIN_ACTIVITY).execute(sUrl, Common.PAST_24HR_DATA);
        //sText = doHttpUrlConnectionAction(sUrl);
        //Common.DP(sText);
        //GovData.parsePastRain(sText);


        iStationIndex = Common.getNearLocationIndex(GovData.TYPE_FUTURE_STATION, fLat, fLon);
        sUrl = GovData.getStationWeatherUrl(GovData.TYPE_FUTURE_STATION, iStationIndex);
        Common.DP("URL:" + sUrl);
        new DownloadAndParse(MainActivity.this, Common.MAIN_ACTIVITY).execute(sUrl, Common.FUTURE_DATA);
        //sText = doHttpUrlConnectionAction(sUrl);
        //Common.DP(sText);
        //GovData.parseFutureRain(sText);

    }

    private void getTowerInfo()
    {
        TelephonyManager teleManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        List<NeighboringCellInfo> neighborInfo = teleManager.getNeighboringCellInfo();
        Log.e("xxxxx", "Size: " + neighborInfo.size()  );

        for (int i = 0; i < neighborInfo.size(); i++)
        {
            NeighboringCellInfo cellInfo = neighborInfo.get(i);

            int cid = cellInfo.getCid();
            int lac = cellInfo.getLac();
            int psc = cellInfo.getPsc();
            int rssi = cellInfo.getRssi();
            int networkType = cellInfo.getNetworkType();

            Log.e("N CellInfo", "No." + i +
                    " Cid:" + cid +
                    " Rssi:" + rssi +
                    " Lac:" + lac +
                    " Psc:" + psc +
                    " NetworkType:" + getNetworkType(networkType));
        }

        GsmCellLocation cellLocation = (GsmCellLocation) teleManager.getCellLocation();

        int cid = cellLocation.getCid();
        int lac = cellLocation.getLac();
        int psc = cellLocation.getPsc();
        String str = cellLocation.toString();

        Log.e("My CellInfo", "No." +
                " Cid:" + cid +
                " Lac:" + lac +
                " Psc:" + psc +
                " str:" + str);
    }

    public static String getNetworkType(int networkType)
    {
        switch ( networkType ) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
        }
        return "New type of network";
    }

    private void handleIntent() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // 根据分享的文字更新UI
            //TextView myAwesomeTextView = (TextView) findViewById(R.id.textView);
            //myAwesomeTextView.setText("Get:[" + sharedText + "]");
        }
        //}
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
            DP("無法讀入Resource: " + iResourceID);
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

        DP("All Station Data Init Done");
    }

    // Debug Print
    private static void DP(String str)
    {
        System.out.print(str + "\r\n");
    }
}
