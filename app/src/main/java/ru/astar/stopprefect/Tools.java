package ru.astar.stopprefect;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Tools {
    public static final String TAG = "Class Tools";
    public static final String DIR_SOURCE = Environment.getExternalStorageDirectory() + "/StopPrefect/Source";
    public static final String DIR_OUT = Environment.getExternalStorageDirectory() + "/StopPrefect/Output";

    private Context context;

    public Tools(Context context) {
        this.context = context;
    }

    public void createDirectoriesForApplication() {
        File sourceDirectory = new File(DIR_SOURCE);
        File outDirectory = new File(DIR_OUT);

        if (!sourceDirectory.exists()) {
            String msg = (sourceDirectory.mkdirs()) ? "Папка " + sourceDirectory.getName() + " создана!" : "Ошибка при создании папки " + sourceDirectory.getName();
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
        }

        if (!outDirectory.exists()) {
            String msg = (outDirectory.mkdirs()) ? "Папка " + outDirectory.getName() + " создана!" : "Ошибка при создании папки " + outDirectory.getName();
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> getListFiles() {
        File directorySource = new File(DIR_SOURCE);
        ArrayList<String> list = new ArrayList<>();
        if (directorySource.exists()) {
            if (directorySource.isDirectory()) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String name) {
                        String lowercaseName = name.toLowerCase();
                        if (lowercaseName.endsWith(".jpg")) {
                            return true;
                        }
                        return false;
                    }
                };

                File[] files = directorySource.listFiles(filter);
                Log.d(TAG, "Количество найденых файлов = " + files.length);
                for (File fileItem : files) {
                    list.add(fileItem.getAbsolutePath());
                }
            }
        }

        return list;
    }

    public void clearDirectory(String path) {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                file.delete();
            }
        }

    }

}
