package sz.itguy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2015/9/17.
 */
public class BitmapUtil {

    public static Bitmap byte2Bitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static void saveBitmap2File(Context context, byte[] data) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            FileOutputStream fileOutputStream = new FileOutputStream(FileUtil.getOutputMediaFile(context, FileUtil.MEDIA_TYPE_IMAGE));
            byte[] buffer = new byte[2048];
            int length;
            while ((length = byteArrayInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            byteArrayInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
