package randy_chen.weathertw4;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class FutureInfoActivity extends AppCompatActivity {

    //static String[]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_info);

        setFutureDataIntoTableRow();
        //setSample();
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

        if (text == null)
        {
            text = "";
        }

        if (text.indexOf(".gif") > 0)
        {
            Common.DP("PIC:" +text);

            String[] token = text.split("_");

            if (token.length == 2)
            {
                button.setText(token[1]);
            }

            //String note = text.split("_")[1];
            //int icon = Common.getWeatherIcon(text);

            //Drawable img = getApplicationContext().getResources().getDrawable(icon);
            //button.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null); // left, top, right, bottom
        }
        else {

            button.setText(text);

        }
        button.setTextColor(Color.WHITE);
        button.getBackground().setColorFilter(new LightingColorFilter(Common.getRandomColorCode(), Common.getRandomColorCode()));
        //button.color(Common.getRandomColorCode());
        return button;
    }

    private void addTableRow(String[] time, String[] temperature, String[] status, String[] rainfall) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.future_info_table_layout);


        for (int i = 0; i < time.length; i++) {
            TableRow row = new TableRow(this);

            row.addView(getNewButton(time[i]));
            row.addView(getNewButton(temperature[i]));
            row.addView(getNewButton(status[i]));
            row.addView(getNewButton(rainfall[i]));

            tableLayout.addView(row, i + 1);
        }
    }

    private void setFutureDataIntoTableRow() {
        if (Common.futureData == null || Common.futureData.length < 3) {
            return;
        }

        TableLayout tableLayout = (TableLayout) findViewById(R.id.future_info_table_layout);

        for (int i = 1; i < Common.futureData.length; i++) {
            TableRow row = new TableRow(this);

            if (Common.futureData[i][0] == null)
            {
                Common.futureData[i][0] = "";
            }

            row.addView(getNewButton(Common.futureData[i][0].split("_")[0] + " " + Common.futureData[i][1]));
            row.addView(getNewButton(Common.futureData[i][3]));
            row.addView(getNewButton(Common.futureData[i][2]));
            row.addView(getNewButton(Common.futureData[i][8]));

            tableLayout.addView(row, i);
        }
    }
}
