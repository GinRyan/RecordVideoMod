package sz.itguy.recordvideodemo.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sz.itguy.recordvideodemo.R;
import sz.itguy.wxlikevideo.views.PreviewScalableVideoView;

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
        final ViewHolder h;
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
            h.playImageView.setVisibility(View.VISIBLE);
            h.thumbnailImageView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(new File(path.getAbsolutePath().replace("Media", "cache"))).into(h.thumbnailImageView);
            h.playImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (h.videoView.isPlaying()) {
                        h.videoView.stop();
                        h.playImageView.setVisibility(View.VISIBLE);
                        h.thumbnailImageView.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            h.videoView.prepareAsync(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    h.videoView.setLooping(true);
                                    h.videoView.start();
                                    h.playImageView.setVisibility(View.INVISIBLE);
                                    h.thumbnailImageView.setVisibility(View.INVISIBLE);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.video_view)
        PreviewScalableVideoView videoView;
        @BindView(R.id.thumbnailImageView)
        ImageView thumbnailImageView;
        @BindView(R.id.playImageView)
        ImageView playImageView;

        ViewHolder(final View view) {
            ButterKnife.bind(this, view);
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        videoView.stop();
                    }
                    playImageView.setVisibility(View.VISIBLE);
                    thumbnailImageView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
