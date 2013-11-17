package edu.ufl.cise.mathbox;

import java.util.HashMap;

public class Constants {
	//Constants
	public static final String appName = "MathBox";
	public static final String textExpression = "Expression";
	public static final String neverMind = "Never mind!";
	
	public static float decimalGestureLength = 500;
	
	//Gesture strings starts
	public static final String backspace = "backspace";
	public static final String clear = "clear";
	public static final String checkmark = "checkmark";
	public static final String equalTo = "=";
	//Gesture string ends
	
	//Arithmetic Symbols start
	public static final String plus = "+";
	public static final String divideBy = "/";
	public static final String minus = "-";
	public static final String asterik = "*";
	public static final String zero = "0";
	public static final String decimal = ".";
	public static final String four = "4";
	//Arithmetic Symbols end
	
	//Special symbols start
	public static final String sigma = "sigma";
	public static final String pi = "pi";
	//Special symbols end
	
	//Added by Anirudh Subramanian on 17th November (Memory) Start
	public static final String memorize = "m";
	public static final String retrieveMemory = "r";
	//Added by Anirudh Subramanian on 17th November (Memory) End
	
	
	//Added by Sagar Parmar on 17th November ShowCaseview start
	public static final float RADIUS_SCALE = 0.35f;
	public static final float BIG_RADIUS_SCALE = 1f;
	public static final float MID_RADIUS_SCALE = 0.6f;
	//Added by Sagar Parmar on 17th November ShowCaseview start
	
	//Added by Sagar Parmar as constants 17th Nov start
	public static final String checkTheExpression = "Please check the expression!";
	public static final String cantBeMemorized = "The expression cannot be evaluated and so cannot be memorized!";
	//Added by Sagar Parmar as constants 17th Nov end
	
	public static final HashMap<String,String> userReadableNames = new HashMap<String, String>();
	
	public class UserReadableNames {
		public static final String backspace = "Backspace";
		public static final String clear = "Clear";
		public static final String checkmark = "\u2713";
		public static final String pi = "\u03C0";
		public static final String sigma = "\u03A3";
	}
	
	static void initUserReadableNames() {
		userReadableNames.put(backspace, UserReadableNames.backspace);
		userReadableNames.put(clear, UserReadableNames.clear);
		userReadableNames.put(checkmark, UserReadableNames.checkmark);
		userReadableNames.put(pi, UserReadableNames.pi);
		userReadableNames.put(sigma, UserReadableNames.sigma);
	}
}
