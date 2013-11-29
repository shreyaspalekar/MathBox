package edu.ufl.cise.mathbox;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

/* Sagar Parmar - Added help activity */

/**
 * A new activity for showing help screen on performing "?" gesture  
 */
public class HelpActivity extends Activity {

	private WebView mWebViewHelp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		mWebViewHelp = (WebView) findViewById(R.id.helpWebView);
        mWebViewHelp.getSettings().setBuiltInZoomControls(false);
        mWebViewHelp.setVerticalScrollBarEnabled(true);
        mWebViewHelp.setHorizontalScrollBarEnabled(true);
        mWebViewHelp.loadUrl("file:///android_asset/help.html");
	}
}
