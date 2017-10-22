package org.macho.beforeandafter.record.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by yuukimatsushima on 2017/09/17.
 */

public class PictureSaver implements Runnable {
    private static final String FILE_NAME_TEMPLATE = "image-%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
    private File outputDir;
    private byte[] data;
    private Context context;

    public PictureSaver(Context context, byte[] data) {
        this.context = context;
//        outputDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        outputDir = context.getApplicationContext().getFilesDir();
        this.data = data;
    }

    @Override
    public void run() {
        String fileName = String.format(FILE_NAME_TEMPLATE, Calendar.getInstance());
        File outputFile = new File(outputDir, fileName);
        System.out.println("file:" + outputFile);
        try (FileOutputStream output = new FileOutputStream(outputFile)) {
            output.write(data);
            Intent intent = new Intent();
            intent.putExtra("PATH", outputFile.toString());
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
            ((Activity) context).finish();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
