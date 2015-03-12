package org.bottiger.podcast;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Window;
import android.view.WindowManager;

public class TopActivity extends ActionBarActivity {
	
	private static SharedPreferences prefs;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Slide();
            getWindow().setEnterTransition(transition);
            getWindow().setExitTransition(transition);
        }

        if (ApplicationConfiguration.DEBUGGING)
            ViewServer.get(this).addWindow(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

    @Override
    protected void onPause() {
        super.onPause();
        SoundWaves.sAnalytics.activityPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundWaves.sAnalytics.activityResume();
    }

    public static SharedPreferences getPreferences() {
		return prefs;
	}

}
