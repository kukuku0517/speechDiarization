package com.example.user.mediacodecpractice.SRUtil.feature;

public class MFCC {
    //    private int numMelFilters = 30;// how much
    private int numMelFilters = 26;// how much
    private int numCepstra;// number of mfcc coeffs
    private double preEmphasisAlpha = 0.95;
    //    private double lowerFilterFreq = 80.00;// FmelLow
    private double lowerFilterFreq = 0.00;// FmelLow
    private double samplingRate;
    private double upperFilterFreq;
    private double bin[];
    private int samplePerFrame;
    private DCT dct;
    private FFT fft;

    private double magSpectrum[];

    public MFCC(int samplePerFrame, int samplingRate, int numCepstra) {
        this.samplePerFrame = samplePerFrame;
        this.samplingRate = samplingRate;
        this.numCepstra = numCepstra;
        this.upperFilterFreq = samplingRate / 2.0;
        this.dct = new DCT(this.numCepstra, numMelFilters);
    }

    public double[] doMFCC(float[] framedSignal) {
        bin = magnitudeSpectrum(framedSignal);
        double energy = 0;
        for (double d : bin) {
            energy += d;
        }
        if (energy == 0) {
            energy = 0.00000000000001f;
        }

        int cbin[] = fftBinIndices();
        double fbank[] = melFilter(bin, cbin);
        for (int i = 0; i < fbank.length; i++) {
            if (fbank[i] == 0) {
                fbank[i] = 0.00000000000001;
            }
        }

        double f[] = nonLinearTransformation(fbank);

        double cepc[] = dct.performDCT(f);

        for (int i = 0; i < cepc.length; i++) {
            double lift = 1 + (22 / 2) * Math.sin(Math.PI * i / 22);
            cepc[i] = cepc[i] * lift;
        }
        cepc[0] = Math.log(energy);

        return cepc;
    }

    private double[] magnitudeSpectrum(float frame[]) {
        magSpectrum = new double[1024 / 2 + 1];
        fft = new FFT(1024);
        fft.fft(frame);
        for (int k = 0; k < 1024 / 2 + 1; k++) {
            magSpectrum[k] = (fft.re[k] * fft.re[k] + fft.im[k] * fft.im[k]) / (1024);
        }
        return magSpectrum;
    }


    private int[] fftBinIndices() {
        int cbin[] = new int[numMelFilters + 2];
        cbin[0] = (int) Math.round(lowerFilterFreq / samplingRate * samplePerFrame);// cbin0
        cbin[cbin.length - 1] = (samplePerFrame / 2);// cbin24
        for (int i = 1; i <= numMelFilters; i++) {// from cbin1 to cbin23
            double fc = centerFreq(i);// center freq for i th filter
            cbin[i] = (int) Math.round(fc / samplingRate * samplePerFrame);
        }
        return cbin;
    }

    /**
     * performs mel filter operation
     *
     * @param bin  magnitude spectrum (| |)^2 of fft
     * @param cbin mel filter coeffs
     * @return mel filtered coeffs--> filter bank coefficients.
     */
    private double[] melFilter(double bin[], int cbin[]) {
        double temp[] = new double[numMelFilters + 2];
        for (int k = 1; k <= numMelFilters; k++) {
            double num1 = 0.0, num2 = 0.0;
            for (int i = cbin[k - 1]; i <= cbin[k]; i++) {
                num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k - 1] + 1)) * bin[i];
            }

            for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++) {
                num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
            }

            temp[k] = num1 + num2;
        }
        double fbank[] = new double[numMelFilters];
        for (int i = 0; i < numMelFilters; i++) {
            fbank[i] = temp[i + 1];
            // System.out.println(fbank[i]);
        }
        return fbank;
    }

    /**
     * performs nonlinear transformation
     *
     * @param fbank
     * @return f log of filter bac
     */
    private double[] nonLinearTransformation(double fbank[]) {
        double f[] = new double[fbank.length];
        final double FLOOR = -50;
        for (int i = 0; i < fbank.length; i++) {
            f[i] = Math.log(fbank[i]);
            // check if ln() returns a value less than the floor
            if (f[i] < FLOOR) {
//                f[i] = FLOOR;
            }
        }
        return f;
    }

    private double centerFreq(int i) {
        double melFLow, melFHigh;
        melFLow = freqToMel(lowerFilterFreq);
        melFHigh = freqToMel(upperFilterFreq);
        double temp = melFLow + ((melFHigh - melFLow) / (numMelFilters + 1)) * i;
        return inverseMel(temp);
    }

    private double inverseMel(double x) {
        double temp = Math.pow(10, x / 2595) - 1;
        return 700 * (temp);
    }

    protected double freqToMel(double freq) {
        return 2595 * log10(1 + freq / 700);
    }

    private double log10(double value) {
        return Math.log(value) / Math.log(10);
    }
}
