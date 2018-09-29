package com.arcsoft.livenessdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.liveness.ErrorInfo;
import com.arcsoft.liveness.FaceInfo;
import com.arcsoft.liveness.LivenessEngine;
import com.arcsoft.liveness.LivenessInfo;

import java.util.ArrayList;
import java.util.List;

public class VideoSampleActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = VideoSampleActivity.class.getSimpleName();
    private SurfaceView svPreview;
    private SurfaceView svRect;
    private LivenessEngine arcFaceEngine;
    private AFT_FSDKEngine ftEngine;
    private Camera camera;
    private int mWidth;
    private int mHeight;
    private SurfaceHolder holder;
    private TextView txtScore;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int cameraOri = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_sample);
        //FT引擎初始化
        ftEngine = new AFT_FSDKEngine();
        int ftInitErrorCode = ftEngine.AFT_FSDK_InitialFaceEngine(Constants.FREESDKAPPID,
                Constants.FTSDKKEY, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT,
                16, 5).getCode();
        if(ftInitErrorCode != 0) {
            toast("FT初始化失败，errorcode：" + ftInitErrorCode);
            return;
        }

        //活体引擎初始化(视频)
        arcFaceEngine = new LivenessEngine();
        ErrorInfo error = arcFaceEngine.initEngine(this, LivenessEngine.AL_DETECT_MODE_VIDEO);
        if(error.getCode() != 0) {
            toast("活体初始化失败，errorcode：" + error.getCode());
            return;
        }
        svPreview = findViewById(R.id.sv_preview);
        svPreview.getHolder().addCallback(this);
        svRect = findViewById(R.id.sv_rect);
        svRect.setZOrderMediaOverlay(true);
        svRect.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        txtScore = findViewById(R.id.txt_score);
    }

    @Override
    protected void onDestroy() {
        ftEngine.AFT_FSDK_UninitialFaceEngine();
        arcFaceEngine.unInitEngine();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
            return;
        }
        //选择摄像头ID
        //camera = Camera.open(cameraId);
        camera = Camera.open(0);
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size previewSize = getBestSupportedSize(parameters.getSupportedPreviewSizes(), metrics);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            camera.setParameters(parameters);
            mWidth = previewSize.width;
            mHeight = previewSize.height;
            camera.setDisplayOrientation(cameraOri);
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    detect(data);
                }
            });
            camera.startPreview();
        } catch (Exception e) {
            camera = null;
        }
    }

    private void closeCamera() {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, DisplayMetrics metrics) {
        Camera.Size bestSize = sizes.get(0);
        float screenRatio = (float) metrics.widthPixels / (float) metrics.heightPixels;
        if (screenRatio > 1) {
            screenRatio = 1 / screenRatio;
        }

        for (Camera.Size s : sizes) {
            if (Math.abs((s.height / (float) s.width) - screenRatio) < Math.abs(bestSize.height /
                    (float) bestSize.width - screenRatio)) {
                bestSize = s;
            }
        }
        return bestSize;
    }

    private void detect(final byte[] data) {
        List<AFT_FSDKFace> ftFaceList = new ArrayList<>();
        //视频FT检测人脸
        int ftCode = ftEngine.AFT_FSDK_FaceFeatureDetect(data, mWidth, mHeight,
                AFT_FSDKEngine.CP_PAF_NV21, ftFaceList).getCode();
        if(ftCode != AFT_FSDKError.MOK) {
            Log.i(TAG, "AFT_FSDK_FaceFeatureDetect: errorcode " + ftCode);
            return;
        }
        int maxIndex = ImageUtils.findFTMaxAreaFace(ftFaceList);
        if (svRect != null) {
            Canvas canvas = svRect.getHolder().lockCanvas();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if(ftFaceList.size() > 0) {
                Rect rect = ftFaceList.get(maxIndex).getRect();
                if (rect != null) {
                    //画人脸框
                    Rect adjustedRect = DrawUtils.adjustRect(rect, mWidth, mHeight,
                            canvas.getWidth(), canvas.getHeight(), cameraOri, cameraId);
                    DrawUtils.drawFaceRect(canvas, adjustedRect, Color.YELLOW, 5);
                }
            }
            svRect.getHolder().unlockCanvasAndPost(canvas);
        }
        final List<FaceInfo> faceInfos = new ArrayList<>();
        if(maxIndex != -1) {
            AFT_FSDKFace face = ftFaceList.get(maxIndex);
            FaceInfo faceInfo = new FaceInfo(face.getRect(), face.getDegree());
            faceInfos.add(faceInfo);
        }
        //活体检测(目前只支持单人脸，且无论有无人脸都需调用)
        List<LivenessInfo> livenessInfos = new ArrayList<>();
        ErrorInfo livenessError = arcFaceEngine.startLivenessDetect(data, mWidth, mHeight,
                LivenessEngine.CP_PAF_NV21, faceInfos, livenessInfos);
        Log.i(TAG, "startLiveness: errorcode " + livenessError.getCode());
        if (livenessError.getCode() == ErrorInfo.MOK) {
            if(livenessInfos.size() == 0) {
                txtScore.setText("无人脸");
                return;
            }
            final int liveness = livenessInfos.get(0).getLiveness();
            Log.i(TAG, "getLivenessScore: liveness " + liveness);
            if(liveness == LivenessInfo.NOT_LIVE) {
                txtScore.setText("非活体");
            } else if(liveness == LivenessInfo.LIVE) {
                txtScore.setText("活体");
            } else if(liveness == LivenessInfo.MORE_THAN_ONE_FACE) {
                txtScore.setText("非单人脸信息");
            } else {
                txtScore.setText("未知");
            }
        }
    }

    private void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            openCamera();
        }
    }
}
