package com.sensorberg.easyipc.foreground;

import android.content.Context;
import android.content.Intent;

import com.sensorberg.easyipc.IpcActivityHelper;
import com.sensorberg.easyipc.foregrounddetector.ForegroundDetector;

public final class IpcProcessHelper extends IpcActivityHelper {

	public static IpcProcessHelper install(
			ForegroundDetector detector,
			Context context,
			Class serviceClass,
			int serviceFlags) {

		IpcProcessHelper ipc = new IpcProcessHelper(
				context,
				new Intent(context, serviceClass),
				serviceFlags,
				true);

		detector.addListener(ipc.listener);
		return ipc;
	}

	private IpcProcessHelper(Context context, Intent intent, int flags, boolean autoReconnect) {
		super(context, intent, flags, autoReconnect);
	}

	private final ForegroundDetector.Listener listener = new ForegroundDetector.Listener() {
		@Override public void onApplicationInForeground() {
			start();
		}

		@Override public void onApplicationInBackground() {
			stop();
		}
	};
}
