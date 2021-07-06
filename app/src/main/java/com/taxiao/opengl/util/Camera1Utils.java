package com.taxiao.opengl.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;


import java.io.IOException;
import java.util.List;


/**
 * CAMERA1 tv 工具类
 * Created by hanqq on 2020/7/15
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Camera1Utils {
    public static final String TAG = Camera1Utils.class.getSimpleName();
    private static volatile Camera1Utils mCameraUtils;
    private Camera camera;
    private int lastResult = -1;
    private Context mContext;
    private TextureView mTextureView;
    private boolean mIsFront;
    private Point outPoint = new Point();
    private SurfaceHolder.Callback callback;
    private SurfaceHolder holder;

    private HandlerThread mCameraHandler;

    private Camera1Utils() {
    }

    public static Camera1Utils getInstance() {
        if (mCameraUtils == null) {
            synchronized (Camera1Utils.class) {
                if (mCameraUtils == null) {
                    mCameraUtils = new Camera1Utils();
                }
            }
        }
        return mCameraUtils;
    }

    /**
     * 初始化相机
     *
     * @param context
     * @param surfaceTexture
     * @param isFront
     * @param point
     */
    public void initSurfaceTexture(Context context, SurfaceTexture surfaceTexture, boolean isFront, Point point) {
        LogUtils.d(TAG, "initCamera1");
        if (!checkCameraHardware(context)) return;
        createCamera(context, surfaceTexture, isFront, point);
    }

    /**
     * HandlerThread/Looper/MessageQueue fd泄露
     *
     * @param context
     * @param surfaceTexture
     * @param isFront
     * @param point
     */
    private void createCamera(final Context context, final SurfaceTexture surfaceTexture, final boolean isFront, final Point point) {
        LogUtils.d(TAG, "createCamera");
        mCameraHandler = new HandlerThread("camera1");
        mCameraHandler.start();
        Looper looper = mCameraHandler.getLooper();
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                // 切换到子线程
                initCamera(context, surfaceTexture, isFront, point);
            }
        });
    }

    private void initCamera(Context context, SurfaceTexture surfaceTexture, boolean isFront, Point point) {
        LogUtils.d(TAG, "initCamera inner width:" + point.x + " height:" + point.y);
        try {
            int cameraID = getCameraID(isFront);
            camera = Camera.open(cameraID);
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                if (parameters != null) {
                    List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                    for (int i = 0; i < sizeList.size(); i++) {
                        Camera.Size size = sizeList.get(i);
                        if (size != null) {
//                            LogUtils.d(TAG, " size width: " + size.width + " height: " + size.height);
                        }
                    }
//                parameters.setRecordingHint(true);
                    //parameters.setPreviewFormat(ImageFormat.NV21);
                    // getSupportedPreviewFormats 返回 [17, 842094169]，可以查到这两种正是NV21 和YV12 格式的十进制表示
                    // public static final int NV21 = 0x11;
                    // public static final int YV12 = 0x32315659;
                    if (point != null) {
                        parameters.setPreviewSize(point.x, point.y);
                        outPoint.x = point.x;
                        outPoint.y = point.y;
                    } else {
                        Camera.Size size = sizeList.get(0);
                        parameters.setPreviewSize(size.width, size.height);
                        outPoint.x = size.width;
                        outPoint.y = size.height;
                    }
                    camera.setParameters(parameters);
                }
                setPreview(holder, surfaceTexture);
                camera.startPreview();
                LogUtils.d(TAG, "camera.startPreview ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "exception " + e.getMessage());
        }
    }


    /**
     * 初始化相机
     *
     * @param context     上下文
     * @param surfaceView SurfaceView
     * @param isMirror    未用到
     * @param isFront     true 前置 false 后置  默认前置
     * @param point       预览界面尺寸
     */
    public void initSurfaceView(final Context context, SurfaceView surfaceView, boolean isMirror, final boolean isFront, final Point point) {
        if (!checkCameraHardware(context)) return;
        holder = surfaceView.getHolder();
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                LogUtils.d(TAG, "surfaceCreated thread " + Thread.currentThread().getName() + " id: " + Thread.currentThread().getId() + " loop :" + (Looper.myLooper() == Looper.getMainLooper()));
                createCamera(context, holder, null, isFront, point);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                LogUtils.d(TAG, "surfaceChanged ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                LogUtils.d(TAG, "surfaceDestroyed ");
            }
        };
        holder.addCallback(callback);
    }

    /**
     * 初始化相机
     *
     * @param context 上下文
     * @param holder  SurfaceHolder
     * @param isFront true 前置 false 后置  默认前置
     * @param point   预览界面尺寸
     */
    private void createCamera(final Context context, final SurfaceHolder holder, final TextureView textureView, final boolean isFront, final Point point) {
        LogUtils.d(TAG, "6 params createCamera");
        mCameraHandler = new HandlerThread("camera1");
        mCameraHandler.start();
        Looper looper = mCameraHandler.getLooper();
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                // 切换到子线程
                initCamera(context, holder, textureView, isFront, point);
            }
        });
    }

    /**
     * 初始化相机
     *
     * @param context 上下文
     * @param holder  SurfaceHolder
     * @param isFront true 前置 false 后置  默认前置
     * @param point   预览界面尺寸
     */
    private void initCamera(Context context, SurfaceHolder holder, TextureView textureView, boolean isFront, Point point) {
        try {
            SurfaceTexture surfaceTexture = null;
            if (textureView != null) {
                surfaceTexture = textureView.getSurfaceTexture();
            }
            int cameraID = getCameraID(isFront);
            camera = Camera.open(cameraID);
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                if (parameters != null) {
                    List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                    for (int i = 0; i < sizeList.size(); i++) {
                        Camera.Size size = sizeList.get(i);
                        if (size != null) {
//                            LogUtils.d(TAG, " size width: " + size.width + " height: " + size.height);
                        }
                    }
//                parameters.setRecordingHint(true);
                    parameters.setPreviewFormat(ImageFormat.NV21);
                    // getSupportedPreviewFormats 返回 [17, 842094169]，可以查到这两种正是NV21 和YV12 格式的十进制表示
                    // public static final int NV21 = 0x11;
                    // public static final int YV12 = 0x32315659;
                    if (point != null) {
                        parameters.setPreviewSize(point.x, point.y);
                        outPoint.x = point.x;
                        outPoint.y = point.y;
                    } else {
                        Camera.Size size = sizeList.get(0);
                        parameters.setPreviewSize(size.width, size.height);
                        outPoint.x = size.width;
                        outPoint.y = size.height;
                    }
                    if (textureView != null) {
                        setRotation(context, textureView, cameraID, parameters);
                    }
                    camera.setParameters(parameters);
                }
                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
//                    LogUtils.d(TAG, "setPreviewCallback thread: " + Thread.currentThread().getName() + " id: " + Thread.currentThread().getId() + " loop :" + (Looper.myLooper() == Looper.getMainLooper()));

                    }
                });
                setPreview(holder, surfaceTexture);
                camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "exception " + e.getMessage());
        }
    }

    /**
     * 设置预览
     *
     * @param holder         SurfaceHolder
     * @param surfaceTexture SurfaceTexture
     * @throws IOException
     */
    private void setPreview(SurfaceHolder holder, SurfaceTexture surfaceTexture) throws IOException {
        LogUtils.d(TAG, "Camera setPreview");
        if (holder != null) {
            LogUtils.d(TAG, "Camera setPreview surfaceview");
            camera.setPreviewDisplay(holder);
        } else if (surfaceTexture != null) {
            LogUtils.d(TAG, "camera setPreviewTexture");
            camera.setPreviewTexture(surfaceTexture);
        } else {
            LogUtils.d(TAG, "no suface available");
        }
        LogUtils.d("Camera setPreview exit");
    }

    public void setOrientation() {
        try {
            if (camera != null && mContext != null && mTextureView != null) {
                int cameraID = getCameraID(mIsFront);
                Camera.Parameters parameters = camera.getParameters();
                if (parameters != null) {
                    setRotation(mContext, mTextureView, cameraID, parameters);
                    camera.setParameters(parameters);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "setOrientation e " + e.getMessage());
        }
    }

    /***
     * 设置旋转角度
     */
    private void setRotation(Context context, TextureView textureView, int cameraID, Camera.Parameters parameters) {
        int rotation = 0;
        int result = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);
        WindowManager windowManagerService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManagerService != null) {
            Display defaultDisplay = windowManagerService.getDefaultDisplay();
            if (defaultDisplay != null) {
                rotation = defaultDisplay.getRotation();
                LogUtils.d(TAG, "rotation: " + rotation + " width: " + defaultDisplay.getWidth() + " height: " + defaultDisplay.getHeight());
                switch (rotation) {
                    case Surface.ROTATION_0:
                        rotation = 0;
                        break;
                    case Surface.ROTATION_90:
                        rotation = 90;
                        break;
                    case Surface.ROTATION_180:
                        rotation = 180;
                        break;
                    case Surface.ROTATION_270:
                        rotation = 270;
                        break;
                }
                // 预览和拍摄方向不一致
                parameters.setRotation(rotation);
            }
            // 计算图像所要旋转的角度
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (cameraInfo.orientation + rotation) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (cameraInfo.orientation - rotation + 360) % 360;
            }
            if (lastResult == result) return;
            LogUtils.d(TAG, "result: " + result);
            camera.setDisplayOrientation(result);
            lastResult = result;
        }
    }

    /**
     * 获取cameraID
     *
     * @param isFront true 前置 false 后置  默认前置
     */
    private int getCameraID(boolean isFront) {
        int cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 1) {
            // 如果只有一个摄像头，默认是0
            cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else if (numberOfCameras == 2) {
            if (isFront) {
                cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }
        return cameraID;
    }

    /**
     * 是否有摄像头
     */
    private boolean checkCameraHardware(Context context) {
        if (context == null) return false;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            LogUtils.d("摄像头不存在");
            return false;
        }
    }

    public void tackPhoto() {
        if (camera != null) {
            camera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                }
            });
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        LogUtils.d(TAG, "release");
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
            if (holder != null && callback != null) {
                holder.removeCallback(callback);
                callback = null;
                holder = null;
            }
            lastResult = -1;

            if (mCameraHandler != null) {
                mCameraHandler.getLooper().quit();
                mCameraHandler.quit();
                mCameraHandler = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "exception surfaceDestroyed " + e.getMessage());
        }
    }

}
