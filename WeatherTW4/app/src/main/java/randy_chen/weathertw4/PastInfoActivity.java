package randy_chen.weathertw4;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class PastInfoActivity extends AppCompatActivity {

    //static String[]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_info);

        setPastDataIntoTableRow();
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
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.getBackground().setColorFilter(new LightingColorFilter(Common.getRandomColorCode(), Common.getRandomColorCode()));
        //button.color(Common.getRandomColorCode());
        return button;
    }

    private void addTableRow(String[] time, String[] temperature, String[] status, String[] rainfall) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.past_info_table_layout);


        for (int i = 0; i < time.length; i++) {
            TableRow row = new TableRow(this);

            row.addView(getNewButton(time[i]));
            row.addView(getNewButton(temperature[i]));
            row.addView(getNewButton(status[i]));
            row.addView(getNewButton(rainfall[i]));

            tableLayout.addView(row, i + 1);
        }
    }

    private void setPastDataIntoTableRow() {
        if (Common.pastData == null || Common.pastData.length < 3) {
            return;
        }

        TableLayout tableLayout = (TableLayout) findViewById(R.id.past_info_table_layout);

        for (int i = 1; i < Common.pastData.length; i++) {
            TableRow row = new TableRow(this);

            row.addView(getNewButton(Common.pastData[i][0]));
            row.addView(getNewButton(Common.pastData[i][1]));
            row.addView(getNewButton(Common.pastData[i][2]));
            row.addView(getNewButton(Common.pastData[i][6]));

            tableLayout.addView(row, i);
        }
    }
}
