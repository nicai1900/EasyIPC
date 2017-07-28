package com.sensorberg.easyipc.testapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sensorberg.easyipc.IpcActivityHelper;
import com.sensorberg.easyipc.IpcListener;

/**
 * This checks for the activity registering/un-registering during onResume/onPause while the
 * IpcActivityHelper is connecting to the Service during start/stop
 * Also it sends a List with loads of data while the service will send one-by-one.
 */
public class EspressoActivity1 extends Activity implements IpcListener<Data>, View.OnClickListener {

	public static String sentData = "";
	private Data.List received = new Data.List();
	private IpcActivityHelper ipcHelper;
	private TextView textView;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_espresso);
		findViewById(R.id.button).setOnClickListener(this);
		textView = (TextView) findViewById(R.id.text);
		ipcHelper = new IpcActivityHelper(this, new Intent(this, EspressoService.class), BIND_AUTO_CREATE);
		Data.List sent = Data.getRandomDataList();
		sentData = sent.toString();
		ipcHelper.dispatchEvent(sent);
	}

	@Override protected void onStart() {
		super.onStart();
		ipcHelper.start();
	}

	@Override protected void onResume() {
		super.onResume();
		ipcHelper.addListener(Data.class, this);
	}

	@Override protected void onPause() {
		ipcHelper.removeListener(Data.class, this);
		super.onPause();
	}

	@Override protected void onStop() {
		ipcHelper.stop();
		super.onStop();
	}

	@Override public void onEvent(Data event) {
		received.add(event);
		textView.setText(received.toString());
	}

	@Override public void onClick(View v) {

	}
}
