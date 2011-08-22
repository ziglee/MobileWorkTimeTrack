package br.com.smartfingers.android.mwtt.activity;

import br.com.smartfingers.android.mwtt.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WhatsNew extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView text = (TextView) findViewById(R.id.sobre_text);
		text.setText(R.string.whats_new_text);
	}
}
