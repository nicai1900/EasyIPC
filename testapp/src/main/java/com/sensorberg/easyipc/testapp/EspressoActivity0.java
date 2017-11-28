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
 * This checks for activity registering during onCreate and never un-registering.
 * On every click a new value is generated, sent and (hopefully) received back
 */
public class EspressoActivity0 extends Activity implements IpcListener<Data>, View.OnClickListener {

	public static String lastSent = "";
	private IpcActivityHelper ipcHelper;
	private TextView textView;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_espresso);
		findViewById(R.id.button).setOnClickListener(this);
		textView = (TextView) findViewById(R.id.text);
		ipcHelper = new IpcActivityHelper(this, new Intent(this, EspressoService.class), BIND_AUTO_CREATE);
		ipcHelper.addListener(Data.class, this);
		send(Data.getRandomData());
	}

	@Override protected void onStart() {
		super.onStart();
		ipcHelper.start();
	}

	@Override protected void onStop() {
		ipcHelper.stop();
		super.onStop();
	}

	@Override public void onEvent(Data event) {
		textView.setText(event.toString());
	}

	public void send(Data data) {
		lastSent = data.toString();
		ipcHelper.dispatchEvent(data);
	}

	@Override public void onClick(View v) {

		send(Data.getRandomData());
	}
}
