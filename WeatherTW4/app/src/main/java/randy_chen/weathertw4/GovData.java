package randy_chen.weathertw4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Chien-Yu on 2016/7/24.
 */
public class GovData {

    // 2017.4.21 : global TW weather predition : http://www.cwb.gov.tw/V7/forecast/f_index.htm

    public static int TYPE_PAST_STATION      = 1; // PAST_STATIONS
    public static int TYPE_PAST_24HR_STATION = 2; // PAST_24HR_STATIONS
    public static int TYPE_FUTURE_STATION    = 3; // FUTURE_STATIONS
    public static int TYPE_PAST_RAIN_STATION = 4;

    public static int STATION_LOCATION  = 0;
    public static int STATION_NAME      = 1;
    public static int STATION_ID        = 2;
    public static int STATION_LAT       = 3;
    public static int STATION_LON       = 4;


    private static String S_NOT_FOUND   = "NOT_FOUND";

    public static String[][] PAST_STATION;
    public static String[][] PAST_24HR_STATION;
    public static String[][] PAST_RAIN_STATION;
    public static String[][] FUTURE_STATION;

    // [Total:22]
    private static String[][] LOCATION_XID = new String[][] {
            {"0", "基隆市"},
            {"1", "臺北市"},
            {"2", "新北市"},
            {"3", "桃園市"},
            {"4", "新竹市"},
            {"5", "新竹縣"},
            {"6", "苗栗縣"},
            {"7", "臺中市"},
            {"8", "彰化縣"},
            {"9", "南投縣"},
            {"10", "嘉義市"},
            {"11", "嘉義縣"},
            {"12", "雲林縣"},
            {"13", "臺南市"},
            {"14", "高雄市"},
            {"15", "屏東縣"},
            {"16", "宜蘭縣"},
            {"17", "花蓮縣"},
            {"18", "臺東縣"},
            {"19", "澎湖縣"},
            {"20", "金門縣"},
            {"21", "連江縣"}
    };

    public static String getStationType(int iStationType)
    {
        if (iStationType == TYPE_PAST_STATION)
        {
            return "Past ";
        }
        else if (iStationType == TYPE_PAST_24HR_STATION)
        {
            return "Past24HR ";
        }
        else if (iStationType == TYPE_PAST_RAIN_STATION)
        {
            return "PastRain ";
        }
        else
        {
            return "Future ";
        }
    };

    public static int getStationAmount(int iStationType)
    {
        if (iStationType == TYPE_PAST_STATION)
        {
            return PAST_STATION.length;
        }
        else if (iStationType == TYPE_PAST_24HR_STATION)
        {
            return PAST_24HR_STATION.length;
        }
        else if (iStationType == TYPE_PAST_RAIN_STATION)
        {
            return PAST_RAIN_STATION.length;
        }
        else if (iStationType == TYPE_FUTURE_STATION)
        {
            return FUTURE_STATION.length;
        }
        else
        {
            return 0;
        }
    }



    public static String getStationInfo(
            int iStationType, int iStationIndex, int iStationInfoIndex)
    {
        if (iStationType == TYPE_PAST_STATION)
        {
            return PAST_STATION[iStationIndex][iStationInfoIndex];
        }
        else if (iStationType == TYPE_PAST_24HR_STATION)
        {
            return PAST_24HR_STATION[iStationIndex][iStationInfoIndex];
        }
        else if (iStationType == TYPE_PAST_RAIN_STATION)
        {
            return PAST_RAIN_STATION[iStationIndex][iStationInfoIndex];
        }
        else if (iStationType == TYPE_FUTURE_STATION)
        {
            return FUTURE_STATION[iStationIndex][iStationInfoIndex];
        }
        else
        {
            return S_NOT_FOUND;
        }
    }

    // ex. 24
    public static double getStationLatitude(int iStationType, int iStationIndex)
    {
        String str = getStationInfo(iStationType, iStationIndex, STATION_LAT);

        return Double.parseDouble(str);
    }

    // ex. 121
    public static double getStationLongitude(int iStationType, int iStationIndex)
    {
        String str = getStationInfo(iStationType, iStationIndex, STATION_LON);

        return Double.parseDouble(str);
    }

    // ex. sStationID="C1R61" -> return "北大武山步道2.6K_西大武山"
    public static String getStationAddress(String sStationID)
    {
        int iAmount = PAST_RAIN_STATION.length;

        for (int i = 0; i < iAmount; i++)
        {
            if (sStationID.equals(PAST_RAIN_STATION[i][STATION_ID]))
            {
                return PAST_RAIN_STATION[i][STATION_NAME];
            }
        }

        return S_NOT_FOUND;
    }

    //
    public static String getStationWeatherUrl(int iStationType, int iStationIndex)
    {
        String sID = getStationInfo(iStationType, iStationIndex, STATION_ID);
        String sUrl = "";

        if (iStationType == TYPE_PAST_STATION)
        {
            String sLocation = getStationInfo(iStationType, iStationIndex, STATION_LOCATION);
            String sXID = getStationXID(sLocation);

            //sUrl = "http://www.cwb.gov.tw/V7/observe/rainfall/Plot/plotChart_All2.php?stid=" + sID + "&xid=" + sXID;
            sUrl = "http://www.cwb.gov.tw/V7/observe/24real/Data/" + sID + ".htm";
        }
        else if (iStationType == TYPE_PAST_24HR_STATION)
        {
            sUrl = "http://www.cwb.gov.tw/V7/observe/24real/Data/" + sID + ".htm";
        }
        else if (iStationType == TYPE_FUTURE_STATION)
        {
            sUrl = "http://www.cwb.gov.tw/V7/forecast/town368/3Hr/" + sID + ".htm";
        }

        return sUrl;
    }

    public static String getStationXID(String sLocation)
    {
        for (int i = 0; i < LOCATION_XID.length; i++)
        {
            if (sLocation.indexOf(LOCATION_XID[i][1]) >= 0)
            {
                return LOCATION_XID[i][0];
            }
        }

        return S_NOT_FOUND;
    }

    public static String getFileString(String fileName)
    {
        String filePath = "C:\\MY\\DEV\\TestJava\\weatherTW\\weatherTW\\data\\";
        String str = "";
        StringBuffer sb = new StringBuffer( "" );

        if ( new File( filePath + fileName ).exists() )
        {
            try
            {
                FileInputStream fileInputStream = new FileInputStream( filePath + fileName );

                InputStreamReader inputStreamReader = new InputStreamReader( fileInputStream, "UTF8" );

                int ch = 0;
                while ( (ch = inputStreamReader.read()) != -1 )
                {
                    sb.append( ( char ) ch );
                }

                fileInputStream.close(); // 加這句才能讓official.html刪除，還在實驗中

            }
            catch ( IOException e )
            {
                DP("無法讀入" + filePath + fileName);
                e.printStackTrace();
            }
        }
        else
        {
            DP( "沒有找到" + filePath + fileName + "此一檔案" );
        }

        return sb.toString();
    }

    public static int getStationResourceID(int iStationType)
    {
        int iResourceID = 0;
        int iSingleLength = 5;

        if (iStationType == TYPE_PAST_STATION)
        {
            iResourceID = R.raw.station_past;
        }
        else if (iStationType == TYPE_PAST_24HR_STATION)
        {
            iResourceID = R.raw.station_past24hr;
        }
        else if (iStationType == TYPE_FUTURE_STATION)
        {
            iResourceID = R.raw.station_future;
        }
        else if (iStationType == TYPE_PAST_RAIN_STATION)
        {
            iResourceID = R.raw.station_past_rain;

            iSingleLength = 3;
        }

        return iResourceID;
    }

    public static String[][] initStationData(String sAllText)
    {
        DP(" length is " + sAllText.length());

        String[] asTemp = sAllText.split("\\r\\n");

        int iLength = asTemp.length - 1;
        int iSingleLength = 5;

        String[][] aasData = new String[iLength][iSingleLength];

        for (int i = 0; i < iLength; i++)
        {
            aasData[i] = asTemp[i + 1].split(",");
        }

        return aasData;
    }


    public static String[][] parsePastRain(String sText)
    {
        DP("Parse Past Rain");

        String[] asTokens = sText.split("<tr");

        int iLength1 = asTokens.length - 1;
        int iLength2 = 7;

        // iLength1: 有幾組紀錄 + 1 個欄位名稱
        // iLength2: 時間, 溫度, 天氣描述, 風向, 蒲福風級, 相對溼度, 累積雨量

        String[][] aasPastRainData = new String[iLength1][iLength2];

        for (int i = 0; i < iLength1; i++)
        {
            String sTrimRow = asTokens[i + 1].trim();
            String[] asRow;
            int j2 = 0;

            if (i == 0)
            {
                asRow = sTrimRow.split("<th");
            }
            else
            {
                asRow = sTrimRow.split("<td");
            }

            for (int j = 0; j < asRow.length; j ++)
            {
                if (i == 0 && j == 0)
                {
                    j = 1; // skip the first token for the first row
                }

                //DP(j + ":" + asRow[j]);

                String sTemp = "<td" + asRow[j];
                String sData = sTemp.replaceAll("<[^<>]+>", " " ).trim().replaceAll("\\s+", "_");
                sData = sData.replaceAll("&deg;", "°");

                if (j == 2 || j == 6 || j == 7 || j == 9)
                {
                    continue;
                }

                aasPastRainData[i][j2++] = sData;
            }
        }

        for (int i = 0; i < aasPastRainData.length; i ++)
        {
            for (int j = 0; j < aasPastRainData[i].length; j++)
            {
                DP( i + "_" + j + " : " + aasPastRainData[i][j]);
            }
        }

        return aasPastRainData;
    }

    public static String[][] parseFutureRain(String sText)
    {
        DP("Parse Future Rain");

        String[] asTokens = sText.split("<tr");

        int iLength1 = asTokens.length - 1;
        int iLength2 = 18;

        // iLength1: 日期, 時間, 天氣狀況, 溫度, 蒲福風級, 風向, 相對溼度, 降雨機率, 舒適度
        // iLength2: 三小時為單位 , 共 17 個未來天氣預測 + 1 個欄位名稱 .
        String[][] aasTempData = new String[iLength1][iLength2];

        for (int i = 0; i < iLength1; i++)
        {
            String[] asRow = asTokens[i+1].trim().split("<td");

            for (int j = 0; j < asRow.length - 1; j++)
            {
                String sTemp = "<td" + asRow[j+1];
                String sData = sTemp.replaceAll("<[^<>]+>", " " ).trim().replaceAll("\\s+", "_");
                sData = sData.replaceAll("&deg;", "°");

                if (sTemp.indexOf("<img") > 0)
                {
                    String[] asTemp2 = sTemp.split("\"");
                    sData = asTemp2[1] + "_" + asTemp2[3];
                }

                if (sTemp.indexOf("colspan=") > 0)
                {
                    int iColspan = Integer.parseInt(sTemp.split("\"")[1]);

                    /*
                    for (int k = 0; k < iColspan; k ++)
                    {
                        asData[asData.length] = sData;
                    }
                    */

                    //DP("-" + sData);
                }
                else
                {
                    //asData[asData.length] = sData;

                    //DP("+" + sData);
                }

                aasTempData[i][j] = sData;

                //DP( i + "_" + j + " : " + sData);
            }
        }

        String[][] aasFutureRainData = new String[iLength2][iLength1];

        for (int i = 0; i < iLength2; i++)
        {
            for (int j = 0; j < iLength1; j++)
            {
                aasFutureRainData[i][j] = aasTempData[j][i];

                DP( i + "_" + j + " : " + aasTempData[j][i]);
            }
        }

        return aasFutureRainData;
    }

    public static String[][] parseToday(String sText)
    {
        DP("Parse Today Data");

        String[] asTokens = sText.split("<tr ");

        int iLength1 = asTokens.length - 1;
        int iLength2 = 4;

        // _AreaList

        // iLength1: 有幾組紀錄 + 1 個欄位名稱
        // iLength2: 區域 , 縣市名 , 溫度 , 降雨機率 , 天氣描述

        String[][] aasTodayData = new String[iLength1][iLength2];

        int begin = 0;
        int end = 0;

        String token;

        for (int i = 0; i < iLength1; i++)
        {
            String sTrimRow = asTokens[i + 1].trim();
            String[] asRow;
            int j2 = 0;

            asRow = sTrimRow.split("<td");

            for (int j = 1; j < iLength2 + 1; j ++)
            {
                /*
                if (j == 0)
                {
                    end = asRow[j].indexOf("</th>");
                    begin = asRow[j].lastIndexOf(">", end) + 1;
                    aasTodayData[i][j] = asRow[j].substring(begin, end).trim();

                    continue;
                }
                */

                begin = asRow[j].indexOf("title=");

                if (begin > 0)
                {
                    begin = asRow[j].indexOf("\"", begin) + 1;
                    end = asRow[j].indexOf("\"", begin);
                }
                else
                {
                    begin = asRow[j].indexOf("<a");
                    begin = asRow[j].indexOf(">", begin) + 1;
                    end = asRow[j].indexOf("<", begin);
                }

                //DP("row:[" + j + "," + asRow[j] + "] ");
                //DP("begin:" +begin + " end:" + end);
                token = asRow[j].substring(begin, end).trim();

                token = token.replace("&#37;", "%");
                token = token.replace("陰", "");
                token = token.replace("時", "");
                token = token.replace("暫", "");

                aasTodayData[i][j - 1] = token;
                //DP(j + ":" + asRow[j]);
            }
        }

        for (int i = 0; i < aasTodayData.length; i ++)
        {
            for (int j = 0; j < aasTodayData[i].length; j++)
            {
                DP( i + "_" + j + " : " + aasTodayData[i][j]);
            }
        }

        return aasTodayData;
    }




    // Debug Print
    private static void DP(String str)
    {
        //System.out.print(str + "\r\n");
        Common.DP(str);
    }

    // LOCATION_XID: [Total:22]

    // PAST_RAIN_STATION: [Total:824]
    // Not all locations have onling record log
    // ex. http://www.cwb.gov.tw/V7/observe/rainfall/Plot/plotChart_All2.php?stid=C1R61&xid=15
    // ex. http://www.cwb.gov.tw/V7/observe/real/C0S77.htm


    // PAST_STATION : [Total:780]
    // Not all locations have onling record log
    // ex. http://www.cwb.gov.tw/V7/observe/rainfall/Plot/plotChart_All2.php?stid=01A20&xid=2
    // ex. http://www.cwb.gov.tw/V7/observe/real/01A20.htm

    // FUTURE_STATION
    // ex. 3 -> 1001703 -> http://www.cwb.gov.tw/V7/forecast/town368/3Hr/1001703.htm

    // PAST_24HR_STATIONS: [Total: 92]
    // ex. 5 -> 46690 -> http://www.cwb.gov.tw/V7/observe/24real/Data/46690.htm
}
