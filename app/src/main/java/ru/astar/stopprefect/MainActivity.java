package ru.astar.stopprefect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    public static final int LEFT = 11;
    public static final int RIGHT = 12;

    private ImageView previewImage;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private ImageButton decrementButton;
    private ImageButton incrementButton;
    private Button dateTimeButton;
    private Button saveButton;
    private CheckBox verticalPhotoCheckBox;

    private TextView statusTextView;
    private TextView dateTextView;

    private Tools tools;
    private Photo photo;
    private ArrayList<String> listFiles;
    private ListIterator iterator;
    private SharedPreferences preferences;

    private int photoCount = 0;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // класс инструментов
        tools = new Tools(this);
        tools.createDirectoriesForApplication(); // проверка и создание необходимых файловых дирректорий

        // класс изменения изображения
        photo = new Photo();
        loadSettings();

        listFiles = new ArrayList<>();
        calendar = Calendar.getInstance();

        previewImage = (ImageView) findViewById(R.id.previewImage);
        previousButton = (ImageButton) findViewById(R.id.previousButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        decrementButton = (ImageButton) findViewById(R.id.decrementButton);
        incrementButton = (ImageButton) findViewById(R.id.incrementButton);
        dateTimeButton = (Button) findViewById(R.id.dateTimeButton);
        statusTextView = (TextView) findViewById(R.id.statusText);
        dateTextView = (TextView) findViewById(R.id.dateText);
        saveButton = (Button) findViewById(R.id.saveButton);
        verticalPhotoCheckBox = (CheckBox) findViewById(R.id.verticalPhotoCheckbox);

        previewImage.setOnClickListener(clickListener);

        previousButton.setOnClickListener(clickListener);
        nextButton.setOnClickListener(clickListener);
        saveButton.setOnClickListener(clickListener);
        dateTimeButton.setOnClickListener(clickListener);
        decrementButton.setOnClickListener(clickListener);
        incrementButton.setOnClickListener(clickListener);

        dateTextView.setText(getString(R.string.date) + " " + getDateTimeString());
        statusTextView.setText(getString(R.string.status) + " Добавьте фото в папку Source и нажмите на плюс выше. ");
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.previewImage: // тап по изображению
                    reloadImage();
                    break;
                case R.id.previousButton:
                    checkImage(LEFT);
                    break;

                case R.id.nextButton:
                    checkImage(RIGHT);
                    break;

                case R.id.dateTimeButton:
                    setDateTimeDialog();
                    break;

                case R.id.decrementButton:
                    setDateTime(LEFT);
                    break;

                case R.id.incrementButton:
                    setDateTime(RIGHT);
                    break;

                case R.id.saveButton:
                    saveImage();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.openMenuItem:
                openPhotos();
                break;
            case R.id.settingsMenuItem:
                SettingsActivity.startActivity(this, SettingsActivity.REQUEST_CODE);
                break;
            case R.id.aboutMenuItem:
                aboutInfoShow();
                break;
            case R.id.exitMenuItem:
                finish();
                break;
        }

        return true;
    }

    private void aboutInfoShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("О программе");
        builder.setMessage("Программа для вставки заданных пользователем даты и времени в фотографии.\n" +
                "Как действовать?\n" +
                "При первом запуске программа создает в файловой системе папку для данной программы с именем StopPrefect. " +
                "Это рабочая папка, в которой находится еще две папки Source - для фотографий, в которые необходимо вставить " +
                "дату, и папка Out - для готовых фотографий.\n" +
                "Подготовьте заранее необходимые фото и скопируйте их в папку Source. Позже зайдите в данную программу и " +
                "нажмите на кнопку с плюсом, которая находится сверху. Если папка Source не пуста, то фотографии будут отображаться " +
                "в программе. При помощи кнопок Влево или Вправо вы можете осуществлять навигацию по фото.\n" +
                "Далее необходимо настроить вставку даты и времени. Нажмите сверху в меню на Настройки, и настройте опытным путем " +
                "координаты для вертикального и горизонтального фото, размер текста, цвет и тип шрифта для текста. Что бы вставить " +
                "дату - нажмите кнопку SAVE. В прогремме вы увидете результат и фото сохраниться в папке Out. Фото будет " +
                "перезаписываться при последующем нажатии кнопки SAVE. При помощи кнопки УСТ. ДАТУ вы можете задать любую дату и " +
                "время. Что бы изменять время поминутно в обратную или прямую сторону есть две кнопки по бокам + и - соответственно. " +
                "Если нажать на фотографию, то фотография перезагрузится из папки Source.\n\n" +
                "Автор программы: (c) Astar, 2017\n" +
                "E-mail: rrogea75@gmail.com");
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                loadSettings();
            }
        }
    }

    private void setDateTime(int mode) {
        int minute = calendar.get(Calendar.MINUTE);
        switch (mode) {
            case LEFT:
                minute--;
                break;
            case RIGHT:
                minute++;
                break;
        }
        calendar.set(Calendar.MINUTE, minute);
        dateTextView.setText(getString(R.string.date) + " " + getDateTimeString());
    }

    private void setDateTimeDialog() {
        DatePickerDialog dialogDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateTextView.setText(getString(R.string.date) + " " + getDateTimeString());
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        TimePickerDialog dialogTime = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                dateTextView.setText(getString(R.string.date) + " " + getDateTimeString());
            }
        },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);

        dialogDate.show();
        dialogTime.show();
    }

    // дата и время
    private String getDateTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(calendar.getTime());
    }

    // загрузка настроек
    private void loadSettings() {
        preferences = getSharedPreferences(SettingsActivity.PREF_APP, Context.MODE_PRIVATE);
        if (preferences != null || preferences.getAll() != null) {
            photo.setSize(new Size(preferences.getInt(SettingsActivity.PREF_XPOS, 1000), preferences.getInt(SettingsActivity.PREF_YPOS, 1000)));
            photo.setColor(preferences.getInt(SettingsActivity.PREF_COLOR, Color.RED));
            photo.setFontSize(preferences.getInt(SettingsActivity.PREF_FONTSIZE, 145));
            photo.setFontType(preferences.getInt(SettingsActivity.PREF_FONTTYPE, 0));
            photo.setQuality(preferences.getInt(SettingsActivity.PREF_QUALITY, 70));
        }
    }

    // открыть папку с фотографиями
    private void openPhotos() {
        // получаем список файлов в папке с расширением JPG
        listFiles = tools.getListFiles();
        if (listFiles.isEmpty()) {
            Toast.makeText(this, "Папка Source пуста!", Toast.LENGTH_SHORT).show();
            return;
        }
        iterator = listFiles.listIterator();
        String filename = listFiles.get(0);
        photo.setFilename(filename);
        photo.open();
        setPreviewImage(photo.getCurrent());
        statusTextView.setText(getString(R.string.status) + " Фото " + (photoCount + 1) + " из " + listFiles.size());
    }

    // перезагрузка изображения
    private void reloadImage() {
        if (!listFiles.isEmpty() && photoCount > -1 && photoCount < listFiles.size() + 1) {
            String filename = listFiles.get(photoCount);
            photo.setFilename(filename);
            photo.open();
            setPreviewImage(photo.getCurrent());
            Toast.makeText(this, "Изображение перезагружено!", Toast.LENGTH_SHORT).show();
        }
    }

    // навигация назад - вперед
    private void checkImage(int mode) {
        if (!listFiles.isEmpty()) {

            String filename = "";

            switch (mode) {
                case LEFT:
                    if (iterator.hasPrevious()) {
                        photoCount = iterator.previousIndex();
                        filename = (String) iterator.previous();
                    }

                    Log.d("Main Activity", "Влево = " + filename);
                    break;

                case RIGHT:
                    if (iterator.hasNext()) {
                        photoCount = iterator.nextIndex();
                        filename = (String) iterator.next();
                    }
                    Log.d("Main Activity", "Вправо = " + filename);
                    break;
            }

            Log.d("Main Activity", "Путь = " + filename);
            photo.setFilename(filename);
            photo.open();
            setPreviewImage(photo.getCurrent());
            statusTextView.setText(getString(R.string.status) + " Фото " + (photoCount + 1) + " из " + listFiles.size());
        }
    }


    public void setPreviewImage(Bitmap image) {
        if (image != null) {
            if (image.getWidth() < image.getHeight())
                verticalPhotoCheckBox.setChecked(true);
            else
                verticalPhotoCheckBox.setChecked(false);
            previewImage.setImageBitmap(image);
        }
    }

    // сохранение отредактированного изображения
    private void saveImage() {
        if (!listFiles.isEmpty()) {
            if (photo.getCurrent() != null) {
                // чекбокс, вертикальное или горизонтальное фото
                if (preferences != null) {
                    if (verticalPhotoCheckBox.isChecked()) {
                        photo.setSize(new Size(
                                preferences.getInt(SettingsActivity.PREF_XPOS_VERTICAL, 1000),
                                preferences.getInt(SettingsActivity.PREF_YPOS_VERTICAL, 1000)
                        ));
                    } else {
                        photo.setSize(new Size(
                                preferences.getInt(SettingsActivity.PREF_XPOS, 1000),
                                preferences.getInt(SettingsActivity.PREF_YPOS, 1000)
                        ));
                    }
                }
                photo.setText(getDateTimeString());
                photo.edit();
                setPreviewImage(photo.getCurrent());
                String filename = photo.getFilenameMin().replace("IMG_", "photo_");
                String msg = (photo.save(Tools.DIR_OUT + "/" + filename)) ? "Изображение сохранено " + filename : "Ошибка при сохранении!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                Log.d("Main Activity Class", "Путь сохранения " + Tools.DIR_OUT + "/" + filename);
                return;
            }
        }
        Toast.makeText(this, "Изображение не открыто!", Toast.LENGTH_SHORT).show();
    }
}
