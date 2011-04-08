package br.com.smartfingers.android.mwtt;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {
	
	public static PackageInfo getPackageInfo(Context context)
			throws NameNotFoundException {
		PackageManager manager = context.getPackageManager();
		return manager.getPackageInfo(context.getPackageName(), 0);
	}
}
