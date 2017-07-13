package com.sensorberg.easyipc.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;

import com.sensorberg.easyipc.IpcListener;
import com.sensorberg.easyipc.IpcService;

import java.util.Random;

public class MainService extends IpcService implements IpcListener<Data> {

	private static final Random RANDOM = new Random();
	private static final String CHARS = "1234567890qwertyuiopasdfghjklzxcvbnm ";

	private Handler handler;
	private StringBuilder sb;

	@Override public void onCreate() {
		super.onCreate();
		sb = new StringBuilder();
		handler = new Handler();
		handler.postDelayed(dispatchSingleData, 1333);
		handler.postDelayed(dispatchListData, 2500);

		addListener(Data.class, this);
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
			Data d = getRandom();
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
			ParcelableRectList list = new ParcelableRectList();
			for (int i = 0; i < 6; i++) {
				list.add(new Rect(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
			}
			Log.d("MainService sent", list.toString());
			dispatchEvent(list);
		}
	};

	@Override public void onEvent(Data event) {
		Notification.Builder b = new Notification.Builder(this);
		b.setSmallIcon(R.drawable.ic_notification);
		b.setContentTitle(event.otherVal);
		b.setTicker(event.otherVal);
		b.setContentText(event.val + " - " + event.otherVal);
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(32, b.build());
	}

	private Data getRandom() {
		sb.setLength(0);
		for (int i = 0; i < 16; i++) {
			sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
		}
		return new Data(RANDOM.nextInt(), sb.toString());
	}
}
