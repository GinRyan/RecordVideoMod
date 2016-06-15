package sz.itguy.recordvideodemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.ButterKnife;
import sz.itguy.recordvideodemo.adapter.VideoAdapter;
import sz.itguy.utils.FileUtil;
import sz.itguy.utils.VideoThumbnailCacheGenerator;

public class VideosListActivity extends Activity {
    @BindView(R.id.videos)
    ListView videos;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    VideoThumbnailCacheGenerator videoThumbnailCacheGenerator = new VideoThumbnailCacheGenerator();
    VideoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_list);
        ButterKnife.bind(this);
        final File[] list = new File(Environment.getExternalStorageDirectory() + FileUtil.MEDIA_FILE_DIR).listFiles();
        for (int i = 0; i < list.length; i++) {
            Log.d("file", list[i].getAbsolutePath());
        }
        adapter = new VideoAdapter(this, list);

        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Future<File>> videosThumbnailsFuture = videoThumbnailCacheGenerator.getVideosThumbnails(list);
                for (int i = 0; i < videosThumbnailsFuture.size(); i++) {
                    try {
                        videosThumbnailsFuture.get(i).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        videos.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
}
