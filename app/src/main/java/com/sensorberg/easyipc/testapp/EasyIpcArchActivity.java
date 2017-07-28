package com.sensorberg.easyipc.testapp;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.sensorberg.easyipc.arch.IpcViewModel;
import com.sensorberg.easyipc.log.Log;

public class EasyIpcArchActivity extends LifecycleActivity {

	private IpcViewModel viewModel;
	private Adapter adapter;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
		recyclerView.setAdapter(adapter = new Adapter());

		viewModel = ViewModelProviders
				.of(this)
				.get(IpcViewModel.class)
				.init(MainService.class, BIND_AUTO_CREATE);

		viewModel.dispatchEvent(new EarlyData("sent from the activity")); // get's queued

		viewModel
				.getLiveData(Data.class)
				.observe(ProcessLifecycleOwner.get(), onData);

		viewModel
				.getLiveData(Data.List.class)
				.observe(this, onDataList);

		viewModel
				.getLiveData(EarlyData.class)
				.observe(this, onEarlyData);

	}

	private final Observer<Data> onData = new Observer<Data>() {
		@Override public void onChanged(@Nullable Data event) {
			Log.d("EasyIpcArchActivity received %s", event);
			if (event != null) {
				adapter.add(event.toString());
			}
			viewModel.dispatchEvent(event);
		}
	};

	private final Observer<Data.List> onDataList = new Observer<Data.List>() {
		@Override public void onChanged(@Nullable Data.List event) {
			Log.d("EasyIpcArchActivity received list %s", event);
			if (event != null) {
				adapter.add(event.toString());
			}
		}
	};

	private final Observer<EarlyData> onEarlyData = new Observer<EarlyData>() {
		@Override public void onChanged(@Nullable EarlyData earlyData) {
			Log.d("EasyIpcActivity received %s", earlyData);
			if (earlyData != null) {
				adapter.add(earlyData.toString());
			}
		}
	};
}
