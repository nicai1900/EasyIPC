package com.sensorberg.testapp_foreground;

import android.app.Application;
import android.graphics.Rect;

import com.sensorberg.easyipc.IpcActivityHelper;
import com.sensorberg.easyipc.IpcConnection;
import com.sensorberg.easyipc.foreground.IpcProcessHelper;
import com.sensorberg.easyipc.foregrounddetector.ForegroundDetector;
import com.sensorberg.easyipc.log.Log;

public class App extends Application implements IpcActivityHelper.ConnectionListener {

	static {
		com.sensorberg.easyipc.log.Log.ENABLED = BuildConfig.DEBUG;
	}

	@Override public void onCreate() {
		super.onCreate();
		IpcProcessHelper ipcProcessHelper = IpcProcessHelper.install(
				ForegroundDetector.install(this),
				this,
				MainService.class,
				BIND_AUTO_CREATE);
		ipcProcessHelper.addConnectionListener(this);
	}

	@Override public void onServiceConnected(IpcConnection ipcConnection) {
		Log.d("onServiceConnected");
		ipcConnection.dispatchEvent(new Rect());
	}

	@Override public void onServiceDisconnected() {
		Log.d("onServiceDisconnected");
	}
}
