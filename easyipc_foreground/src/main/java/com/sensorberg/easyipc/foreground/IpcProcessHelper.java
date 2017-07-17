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
				serviceFlags);

		detector.addListener(ipc.listener);
		return ipc;
	}

	public static IpcProcessHelper install(
			ForegroundDetector detector,
			Context context,
			Class serviceClass,
			int serviceFlags,
			int maxQueueSize) {

		IpcProcessHelper ipc = new IpcProcessHelper(
				context,
				new Intent(context, serviceClass),
				serviceFlags,
				maxQueueSize);

		detector.addListener(ipc.listener);
		return ipc;
	}

	private IpcProcessHelper(Context context, Intent intent, int flags) {
		super(context, intent, flags);
	}

	private IpcProcessHelper(Context context, Intent intent, int flags, int maxQueueSize) {
		super(context, intent, flags, maxQueueSize);
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
