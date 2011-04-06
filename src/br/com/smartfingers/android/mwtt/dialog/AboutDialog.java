package br.com.smartfingers.android.mwtt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import br.com.smartfingers.android.mwtt.R;

public class AboutDialog extends Dialog {

	private final Intent emailIntent;
	
	public AboutDialog(final Context context) {
		super(context);
		
		emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("message/rfc822");
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"ziglee@gmail.com"});
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WorkTimeTrack");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
    	
		setContentView(R.layout.sobre_dialog);
    	setTitle(R.string.about_menu);

    	TextView text = (TextView) findViewById(R.id.sobre_text);
    	text.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
    	text.setLinksClickable(false);
    	text.setText(R.string.about_text);
    	text.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				context.startActivity(Intent.createChooser(emailIntent, "Email"));
			}
		});
	}

}
