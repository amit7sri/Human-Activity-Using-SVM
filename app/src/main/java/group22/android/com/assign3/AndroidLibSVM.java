package group22.android.com.assign3;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Lakshmisagar on 3/26/2017.
 */

public class AndroidLibSVM {

    private static final String TAG = "Libsvm";

    // svm native
    private native int trainClassifierNative(String trainingFile, int kernelType,
                                             int cost, float gamma, int isProb, String modelFile);
    private native int doClassificationNative(float values[][], int indices[][],
                                              int isProb, String modelFile, int labels[], double probs[]);

    static {
        System.loadLibrary("signal");
    }

    public int train() {
        // Svm training
        int kernelType = 2; // Radial basis function
        int cost = 4; // Cost
        int isProb = 0;
        float gamma = 0.25f; // Gamma
        String trainingFileLoc = Environment.getExternalStorageDirectory()+"/Download/trainingdata.txt";
        String modelFileLoc = Environment.getExternalStorageDirectory()+"/model";
        int accuracy = trainClassifierNative(trainingFileLoc, kernelType, cost, gamma, isProb,
                modelFileLoc);
        if (accuracy == -1) {
            Log.d(TAG, "training err");
        }
        Log.d(TAG,"training Finished");
        return accuracy;
    }

    /**
     * classify generate labels for features.
     * Return:
     * 	-1: Error
     * 	0: Correct
     */
    public int callSVM(float values[][], int indices[][], int groundTruth[], int isProb, String modelFile,
                       int labels[], double probs[]) {
        // SVM type
        final int C_SVC = 0;
        final int NU_SVC = 1;
        final int ONE_CLASS_SVM = 2;
        final int EPSILON_SVR = 3;
        final int NU_SVR = 4;

        // For accuracy calculation
        int correct = 0;
        int total = 0;
        float error = 0;
        float sump = 0, sumt = 0, sumpp = 0, sumtt = 0, sumpt = 0;
        float MSE, SCC, accuracy;

        int num = values.length;
        int svm_type = C_SVC;
        if (num != indices.length)
            return -1;
        // If isProb is true, you need to pass in a real double array for probability array
        int r = doClassificationNative(values, indices, isProb, modelFile, labels, probs);

        // Calculate accuracy
        if (groundTruth != null) {
            if (groundTruth.length != indices.length) {
                return -1;
            }
            for (int i = 0; i < num; i++) {
                int predict_label = labels[i];
                int target_label = groundTruth[i];
                if(predict_label == target_label)
                    ++correct;
                error += (predict_label-target_label)*(predict_label-target_label);
                sump += predict_label;
                sumt += target_label;
                sumpp += predict_label*predict_label;
                sumtt += target_label*target_label;
                sumpt += predict_label*target_label;
                ++total;
            }

            if (svm_type==NU_SVR || svm_type==EPSILON_SVR)
            {
                MSE = error/total; // Mean square error
                SCC = ((total*sumpt-sump*sumt)*(total*sumpt-sump*sumt)) / ((total*sumpp-sump*sump)*(total*sumtt-sumt*sumt)); // Squared correlation coefficient
            }
            accuracy = (float)correct/total*100;
            Log.d(TAG, "Classification accuracy is " + accuracy);
        }

        return r;
    }

    public String classify(ArrayList<DataValues> valueHolderClassify) {
        // Svm classification
        float[][] values = new float[1][150];
        float[] row = new float[150];
        values[0] = row;

        for(int i =0;i<50;i++){
            row[i++] = (float)valueHolderClassify.get(i).getX();
        }

        int[][] indices = new int [1][150];
        int[] newRow = new int [150];
        indices[0] = newRow;
        for(int j=0;j<150;j++) {
            newRow[j] = j+1;
        }
        int[] groundTruth = null;
        int[] labels = new int[1];
        double[] probs = new double[4];
        int isProb = 0; // Not probability prediction
        String modelFileLoc = Environment.getExternalStorageDirectory()+"/model";

        if (callSVM(values, indices, groundTruth, isProb, modelFileLoc, labels, probs) != 0) {
            Log.d(TAG, "Classification is incorrect");
        }

        switch (labels[0]) {
            case 1:
                return "Walking";
            case 2:
                return "Running";
            case 3:
                return "Eating";
            default:
                return "Activity not found";
        }
    }
}
