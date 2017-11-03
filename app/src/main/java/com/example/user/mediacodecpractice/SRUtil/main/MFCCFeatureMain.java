package com.example.user.mediacodecpractice.SRUtil.main;

import android.util.Log;

import com.example.user.mediacodecpractice.LogUtil;
import com.example.user.mediacodecpractice.SRUtil.FeatureExtract;
import com.example.user.mediacodecpractice.SRUtil.PreProcess;
import com.example.user.mediacodecpractice.SRUtil.feature.FeatureVector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MFCCFeatureMain {

    //	private static FormatControlConf fc = new FormatControlConf();
    private static final int SAMPLING_RATE = 44100; // (int) fc.getRate();
    // int samplePerFrame = 256; // 16ms for 8 khz
    private static final int SAMPLE_PER_FRAME = 1024; // 512,23.22ms
    private static final int FEATURE_DIMENSION = 39;
    private FeatureExtract featureExtract;
    //	private WaveData waveData;
    private PreProcess prp;
    private List<double[]> allFeaturesList = new ArrayList<double[]>();

    private static final String BASE_DIR = "data";

    public MFCCFeatureMain() {
//		waveData = new WaveData();
    }

    /**
     * 主函数
     */
//	public static void main(String[] args) {
//		MFCCFeatureMain mfcc = new MFCCFeatureMain();
//		if (args.length != 1) {
//			System.err.println("Usage: <string-type> e.g. knn--KNN,svmiris--SVM-Iris,svm--SVM,ann--ANN");
//			System.exit(-1);
//		}
//		/**
//		 * 将data下面的train和test的所有音频文件的特征写到对应的文件夹下面，Iris格式
//		 * 分别为data/knn/train.data,data/knn/test.data
//		 */
//		if ("knn".equalsIgnoreCase(args[0])) {
//			mfcc.writeFeaturesKnn("train");
//			mfcc.writeFeaturesKnn("test");
//		}
//		/**
//		 * 将data下面的train和test的所有音频文件的特征写到对应的文件夹下面，Iris格式
//		 * 分别为data/svm-iris/train.data,data/svm-iris/test.data
//		 */
//		if ("svmiris".equalsIgnoreCase(args[0])) {
//			mfcc.writeFeaturesIris("train");
//			mfcc.writeFeaturesIris("test");
//		}
//		/**
//		 * 将data下面的train和test的所有音频文件的特征写到对应的文件夹下面，SimpleSVM格式
//		 * 分别为data/svm/train_bc,data/svm/test_bc
//		 */
//		if ("svm".equalsIgnoreCase(args[0])) {
//			mfcc.writeFeaturesSimpleSVM("train");
//			mfcc.writeFeaturesSimpleSVM("test");
//		}
//		/**
//		 * 将data下面的train和test的所有音频文件的特征写到对应的文件夹下面，CNN格式
//		 * 分别为data/ann/train.feature,data/ann/train.label,data/ann/test.feature,data/ann/test.label
//		 */
//		if ("ann".equalsIgnoreCase(args[0])) {
//			//		mfcc.writeFeaturesCNNTrain();
//			//		mfcc.writeFeaturesCNNTest();
//			mfcc.writeFeaturesANN("train");
//			mfcc.writeFeaturesANN("test");
//		}
//	}


    public FeatureVector extractFeatureFromFile(float[] arrAmp) {
//		float[] arrAmp;
//		arrAmp = waveData.extractAmplitudeFromFile(speechFile);

        prp = new PreProcess(arrAmp, SAMPLING_RATE);
        Log.d("preprocess origin", String.valueOf(arrAmp.length));
//        Log.d("preprocess prp", String.valueOf(prp.framedSignal.length));

        if (prp.framedSignal[0].length >= SAMPLE_PER_FRAME && prp.framedSignal.length > 1) {
            featureExtract = new FeatureExtract(prp.framedSignal, SAMPLING_RATE, SAMPLE_PER_FRAME);
            featureExtract.makeMfccFeatureVector();
            Log.d("preprocess fv", String.valueOf(featureExtract.getFeatureVector().getFeatureVector().length));

            return featureExtract.getFeatureVector();
        } else {

            return null;
        }
    }

    public FeatureVector extractFeatureFromFile(File speechFile) {
        float[] arrAmp = new float[0];
//		arrAmp = waveData.extractAmplitudeFromFile(speechFile);
        prp = new PreProcess(arrAmp,  SAMPLING_RATE);
        featureExtract = new FeatureExtract(prp.framedSignal, SAMPLING_RATE, SAMPLE_PER_FRAME);
        featureExtract.makeMfccFeatureVector();
        return featureExtract.getFeatureVector();
    }

}
