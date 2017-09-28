package ru.astar.stopprefect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chiralcode.colorpicker.ColorPickerDialog;

public class SettingsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1234;

    public static final String PREF_APP = "Settings";
    public static final String PREF_XPOS = "xpos";
    public static final String PREF_YPOS = "ypos";
    public static final String PREF_XPOS_VERTICAL = "xpos_vertical";
    public static final String PREF_YPOS_VERTICAL = "ypos_vertical";
    public static final String PREF_COLOR = "color";
    public static final String PREF_FONTSIZE = "fontsize";
    public static final String PREF_FONTTYPE = "fonttype";
    public static final String PREF_QUALITY = "quality";

    private EditText xPosEdit, yPosEdit;
    private EditText xPosVerticalEdit, yPosVerticalEdit;
    private EditText fontSizeEdit;
    private Spinner fontTypeSpinner;
    private Button colorButton;
    private TextView codeColorText;
    private TextView qualityImageText;
    private SeekBar qualityImageBar;

    private SharedPreferences preferences;
    private int colorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        xPosEdit = (EditText) findViewById(R.id.xPosEdit);
        yPosEdit = (EditText) findViewById(R.id.yPosEdit);
        xPosVerticalEdit = (EditText) findViewById(R.id.xPosVerticalEdit);
        yPosVerticalEdit = (EditText) findViewById(R.id.yPosVerticalEdit);
        fontSizeEdit = (EditText) findViewById(R.id.fontSizeEdit);
        fontTypeSpinner = (Spinner) findViewById(R.id.fontTypeSpinner);
        colorButton = (Button) findViewById(R.id.colorTextEdit);
        codeColorText = (TextView) findViewById(R.id.colorCodeText);
        qualityImageBar = (SeekBar) findViewById(R.id.qualityEdit);
        qualityImageText = (TextView) findViewById(R.id.qualityText);

        colorButton.setOnClickListener(clickListener);
        qualityImageBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int xpos = 1000;
        int ypos = 1000;
        int xposVertical = 1000;
        int yposVertical = 1000;
        int fontsize = 145;
        int fonttype = 0;
        int qualityImage = 70;
        colorCode = Color.RED;

        preferences = getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        if (preferences != null || preferences.getAll() != null) {
            xpos = preferences.getInt(PREF_XPOS, xpos);
            ypos = preferences.getInt(PREF_YPOS, ypos);
            xposVertical = preferences.getInt(PREF_XPOS_VERTICAL, xposVertical);
            yposVertical = preferences.getInt(PREF_YPOS_VERTICAL, yposVertical);
            fontsize = preferences.getInt(PREF_FONTSIZE, 145);
            fonttype = preferences.getInt(PREF_FONTTYPE, 0);
            colorCode = preferences.getInt(PREF_COLOR, Color.RED);
            qualityImage = preferences.getInt(PREF_QUALITY, 70);
        }

        xPosEdit.setText(String.valueOf(xpos));
        yPosEdit.setText(String.valueOf(ypos));
        xPosVerticalEdit.setText(String.valueOf(xposVertical));
        yPosVerticalEdit.setText(String.valueOf(yposVertical));
        fontSizeEdit.setText(String.valueOf(fontsize));
        fontTypeSpinner.setSelection(fonttype);
        colorButton.setBackgroundColor(colorCode);
        codeColorText.setText(getString(R.string.colorCode)
                + "\nR: " + Color.red(colorCode)
                + "\nG: " + Color.green(colorCode)
                + "\nB: " + Color.blue(colorCode)
        );
        qualityImageBar.setProgress(qualityImage);
        qualityImageText.setText(getString(R.string.quality_text) + " " + qualityImage + "%");

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.colorTextEdit:
                    chooseColor();
                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            if (seekBar == qualityImageBar) {
                qualityImageText.setText(getString(R.string.quality_text) + " " + progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    private void chooseColor() {
        ColorPickerDialog dialog = new ColorPickerDialog(this, colorCode, new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                colorCode = color;
                colorButton.setBackgroundColor(colorCode);
                codeColorText.setText(getString(R.string.colorCode)
                        + "\nR: " + Color.red(colorCode)
                        + "\nG: " + Color.green(colorCode)
                        + "\nB: " + Color.blue(colorCode)
                );
            }
        });
        dialog.setTitle("Выберите цвет для текста...");
        dialog.show();
    }

    public static void startActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Tools tools = new Tools(this);
        switch (item.getItemId()) {
            case R.id.saveSettingsItem:
                saveSettings();
                break;

            case R.id.clearOutDir:
                tools.clearDirectory(Tools.DIR_OUT);
                Toast.makeText(this, "Папка Out очищена!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.clearSourceDir:
                tools.clearDirectory(Tools.DIR_SOURCE);
                Toast.makeText(this, "Папка Source очищена!", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void saveSettings() {
        if (preferences == null) preferences = getSharedPreferences(PREF_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_XPOS, Integer.parseInt(xPosEdit.getText().toString()));
        editor.putInt(PREF_YPOS, Integer.parseInt(yPosEdit.getText().toString()));
        editor.putInt(PREF_XPOS_VERTICAL, Integer.parseInt(xPosVerticalEdit.getText().toString()));
        editor.putInt(PREF_YPOS_VERTICAL, Integer.parseInt(yPosVerticalEdit.getText().toString()));
        editor.putInt(PREF_FONTSIZE, Integer.parseInt(fontSizeEdit.getText().toString()));
        editor.putInt(PREF_FONTTYPE, fontTypeSpinner.getSelectedItemPosition());
        editor.putInt(PREF_COLOR, colorCode);
        editor.putInt(PREF_QUALITY, qualityImageBar.getProgress());
        editor.apply();
        String msg = (editor.commit()) ? "Настройки сохранены!" : "Ошибка при сохранении настроек!";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
