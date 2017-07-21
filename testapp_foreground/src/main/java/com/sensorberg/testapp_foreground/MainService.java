package com.sensorberg.testapp_foreground;

import android.graphics.Rect;

import com.sensorberg.easyipc.IpcListener;
import com.sensorberg.easyipc.IpcService;
import com.sensorberg.easyipc.log.Log;

public class MainService extends IpcService {

	@Override public void onCreate() {
		super.onCreate();
		addListener(Rect.class, onRect);
	}

	@Override public void onDestroy() {
		super.onDestroy();
	}

	private final IpcListener<Rect> onRect = new IpcListener<Rect>() {
		@Override public void onEvent(Rect event) {
			Log.d("onRect %s", event);
		}
	};
}
