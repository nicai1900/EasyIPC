package com.sensorberg.easyipc.testapp;

import com.sensorberg.easyipc.IpcListener;
import com.sensorberg.easyipc.IpcService;

/**
 * The service simply pings-back all the data received.
 * Espresso will do all the checks on the activity.
 */
public class EspressoService extends IpcService {

	@Override public void onCreate() {
		super.onCreate();
		addListener(Data.class, onData);
		addListener(Data.List.class, onDataList);
	}

	private final IpcListener<Data> onData = new IpcListener<Data>() {
		@Override public void onEvent(Data event) {
			dispatchEvent(event);
		}
	};
	private final IpcListener<Data.List> onDataList = new IpcListener<Data.List>() {
		@Override public void onEvent(Data.List event) {
			for (Data d : event) dispatchEvent(d);
		}
	};
}
