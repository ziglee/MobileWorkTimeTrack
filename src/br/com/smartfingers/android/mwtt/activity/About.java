package br.com.smartfingers.android.mwtt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import br.com.smartfingers.android.mwtt.R;

public class About extends Activity {

	private Intent emailIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("message/rfc822");
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"ziglee@gmail.com"});
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WorkTimeTrack");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

    	TextView text = (TextView) findViewById(R.id.sobre_text);
    	text.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
    	text.setLinksClickable(false);
    	text.setText(R.string.about_text);
    	text.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(Intent.createChooser(emailIntent, "Email"));
			}
		});
	}
}
