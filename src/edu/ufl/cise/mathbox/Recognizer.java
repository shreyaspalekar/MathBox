package edu.ufl.cise.mathbox;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.Prediction;
import android.util.Log;

public class Recognizer {

	/**
	 * Given an arraylist of predictions and the gesture drawn, determine the best recognized gesture.
	 * If not sure about the prediction i.e. prediction score is low then return the list of 4 gestures
	 * that are recognized with the highest scores
	 * @param predictions an array list of prediction as given by android system
	 * @param gesture Gesture class having all gesture atrributes
	 * @return Arraylist of possible recognized symbols
	 */
	public static ArrayList<String> recognizeGesture(ArrayList<Prediction> predictions, Gesture gesture) {
		ArrayList<String> strCurrentPrediction = new ArrayList<String>();
    	Double predictionScore;
    	
    	if (predictions.size() == 0)
    		return strCurrentPrediction;
    	
    	String tempPrediction = predictions.get(0).toString();
    	predictionScore = predictions.get(0).score;
    	Log.d(Constants.TAG, "All predictions = " + predictions);
    	
    	//Filter based on numOfStrokes
    	int numOfStroke = gesture.getStrokesCount();
    	//Length is used to differnciate between 0 & decimal
    	float length = gesture.getLength();
    	if(predictionScore > 35.00) {
    		
        	if(tempPrediction.equals(Constants.minus) || tempPrediction.equals(Constants.divideBy) 
        			|| tempPrediction.equals(Constants.equalTo) ) {
        		Log.d(Constants.TAG, "inside - / or = " + numOfStroke);
        		if (numOfStroke == 1)
        			tempPrediction = Constants.minus;
        		else if (numOfStroke == 2)
        			tempPrediction = Constants.equalTo;
        	}
        	
        	else if(tempPrediction.equals(Constants.plus) || tempPrediction.equals(Constants.asterik)) {
        		Log.d(Constants.TAG, "inside plus,star,help and numstroke=" + numOfStroke);
        		if (numOfStroke == 2)
        			tempPrediction = Constants.plus;
        	}
        	else if(tempPrediction.equals(Constants.zero) || tempPrediction.equals(Constants.decimal)) {
        		Log.d(Constants.TAG, "inside zero/decimal and length=" + length);
        		if (length < Constants.decimalGestureLength)
        			tempPrediction = Constants.decimal;
        		else 
        			tempPrediction = Constants.zero;
        	}
        	else if(tempPrediction.equals(Constants.backspace) || tempPrediction.equals(Constants.four)) {
        		Log.d(Constants.TAG, "inside backspace or four and numstroke=" + numOfStroke);
        		if (numOfStroke == 1)
        			tempPrediction = Constants.backspace;
        		else if(numOfStroke == 2)
        			tempPrediction = Constants.four;
        	}
        	/* Added code for "help" gesture */
        	else if(tempPrediction.equals(Constants.help)) {
        		/* pick the next prediction because numstrokes != 2 */
        		if(numOfStroke != 2)
        			tempPrediction = predictions.get(1).toString();
        	}
        	else if(tempPrediction.equals(Constants.one)) {
        		/* pick the next prediction because numstrokes != 2 */
        		if(numOfStroke != 1)
        			tempPrediction = predictions.get(1).toString();
        	}
        	Log.d(Constants.TAG, "Recognized = " + tempPrediction + " Score = " + predictionScore);
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