package com.example.user.mediacodecpractice;

import android.os.Trace;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

/**Kmeans聚类算法的实现类，将newsgroups文档集聚成10类、20类、30类
 * 算法结束条件:当每个点最近的聚类中心点就是它所属的聚类中心点时，算法结束
 * @author yangliu
 * @qq 772330184
 * @mail yang.liu@pku.edu.cn
 *
 */
/**
 * The "KMeansClustering" class applies the k-means clustering learning algorithm on the data read in the
 * "Data" class. This involves initiating a number of centroids, setting their positions and assigning
 * them to the nearest clumps of training (classified) data. Then, the clumped test (unclassified) data are
 * assigned to the nearest centroid and so assigning them with the training data of that clump. This then
 * allows for a classification to be derived for every test datum.
 *
 * @author Karim Tabet, modified from "Toy K-Means" code by Chris Thornton
 * @version 1.0 24/11/2010
 */

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Vector;

public class KmeansCluster {
    private int k;
    private int v;
    private int n[];
    private ArrayList<double[][]> data = new ArrayList<>();
    private double classifiedData[][];
    private double centroids[][]; //initial value of centroids

    private int time;
    private double newCentroids[][];
    private boolean hasOrigin = false;

    public KmeansCluster(int k, int v, double[][] origin, double[][] data) //k = the number of centroids
    {
        this.k = k;
        this.v = v;
        centroids = origin;
        n = new int[k];
        classifiedData = data;
        hasOrigin = true;

//        for (int i = 0; i < k; i++) { //populate centroids with first k no. of data
//            for(int j = 0; j < 7; j++) {
//                centroids[i][j] = classifiedData[i][j];
//            }
//        }
    }

    ArrayList<Integer> silence;

    int[] frameSize;
    int[][] clusterIndex;
    int[][] clusterIndex2;

    public KmeansCluster(int k, int v, ArrayList<double[][]> data, int time, ArrayList<Integer> silence) //k = the number of centroids
    {
        newCentroids = new double[k][v];// length = k
        this.time = time;
        this.k = k;
        this.v = v;
        this.n = new int[k];
        this.data = data;
        centroids = new double[k][v];
        this.silence = silence;

        int size = 0;
        int clusterSize = 0;
//        for (double[][] d : data) {
//            size += d.length;
//        }

        clusterIndex = new int[silence.size()][];

        int max = -1;
        for (int i = 0; i < silence.size(); i++) {
            int s = silence.get(i);
            size += s;
            if (max < s) {
                max = s;
            }
            if (s == 0) {
                clusterIndex[i] = new int[]{-1};
            } else {
                clusterIndex[i] = new int[s];
            }
        }

        clusterIndex2 = new int[silence.size()][max];
        for (int[] i : clusterIndex2) {
            for (int j = 0; j < i.length; j++) {
                i[j] = -1;
            }
        }

        classifiedData = new double[size][39];
//        clusterIndex = new int[size + clusterSize];
        frameSize = new int[silence.size()];


        int c = 0;
        for (double[][] dd : data) {
            System.arraycopy(dd, 0, classifiedData, c, dd.length);
            c += dd.length;
        }
    }

    void initCents() {
        int a = classifiedData.length;


        for (int i = 0; i < k; i++) { //populate centroids with first k no. of data
            for (int j = 0; j < v; j++) {
                centroids[i][j] = classifiedData[(int) (Math.random() * a)][j];
            }
        }
        printCentroids();
    }

    int getByPossibility(double[] p) {
        double total = 0;
        for (double i : p) {
            total += i;
        }

        double index = Math.random() * total;
        double sum = 0;
        int i = 0;
        while (sum < index) {
            sum = sum + p[i++];
        }
        return i - 1;
    }


    void initKmeansPP() {
        Random random = new Random();
        ArrayList<double[]> cents = new ArrayList<>();
        cents.add(classifiedData[random.nextInt(classifiedData.length)]);
        double[] d = new double[classifiedData.length];

        while (cents.size() < k) {
            for (int i = 0; i < classifiedData.length; i++) {
                double dist = Math.pow(getClosestCentroidDX(classifiedData[i], cents), 2);
                d[i] = dist;
            }

            int i = getByPossibility(d);
            cents.add(classifiedData[i]);
        }

        for (int i = 0; i < k; i++) { //populate centroids with first k no. of data
            for (int j = 0; j < v; j++) {
                centroids[i][j] = cents.get(i)[j];
            }
        }
        Log.d("kcluster", "init cents");
        printVectors(centroids);

    }

    public double getClosestCentroidDX(double[] datum, ArrayList<double[]> cents) {
        double max = -1; //starts with minimum = centroids[0]
        for (int i = 0; i < cents.size(); i++) { //for each centroid
            double d = getDistance(datum, cents.get(i)); // between cluster centroid and object
            if (d > max) { //current distance is less than the minimum distance
                max = d;
            }
        }
        return max;
    }

    /**
     * Calculates Euclidean distance between cluster centroid to each object in data[][].
     * data[i] is the whole row eg: data[0] = {1.0,1.0} (same with centroids[i]).
     *
     * @param Array of datum and array of centroid.
     * @return Distances for one row of centroid.
     */
    public double getDistance(double[] datum, double[] centroid) {
        double d = 0.0;

        for (int i = 0; i < datum.length; i++) { //calculate distance for each row of data
            d += Math.pow(datum[i] - centroid[i], 2); //Euclidean distance
        }

        return (Math.sqrt(d)); //return distance (note: only returns distances for one row of centroid)
    }

    /**
     * Gets centroids with the smallest distance for one datum.
     *
     * @param Array of datum.
     * @return closestCentroid to datum.
     */

    double min;
    int closestIndex;
    double d;

    public int getClosestCentroid(double[] datum) {
        min = Double.MAX_VALUE; //starts with minimum = centroids[0]
        closestIndex = -1; //-1 because 0 could be a result value
        d = Double.MAX_VALUE;

        for (int i = 0; i < centroids.length; i++) { //for each centroid
            d = getDistance(datum, centroids[i]); // between cluster centroid and object

            if (d < min) { //current distance is less than the minimum distance
                closestIndex = i; //k is now the location of the closest centroid
                min = d;
            }
        }
        return (closestIndex); //returns index of the closest centroid for current datum
    }

    public double getClosestCentroidDist(double[] datum) {
        double min = Double.MAX_VALUE; //starts with minimum = centroids[0]

        for (int i = 0; i < centroids.length; i++) { //for each centroid
            double d = getDistance(datum, centroids[i]); // between cluster centroid and object
            if (d < min) { //current distance is less than the minimum distance
                min = d;
            }
        }
        return min;
    }


    /**
     * Print array of datum to terminal.
     *
     * @param Array of datum.
     */
    public void printDatum(double[] datum) {
        Vector<Double> v = new Vector<Double>();

        for (int j = 0; j < datum.length - 1; j++) {
            v.add(new Double(datum[j]));
        }

        System.out.println();
    }

    /**
     * Print centroids to terminal.
     *
     * @param Array of datum.
     */
    public void printCentroids() {
        for (int i = 0; i < centroids.length; i++) {
            printDatum(centroids[i]);
        }

        System.out.println("-------------------");
    }

    /**
     * The run method essentially creates new centroids. Firstly, it resets the value of n as this counts
     * how many data objects belong to a centroid - it needs to be 0 as the centroids modify themelf at
     * every iteration. The closestCentroid variable holds the index of the closest centroid of certain data,
     * it does this using Euclidean Distance. It sums up all datum sharing the same closest centroid in order
     * to get the mean of all the data belonging to that centroid.
     * It calls the terminator method to check for stability between old and new centroids, stability will
     * cause the run method to terminate.
     * It then calls the getClassification method to assign centroids to a classication value, then print
     * output to file.
     */

    public void printVector(double[] v) {
        StringBuffer buffer = new StringBuffer();
        for (double d : v) {
            buffer.append("\t" + d);
        }
        buffer.append("\n");
        Log.d("kcluster", buffer.toString());
    }

    public void printVectors(double[][] v) {
        for (double[] d : v) {
            printVector(d);
        }
        Log.d("kcluster", "---------");
    }

    HashMap<double[][], Double> map = new HashMap<>();

    public void iterRun() {

        for (int i = 0; i < time; i++) {
//            initCents();
            initKmeansPP();
            Trace.beginSection("iterun");
            try {
                double cents[][];
                double err;
                Trace.beginSection("run");
                try {
                    cents = run();
                    Log.d("kcluster", "init results");
                    printVectors(cents);
                } finally {
                    Trace.endSection();
                }
                Trace.beginSection("err");
                try {
                    err = getErr();
                } finally {
                    Trace.endSection();
                }

                map.put(cents, err);
            } finally {
                Trace.endSection();
            }

        }
        double min = Integer.MAX_VALUE;
        Log.d("kcluster", "results: ");
        for (Map.Entry<double[][], Double> entry : this.map.entrySet()) {
            printVectors(entry.getKey());
            Log.d("kcluster", "err: " + String.valueOf(entry.getValue()));

            min = Math.min(min, entry.getValue());

        }

        double[][] cents = new double[0][v];
        for (Map.Entry<double[][], Double> entry : this.map.entrySet()) {
            if (entry.getValue() == min) {
                cents = entry.getKey();
                break;
            }
        }
        try {
            printNewClassifications(classifiedData, cents);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double getErr() {
        double total = 0;

        for (int i = 0; i < classifiedData.length; i++) { //ALL data objects
//            int count = getClosestCentroid(classifiedData[i]); //gets closest centroid for ALL distances
            double dis = getClosestCentroidDist(classifiedData[i]);
            total += dis;
        }
        Log.d("kcluster", "err temp: " + String.valueOf(total));
        return total;
    }


    public double[][] run() {
        boolean check = false;
        int c = 0;
        int threshHold = 50;
        while (check == false) {
            boolean result = true;
//            double newCentroids[][] = new double[k][v];// length = k
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < v; j++) {
                    newCentroids[i][j] = 0;
                }
            }


//         double newCentroids[][]=new double[k][v];
            for (int i = 0; i < k; i++) {
                n[i] = 0; //reset value of n[]
            }
//            printCentroids();
            Trace.beginSection("close");
            try {
                for (int i = 0; i < classifiedData.length; i++) { //ALL data objects
                    int count = getClosestCentroid(classifiedData[i]); //gets closest centroid for ALL distances

                    for (int j = 0; j < v; j++) {
                        newCentroids[count][j] += classifiedData[i][j]; //sums all datum belonging to certain centroid
                    }
                    n[count]++; //counts the no. of members of datum that belong to centroid group
                }


            } finally {
                Trace.endSection();
            }


            //finds the average between all datum belonging to certain centroid
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < v; j++) {
                    newCentroids[i][j] = newCentroids[i][j] / n[i];
                }
            }

            //checks if newCentroid values are same as Centroid
            //If they are then there are no more move groups and no more iterations are needed
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < v; j++) {
                    if (result == true) {
                        if (newCentroids[i][j] == centroids[i][j] || ++c > threshHold) { //checks for stability
                            check = true;
                            result = true;
                        } else {

                            check = false;
                            result = false;
                        }
                    }
                }
            }


//            System.arraycopy(newCentroids,0,centroids,0,centroids.length);
            for (int i = 0; i < k; i++) {
                System.arraycopy(newCentroids[i], 0, centroids[i], 0, v);

            }

//            getClassification(classifiedData, centroids);
        }

        return centroids;
//        try {
//            printNewClassifications(classifiedData, newCentroids);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Assigns new centroid arrays with a clasification value of 1 or 0. It looks for target
     * (classified) data that are closest to the new centroid being processed and checks to see if the
     * classification value for the classifiedData array being processed is a 1 or a 0 then assigns
     * that classification value to the centroid array being processed.
     */
    public void getClassification(double[][] datum, double[][] centroid) {

        int positive = 0; //represents 1
        int negative = 0; //represents 0

        for (int i = 0; i < centroid.length; i++) {
            for (int j = 0; j < datum.length; j++) {
                if (i == getClosestCentroid(datum[j])) { //if data is closest to current newCentroid
                    if (datum[j][6] == 1) { //count positive or negative
                        positive++;
                    } else {
                        negative++;
                    }
                }

                if (positive > negative) { //use counted values to label new centroid
                    centroid[i][6] = 1;
                } else {
                    centroid[i][6] = 0;
                }
            }

            positive = 0;
            negative = 0;
        }
    }

    /**
     * Prints out classification values for test (unclassified) data in both the terminal window and
     * a new file "output.txt" in the root folder. Throws an input output exception.
     *
     * @param An unclassifiedData array, a centroid array.
     */
    public void printNewClassifications(double[][] unclassifiedData, double[][] centroid) throws IOException {

        //creates an outputfile in ascii format
//        FileWriter outputFile = new FileWriter("output.txt");
//        PrintWriter outputPrint = new PrintWriter(outputFile);
        Trace.beginSection("err");
        try {
            StringBuilder sb = new StringBuilder();
            int clusterCount = 0;
            int count = 0;

            for (int i = 0; i < clusterIndex.length; i++) {
                if (clusterIndex[i][0] == -1) {
                } else {
                    for (int j = 0; j < clusterIndex[i].length; j++) {
                        int closest = getClosestCentroid(unclassifiedData[count]);
                        clusterIndex[i][j] = closest;
                        count++;
                    }
                }
            }

            count=0;
            for(int i = 0; i < clusterIndex2.length; i++){
                if (silence.get(i) == 0) {

                }else{
                    for (int j = 0; j < silence.get(i); j++) {
                        int closest = getClosestCentroid(unclassifiedData[count]);
                        clusterIndex2[i][j] = 5+closest*5;
                        count++;
                    }
                }
            }

            StringBuffer buffer = new StringBuffer();
            for(int a[]:clusterIndex2){
                for(int b:a){
                    buffer.append("\t"+String.valueOf(b));
                }
            }
            Log.d("asdfasdf",buffer.toString());

//            for (int i = 0; i < silence.size(); i++) {
//                if (silence.get(i) == 0) { //silence
//                    clusterIndex[clusterCount] = -1;
//                    clusterCount++;
//                } else {
//                    for (int j = 0; j < silence.get(i); j++) {
//                        int closest = getClosestCentroid(unclassifiedData[count]);
//                        clusterIndex[clusterCount] = closest;
//                        count++;
//                        clusterCount++;
//                    }
//                }
//            }
//            for (int[] i : clusterIndex) {
//                LogUtil.log(i, "cluster");
//            }


            for (int i = 0; i < unclassifiedData.length; i++) {
                int closest = getClosestCentroid(unclassifiedData[i]);
//            Log.d("kcluster", String.valueOf(closest));
//            outputPrint.println((int)centroids[closest][6]);
                sb.append(closest + "\t");

            }

            Log.d("kcluster", sb.toString());
        } finally {
            Trace.endSection();
        }
        //for all test data length, checks distance and classifies new centroids


        for (int i = 0; i < centroids.length; i++) {
            Vector<Double> v = new Vector<Double>();
            for (int j = 0; j < centroids[i].length; j++) {
                v.add(new Double(centroids[i][j]));
            }
            Log.d("kcluster", "\t" + v.get(0) + "\t" + v.get(1));
        }


//        System.out.println(v);
//        outputPrint.println("");
//        outputPrint.close();
    }


}