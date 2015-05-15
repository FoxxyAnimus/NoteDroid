package com.notedroid.fuck;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class AboutActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Window w = getWindow();
		w.requestFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.activity_about);
	}
	
	public void closeAboutDialog(View view) {
		finish();
	}
}