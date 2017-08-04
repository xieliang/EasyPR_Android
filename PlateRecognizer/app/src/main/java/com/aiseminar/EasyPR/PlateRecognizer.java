package com.aiseminar.EasyPR;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.aiseminar.platerecognizer.R;
import com.aiseminar.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class PlateRecognizer {
    private Context mContext;
    private String mSvmpath = null;
    private String mAnnpath = null;
    private String mChAnnpath = null;
    private String mGrayChpath = null;
    private String mChMappingpath = null;
    private boolean mRecognizerInited = false;
    private long mRecognizerPtr = 0;

    public PlateRecognizer(Context context) {
        mContext = context;
        if (checkAndUpdateModelFile()) {
            mRecognizerPtr = initPR(mSvmpath, mAnnpath, mChAnnpath, mGrayChpath, mChMappingpath);
            if (0 != mRecognizerPtr) {
                mRecognizerInited = true;
            }
        }
    }

    protected void finalize() {
        uninitPR(mRecognizerPtr);
        mRecognizerPtr = 0;
        mRecognizerInited = false;
    }

    public boolean checkAndUpdateModelFile() {
        if (null == mContext) {
            return false;
        }

        mSvmpath = FileUtil.getMediaFilePath(FileUtil.FILE_TYPE_SVM_MODEL);
        mAnnpath = FileUtil.getMediaFilePath(FileUtil.FILE_TYPE_ANN_MODEL);
        mChAnnpath = FileUtil.getMediaFilePath(FileUtil.FILE_TYPE_CHANN_MODEL);
        mGrayChpath = FileUtil.getMediaFilePath(FileUtil.FILE_TYPE_GRAYCH_MODEL);
        mChMappingpath = FileUtil.getMediaFilePath(FileUtil.FILE_TYPE_CHMAPPING_MODEL);
        //如果模型文件不存在从APP的资源中拷贝
        File svmFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_SVM_MODEL);
        File annFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_ANN_MODEL);
        File channFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_CHANN_MODEL);
        File graychFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_GRAYCH_MODEL);
        File chmappingFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_CHMAPPING_MODEL);
        if (/*! svmFile.exists()*/true) {
            try {
                InputStream fis = mContext.getResources().openRawResource(R.raw.svm_hist);
                FileOutputStream fos = new FileOutputStream(svmFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d("PlateRecognizer", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PlateRecognizer", "Error accessing file: " + e.getMessage());
            }
        }
        if (/*! annFile.exists()*/true) {
            try {
                InputStream fis = mContext.getResources().openRawResource(R.raw.ann);
                FileOutputStream fos = new FileOutputStream(annFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d("PlateRecognizer", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PlateRecognizer", "Error accessing file: " + e.getMessage());
            }
        }
        if (/*! channFile.exists()*/true) {
            try {
                InputStream fis = mContext.getResources().openRawResource(R.raw.ann_chinese);
                FileOutputStream fos = new FileOutputStream(channFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d("PlateRecognizer", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PlateRecognizer", "Error accessing file: " + e.getMessage());
            }
        }
        if (/*! graychFile.exists()*/true) {
            try {
                InputStream fis = mContext.getResources().openRawResource(R.raw.annch);
                FileOutputStream fos = new FileOutputStream(graychFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d("PlateRecognizer", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PlateRecognizer", "Error accessing file: " + e.getMessage());
            }
        }
        if (/*! chmappingFile.exists()*/true) {
            try {
                InputStream fis = mContext.getResources().openRawResource(R.raw.province_mapping);
                FileOutputStream fos = new FileOutputStream(chmappingFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d("PlateRecognizer", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PlateRecognizer", "Error accessing file: " + e.getMessage());
            }
        }
        Log.d("PlateRecognizer", "svmFile len: " + svmFile.length());
        Log.d("PlateRecognizer", "annFile len: " + annFile.length());
        Log.d("PlateRecognizer", "channFile len: " + channFile.length());
        Log.d("PlateRecognizer", "graychFile len: " + graychFile.length());
        Log.d("PlateRecognizer", "chmappingFile len: " + chmappingFile.length());
        return svmFile.exists() && annFile.exists() && channFile.exists() && graychFile.exists() && chmappingFile.exists();
    }

    public String recognize(String imagePath) {
        //判断文件夹是否存在
        File imageFile = new File(imagePath);
        if (! mRecognizerInited || ! imageFile.exists()) {
            return null;
        }

        if (0 == mRecognizerPtr) {
            return null;
        }

        byte[] retBytes = plateRecognize(mRecognizerPtr, imagePath);
        String result = null;
        try {
            result = new String(retBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * JNI Functions
     */
    // 加载车牌识别库
    static {
        try {
            System.loadLibrary("EasyPR");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("WARNING: Could not load EasyPR library!");
        }
    }

    public static native String stringFromJNI();
    public static native long initPR(String svmpath, String annpath, String channpath, String graychpath, String chmappingpath);
    public static native long uninitPR(long recognizerPtr);
    public static native byte[] plateRecognize(long recognizerPtr, String imgpath);
}
