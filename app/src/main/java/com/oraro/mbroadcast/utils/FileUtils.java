package com.oraro.mbroadcast.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.net.URL;

/**
 * Created by admin on 2016/9/8
 *
 * @author zmy
 */
public class FileUtils {

    private static String filePath;

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, null,null, null, null);
                String[] cs = cursor.getColumnNames();
                for(String s : cs){
                }
                cursor.moveToFirst();
                String p = cursor.getString(0);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                boolean b = cursor.moveToFirst();
                if (b) {
                    String path = cursor.getString(column_index);
                    return path;
                }
//                String decodeStr = Uri.decode(uri.toString());
//                String id = decodeStr.substring(decodeStr.lastIndexOf(":") + 1);
//                String[] column = {MediaStore.Audio.Media.DATA};
//                String sel = MediaStore.Audio.Media._ID + " =? ";
//                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, column,
//                        sel, new String[]{id}, null);
//                int columnIndex = cursor.getColumnIndex(column[0]);
//                if (cursor.moveToFirst()) {
//                    filePath = cursor.getString(columnIndex);
//                }
//                cursor.close();
//                return filePath;
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
