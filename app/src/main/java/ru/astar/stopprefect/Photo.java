package ru.astar.stopprefect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.util.Size;

import java.io.FileOutputStream;
import java.io.IOException;

class Photo {
    public static final String TAG = "Class Photo";

    private Size size;
    private int fontSize;
    private int fontType;
    private int color;
    private int quality;
    private String text;
    private Bitmap current;
    private String filename;
    private Typeface typeface;

    public Photo() {
        this.size = new Size(10, 10);
        this.fontSize = 100;
        this.fontType = 0;
        this.color = Color.parseColor("#FF9911");
        this.text = "Sample Text";
        this.filename = Environment.DIRECTORY_DCIM + "/test.jpg";
    }

    public Photo(String filename, Size size, int fontSize, int fontType, int color, String text) {
        this.filename = filename;
        this.size = size;
        this.fontSize = fontSize;
        this.fontType = fontType;
        this.color = color;
        this.text = text;
        Log.d(TAG, "Конструктор: " + filename
                + "\nX = " + size.getWidth() + "\nY = " + size.getHeight()
                + "\nРазмер шрифта = " + fontSize
                + "\nТип шрифта = " + fontType
                + "\nЦвет текста = " + color
                + "\nСобственно, текст = " + text
        );
    }

    public void open() {
        if (!filename.isEmpty()) {
            current = BitmapFactory.decodeFile(filename);
            Log.d(TAG, "Открыли изображение #" + filename);
        }
    }

    public boolean save(String newFilename) {
        if (current == null) {
            Log.d(TAG, "Изображение не открыто, сохранить невозможно!");
            return false;
        }
        try {
            FileOutputStream stream = new FileOutputStream(newFilename);
            if (quality <= 10) quality = 70;
            current.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            stream.flush();
            stream.close();
            Log.d(TAG, "Файл записан " + filename);
            return true;
        } catch (IOException e) {
            Log.d(TAG, "Ошибка при записи в файл!");
            return false;
        }
    }

    public void close() {
        current = null;
        Log.d(TAG, "Закрыли изображение.");
    }

    public boolean edit() {
        if (current == null) return false;
        // шрифт по умолчанию
        if (typeface == null) typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        switch (fontType) {
            case 0:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                break;
            case 1:
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
                break;
            case 2:
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
                break;
            case 3:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
                break;
            case 4:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
                break;
            case 5:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
                break;
        }

        Bitmap.Config config = current.getConfig();
        if (config == null) config = Bitmap.Config.ARGB_8888;
        Bitmap source = current.copy(config, true);
        Canvas canvas = new Canvas(source);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(fontSize);
        paint.setTypeface(typeface);
        canvas.drawText(text, size.getWidth(), size.getHeight(), paint);
        current = source;
        Log.d(TAG, "Редактирование изображения " + current.toString());
        return true;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setFontType(int fontType) {
        this.fontType = fontType;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCurrent(Bitmap current) {
        this.current = current;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Size getSize() {
        return size;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getFontType() {
        return fontType;
    }

    public int getColor() {
        return color;
    }

    public int getQuality() {
        return quality;
    }

    public String getText() {
        return text;
    }

    public Bitmap getCurrent() {
        return current;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilenameMin() {
        return filename.substring(filename.lastIndexOf('/'));
    }
}