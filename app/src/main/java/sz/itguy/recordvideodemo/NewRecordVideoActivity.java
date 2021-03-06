package sz.itguy.recordvideodemo;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import sz.itguy.utils.FileUtil;
import sz.itguy.wxlikevideo.camera.CameraHelper;
import sz.itguy.wxlikevideo.recorder.WXLikeVideoRecorder;
import sz.itguy.wxlikevideo.views.CameraPreviewView;
import sz.itguy.wxlikevideo.views.CircleBackgroundTextView;

/**
 * 新视频录制页面
 *
 * @author Martin
 */
public class NewRecordVideoActivity extends Activity {

    private static final String TAG = "NewRecordVideoActivity";

    // 输出宽度
    private static final int OUTPUT_WIDTH = 320;
    // 输出高度
    private static final int OUTPUT_HEIGHT = 240;
    // 宽高比
    private static final float RATIO = 1f * OUTPUT_WIDTH / OUTPUT_HEIGHT;
    @BindView(R.id.button_other)
    CircleBackgroundTextView buttonOther;

    private Camera mCamera;

    private WXLikeVideoRecorder mRecorder;

    private static final int CANCEL_RECORD_OFFSET = -100;
    private float mDownX, mDownY;
    private boolean isCancelRecord = false;
    CircleBackgroundTextView circleBackgroundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int cameraId = CameraHelper.getDefaultCameraID();
        // Create an instance of Camera
        mCamera = CameraHelper.getCameraInstance(cameraId);
        if (null == mCamera) {
            Toast.makeText(this, "打开相机失败！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // 初始化录像机
        mRecorder = new WXLikeVideoRecorder(this, FileUtil.getAppMediaDir(this));
        mRecorder.setOutputSize(OUTPUT_WIDTH, OUTPUT_HEIGHT);

        setContentView(R.layout.activity_new_recorder);
        ButterKnife.bind(this);

        CameraPreviewView preview = (CameraPreviewView) findViewById(R.id.camera_preview);
        preview.setCamera(mCamera, cameraId);

        mRecorder.setCameraPreviewView(preview);


        circleBackgroundTextView = (CircleBackgroundTextView) findViewById(R.id.button_start);

        ((TextView) findViewById(R.id.filePathTextView)).setText("请在" + FileUtil.getAppMediaDir(this) + "查看录制的视频文件");

        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewRecordVideoActivity.this, VideosListActivity.class));
            }
        });

        initClickEvent();
    }

    private void initClickEvent() {
        circleBackgroundTextView
                .setOnFingerMoveListener(new CircleBackgroundTextView.OnFingerMoveListener() {
                    @Override
                    public void onMove(CircleBackgroundTextView view, MotionEvent event) {
                        onTouch(view, event);
                    }
                })
                .setOnFingerDownListener(new CircleBackgroundTextView.OnFingerDownListener() {
                    @Override
                    public void onDown(CircleBackgroundTextView view, MotionEvent event) {
                        onTouch(view, event);
                    }
                })
                .setOnFingerUpListener(new CircleBackgroundTextView.OnFingerUpListener() {
                    @Override
                    public void onUp(CircleBackgroundTextView view, MotionEvent event) {
                        onTouch(view, event);
                    }
                })
                .setOnProgressListener(new CircleBackgroundTextView.OnProgressListener() {
                    @Override
                    public void onProgress(int progress) {
                        tProgress = progress;
                        if (progress >= 100) {
                            stopRecord();
                        } else {
                            Log.d("record", "Recording progress :" + progress);
                        }
                    }
                });
    }

    int tProgress = 0;

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecorder != null) {
            boolean recording = mRecorder.isRecording();
            // 页面不可见就要停止录制
            mRecorder.stopRecording();
            // 录制时退出，直接舍弃视频
            if (recording) {
                FileUtil.deleteFile(mRecorder.getFilePath());
            }
        }
        //releaseCamera();              // release the camera immediately on pause event
        mCamera.stopPreview();
        tProgress = 0;
        circleBackgroundTextView.reset();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            // 释放前先停止预览
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera.startPreview();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mRecorder.isRecording()) {
            Toast.makeText(this, "正在录制中…", Toast.LENGTH_SHORT).show();
            return;
        }

        // initialize video camera
        if (prepareVideoRecorder()) {
            // 录制视频
            if (!mRecorder.startRecording())
                Toast.makeText(this, "录制失败…", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 准备视频录制器
     *
     * @return
     */
    private boolean prepareVideoRecorder() {
        if (!FileUtil.isSDCardMounted()) {
            Toast.makeText(this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        mRecorder.stopRecording();
        String videoPath = mRecorder.getFilePath();
        // 没有录制视频
        if (null == videoPath) {
            return;
        }
        // 若取消录制，则删除文件，否则通知宿主页面发送视频
        if (isCancelRecord) {
            FileUtil.deleteFile(videoPath);
        } else {
            // 告诉宿主页面录制视频的路径
            startActivity(new Intent(this, PlayVideoActiviy.class).putExtra(PlayVideoActiviy.KEY_FILE_PATH, videoPath));
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCancelRecord = false;
                mDownX = event.getX();
                mDownY = event.getY();
                startRecord();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRecorder.isRecording())
                    return false;

                float y = event.getY();
                if (y - mDownY < CANCEL_RECORD_OFFSET) {
                    if (!isCancelRecord) {
                        // cancel record
                        isCancelRecord = true;
                        Toast.makeText(this, "cancel record", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isCancelRecord = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (tProgress < 100) {
                    stopRecord();
                } else {
                    return true;
                }
                break;
        }
        return true;
    }

}
