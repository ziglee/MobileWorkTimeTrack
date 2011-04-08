package br.com.smartfingers.android.mwtt.activity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TabHost;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.Utils;

public class AboutTabHost extends TabActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("WorkTimeTrack ("+getAppVersion(this)+")");
		
		addTabAbout();
		addTabWhatsNew();
		addTabCredits();
	}

    private void addTabAbout() {
        Intent intent = new Intent(this, About.class);
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("about")
                .setIndicator(getString(R.string.about_menu))
                .setContent(intent));
    }

    private void addTabWhatsNew() {
        Intent intent = new Intent(this, WhatsNew.class);
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("whatsnew")
                .setIndicator(getString(R.string.whats_new_tab_title))
                .setContent(intent));
    }

    private void addTabCredits() {
        Intent intent = new Intent(this, Credits.class);
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("credits")
                .setIndicator(getString(R.string.credits_tab_title))
                .setContent(intent));
    }

	
	public static String getAppVersion(Context context) {
        try {
            PackageInfo info = Utils.getPackageInfo(context);
            return "v. "+info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
