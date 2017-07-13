package com.sensorberg.easyipc.testapp;

import android.app.Application;

import com.sensorberg.easyipc.foregrounddetector.ForegroundDetector;
import com.sensorberg.easyipc.log.Log;

public class App extends Application implements ForegroundDetector.Listener {

	@Override public void onCreate() {
		super.onCreate();
		ForegroundDetector detector = ForegroundDetector.install(this);
		detector.addListener(this);
	}

	@Override public void onApplicationInForeground() {
		Log.i("onApplicationInForeground");
	}

	@Override public void onApplicationInBackground() {
		Log.i("onApplicationInBackground");
	}
}
