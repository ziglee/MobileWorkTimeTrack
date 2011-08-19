package br.com.smartfingers.android.mwtt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import br.com.smartfingers.android.mwtt.R;

public class Help extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		WebView web = (WebView) findViewById(R.id.help_webview);
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("http://smartfingersinc.wordpress.com/2011/04/07/our-first-application/");
	}
}
