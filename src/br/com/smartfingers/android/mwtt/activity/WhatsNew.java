package br.com.smartfingers.android.mwtt.activity;

import java.util.Locale;

import br.com.smartfingers.android.mwtt.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WhatsNew extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView text = (TextView) findViewById(R.id.sobre_text);
		text.setText(R.string.whats_new_text);
    	
    	LinearLayout sabedoriaLayout = (LinearLayout) findViewById(R.id.sabedoria_layout);
    	if (Locale.getDefault().equals(new Locale("pt", "BR"))) {
    		sabedoriaLayout.setVisibility(View.VISIBLE);
    		TextView sabedoriaText = (TextView) findViewById(R.id.sabedoria_text);
    		sabedoriaText.setAutoLinkMask(Linkify.ALL);
    		sabedoriaText.setLinksClickable(true);
    		sabedoriaText.setText("Conheça nossa mais novo aplicativo 'Sabedoria'." +
    				"\nAcesse o Android Market https://market.android.com/details?id=net.cassiolandim.sabedoria");
    	} else {
    		sabedoriaLayout.setVisibility(View.GONE);
    	}
	}
}
