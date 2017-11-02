package com.example.user.mediacodecpractice.SRUtil;

import android.util.Log;

/**
 * 预处理过程
 *
 * @author wanggang
 */
public class PreProcess {

    float[] originalSignal;// initial extracted PCM,
    float[] afterEndPtDetection;// after endPointDetection
    public int noOfFrames;// calculated total no of frames
    int samplePerFrame;// how many samples in one frame
    int framedArrayLength;// how many samples in framed array
    public float[][] framedSignal = null;
    float[] hammingWindow;
    EndPointDetection epd;
    int samplingRate;

    /**
     * constructor, all steps are called frm here
     *
     * @param audioData      extracted PCM data
     * @param samplePerFrame how many samples in one frame,=660 << frameDuration, typically
     * 30; samplingFreq, typically 22Khz
     */

    public float windowSize = 0.025f;
    public float windowStep = 0.01f;
    int samplePerStep;

    public PreProcess(float[] originalSignal, int samplePerFrame, int samplingRate) {
        this.originalSignal = originalSignal;
        this.samplePerFrame = (int) (samplingRate * windowSize);
        this.samplePerStep = (int) (samplingRate * windowStep);

        this.samplingRate = samplingRate;

//        normalizePCM();
        originalSignal = preEmphasis(originalSignal);

//        epd = new EndPointDetection(this.originalSignal, this.samplingRate);
        afterEndPtDetection = originalSignal;
//        afterEndPtDetection = epd.doEndPointDetection();
//        Log.d("preprocess epd", String.valueOf(afterEndPtDetection.length));
        // ArrayWriter.printFloatArrayToFile(afterEndPtDetection, "endPt.txt");
//		if(afterEndPtDetection.length>=samplePerFrame){
        doFraming();
        doWindowing();
//		}

    }

    float preEmphasisAlpha = 0.97f;

    private float[] preEmphasis(float inputSignal[]) {
        // System.err.println(" inside pre Emphasis");
        float outputSignal[] = new float[inputSignal.length];
        // apply pre-emphasis to each sample
        for (int n = 1; n < inputSignal.length; n++) {
            outputSignal[n] = (float) (inputSignal[n] - preEmphasisAlpha * inputSignal[n - 1]);
        }
        return outputSignal;
    }

    private void normalizePCM() {
        float max = originalSignal[0];
        for (int i = 1; i < originalSignal.length; i++) {
            if (max < Math.abs(originalSignal[i])) {
                max = Math.abs(originalSignal[i]);
            }
        }
        // System.out.println("max PCM =  " + max);
        for (int i = 0; i < originalSignal.length; i++) {
            originalSignal[i] = originalSignal[i] / max;
        }
    }

    /**
     * divides the whole signal into frames of samplerPerFrame
     */


    private void doFraming() {
        // calculate no of frames, for framing
        if (afterEndPtDetection.length < samplePerFrame) {
            noOfFrames = 1;
        } else {
            noOfFrames = 1 + (int) (Math.ceil((afterEndPtDetection.length - samplePerFrame) / samplePerStep));
        }


        framedSignal = new float[noOfFrames][samplePerFrame];


        for (int i = 0; i < noOfFrames - 1; i++) {
            int startIndex = i * samplePerStep;
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = afterEndPtDetection[startIndex + j];
            }
        }

        for (int j = 0; j < samplePerFrame; j++) {
            if (j < afterEndPtDetection.length) {
                framedSignal[noOfFrames - 1][j] = afterEndPtDetection[(noOfFrames - 1) * samplePerStep+ j];
            } else {
                framedSignal[noOfFrames - 1][j] = 0;
            }
        }

//        if (afterEndPtDetection.length < samplePerFrame) {
//            noOfFrames = 1;
//            framedSignal = new float[noOfFrames][afterEndPtDetection.length];
//
//            for (int i = 0; i < noOfFrames; i++) {
//                int startIndex = (i * afterEndPtDetection.length / 2);
//                for (int j = 0; j < afterEndPtDetection.length; j++) {
//                    framedSignal[i][j] = afterEndPtDetection[startIndex + j];
//                }
//            }
//
//
//        } else {
//            noOfFrames = 2 * afterEndPtDetection.length / samplePerFrame - 1;
//            framedSignal = new float[noOfFrames][samplePerFrame];
//
//            for (int i = 0; i < noOfFrames; i++) {
//                int startIndex = (i * samplePerFrame / 2);
//                for (int j = 0; j < samplePerFrame; j++) {
//                    framedSignal[i][j] = afterEndPtDetection[startIndex + j];
//                }
//            }
//
//
//        }
        //		System.out.println("noOfFrames       " + noOfFrames + "  samplePerFrame     " + samplePerFrame
        //				+ "  EPD length   " + afterEndPtDetection.length);


    }

    /**
     * does hamming window on each frame
     */
    private void doWindowing() {
        // prepare hammingWindow
        hammingWindow = new float[samplePerFrame + 1];
        // prepare for through out the data
        for (int i = 1; i <= samplePerFrame; i++) {
            hammingWindow[i] = (float) (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
        }
        // do windowing
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < framedSignal[i].length; j++) {
                framedSignal[i][j] = framedSignal[i][j] * hammingWindow[j + 1];
            }
        }
    }
}
