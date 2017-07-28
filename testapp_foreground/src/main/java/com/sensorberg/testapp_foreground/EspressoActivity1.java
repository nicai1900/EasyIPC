package com.sensorberg.testapp_foreground;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sensorberg.easyipc.IpcListener;

/**
 * This checks for the activity registering/un-registering during onResume/onPause while
 * the data list with loads of data was sent from the previous activity.
 */
public class EspressoActivity1 extends Activity implements IpcListener<Data>, View.OnClickListener {

	public Data.List received = new Data.List();
	private TextView textView;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_espresso);
		findViewById(R.id.button).setOnClickListener(this);
		textView = (TextView) findViewById(R.id.text);
	}

	@Override protected void onResume() {
		super.onResume();
		App.ipcProcessHelper.addListener(Data.class, this);
	}

	@Override protected void onPause() {
		App.ipcProcessHelper.removeListener(Data.class, this);
		super.onPause();
	}

	@Override public void onEvent(Data event) {
		received.add(event);
		textView.setText(received.toString());
	}

	@Override public void onClick(View v) {
		App.ipcProcessHelper.dispatchEvent(Data.getRandomData());
	}
}
