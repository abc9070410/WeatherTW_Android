package randy_chen.weathertw4;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        updateHistoryButton();
    }

    public void updateHistoryButton()
    {


        setHistoryTableRow();
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
        TableLayout tableLayout = (TableLayout) findViewById(R.id.history_table_layout);


        for (int i = 0; i < time.length; i++) {
            TableRow row = new TableRow(this);

            row.addView(getNewButton(time[i]));
            row.addView(getNewButton(temperature[i]));
            row.addView(getNewButton(status[i]));
            row.addView(getNewButton(rainfall[i]));

            tableLayout.addView(row, i + 1);
        }
    }

    private void setHistoryTableRow() {

        SharedPreferences sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        String history = sharedPreferences.getString(Common.KEY_HISTORY_SEARCH, null);

        if (history == null)
        {
            Common.DP("No History");
            return;
        }

        String[] multipleHistory = history.split(Common.SPLIT_EXTERNAL_TOKEN);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.history_table_layout);

        for (int i = 0; i < multipleHistory.length; i++) {
            TableRow row = new TableRow(this);

            String[] multipleItem = multipleHistory[i].split(Common.SPLIT_INTERNAL_TOKEN);

            row.addView(getNewButton(multipleItem[0]));
            row.addView(getNewButton(multipleItem[1]));

            tableLayout.addView(row, i + 1);
        }
    }
}
