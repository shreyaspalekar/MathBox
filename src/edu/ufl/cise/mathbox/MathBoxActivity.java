package edu.ufl.cise.mathbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStore;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;

import expr.Expr;
import expr.Parser;
import expr.SyntaxException;
/*Added by Anirudh Subramanian on 17th November Start*/
/*Added by Anirudh Subramanian on 17th November End*/

public class MathBoxActivity extends Activity implements OnGesturePerformedListener,OnTouchListener {

	/*Added by Anirudh Subramanian on 17th November Start*/
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;
	/*Added by Anirudh Subramanian on 17th November End*/
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private GestureOverlayView mGestureOverlayView;
    private GestureLibrary mGestureLibrary;
    private ImageButton mClearButton;
    private ImageButton mEvaluateButton;
    private ImageButton mBackspaceButton;
    private WebView mWebViewExpr;
    private boolean bExpressionEvaluated =  false;
    private String mStrExpression = "";
    /*Added by Anirudh Subramanian on 16th November Start*/

    private String mEvaluatedExpression = "";
    private boolean bExpressionMemorized = false;
    private String mMemorizedExpression = "";
    private boolean bFromMemorize = false;
    private boolean bFromXMemorize = false;
    private boolean bFromYMemorize = false; 
    /*Added by Anirudh Subramanian on 16th November End*/
    private ArrayList<String> mArrayListHistory = null;
    private ListView mDrawerList;
    private String[] mListItemTitles;
    /*Added by Anirudh Subramanian on 17th November Begin*/
    private HashMap<String, Double> variablesSet = new HashMap<String, Double>();
    /*Added by Anirudh Subramanian on 17th November End*/

    /* Added by Sagar Parmar on 17th November Start */
    private ShowcaseViews sViews;
    private ShowcaseView.ConfigOptions svOptions = new ShowcaseView.ConfigOptions();
    //For saving screenshot
    private String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "mathbox_screenshot";
    private static final String PREFS_NAME = "MathBoxPrefFile";
    private boolean bShowTutorialOnLaunch = true;
    /* Added by Sagar Parmar on 17th November End*/
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		// app initialization
	Constants.initUserReadableNames();
	Constants.initVariableNames();
	Constants.initConstantNames();	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_box);
        
        Log.d(Constants.TAG,"on create called");
        if(savedInstanceState == null)
        	mStrExpression = "";
        else
        	mStrExpression = savedInstanceState.getString("savedExpression");
        /* Added by Sagar Parmar on 17th November for showcase view Start */
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        bShowTutorialOnLaunch = settings.getBoolean(Constants.tutOnLaunch, true);
        
        if(bShowTutorialOnLaunch) {
	        svOptions.hideOnClickOutside = false;
			svOptions.block = false;
			sViews = new ShowcaseViews(this,new ShowcaseViews.OnShowcaseAcknowledged() {
	            @Override
	            public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
	            	showTutorialAlertDialog("Tutorial","Show this tutorial on launch?");
	            	if(mStrExpression.length() != 0)
						setWebViewText(mStrExpression);
					else
						setWebViewText(Constants.textExpression);
	            }
	        });
			sViews.addView( new ShowcaseViews.ItemViewProperties(R.id.gestureOverlayView1,
	                R.string.gesture_area_title,
	                R.string.gesture_area_msg,
	                Constants.BIG_RADIUS_SCALE));
			sViews.addView( new ShowcaseViews.ItemViewProperties(R.id.exprTextView1,
	                R.string.output_area_title,
	                R.string.output_area_msg,
	                Constants.MID_RADIUS_SCALE));
	        sViews.addView( new ShowcaseViews.ItemViewProperties(R.id.deleteButton1,
	                R.string.delete_button_title,
	                R.string.delete_button_msg,
	                Constants.RADIUS_SCALE));
	        sViews.addView( new ShowcaseViews.ItemViewProperties(R.id.checkMarkButton1,
	                R.string.checkmark_button_title,
	                R.string.checkmark_button_msg,
	                Constants.RADIUS_SCALE));
	        sViews.addView( new ShowcaseViews.ItemViewProperties(R.id.backspaceButton1,
	                R.string.backspace_button_title,
	                R.string.backspace_button_msg,
	                Constants.RADIUS_SCALE));
	        sViews.show();
        }
        /* Added by Sagar Parmar on 17th November for showcase view End */
        
	
        mTitle = mDrawerTitle = getTitle();
        mListItemTitles = getResources().getStringArray(R.array.list_item_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mListItemTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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
        mGestureOverlayView.setBackgroundColor(0xffffff);
        
        mGestureLibrary = GestureLibraries.fromRawResource(this,R.raw.gestures);
        mGestureLibrary.setOrientationStyle(8);
        mGestureLibrary.setSequenceType(GestureStore.SEQUENCE_INVARIANT);
        if (!mGestureLibrary.load()) 
            finish();
        
        
        mClearButton = (ImageButton) findViewById(R.id.deleteButton1);
        mClearButton.setOnTouchListener(this);

        mEvaluateButton = (ImageButton) findViewById(R.id.checkMarkButton1);
        mEvaluateButton.setOnTouchListener(this);
        
        mBackspaceButton = (ImageButton) findViewById(R.id.backspaceButton1);
        mBackspaceButton.setOnTouchListener(this);
       
	  /* Modified by Sagar Parmar on 17th Nov
	   * Note: Modified to show "Expression" text on showcaseview ack 
	   * */

        
        /* Modified by Sagar Parmar on 17th Nov
         * Note: Modified to show "Expression" text on showcaseview ack 
         * */
        mWebViewExpr = (WebView) findViewById(R.id.exprTextView1);
        mWebViewExpr.getSettings().setJavaScriptEnabled(true);
        mWebViewExpr.getSettings().setBuiltInZoomControls(false);
        //Moved part of loading url to onResume
        
	mArrayListHistory = new ArrayList<String>();

	/*Added by Anirudh Subramanian on 17th November for Shake Support Start*/
	
	mSensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	mSensorListener = new ShakeEventListener();
        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener(){
			public void onShake() {
				mStrExpression = "";
				Toast.makeText(MathBoxActivity.this, "Expression cleared!", Toast.LENGTH_SHORT).show();
				//Modified by Sagar - 18th Nov
				setWebViewText(Constants.textExpression);
			}
	});	
	
	/*Added by Anirudh Subramanian on 17th November for Shake Support End*/
        
    }

	/* Sagar: Changed method signature on 18th nov */
	private void showTutorialAlertDialog(String title, String msg) {
		new AlertDialog.Builder(this).setTitle(title)
    	.setMessage(msg)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {          
            	bShowTutorialOnLaunch = true;
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	bShowTutorialOnLaunch = false;
            }
        })
        .show();	
	}
	
	@Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(Constants.tutOnLaunch, bShowTutorialOnLaunch);

      // Commit the edits!
      editor.commit();
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.math_box, menu);
        return super.onCreateOptionsMenu(menu); 
    }
    
    private void prepareImage() {
    	// create bitmap screen capture
    	if(mWebViewExpr != null) {
	    	Bitmap bitmap = null;
	    	mWebViewExpr.setDrawingCacheEnabled(true);
	    	bitmap = Bitmap.createBitmap(mWebViewExpr.getDrawingCache());
	    	mWebViewExpr.setDrawingCacheEnabled(false);
	
	    	OutputStream fout = null;
	    	File imageFile = new File(mPath);
	
	    	try {
	    	    fout = new FileOutputStream(imageFile);
	    	    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
	    	    fout.flush();
	    	    fout.close();
	    	} catch (FileNotFoundException e) {
	    	    e.printStackTrace();
	    	} catch (IOException e) {
	    	    e.printStackTrace();
	    	}
    	}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.action_share:
	        	prepareImage();
	            Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    		shareIntent.setType("image/*");
	    		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mPath)));
	    		startActivity(Intent.createChooser(shareIntent, "Share Expression"));
	            return true;
	        case R.id.action_settings:
	        	showTutorialAlertDialog("Tutorial","Show tutorial on launch?");
	            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        switch(position) {
        	case 0:
        		for(String historyItem:mArrayListHistory)
        			Log.d(Constants.TAG,historyItem);
        		mDrawerList.setItemChecked(position, false);
        		mDrawerLayout.closeDrawer(mDrawerList);
        		break;
        	case 1:
                mDrawerList.setItemChecked(position, false);
        		mDrawerLayout.closeDrawer(mDrawerList);
        		startActivity(new Intent(this,HelpActivity.class));
        		break;
        	case 2:
        		mDrawerList.setItemChecked(position, false);
        		mDrawerLayout.closeDrawer(mDrawerList);
        		WebView webView = new WebView(this);
        	    webView.loadUrl("file:///android_asset/aboutus.html");
        	    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        	    dialog.setView(webView);
        	    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) {
        	            dialog.dismiss();
        	        }
        	    });
        	    dialog.show();
        		break;
        	default:
        		mDrawerList.setItemChecked(position, false);
        		mDrawerLayout.closeDrawer(mDrawerList);
        		break;
        }
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
    	final ArrayList<String> arrListPredictions;
    	
    	if(bExpressionEvaluated) {
    		bExpressionEvaluated = false;
    		mStrExpression = "";
    		/*Added by Anirudh Subramanian on 16th November Start*/
    		mStrExpression = mEvaluatedExpression;	//setting the value of expression to the evaluated expression	
    		/*Added by Anirudh Subramanian on 16th November End*/
    		setWebViewText(mStrExpression);
    	}
    	arrListPredictions = Recognizer.recognizeGesture(predictions, gesture);
    	if(arrListPredictions.size() != 0) {
    		if(arrListPredictions.size() == 1) {
    			// you are damn sure about the prediction
    			handlePrediction(arrListPredictions.get(0));
    		}
    		else {
    			String[] predictionsArr = new String[arrListPredictions.size() + 1];
    			predictionsArr = arrListPredictions.toArray(predictionsArr);
    			
    			for(int i=0; i < predictionsArr.length; i++) {
    				if(Constants.userReadableNames.containsKey(predictionsArr[i]))
    						predictionsArr[i] = Constants.userReadableNames.get(predictionsArr[i]); 
    			}
    			predictionsArr[arrListPredictions.size()] = Constants.neverMind; 
    					
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	    builder.setTitle(R.string.reco_pop);
        	    builder.setItems(predictionsArr, new DialogInterface.OnClickListener() {
        	    	public void onClick(DialogInterface dialog, int which) {
        	    		// The 'which' argument contains the index position
        	    		// of the selected item
        	    		if(which < arrListPredictions.size()) {
        	    			handlePrediction(arrListPredictions.get(which).toString());
        	    		}
        	    		//Else, ignoring selected - close the dialog
        	    	}
        	    });
        	    builder.show();
    		}
    	}
    	else {
    		if(mStrExpression.length() == 0) {
    			setWebViewText(Constants.textExpression);
    		}
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mWebViewExpr.startAnimation(shake);
			Toast.makeText(this, "Couldn't recognize the symbol!", Toast.LENGTH_SHORT).show();
		}
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
					if(bExpressionEvaluated) {
			    		bExpressionEvaluated = false;
			    		mStrExpression = "";
					/*Added by Anirudh Subramanian on 16th November Start*/
					mStrExpression = mEvaluatedExpression;
					/*Added by Anirudh Subramanian on 16th November End*/
			    		setWebViewText(mStrExpression);
			    	}
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
    
    private void handlePrediction(String predicted) {
    	if(predicted.equals(Constants.equalTo)) {
    		evaluateExpression();
    	}
    	else if(predicted.equals(Constants.backspace)){
    		handleBackspace();
    	}
    	else if(predicted.equals(Constants.checkmark)){
			evaluateExpression();
		}
    	else if(predicted.equals(Constants.clear)){
    		clearCanvas();
    	}
    	else if(predicted.equals(Constants.sigma)) {
    		mStrExpression += "\\sum\\limits_{i=1}^n";
    	}
	/*Added by Anirudh Subramanian on 17th November for memory feature implementation Start*/
	/*if gesture is m, first evaluate expression and then memorize value of the evaluation*/
	else if(predicted.equals(Constants.memorize)) {
		bFromMemorize = true;
		evaluateExpression();
		bFromMemorize = false;
		memorizeValue();
	}
	else if(predicted.equals(Constants.retrieveMemory)) {
		retrieveMemory();	
	}
	else if(predicted.equals(Constants.memorizeX)) {
		bFromXMemorize = true;
		evaluateExpression();
		bFromXMemorize = false;
		memorizeVariable("x");
	}
	else if(predicted.equals(Constants.memorizeY)) {
		bFromYMemorize = true;
		evaluateExpression();
		bFromYMemorize = false;
		memorizeVariable("y");	
	}
	else if(predicted.equals(Constants.help)) {
		startActivity(new Intent(this,HelpActivity.class));
	}	
	/*Added by Anirudh Subramanian on 17th November for memory feature implementation End*/
    	else {
		predicted = returnVariableOrConstant(predicted);
    		mStrExpression += predicted;
    	}
    	if(mStrExpression.length() != 0)
    		setWebViewText(mStrExpression);
    }
    
	private void evaluateExpression() {
    	//Evaluate expression
		try {
			Log.d(Constants.TAG, "Going to evaluate:\"" + mStrExpression + "\"");
			//Calculable calc = new ExpressionBuilder(mStrExpression).build();
			Expr expr = Parser.parse(mStrExpression); 
			/*Commented by Anirudh Subramanian on 16th November Start*/
			//mStrExpression = mStrExpression + "=" +  expr.value();
			//setWebViewText(mStrExpression);
			//bExpressionEvaluated =  true;
			//mArrayListHistory.add(mStrExpression);
			/*Commented by Anirudh Subramanian on 16th November end*/
			Double exprValue = expr.value();
    			/*Added by Anirudh Subramanian on 16th November Start*/
			if (( exprValue == Math.floor(exprValue)) && !Double.isInfinite(exprValue)) {
				    mEvaluatedExpression = "" + exprValue.intValue();
			}
			else {
				mEvaluatedExpression = "" + expr.value();
			}
			
			mStrExpression = mStrExpression + "=" +  mEvaluatedExpression;
			bExpressionEvaluated =  true;
			mArrayListHistory.add(mStrExpression);
			if(bFromMemorize || bFromXMemorize || bFromYMemorize)
				mStrExpression = mEvaluatedExpression;
			setWebViewText(mStrExpression);
    			/*Added by Anirudh Subramanian on 16th November End*/
		}
		catch (SyntaxException e) {
			Log.d(Constants.TAG,e.explain());

    			/*Modified by Anirudh Subramanian on 17th November Begin*/
			if(!bFromMemorize && !bFromXMemorize && !bFromYMemorize)
				Toast.makeText(this, Constants.checkTheExpression, Toast.LENGTH_SHORT).show();
			else {
				if(bFromMemorize || bFromXMemorize || bFromYMemorize)
					Toast.makeText(this, Constants.checkTheExpression, Toast.LENGTH_SHORT).show();
			}
			/*Modified by Anirudh Subramanian on 17th November End*/
    			/*Modified by Anirudh Subramanian on 17th November End*/
			/* Modified by Sagar on 17th November Start*/
			if(!bFromMemorize)
				Toast.makeText(this, Constants.checkTheExpression, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, Constants.cantBeMemorized,Toast.LENGTH_SHORT).show();
			/* Modified by Sagar on 17th November End*/
			
			setWebViewText(Constants.textExpression);
	        mStrExpression = "";
		}
		mGestureOverlayView.cancelClearAnimation();
		mGestureOverlayView.clear(true);
    }

   /*Added by Anirudh Subramanian on 17th November Begin*/
	private void memorizeValue() {
		mMemorizedExpression = mEvaluatedExpression;
		bExpressionMemorized = true;
		Toast.makeText(this, "Value stored in memory!", Toast.LENGTH_SHORT).show();
	}

	private void retrieveMemory() {
		if(!bExpressionMemorized) {
			Toast.makeText(this,"Nothing in memory!",Toast.LENGTH_SHORT).show();
			return;
		}
		else {
			mStrExpression += mMemorizedExpression;
			bExpressionMemorized = false;
		}


	}
	
	private void memorizeVariable(String inputString) {
		String mXValue = "";
		if(mEvaluatedExpression != null && !"".equals(mEvaluatedExpression)) {
			mXValue = mEvaluatedExpression;

		}

		//if(variablesSet.get(inputString) == null) {
			if(mXValue != null && !"".equals(mXValue)){
				variablesSet.put(inputString, Double.parseDouble(mXValue));
				Toast.makeText(this, Constants.valueForVariable + inputString, Toast.LENGTH_SHORT).show();
			}
		//}
	}
	/*
	private void retrieveX() {
		if(!bXMemoried) {
			 Toast.makeText(this,"Nothing in memory for X!",Toast.LENGTH_SHORT).show();
			return;
		}
		else {
			mStrExpression += mXValue;
			bXMemoried = false;
		}
	}
	}
	*/
	
	@Override
	protected void onResume() {
		Log.d(Constants.TAG,"On resume called");
		super.onResume();
		mWebViewExpr.loadDataWithBaseURL("http://bar", "<body bgcolor=\"#ffffff\"><script type='text/x-mathjax-config'>"
                +"MathJax.Hub.Config({ " 
				  	+"showMathMenu: false,"
				  	+"jax: ['input/TeX','output/HTML-CSS'], "
				  	+"extensions: ['tex2jax.js'], " 
				  	+"TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
				  	  +"'noErrors.js','noUndefined.js'] }, "
				  	+"showProcessingMessages: false,"
				  	+ "displayAlign: \"left\", "
				  	+ "messageStyle: \"none\", "
				  	+ "\"HTML-CSS\": { scale: 130}"
				  +"});</script>"
                +"<script type='text/javascript' "
				  +"src='file:///android_asset/MathJax/MathJax.js'"
						  +"></script><span id='math'></span></body>","text/html","utf-8",null);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				//Do something after 100ms
				if(mStrExpression.length() != 0)
					setWebViewText(mStrExpression);
				else if(bShowTutorialOnLaunch)
					/* Need to show blank in webview when tutorial is showing */
					setWebViewText("");
				else
					setWebViewText(Constants.textExpression);
			}
		}, 100);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);

	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}
	
   /*Added by Anirudh Subramanian on 17th November End*/ 

   /*Added by Anirudh Subramanian Begin*/
    /*Method to replace constants and variables with their values*/
   	private String returnVariableOrConstant(String inputString) {
		try{
			if(Constants.constantsList.contains(inputString) ) {
				Expr expr = Parser.parse(inputString);
				Double exprValue = expr.value();
				String val = "";
				if(exprValue != null){
					DecimalFormat df = new DecimalFormat("#.##");
					exprValue = Double.parseDouble(df.format(exprValue));
					if (( exprValue == Math.floor(exprValue)) && !Double.isInfinite(exprValue)) {
						val = "" + exprValue.intValue();
					} else {
						val = "" + exprValue;
					}
				}
				if(val != null && !"".equals(val)) {
					Toast.makeText(this, Constants.constSubstBy + val,Toast.LENGTH_SHORT).show();
					return val;
				}
				
				else {
					return inputString;
				}
			}
			else if(Constants.variablesList.contains(inputString)) {
				Double exprValue = variablesSet.get(inputString);
				String val = "";
				if(exprValue != null){
					DecimalFormat df = new DecimalFormat("#.##");
					exprValue = Double.parseDouble(df.format(exprValue));
					if (( exprValue == Math.floor(exprValue)) && !Double.isInfinite(exprValue)) {
						val = "" + exprValue.intValue();
					} else {
						val = "" + exprValue;
					}
				}
				if(val != null && !"".equals(val)) {
					Toast.makeText(this, "The variable " + inputString + " is subtituted by " + val,Toast.LENGTH_SHORT).show();
					return val;
				}
				else {
					return inputString;
				}
			}
			else {
			}
		}
		catch (SyntaxException e) {
			Toast.makeText(this, "Constant value cannot be evaluated!",Toast.LENGTH_SHORT).show();
		}
		return inputString;
	} 

   /*Added by Anirudh Subramanian End*/
	
	private void handleBackspace() {
		if(mStrExpression.length() >= 2 && !bExpressionEvaluated) {
			mStrExpression = mStrExpression.substring(0, mStrExpression.length()-1);
			setWebViewText(mStrExpression);
			Log.d(Constants.TAG,"Setting webview to = " + mStrExpression + " bExpressionEvaluated = " + bExpressionEvaluated);
		}
		else {
			mStrExpression = "";
			mEvaluatedExpression = "";
	        setWebViewText(Constants.textExpression);
		}
	}
	
	private void clearCanvas() {
		setWebViewText(Constants.textExpression);
        	mStrExpression = "";
        	mEvaluatedExpression = "";
		mGestureOverlayView.cancelClearAnimation();
		mGestureOverlayView.clear(true);
	}
	
	private String doubleEscapeTeX(String s) {
		String t="";
		for (int i=0; i < s.length(); i++) {
			if (s.charAt(i) == '\'') t += '\\';
			if (s.charAt(i) != '\n') t += s.charAt(i);
			if (s.charAt(i) == '\\') t += "\\";
		}
		return t;
	}
	
	private void setWebViewText(String s) {
		if (s.matches("")) {
			mWebViewExpr.loadUrl("javascript:document.getElementById('math').innerHTML='';");
			mWebViewExpr.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
		}
		else {
			mWebViewExpr.loadUrl("javascript:document.getElementById('math').innerHTML='\\\\["
			           +doubleEscapeTeX(s)+"\\\\]';");
			mWebViewExpr.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(Constants.TAG, "onSaveInstanceState");
		outState.putString("savedExpression", mStrExpression);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedState) {		
		Log.i(Constants.TAG, "onRestoreInstanceState");
		mStrExpression = savedState.getString("savedExpression", "");
	}
}
