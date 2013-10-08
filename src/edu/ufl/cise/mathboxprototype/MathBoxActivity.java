package edu.ufl.cise.mathboxprototype;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import expr.Expr;
import expr.Parser;
import expr.SyntaxException;

public class MathBoxActivity extends Activity implements OnGesturePerformedListener,OnTouchListener {

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private GestureOverlayView mGestureOverlayView;
    private GestureLibrary mGestureLibrary;
    private ImageButton mClearButton;
    private ImageButton mEvaluateButton;
    private ImageButton mBackspaceButton;
    private TextView mTextViewExpr;
    private boolean bExpressionEvaluated =  false;
    private String mStrExpression = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_box);
        
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mGestureOverlayView = (GestureOverlayView) findViewById(R.id.gestureOverlayView1);
        mGestureOverlayView.addOnGesturePerformedListener(this);
        mGestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures_10_7_2_36);
        if (!mGestureLibrary.load()) 
            finish();
        
        
        mClearButton = (ImageButton) findViewById(R.id.deleteButton1);
        mClearButton.setOnTouchListener(this);

        mEvaluateButton = (ImageButton) findViewById(R.id.checkMarkButton1);
        mEvaluateButton.setOnTouchListener(this);
        
        mBackspaceButton = (ImageButton) findViewById(R.id.backspaceButton1);
        mBackspaceButton.setOnTouchListener(this);
        
        mTextViewExpr = (TextView) findViewById(R.id.exprTextView1);
        mTextViewExpr.setTextColor(Color.GRAY);
        mTextViewExpr.setText(Constants.textExpression);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.math_box, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    	ArrayList<Prediction> predictions = mGestureLibrary.recognize(gesture);
    	
    	String strCurrentPrediction;
    	Double predictionScore;
    	
    	strCurrentPrediction = predictions.get(0).toString();
    	predictionScore = predictions.get(0).score;
    	
    	Log.d(Constants.appName, "Recognized = " + strCurrentPrediction + " Score = " + predictionScore);
    	
    	if(bExpressionEvaluated || mTextViewExpr.getText().toString().matches(Constants.textExpression)) {
    		bExpressionEvaluated = false;
    		mStrExpression = "";
    		mTextViewExpr.setText(mStrExpression);
    	}
    		
    	if(mTextViewExpr.getCurrentTextColor() == Color.GRAY)
    		mTextViewExpr.setTextColor(Color.BLACK);
    	
    	if(predictionScore > 2.00) {
    		if(strCurrentPrediction.matches("=")) {
        		evaluateExpression();
        		bExpressionEvaluated = true;
        	}
        	else if(strCurrentPrediction.matches(Constants.backspace)){
        		handleBackspace();
        	}
        	else if(strCurrentPrediction.matches(Constants.checkmark)){
				evaluateExpression();
			}
        	else if(strCurrentPrediction.matches(Constants.clear)){
        		clearCanvas();
        	}
        	else if(strCurrentPrediction.matches(Constants.sigma)) {
        		
        	}
        	else {
        		mStrExpression += strCurrentPrediction;
    			mTextViewExpr.setText(mStrExpression);
        	}
    	}
    	else {
    		if(mStrExpression.length() == 0) {
    			mTextViewExpr.setTextColor(Color.GRAY);
		        mTextViewExpr.setText(Constants.textExpression);
    		}
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mTextViewExpr.startAnimation(shake);
			Toast.makeText(this, "Couldn't recognize the symbol!", Toast.LENGTH_SHORT).show();
		}
    	Log.d(Constants.appName, "mStrExpression = " + mStrExpression);
    }
    
    private void evaluateExpression() {
    	//Evaluate expression
		try {
			Log.d(Constants.appName, "Going to evaluate:\"" + mStrExpression + "\"");
			//Calculable calc = new ExpressionBuilder(mStrExpression).build();
			Expr expr = Parser.parse(mStrExpression); 
			mStrExpression = Double.toString(expr.value());
			mTextViewExpr.setText(mStrExpression);
		}
		catch (SyntaxException e) {
			Log.d(Constants.appName,e.explain());
			Toast.makeText(this, "Please check the expression!", Toast.LENGTH_SHORT).show();
			mTextViewExpr.setTextColor(Color.GRAY);
	        mTextViewExpr.setText(Constants.textExpression);
	        mStrExpression = "";
		}
		mGestureOverlayView.cancelClearAnimation();
		mGestureOverlayView.clear(true);
    }


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
			case R.id.deleteButton1: {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					mClearButton.setImageResource(R.drawable.trash_icon_green);
					return true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					mClearButton.setImageResource(R.drawable.trash_icon_black);
					clearCanvas();
					return true;
				}
				return false;
			}
			case R.id.checkMarkButton1: {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					evaluateExpression();
					return true;
				}
				return false;
			}
			case R.id.backspaceButton1: {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					handleBackspace();
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	private void handleBackspace() {
		if(mStrExpression.length() >= 2) {
			mStrExpression = mStrExpression.substring(0, mStrExpression.length()-1);
			mTextViewExpr.setText(mStrExpression);	
		}
		else {
			mStrExpression = "";
			mTextViewExpr.setTextColor(Color.GRAY);
	        mTextViewExpr.setText(Constants.textExpression);
		}
	}
	
	private void clearCanvas() {
		mTextViewExpr.setTextColor(Color.GRAY);
        mTextViewExpr.setText(Constants.textExpression);
        mStrExpression = "";
		mGestureOverlayView.cancelClearAnimation();
		mGestureOverlayView.clear(true);
	}
}