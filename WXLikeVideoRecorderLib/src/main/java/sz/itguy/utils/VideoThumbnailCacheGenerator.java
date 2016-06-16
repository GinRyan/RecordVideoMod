package sz.itguy.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Liang on 2016/6/15.
 */
public class VideoThumbnailCacheGenerator {

    int cpuNum = 0;
    ExecutorService executor;

    public VideoThumbnailCacheGenerator() {
        cpuNum = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNum);
    }

    /**
     * 获取视频缩略图（这里获取第一帧）
     *
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(TimeUnit.MILLISECONDS.toMicros(1));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取视频缩略图（这里获取第一帧）
     *
     * @param filePath
     * @return
     */
    public List<Future<File>> getVideosThumbnails(final File[] filePath) {
        List<Future<File>> futureList = new ArrayList<>();
        for (int i = 0; i < filePath.length; i++) {
            final int index = i;
            Future<File> future = executor.submit(new Callable<File>() {
                @Override
                public File call() throws Exception {
                    final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    File cacheImageFile = new File(filePath[index].getAbsolutePath().replace("Media", "cache"));
                    if (!cacheImageFile.getParentFile().exists()) {
                        cacheImageFile.getParentFile().mkdir();
                    }
                    if (!cacheImageFile.exists()) {
                        try {
                            retriever.setDataSource(filePath[index].getAbsolutePath());
                            Bitmap bitmap = retriever.getFrameAtTime(TimeUnit.MILLISECONDS.toMicros(15));
                            FileOutputStream outputStream = new FileOutputStream(cacheImageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                retriever.release();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return cacheImageFile;
                }
            });
            futureList.add(future);
        }

        return futureList;
    }
}
