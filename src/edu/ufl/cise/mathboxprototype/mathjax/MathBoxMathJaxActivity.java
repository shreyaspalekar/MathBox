package edu.ufl.cise.mathboxprototype.mathjax;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStore;
import android.gesture.Prediction;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import expr.Expr;
import expr.Parser;
import expr.SyntaxException;

public class MathBoxMathJaxActivity extends Activity implements OnGesturePerformedListener,OnTouchListener {

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
    private ArrayList<String> mArrayListHistory = null;
    private ListView mDrawerList;
	private String[] mListItemTitles;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_box);
        
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
        
        mGestureLibrary = GestureLibraries.fromRawResource(this,R.raw.gestures_10_10_21_29);
        mGestureLibrary.setOrientationStyle(8);
        mGestureLibrary.setSequenceType(GestureStore.SEQUENCE_SENSITIVE);
        if (!mGestureLibrary.load()) 
            finish();
        
        
        mClearButton = (ImageButton) findViewById(R.id.deleteButton1);
        mClearButton.setOnTouchListener(this);

        mEvaluateButton = (ImageButton) findViewById(R.id.checkMarkButton1);
        mEvaluateButton.setOnTouchListener(this);
        
        mBackspaceButton = (ImageButton) findViewById(R.id.backspaceButton1);
        mBackspaceButton.setOnTouchListener(this);
        
        mWebViewExpr = (WebView) findViewById(R.id.exprTextView1);
        mWebViewExpr.getSettings().setJavaScriptEnabled(true);
        mWebViewExpr.getSettings().setBuiltInZoomControls(false);
        mWebViewExpr.loadDataWithBaseURL("http://bar", "<body bgcolor=\"#ffffff\"><script type='text/x-mathjax-config'>"
		                      +"MathJax.Hub.Config({ " 
							  	+"showMathMenu: false, "
							  	+"jax: ['input/TeX','output/HTML-CSS'], "
							  	+"extensions: ['tex2jax.js'], " 
							  	+"TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
							  	  +"'noErrors.js','noUndefined.js'] }, "
							  	+"showProcessingMessages: false, "
							  	+ "displayAlign: \"left\", "
							  	+ "\"HTML-CSS\": { scale: 130}"
							  +"});</script>"
		                      +"<script type='text/javascript' "
							  +"src='file:///android_asset/MathJax/MathJax.js'"
							  +"></script><span id='math'>"+ Constants.textExpression +"</span></body>","text/html","utf-8","");
        mWebViewExpr.reload();
        mArrayListHistory = new ArrayList<String>();
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
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.action_share:
	        	Log.d(Constants.appName,"Share");
	            return true;
	        case R.id.action_settings:
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
        Log.d(Constants.appName,"Item at pos " + position + " clicked");
        switch(position) {
        	case 0:
        		for(String historyItem:mArrayListHistory)
        			Log.d(Constants.appName,historyItem);
        	default:
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        mDrawerList.setItemChecked(position, false);
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
    	
    	//Filter based on numOfStrokes
    	int numOfStroke = gesture.getStrokesCount();
    	Log.d(Constants.appName, "Recognized = " + strCurrentPrediction + " Score = " + predictionScore);
    	
    	//Handling for plus and *
    	if(strCurrentPrediction.equals(Constants.plus) || strCurrentPrediction.equals(Constants.asterik)) {
    		Log.d(Constants.appName, "inside plus or star numstroke=" + numOfStroke);
    		if (numOfStroke == 2)
    			strCurrentPrediction = Constants.plus;
    		else if(numOfStroke == 4)
    			strCurrentPrediction = Constants.asterik;
    	}
    	
    	if(bExpressionEvaluated) {
    		bExpressionEvaluated = false;
    		mStrExpression = "";
    		setWebViewText(mStrExpression);
    	}
    		
    	if(predictionScore > 2.00) {
    		if(strCurrentPrediction.matches(Constants.equalTo)) {
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
        		mStrExpression += "\\sum\\limits_{x=1}^n";
        	}
        	else {
        		mStrExpression += strCurrentPrediction;
        	}
    		setWebViewText(mStrExpression);
    	}
    	else {
    		if(mStrExpression.length() == 0) {
    			setWebViewText(Constants.textExpression);
    		}
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mWebViewExpr.startAnimation(shake);
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
			mStrExpression = mStrExpression + "=" +  expr.value();
			setWebViewText(mStrExpression);
			bExpressionEvaluated =  true;
			mArrayListHistory.add(mStrExpression);
		}
		catch (SyntaxException e) {
			Log.d(Constants.appName,e.explain());
			Toast.makeText(this, "Please check the expression!", Toast.LENGTH_SHORT).show();
	        setWebViewText(Constants.textExpression);
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
		if(mStrExpression.length() >= 2 && !bExpressionEvaluated) {
			mStrExpression = mStrExpression.substring(0, mStrExpression.length()-1);
			setWebViewText(mStrExpression);
		}
		else {
			mStrExpression = "";
	        setWebViewText(Constants.textExpression);
		}
	}
	
	private void clearCanvas() {
		setWebViewText(Constants.textExpression);
        mStrExpression = "";
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
}