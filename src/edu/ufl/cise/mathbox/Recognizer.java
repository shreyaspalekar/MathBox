package edu.ufl.cise.mathbox;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.Prediction;
import android.util.Log;

public class Recognizer {
	
	public static ArrayList<String> recognizeGesture(ArrayList<Prediction> predictions, Gesture gesture) {
		ArrayList<String> strCurrentPrediction = new ArrayList<String>();
    	Double predictionScore;
    	
    	if (predictions.size() == 0)
    		return strCurrentPrediction;
    	
    	String tempPrediction = predictions.get(0).toString();
    	predictionScore = predictions.get(0).score;
    	Log.d(Constants.appName, "All predictions = " + predictions);
    	
    	//Filter based on numOfStrokes
    	int numOfStroke = gesture.getStrokesCount();
    	//Length is used to differnciate between 0 & decimal
    	float length = gesture.getLength();
    	if(predictionScore > 3.00) {
    		
        	if(tempPrediction.equals(Constants.minus) || tempPrediction.equals(Constants.divideBy) 
        			|| tempPrediction.equals(Constants.equalTo) ) {
        		Log.d(Constants.appName, "inside - / or = " + numOfStroke);
        		if (numOfStroke == 2)
        			tempPrediction = Constants.equalTo;
        	}
        	
        	else if(tempPrediction.equals(Constants.plus) || tempPrediction.equals(Constants.asterik)) {
        		Log.d(Constants.appName, "inside plus or star and numstroke=" + numOfStroke);
        		if (numOfStroke == 2)
        			tempPrediction = Constants.plus;
        		else if(numOfStroke == 4)
        			tempPrediction = Constants.asterik;
        	}
        	else if(tempPrediction.equals(Constants.zero) || tempPrediction.equals(Constants.decimal)) {
        		Log.d(Constants.appName, "inside zero/decimal and length=" + length);
        		if (length < Constants.decimalGestureLength)
        			tempPrediction = Constants.decimal;
        		else 
        			tempPrediction = Constants.zero;
        	}
        	else if(tempPrediction.equals(Constants.backspace) || tempPrediction.equals(Constants.four)) {
        		Log.d(Constants.appName, "inside backspace or four and numstroke=" + numOfStroke);
        		if (numOfStroke == 1)
        			tempPrediction = Constants.backspace;
        		else if(numOfStroke == 2)
        			tempPrediction = Constants.four;
        	}
        	Log.d(Constants.appName, "Recognized = " + tempPrediction + " Score = " + predictionScore);
        	strCurrentPrediction.add(tempPrediction);
    	}
    	else if(predictionScore > 1.60){
    		strCurrentPrediction.add(tempPrediction);
    		strCurrentPrediction.add(predictions.get(1).toString());
    		strCurrentPrediction.add(predictions.get(2).toString());
    		strCurrentPrediction.add(predictions.get(3).toString());
    	}
		return strCurrentPrediction;
	}
}