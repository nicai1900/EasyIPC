package com.sensorberg.testapp_foreground;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.sensorberg.easyipc.IpcActivityHelper;
import com.sensorberg.easyipc.IpcConnection;
import com.sensorberg.easyipc.foreground.IpcProcessHelper;
import com.sensorberg.easyipc.foregrounddetector.ForegroundDetector;
import com.sensorberg.easyipc.log.Log;

public class App extends Application implements IpcActivityHelper.ConnectionListener {

	static {
		com.sensorberg.easyipc.log.Log.ENABLED = BuildConfig.DEBUG;
	}

	public static IpcProcessHelper ipcProcessHelper;

	@Override public void onCreate() {
		super.onCreate();

		if (getProcessName(this).endsWith("other")) {
			return;
		}

		ipcProcessHelper = IpcProcessHelper.install(
				ForegroundDetector.install(this),
				this,
				EspressoService.class,
				BIND_AUTO_CREATE);
		ipcProcessHelper.addConnectionListener(this);
	}

	@Override public void onServiceConnected(IpcConnection ipcConnection) {
		Log.d("onServiceConnected");
	}

	@Override public void onServiceDisconnected() {
		Log.d("onServiceDisconnected");
	}

	public static String getProcessName(Context context) {
		String processName = "";
		int pID = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
			if (processInfo.pid == pID) {
				processName = processInfo.processName;
				break;
			}
		}
		return processName;
	}
}
