package com.sensorberg.testapp_foreground;

import android.os.Handler;

import com.sensorberg.easyipc.IpcListener;
import com.sensorberg.easyipc.IpcService;

/**
 * The service simply pings-back all the data received.
 * Espresso will do all the checks on the activity.
 */
public class EspressoService extends IpcService {

	private Handler handler = new Handler();
	private boolean stream = false;

	@Override public void onCreate() {
		super.onCreate();
		addListener(Command.class, onCommand);
		addListener(Data.class, onData);
		addListener(Data.List.class, onDataList);
	}

	private final IpcListener<Data> onData = new IpcListener<Data>() {
		@Override public void onEvent(Data event) {
			dispatchEvent(event);
		}
	};
	private final IpcListener<Command> onCommand = new IpcListener<Command>() {
		@Override public void onEvent(Command event) {
			stream = event.stream;
			streamData.run();
		}
	};
	private final IpcListener<Data.List> onDataList = new IpcListener<Data.List>() {
		@Override public void onEvent(Data.List event) {
			for (Data d : event) dispatchEvent(d);
		}
	};

	private final Runnable streamData = new Runnable() {
		@Override public void run() {
			if (!stream) {
				handler.removeCallbacks(this);
				return;
			}

			dispatchEvent(Data.getRandomData());

			handler.postDelayed(this, 1333);
		}
	};

}
