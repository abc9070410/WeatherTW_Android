package randy_chen.weathertw4;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Chien-Yu on 2016/12/21.
 */
public class DownloadAndParse extends AsyncTask<String, String, String> {

    private AppCompatActivity activity;
    private MainActivity mainActivity;
    private SearchActivity searchActivity;
    private TodayActivity todayActivity;
    private HistoryActivity historyActivity;
    private CollectionActivity collectionActivity;

    private String targetAddress = "";
    private double targetLat = 0;
    private double targetLng = 0;

    private String pastStationAddress = "";
    private String pastStationName = "";
    private String futureStationAddress = "";
    private String futureStationName = "";
    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        /*
        super.onPreExecute();
        showDialog(progress_bar_type);
        */
    }

    public DownloadAndParse(AppCompatActivity activity, int activityIndex)
    {
        if (activityIndex == Common.MAIN_ACTIVITY) {
            this.mainActivity = (MainActivity)activity;
        }
        else if (activityIndex == Common.SEARCH_ACTIVITY) {
            this.searchActivity = (SearchActivity)activity;
        }
        else if (activityIndex == Common.TODAY_ACTIVITY) {
            this.todayActivity = (TodayActivity)activity;
        }

    }

    @Override
    protected String doInBackground(String... f_url) {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try {
            // create the HttpURLConnection
            url = new URL(f_url[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15 * 1000);
            connection.connect();

            int lenghtOfFile = connection.getContentLength();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            int currentTotal = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");

                currentTotal += line.length();

                publishProgress("" + (int) ((currentTotal * 100) / lenghtOfFile));
            }

            String sText = stringBuilder.toString();

            Common.DP("Download Done : Text length is " + sText.length());

            parseData(sText, f_url[1]);

            return sText;
        } catch (Exception e) {
            Common.DP("Download Fail");
            e.printStackTrace();
        } finally {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return null;
    }

    private void parseData(String sText, String sDataType)
    {
        if (Common.DATA_PAST_24HR.equals(sDataType))
        {
            parsePast(sText);
        }
        else if (Common.DATA_FUTURE.equals(sDataType))
        {
            parseFuture(sText);
        }
        else if (Common.DATA_GOOGLEAPIS_JSON.equals(sDataType))
        {
            parseGoogleapisJson(sText);
        }
        else if (Common.DATA_TODAY.equals(sDataType))
        {
            parseToday(sText);
        }
    }

    private void parsePast(String text)
    {
        try {
            text = new String(text.getBytes("BIG5"),"UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Common.pastData = GovData.parsePastRain(text);
        searchActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String date = Common.pastData[1][0];
                String temp = Common.pastData[1][1];
                String humidity = "X";
                String rainfallOneDay = Common.pastData[1][6];
                String rainfallOneHour = "X";
                searchActivity.updatePastInfoButton(date, temp, humidity, rainfallOneDay, rainfallOneHour);
            }
        });
    }

    private void parseFuture(String text)
    {
        Common.futureData = GovData.parseFutureRain(text);
        searchActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String date = Common.futureData[1][0] + " " + Common.futureData[1][1];
                String temp = Common.futureData[1][3];
                String humidity = Common.futureData[1][4];
                String rainChance = Common.futureData[1][8];
                int icon = Common.getWeatherIcon(Common.futureData[1][2]);
                searchActivity.updateFutureInfoButton(date, temp, humidity, rainChance, icon);

            }
        });
    }

    private void parseToday(String text)
    {
        Common.todayData = GovData.parseToday(text);
        todayActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                todayActivity.updateTodayButton();
            }
        });
    }


    public void parseGoogleapisJson(String text)
    {
        try {
            JSONObject jObject = new JSONObject(text);

//            String address = jObject.getString("end_address");
            JSONObject routes = (JSONObject) ((JSONArray)jObject.get("routes")).get(0);
            Common.DP("routes:" + jObject.getString("routes"));
            JSONObject legs = (JSONObject) ((JSONArray)routes.get("legs")).get(0);
            Common.DP("legs:" + routes.getString("legs"));
            String end_address = legs.getString("end_address");
            Common.DP("end_address:" + end_address);
            JSONObject end_location = (JSONObject)legs.get("end_location");
            Common.DP("end_location:" + legs.getString("end_location"));
            Double lat = end_location.getDouble("lat");
            Double lng = end_location.getDouble("lng");
            Common.DP("GPS:" + lat +"," +lng);

            targetAddress = end_address;
            targetLat = lat;
            targetLng = lng;

            int stationType, stationIndex;
            String url = "";

            stationType = GovData.TYPE_PAST_24HR_STATION;
            stationIndex = Common.getNearLocationIndex(stationType, lat, lng);
            url = GovData.getStationWeatherUrl(stationType, stationIndex);
            pastStationAddress = GovData.getStationInfo(stationType, stationIndex, GovData.STATION_LOCATION);
            pastStationName = GovData.getStationInfo(stationType, stationIndex, GovData.STATION_NAME);
            new DownloadAndParse(searchActivity, Common.SEARCH_ACTIVITY).execute(url, Common.DATA_PAST_24HR);


            stationType = GovData.TYPE_FUTURE_STATION;
            stationIndex = Common.getNearLocationIndex(stationType, lat, lng);
            url = GovData.getStationWeatherUrl(stationType, stationIndex);
            futureStationAddress = GovData.getStationInfo(stationType, stationIndex, GovData.STATION_LOCATION);
            futureStationName = GovData.getStationInfo(stationType, stationIndex, GovData.STATION_NAME);
            new DownloadAndParse(searchActivity, Common.SEARCH_ACTIVITY).execute(url, Common.DATA_FUTURE);

            searchActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchActivity.updateTargetButton(targetAddress, targetLat + "," + targetLng);
                    searchActivity.updateDebugPastSiteButton(pastStationName, pastStationAddress);
                    searchActivity.updateDebugFutureSiteButton(futureStationName, futureStationAddress);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Downloading file in background thread
     * */
    /*
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream
            OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }
    */

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        /*
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
        */
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        /*
        // dismiss the dialog after the file was downloaded
        dismissDialog(progress_bar_type);

        // Displaying downloaded image into image view
        // Reading image path from sdcard
        String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
        // setting downloaded into image view
        my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        */
    }
}
