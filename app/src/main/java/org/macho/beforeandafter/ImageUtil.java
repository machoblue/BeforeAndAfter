package org.macho.beforeandafter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by yuukimatsushima on 2018/02/03.
 */

public class ImageUtil {

    public static void setOrientationModifiedImageFile(ImageView imageView, File file) {
        if (file == null || !file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath())); // 今までと同じ仕様（PhotoActivityでファイルが存在しなければ、真っ黒）を維持するため。
            return;
        }
        try {
            int imageViewWidth = imageView.getWidth();
            int imageViewHeight = imageView.getHeight();

            imageView.setScaleType(ImageView.ScaleType.MATRIX);

            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            imageView.setImageBitmap(bitmap);

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            ExifInterface exifInterface = new ExifInterface(file.getPath());
            int orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));

            float ratio = 1f;
            Matrix matrix = new Matrix();
            System.out.println("orientation:" + orientation);

            switch (orientation) {
                // Undefined, Flip, TransXXXは対応しない。
                case ExifInterface.ORIENTATION_UNDEFINED: // 0
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: //2
                case ExifInterface.ORIENTATION_FLIP_VERTICAL: // 4
                case ExifInterface.ORIENTATION_TRANSPOSE: // 5 // TRANSPOST:転置 行と列の入れ替え
                case ExifInterface.ORIENTATION_TRANSVERSE: // 7
                case ExifInterface.ORIENTATION_NORMAL: // 1
                    // cropCenterを自作する。-> 縦横それぞれの　表示サイズ/画像サイズ　の大きい方を採用。
                    ratio = Math.max((float) imageViewWidth / (float) bitmapWidth, (float) imageViewHeight / (float) bitmapHeight);
                    matrix.postScale(ratio, ratio); // TODO:preとpostの違いがわからない。postで試してみる。
                    float scaledBitmapWidth = bitmapWidth * ratio;
                    float scaledBitmapHeight = bitmapHeight * ratio;
                    matrix.postTranslate((imageViewWidth - (scaledBitmapWidth + imageViewWidth) / 2),
                            (imageViewHeight - (scaledBitmapHeight + imageViewHeight) / 2)); // 自作cropCenter
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180: // 3
                    matrix.postRotate(180, bitmapWidth / 2, bitmapHeight / 2);
                    ratio = Math.max((float) imageViewWidth / (float) bitmapWidth, (float) imageViewHeight / (float) bitmapHeight);
                    matrix.postScale(ratio, ratio);
                    float scaledBitmapWidth2 = bitmapWidth * ratio;
                    float scaledBitmapHeight2 = bitmapHeight * ratio;
                    matrix.postTranslate((imageViewWidth - (scaledBitmapWidth2 + imageViewWidth) / 2),
                            (imageViewHeight - (scaledBitmapHeight2 + imageViewHeight) / 2)); // 自作cropCenter
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90: // 6
                    matrix.postRotate(90);
                    matrix.postTranslate(bitmapHeight, 0f);
                    ratio = Math.max((float) imageViewWidth / (float) bitmapHeight, (float) imageViewHeight / (float) bitmapWidth);
                    matrix.postScale(ratio, ratio);
                    float scaledBitmapWidth3 = bitmapWidth * ratio;
                    float scaledBitmapHeight3 = bitmapHeight * ratio;
                    matrix.postTranslate((imageViewWidth - (scaledBitmapHeight3 + imageViewWidth) / 2),
                            (imageViewHeight - (scaledBitmapWidth3 + imageViewHeight) / 2)); // 自作cropCenter
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270: // 8
                    matrix.postRotate(270);
                    matrix.postTranslate(0f, bitmapWidth);
                    ratio = Math.max((float) imageViewWidth / (float) bitmapHeight, (float) imageViewHeight / (float) bitmapWidth);
                    matrix.postScale(ratio, ratio);
                    float scaledBitmapWidth4 = bitmapWidth * ratio;
                    float scaledBitmapHeight4 = bitmapHeight * ratio;
                    matrix.postTranslate((imageViewWidth - (scaledBitmapHeight4 + imageViewWidth) / 2),
                            (imageViewHeight - (scaledBitmapWidth4 + imageViewHeight) / 2)); // 自作cropCenter
                    break;
            }
            imageView.setImageMatrix(matrix);
            imageView.invalidate();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getOrientationModifiedBitmap(Bitmap bitmap, File file) {
        int orientation = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(file.getPath());
            orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getOrientationModifiedBitmap(bitmap, orientation);
    }

    public static Bitmap getOrientationModifiedBitmap(Bitmap bitmap, int orientation) {
        if (orientation != 3 && orientation != 6 && orientation != 8) {
            // 何もしない
            return bitmap;
        }

        Matrix matrix = new Matrix();
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180, ((float) bitmapWidth) / 2, ((float) bitmapHeight) /2);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                matrix.postTranslate(bitmapHeight, 0f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(280);
                matrix.postTranslate(0f, bitmapWidth);
                break;
        }

        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        releaseBitmap(bitmap);
        return newBitmap;
    }

    /*
    public static String uriToPath(Uri uri, Context context) {
        if (uri == null || context == null) {
            return null;
        }

        String path = null;

        String documentId = DocumentsContract.getDocumentId(uri);
        System.out.println("DOCUMENTID:" + documentId);
        String[] splitDocumentId = documentId.split(":");
        String id = splitDocumentId[splitDocumentId.length - 1];
        System.out.println("ID:" + id);

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.MediaColumns.DATA},
                "_id=?",
                new String[] {id},
                null);

        if (cursor.moveToFirst()) {
            path = cursor.getString(0);
        }
        cursor.close();

        System.out.println("PATH:" + path);

        return path;
    }

    public static int getOrientation(String path) {
        if (path == null || "".equals(path) || !new File(path).exists()) {
            System.out.println("path:" + path + " not exists");
            return 0;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            return Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    */

    public static int extractOrientationFromGalleryImage(Context context, Uri galleryUri) {
        int orientation = 0;
        if ("media".equals(galleryUri.getAuthority())) {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = context.getContentResolver().query(galleryUri, projection, null, null, null);
            if (cursor != null) {
                String path = null;
                if (cursor.moveToFirst()) {
                    path = cursor.getString(0);
                }
                cursor.close();
                try {
                    ExifInterface exifInterface = new ExifInterface(path);
                    orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return orientation;
            }
        } else {
            String docId = DocumentsContract.getDocumentId(galleryUri);
            System.out.println("docId:" + docId);
            String[] split = docId.split(":");
            Uri contentUri = MediaStore.Files.getContentUri("external");
            System.out.println("contentUri:" + contentUri);
            String selection = "_id=?";
            String[] selectionArgs = {split[1]};

            final String column = "_data";
            final String[] projection = {column};
            try (Cursor cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    System.out.println("CURSOR:" + cursor);
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    String path = cursor.getString(column_index);
                    System.out.println("path:" + path);
                    try {
                        ExifInterface exifInterface = new ExifInterface(path);
                        orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("ORIENTATION:" + orientation);
                } else {
                    System.out.println("FAIL");
                }
            }

            return orientation;
        }
        return 0; // undefined
    }

    public static void releaseImageView(ImageView imageView) {
        if (imageView == null) {
            return;
        }
        imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);
    }

    public static void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            return;
        }
        bitmap.recycle();
        bitmap = null;
    }

}
