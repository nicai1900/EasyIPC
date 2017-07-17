package com.sensorberg.easyipc.testapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.easy_ipc).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {

				startActivity(new Intent(MainActivity.this, EasyIpcActivity.class));

			}
		});

		findViewById(R.id.easy_ipc_arch).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				startActivity(new Intent(v.getContext(), EasyIpcArchActivity.class));
			}
		});

	}
}
