package randy_chen.weathertw4;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by Chien-Yu on 2016/10/1.
 */
public class Common {

    public static int MAIN_ACTIVITY = 1;
    public static int SEARCH_ACTIVITY = 2;
    public static int PASTINFO_ACTIVITY = 3;
    public static int FEATURE_ACTIVITY = 4;
    public static int TODAY_ACTIVITY = 5;
    public static int OPTION_ACTIVITY = 6;
    public static int HISTORY_ACTIVITY = 7;
    public static int COLLECTION_ACTIVITY = 8;


    public static String DATA_FUTURE = "1";
    public static String DATA_PAST = "2";
    public static String DATA_PAST_24HR = "3";
    public static String DATA_PAST_RAIN = "4";
    public static String DATA_GOOGLEAPIS_JSON = "5";
    public static String DATA_TODAY = "6";
    public static String DATA_HISTORY = "7";
    public static String DATA_COLLECTION = "8";

    public static String SPLIT_INTERNAL_TOKEN = "_";
    public static String SPLIT_EXTERNAL_TOKEN = "#";

    public static String KEY_HISTORY_SEARCH = "KEY_HISTORY_SEARCH";

    private static String gsDistance;
    private static String[] gasStationGPS;

    public static String[][] futureData;
    public static String[][] pastData;
    public static String[][] todayData;
    public static String[][] historyData;

    public static String getGmapSearchLatLngUrl(String location)
    {
        return "http://maps.googleapis.com/maps/api/directions/json?origin=" +
                location + "&destination=" + location + "&sensor=false";
    }

    public static int getRandomColorCode()
    {
        Random rnd = new Random();
        return Color.argb(0, rnd.nextInt(128), rnd.nextInt(128), rnd.nextInt(128));
    }

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    public static int calculateDistanceInKilometer(double userLat, double userLng,
                                            double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }

    private static double getDistanceFromLatLonInKm(
            double lat1, double lon1, double lat2, double lon2)
    {
        /*
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1); // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km

        return d;
        */

        return calculateDistanceInKilometer(lat1, lon1, lat2, lon2);
    }

    private static double deg2rad(double deg)
    {
        return deg * Math.PI / 180;
    }

    public static int getNearLocationIndex(int iStationType, double fLat, double fLon)
    {
        double fMinKM = 1000;
        int fMinIndex = -1;

        double fMinLat = 0;
        double fMinLon = 0;

        //Common.DP("getNearLocationIndex:" + asData[0] + ":" + asData.length + "," + fLat + "," + fLon);

        int iStationAmount = GovData.getStationAmount(iStationType);

        for (int i = 0; i < iStationAmount; i++)
        {
            double fStationLat = GovData.getStationLatitude(iStationType, i);
            double fStationLon = GovData.getStationLongitude(iStationType, i);

            double fKM = getDistanceFromLatLonInKm(fStationLat, fStationLon, fLat, fLon);

            if (fKM < fMinKM)
            {
                fMinKM = fKM;
                fMinIndex = i;

                fMinLat = fStationLat;
                fMinLon = fStationLon;

                //Common.DP("MIN:" + fMinIndex + ":" + fMinKM);
            }
        }

        gsDistance = "" + fMinKM;
        gasStationGPS = new String[]{"" + fMinLat, "" + fMinLon};

        Common.DP("Got nearest site " + GovData.getStationType(iStationType) +
                "[" + fMinIndex + "]: " +
                GovData.getStationInfo(iStationType, fMinIndex, GovData.STATION_LOCATION) + "_" +
                GovData.getStationInfo(iStationType, fMinIndex, GovData.STATION_NAME) + "_" +
                GovData.getStationInfo(iStationType, fMinIndex, GovData.STATION_ID) +
                " DIS:" + gsDistance +
                " GPS:" + gasStationGPS[0] + "_" + gasStationGPS[1]);

        return fMinIndex;
    }


    /**
     * Returns the output from the given URL.
     *
     * I tried to hide some of the ugliness of the exception-handling
     * in this method, and just return a high level Exception from here.
     * Modify this behavior as desired.
     *
     * @param desiredUrl
     * @return
     * @throws Exception
     */
    private static String doHttpUrlConnectionAction(String desiredUrl)
    {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }

            String sText = stringBuilder.toString();

            DP("Download Done : Text length is " + sText.length());

            return sText;
        }
        catch (Exception e)
        {
            DP("Download Fail");
            e.printStackTrace();
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }

        return null;
    }












    /*
    public static void parseTownFutureRain(sText)
    {
        var asTokens = sText.split("<tr");

        // 日期, 時間, 天氣狀況, 溫度, 蒲福風級, 風向, 相對溼度, 降雨機率, 舒適度

        var aasFutureRainData = [];

        for (var i = 1; i < asTokens.length; i ++)
        {
            var asRow = asTokens[i].trim().split("<td");

            var asData = [];

            for (var j = 2; j < asRow.length; j ++)
            {
                var sTemp = "<td" + asRow[j];
                var sData = sTemp.replace( /<[^<>]+>/g, " " ).trim().replace(/\s+/g, "_");

                if (sTemp.indexOf("<img") > 0)
                {
                    var asTemp2 = sTemp.split("\"");
                    sData = asTemp2[1] + "_" + asTemp2[3];
                }

                if (sTemp.indexOf("colspan=") > 0)
                {
                    var iColspan = parseInt(sTemp.split("\"")[1]);

                    for (var k = 0; k < iColspan; k ++)
                    {
                        asData[asData.length] = sData;
                    }
                }
                else
                {
                    asData[asData.length] = sData;
                }
            }

            console.log( i + "," + j + " : " + asData);

            aasFutureRainData[i - 1] = asData;
        }

        return aasFutureRainData;
    }

    */





    // Debug Print
    public static void DP(String str)
    {
        //System.out.print(str + "\r\n");
        Log.e("DEBUG", str);
    }

    //public static void main(String[] args)
    public static void testMain()
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
        //sText = doHttpUrlConnectionAction(sUrl);
        //Common.DP(sText);
        //GovData.parsePastRain(sText);


        iStationIndex = Common.getNearLocationIndex(GovData.TYPE_FUTURE_STATION, fLat, fLon);
        sUrl = GovData.getStationWeatherUrl(GovData.TYPE_FUTURE_STATION, iStationIndex);
        Common.DP("URL:" + sUrl);
        //sText = doHttpUrlConnectionAction(sUrl);
        //Common.DP(sText);
        //GovData.parseFutureRain(sText);

    }

    public static int getWeatherIcon(String iconUrl)
    {
        if (iconUrl.indexOf("01.gif") > 0) {
            return R.drawable.night;
        }
        else if (iconUrl.indexOf("02.gif") > 0) {
            return R.drawable.cloudy;
        }
        else if (iconUrl.indexOf("03.gif") > 0) {
            return R.drawable.more_cloudy;
        }
        else if (iconUrl.indexOf("12.gif") > 0) {
            return R.drawable.day_rainy;
        }
        else if (iconUrl.indexOf("17.gif") > 0) {
            return R.drawable.day_shower;
        }
        else if (iconUrl.indexOf("26.gif") > 0) {
            return R.drawable.rainy;
        }
        else if (iconUrl.indexOf("36.gif") > 0) {
            return R.drawable.shower;
        }
        else
        {
            return R.drawable.icon_target;
        }
    }


}
