package com.sensorberg.easyipc.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Handler;

import com.sensorberg.easyipc.IpcListener;
import com.sensorberg.easyipc.IpcService;
import com.sensorberg.easyipc.log.Log;

public class MainService extends IpcService implements IpcListener<Data> {

	private Handler handler;

	@Override public void onCreate() {
		super.onCreate();

		dispatchEvent(new EarlyData("sent from the service")); // get's queued

		handler = new Handler();
		handler.postDelayed(dispatchSingleData, 1333);
		handler.postDelayed(dispatchListData, 2500);

		addListener(Data.class, this);
		addListener(EarlyData.class, onEarlyData);
	}

	@Override public void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		handler = null;
		super.onDestroy();
	}

	private Runnable dispatchSingleData = new Runnable() {
		@Override public void run() {
			if (handler == null) {
				return;
			}
			handler.postDelayed(this, 1333);
			Data d = Data.getRandomData();
			Log.d("MainService sent", d.toString());
			dispatchEvent(d);
		}
	};

	private Runnable dispatchListData = new Runnable() {
		@Override public void run() {
			if (handler == null) {
				return;
			}
			handler.postDelayed(this, 2500);
			Data.List list = Data.getRandomDataList();
			Log.d("MainService sent", list.toString());
			dispatchEvent(list);
		}
	};

	@Override public void onEvent(Data event) {
		Log.d("MainService received %s", event);
		notify(event.otherVal, event.val, 2);
	}

	private final IpcListener<EarlyData> onEarlyData = new IpcListener<EarlyData>() {
		@Override public void onEvent(EarlyData earlyData) {
			Log.d("MainService received %s", earlyData);
			MainService.this.notify(earlyData.value, 0, 1);
		}
	};

	private void notify(String s, int val, int id) {
		Notification.Builder b = new Notification.Builder(this);
		b.setSmallIcon(R.drawable.ic_notification);
		b.setContentTitle(s);
		b.setTicker(s);
		b.setContentText(val + " - " + s);
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(id, b.build());
	}
}
