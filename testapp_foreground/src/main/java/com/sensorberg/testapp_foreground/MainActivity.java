package com.sensorberg.testapp_foreground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static String sentData;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_espresso);
		((TextView)findViewById(R.id.text)).setText("Main Activity");

		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, EspressoActivity0.class));
			}
		});

		findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {

				Data.List send = Data.getRandomDataList();
				sentData = send.toString();
				//App.ipcProcessHelper.dispatchEvent(new Command(true));
				App.ipcProcessHelper.dispatchEvent(send);
				startActivity(new Intent(MainActivity.this, EspressoActivity1.class));

			}
		});
	}

	@Override protected void onResume() {
		super.onResume();
		//App.ipcProcessHelper.dispatchEvent(new Command(false));
	}
}
