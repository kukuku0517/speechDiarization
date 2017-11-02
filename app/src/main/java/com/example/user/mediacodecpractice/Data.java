package com.example.user.mediacodecpractice;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Data {
    //    public double[][] classifiedData;
    public double[][] unclassifiedData;

    public ArrayList<double[]> data;
    int v;

    public double token;
    public String currentLine;
    Context context;

    public Data(Context context, int v) {
        this.context = context;
        this.v = v;
//        classifiedData = new double[265][7];
        unclassifiedData = new double[100][6];
    }

    //populate trainingData[][] with values from the training file (data with classifications)
    public double[][] readTrainingFile() {
        data = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.getResources().openRawResource(R.raw.filename)));
            currentLine = br.readLine(); //start by reading the current line of text from the file


            while (true) { //from trainingData[0] to trainingData[266]
                if (currentLine==null) break;
// && currentLine.length() == 0
                double[] tokens = new double[v];

                StringTokenizer line = new StringTokenizer(currentLine); //tokenise the current line of text
                int col = 0;

                while (line.hasMoreTokens()) { //check if there are anymore tokens to be read
                    token = Double.parseDouble(line.nextToken()); //convert string token into a double value
//                    classifiedData[row][col] = token; //current row and column is assigned the current token
                    tokens[col] = token;
                    col++;
                }
                data.add(tokens);
                currentLine = br.readLine(); //read the next line from the file
            }
        } catch (FileNotFoundException e) { //print error message if specified file is not found
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return data.toArray(new double[][]{new double[0]});
    }

    //populate testData[][] with values from the test file (the data with no classifications)
    public double[][] readTestFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("unclassifieddata.txt"));
            currentLine = br.readLine(); //start by reading the current line of text from the file
            int row = 0;

            while (row < 100) { //from testData[0] to testData[100]
                StringTokenizer line = new StringTokenizer(currentLine);
                int col = 0;

                while (line.hasMoreTokens()) { //check if there are anymore tokens to be read
                    token = Double.parseDouble(line.nextToken()); //convert string token into a double value
                    unclassifiedData[row][col] = token; //current row and column is assigned the current token
                    col++;
                }
                currentLine = br.readLine(); //read the next line from the file
                row++;
            }
        } catch (FileNotFoundException e) { //print error message if specified file is not found
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return unclassifiedData; //returns testData[][]
    }
}
