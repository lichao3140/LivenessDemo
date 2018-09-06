package com.arcsoft.livenessdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.liveness.ErrorInfo;
import com.arcsoft.liveness.FaceInfo;
import com.arcsoft.liveness.LivenessEngine;
import com.arcsoft.liveness.LivenessInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LivenessEngine livenessEngine;
    private AFD_FSDKEngine fdEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnActive = findViewById(R.id.btn_active);
        Button btnImg = findViewById(R.id.btn_img);
        Button btnVideo = findViewById(R.id.btn_video);
        fdEngine = new AFD_FSDKEngine();
        livenessEngine = new LivenessEngine();

        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        final long activeCode = livenessEngine.activeEngine(Constants.LIVENESSAPPID,
                                Constants.LIVENESSSDKKEY).getCode();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(activeCode != ErrorInfo.MOK) {
                                    toast("活体引擎激活失败，errorcode：" + activeCode);
                                } else {
                                    toast("活体引擎激活成功");
                                }
                            }
                        });
                    }
                });
            }
        });

        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImg();
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VideoSampleActivity.class));
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
        }
    }

    private void detectImg() {
        //FD引擎初始化
        fdEngine = new AFD_FSDKEngine();
        int fdInitErrorCode = fdEngine.AFD_FSDK_InitialFaceEngine(Constants.FREESDKAPPID,
                Constants.FDSDKKEY, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT,
                16, 5).getCode();
        if(fdInitErrorCode != AFD_FSDKError.MOK) {
            toast("FD初始化失败，errorcode：" + fdInitErrorCode);
            return;
        }

        //活体引擎初始化（图片）
        ErrorInfo error = livenessEngine.initEngine(LivenessEngine.AL_DETECT_MODE_IMAGE);
        if(error.getCode() != ErrorInfo.MOK) {
            toast("活体初始化失败，errorcode：" + error.getCode());
            //FD引擎销毁
            fdEngine.AFD_FSDK_UninitialFaceEngine();
            return;
        }

        Bitmap mHeadBmp = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
        if(mHeadBmp == null) {
            toast("图片不存在");
            unInitEngine();
            return;
        }
        final int width = mHeadBmp.getWidth();
        final int height = mHeadBmp.getHeight();
        final byte[] nv21Data = ImageUtils.getNV21(width, height, mHeadBmp);
        List<AFD_FSDKFace> fdFaceList = new ArrayList<>();
        //图片FD检测人脸
        int fdDetectCode = fdEngine.AFD_FSDK_StillImageFaceDetection(nv21Data, width, height,
                AFD_FSDKEngine.CP_PAF_NV21, fdFaceList).getCode();
        Log.d(TAG, "AFD_FSDK_StillImageFaceDetection: errorcode " + fdDetectCode);
        if (fdDetectCode == AFD_FSDKError.MOK) {
            int maxIndex = ImageUtils.findFDMaxAreaFace(fdFaceList);

            final List<FaceInfo> faceInfos = new ArrayList<>();
            if(maxIndex != -1) {
                AFD_FSDKFace face = fdFaceList.get(maxIndex);
                FaceInfo faceInfo = new FaceInfo(face.getRect(), face.getDegree());
                faceInfos.add(faceInfo);
            }
            //活体检测(目前只支持单人脸)
            List<LivenessInfo> livenessInfos = new ArrayList<>();
            ErrorInfo livenessError = livenessEngine.startLivenessDetect(nv21Data, width, height,
                    LivenessEngine.CP_PAF_NV21, faceInfos, livenessInfos);
            Log.d(TAG, "startLiveness: errorcode " + livenessError.getCode());
            if (livenessError.getCode() == ErrorInfo.MOK) {
                if(livenessInfos.size() == 0) {
                    toast("无人脸");
                    return;
                }
                final int liveness = livenessInfos.get(0).getLiveness();
                Log.d(TAG, "getLivenessScore: liveness " + liveness);
                if(liveness == LivenessInfo.NOT_LIVE) {
                    toast("非活体");
                } else if(liveness == LivenessInfo.LIVE) {
                    toast("活体");
                } else if(liveness == LivenessInfo.MORE_THAN_ONE_FACE) {
                    toast("非单人脸信息");
                } else {
                    toast("未知");
                }
            }
        }
        unInitEngine();
    }

    public void unInitEngine() {
        //FD引擎销毁
        fdEngine.AFD_FSDK_UninitialFaceEngine();
        //活体引擎销毁
        livenessEngine.unInitEngine();
    }

    private void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

}
