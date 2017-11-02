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

    /**
     * 按照ANN格式输出,1-male,0-female
     */
    public void writeFeaturesANN(String dirName) {
        String dataName = BASE_DIR + "/ann/" + dirName + ".feature";
        String labelName = BASE_DIR + "/ann/" + dirName + ".label";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));
             BufferedWriter label = new BufferedWriter(new FileWriter(new File(labelName)));) {
            File maleDir = new File(BASE_DIR + "/" + dirName + "/male");
            for (File f : maleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2CNNStr(getFeature(f.getPath())));
                bw.newLine();
                label.write("1");
                label.newLine();
            }
            File femaleDir = new File(BASE_DIR + "/" + dirName + "/female");
            for (File f : femaleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2CNNStr(getFeature(f.getPath())));
                bw.newLine();
                label.write("0");
                label.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照CNN格式输出,1-male,0-female
     */
    public void writeFeaturesCNNTrain() {
        String dataName = BASE_DIR + "/train/train.format";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));) {
            File maleDir = new File(BASE_DIR + "/train/male");
            for (File f : maleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2CNNStr(getFeature(f.getPath())) + ",1");
                bw.newLine();
            }
            File femaleDir = new File(BASE_DIR + "/train/female");
            for (File f : femaleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2CNNStr(getFeature(f.getPath())) + ",0");
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFeaturesCNNTest() {
        String dataName = BASE_DIR + "/test/test.format";
        String labelName = BASE_DIR + "/test/test.label";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));
             BufferedWriter label = new BufferedWriter(new FileWriter(new File(labelName)));) {
            File maleDir = new File(BASE_DIR + "/test/male");
            for (File f : maleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2CNNStr(getFeature(f.getPath())));
                bw.newLine();
                label.write("1");
                label.newLine();
            }
            File femaleDir = new File(BASE_DIR + "/test/female");
            for (File f : femaleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2CNNStr(getFeature(f.getPath())));
                bw.newLine();
                label.write("0");
                label.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将特征数据转换成CNN格式，针对每条数据
     */
    private String transFeature2CNNStr(double[] feature) {
        StringBuffer sb = new StringBuffer();
        for (double f : feature) {
            sb.append(f + "").append(",");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * 按照Iris格式输出
     */
    public void writeFeaturesKnn(String dirName) {
        String dataName = BASE_DIR + "/knn/" + dirName + ".data";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));) {
            File maleDir = new File(BASE_DIR + "/" + dirName + "/male");
            for (File f : maleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2IrisStr(getFeature(f.getPath()), "male"));
                bw.newLine();
            }
            File femaleDir = new File(BASE_DIR + "/" + dirName + "/female");
            for (File f : femaleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2IrisStr(getFeature(f.getPath()), "female"));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照Iris格式输出
     */
    public void writeFeaturesIris(String dirName) {
        String dataName = BASE_DIR + "/svm-iris/" + dirName + ".data";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));) {
            File maleDir = new File(BASE_DIR + "/" + dirName + "/male");
            for (File f : maleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2IrisStr(getFeature(f.getPath()), "male"));
                bw.newLine();
            }
            File femaleDir = new File(BASE_DIR + "/" + dirName + "/female");
            for (File f : femaleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2IrisStr(getFeature(f.getPath()), "female"));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将特征数据转换成iris格式，针对每条数据
     */
    private String transFeature2IrisStr(double[] feature, String cate) {
        StringBuffer sb = new StringBuffer();
        for (double f : feature) {
            sb.append(f + "").append(",");
        }
        sb.append(cate);
        return sb.toString();
    }

    /**
     * 按照SimpleSVM格式输出
     */
    public void writeFeaturesSimpleSVM(String dirName) {
        String dataName = BASE_DIR + "/svm/" + dirName + "_bc";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));) {
            File maleDir = new File(BASE_DIR + "/" + dirName + "/male");
            for (File f : maleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2SimpleStr(getFeature(f.getPath()), 1));
                bw.newLine();
            }
            File femaleDir = new File(BASE_DIR + "/" + dirName + "/female");
            for (File f : femaleDir.listFiles()) {
                System.out.println(f.getPath());
                bw.write(transFeature2SimpleStr(getFeature(f.getPath()), -1));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将特征数据转换成SimpleSVM格式，针对每条数据
     * cate:  1--男，-1--女
     */
    private String transFeature2SimpleStr(double[] feature, int cate) {
        StringBuffer sb = new StringBuffer();
        sb.append(cate + "").append("\t");
        int count = 1;
        for (double f : feature) {
            sb.append(count++ + ":" + f).append("\t");
        }
        return sb.toString();
    }

    /**
     * 提取单个音频的特征数据
     */
    private double[] getFeature(String fileName) {
        int totalFrames = 0;
        FeatureVector feature = extractFeatureFromFile(new File(fileName));
        for (int k = 0; k < feature.getNoOfFrames(); k++) {
            allFeaturesList.add(feature.getFeatureVector()[k]);
            totalFrames++;
        }
        //		System.out.println("帧数： " + totalFrames + "，特征列表大小： " + allFeaturesList.size());
        // 行代表帧数，列代表特征
        double allFeatures[][] = new double[totalFrames][FEATURE_DIMENSION];
        for (int i = 0; i < totalFrames; i++) {
            double[] tmp = allFeaturesList.get(i);
            allFeatures[i] = tmp;
        }
        // 输出特征
        //		for (int i = 0; i < totalFrames; i++) {
        //			for (int j = 0; j < FEATURE_DIMENSION; j++) {
        //				System.out.println(allFeatures[i][j]);
        //			}
        //		}
        // 计算每帧对应特征的平均值
        double avgFeatures[] = new double[FEATURE_DIMENSION];
        for (int j = 0; j < FEATURE_DIMENSION; j++) { // 循环每列
            double tmp = 0.0d;
            for (int i = 0; i < totalFrames; i++) { // 循环每行
                tmp += allFeatures[i][j];
            }
            avgFeatures[j] = tmp / totalFrames;
        }
        // 将特征数据保存到点中
        //		Points pts[] = new Points[totalFrames];
        //		for (int j = 0; j < totalFrames; j++) {
        //			pts[j] = new Points(allFeatures[j]);
        //		}
        return avgFeatures;
    }

    public FeatureVector extractFeatureFromFile(float[] arrAmp) {
//		float[] arrAmp;
//		arrAmp = waveData.extractAmplitudeFromFile(speechFile);

        prp = new PreProcess(arrAmp, SAMPLE_PER_FRAME, SAMPLING_RATE);
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
        prp = new PreProcess(arrAmp, SAMPLE_PER_FRAME, SAMPLING_RATE);
        featureExtract = new FeatureExtract(prp.framedSignal, SAMPLING_RATE, SAMPLE_PER_FRAME);
        featureExtract.makeMfccFeatureVector();
        return featureExtract.getFeatureVector();
    }

}
