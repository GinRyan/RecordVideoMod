package sz.itguy.recordvideodemo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sz.itguy.recordvideodemo.R;

/**
 * Created by Liang on 2016/6/15.
 */
public class VideoAdapter extends ArrayAdapter<File> {
    public VideoAdapter(Context context, List<File> objects) {
        super(context, 0, objects);
    }

    public VideoAdapter(Context context, File[] objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        File path = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
            h = new ViewHolder(convertView);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        try {
            h.videoView.setDataSource(getContext(), Uri.fromFile(new File(path.getAbsolutePath())));
            Picasso.with(getContext()).load(new File(path.getAbsolutePath().replace("Media", "cache"))).into(h.thumbnailImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.video_view)
        ScalableVideoView videoView;
        @BindView(R.id.thumbnailImageView)
        ImageView thumbnailImageView;
        @BindView(R.id.playImageView)
        ImageView playImageView;

        ViewHolder(final View view) {
            ButterKnife.bind(this, view);
            playImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        videoView.stop();
                    } else {
                        try {
                            videoView.prepare();
                            videoView.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

}
