package com.sensorberg.easyipc.testapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;

import com.sensorberg.easyipc.IpcActivityHelper;
import com.sensorberg.easyipc.IpcListener;
import com.sensorberg.easyipc.log.Log;

public class EasyIpcActivity extends Activity {

	private IpcActivityHelper ipcConnection;
	private Handler uiHandler = new Handler(Looper.getMainLooper());
	private Adapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
		recyclerView.setAdapter(adapter = new Adapter());

		ipcConnection = new IpcActivityHelper(this,
											  new Intent(this, MainService.class),
											  BIND_AUTO_CREATE);

		ipcConnection.dispatchEvent(new EarlyData("sent from the activity")); // get's queued
	}

	@Override protected void onStart() {
		super.onStart();
		ipcConnection
				.addListener(Data.class, onData, uiHandler)
				.addListener(Data.List.class, onDataList)
				.addListener(EarlyData.class, onEarlyData);
		ipcConnection.start();
	}

	@Override protected void onStop() {
		ipcConnection
				.removeListener(Data.class, onData)
				.removeListener(Data.List.class, onDataList);
		ipcConnection.stop();
		super.onStop();
	}

	private final IpcListener<Data> onData = new IpcListener<Data>() {
		@Override public void onEvent(Data event) {
			Log.d("EasyIpcActivity received %s", event);
			adapter.add(event.toString());
			ipcConnection.dispatchEvent(event);

		}
	};

	private final IpcListener<Data.List> onDataList = new IpcListener<Data.List>() {
		@Override public void onEvent(Data.List list) {
			Log.d("EasyIpcActivity received list %s", list);
			adapter.add(list.toString());
		}
	};

	private final IpcListener<EarlyData> onEarlyData = new IpcListener<EarlyData>() {
		@Override public void onEvent(EarlyData earlyData) {
			Log.d("EasyIpcActivity received %s", earlyData);
			adapter.add(earlyData.toString());
		}
	};

}
