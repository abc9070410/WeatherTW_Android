package randy_chen.weathertw4;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TodayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);


        updateTodayInfo();
    }

    private void updateTodayInfo()
    {
        //String s="宏碁";
        //new String(s.getBytes("BIG5"),"UTF8");

        String sUrl = "http://www.cwb.gov.tw/V7/forecast/f_index.htm";
        new DownloadAndParse(TodayActivity.this,
                Common.TODAY_ACTIVITY).execute(sUrl, Common.DATA_TODAY);

        //setSample();
    }

    public void updateTodayButton()
    {
        setTodayTableRow();
    }

    private void setSample() {
        addTableRow(new String[]{"2/1", "2/2", "2/1", "2/2", "2/1", "2/2", "2/1", "2/2",
                        "2/1", "2/2", "2/1", "2/2", "2/1", "2/2", "2/1", "2/2"},
                new String[]{"23", "25", "23", "25", "23", "25", "23", "25", "23", "25",
                        "23", "25", "23", "25", "23", "25", "23", "25", "23", "25"},
                new String[]{"1", "2", "1", "2", "1", "2", "1", "2", "1", "2", "1", "2",
                        "1", "2", "1", "2", "1", "2", "1", "2", "1", "2", "1", "2"},
                new String[]{"ok", "good", "ok", "good", "ok", "good", "ok", "good", "ok", "good",
                        "ok", "good", "ok", "good", "ok", "good", "ok", "good", "ok", "good"});
    }

    private Button getNewButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.getBackground().setColorFilter(new LightingColorFilter(Common.getRandomColorCode(), Common.getRandomColorCode()));
        //button.color(Common.getRandomColorCode());
        return button;
    }

    private void addTableRow(String[] time, String[] temperature, String[] status, String[] rainfall) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.today_table_layout);


        for (int i = 0; i < time.length; i++) {
            TableRow row = new TableRow(this);

            row.addView(getNewButton(time[i]));
            row.addView(getNewButton(temperature[i]));
            row.addView(getNewButton(status[i]));
            row.addView(getNewButton(rainfall[i]));

            tableLayout.addView(row, i + 1);
        }
    }

    private void setTodayTableRow() {
        if (Common.todayData == null || Common.todayData.length < 3) {
            return;
        }

        TableLayout tableLayout = (TableLayout) findViewById(R.id.today_table_layout);

        for (int i = 1; i < Common.todayData.length; i++) {
            TableRow row = new TableRow(this);

            row.addView(getNewButton(Common.todayData[i][0]));
            row.addView(getNewButton(Common.todayData[i][1]));
            row.addView(getNewButton(Common.todayData[i][3]));
            row.addView(getNewButton(Common.todayData[i][2]));
            //row.addView(getNewButton(Common.todayData[i][4]));

            tableLayout.addView(row, i);
        }
    }
}
