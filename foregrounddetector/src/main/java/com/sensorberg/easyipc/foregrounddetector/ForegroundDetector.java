package com.sensorberg.easyipc.foregrounddetector;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.sensorberg.easyipc.log.Log;

import java.util.HashSet;
import java.util.Set;

public final class ForegroundDetector {

	private static final long TIMEOUT_MS = 700L;

	private final Set<Listener> listeners;
	private final Handler handler = new Handler(Looper.getMainLooper());

	private int startedCount = 0;
	private boolean isForeground = false;

	public static ForegroundDetector install(Application application) {
		ForegroundDetector b = new ForegroundDetector();
		application.registerActivityLifecycleCallbacks(b.callbacks);
		return b;
	}

	private ForegroundDetector() {
		listeners = new HashSet<>();
	}

	/**
	 * @param listener
	 * @return true if app is in foreground, false if background
	 */
	public boolean addListener(@NonNull Listener listener) {
		listeners.add(listener);
		return isForeground;
	}

	public void removeListener(@NonNull Listener listener) {
		listeners.remove(listener);
	}

	public boolean isForeground() {
		return isForeground;
	}

	private void dispatchApplicationInForeground() {
		if (isForeground) {
			return;
		}
		isForeground = true;
		Log.i("onApplicationInForeground");
		for (Listener l : listeners) {
			l.onApplicationInForeground();
		}
	}

	private void dispatchApplicationInBackground() {
		if (!isForeground) {
			return;
		}
		Log.i("onApplicationInBackground");
		isForeground = false;
		for (Listener l : listeners) {
			l.onApplicationInBackground();
		}
	}

	private final Runnable dispatchBackground = new Runnable() {
		@Override
		public void run() {
			dispatchApplicationInBackground();
		}
	};

	private final Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {

		@Override public void onActivityStarted(Activity activity) {
			handler.removeCallbacksAndMessages(null);
			if (startedCount == 0) {
				dispatchApplicationInForeground();
			}
			startedCount++;
			Log.d("onActivityStarted. %s", activity.getClass().getSimpleName());
		}

		@Override public void onActivityStopped(Activity activity) {
			handler.removeCallbacksAndMessages(null);
			startedCount--;
			if (startedCount == 0) {
				handler.postDelayed(dispatchBackground, TIMEOUT_MS);
			}
		}

		//region not used
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) { /* */ }

		@Override public void onActivityResumed(Activity activity) { /* */ }

		@Override public void onActivityPaused(Activity activity) { /* */ }

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) { /* */ }

		@Override public void onActivityDestroyed(Activity activity) { /* */ }
		//endregion
	};

	public interface Listener {
		void onApplicationInForeground();

		void onApplicationInBackground();
	}

}
