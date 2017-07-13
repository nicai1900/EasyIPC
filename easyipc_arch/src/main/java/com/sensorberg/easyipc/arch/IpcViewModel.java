package com.sensorberg.easyipc.arch;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.os.Parcelable;

import com.sensorberg.easyipc.IpcActivityHelper;

import java.util.HashMap;
import java.util.Map;

public class IpcViewModel extends AndroidViewModel {

	private IpcActivityHelper ipcConnection = null;
	private Map<Class<? extends Parcelable>, LiveData<? extends Parcelable>> map = new HashMap<>();

	public IpcViewModel(Application application) {
		super(application);
	}

	public IpcViewModel init(Class serviceClass, int serviceFlags) {
		if (ipcConnection != null) {
			return this;
		}
		ipcConnection = new IpcActivityHelper(
				getApplication(),
				new Intent(getApplication(), serviceClass),
				serviceFlags);
		ipcConnection.start();
		return this;
	}

	@Override protected void onCleared() {
		ipcConnection.stop();
		ipcConnection = null;
	}

	public void dispatchEvent(Parcelable parcelable) {
		ipcConnection.dispatchEvent(parcelable);
	}

	public <T extends Parcelable> LiveData<T> getLiveData(Class<T> klass) {
		LiveData<T> liveData = (LiveData<T>) map.get(klass);
		if (liveData == null) {
			liveData = new IpcLiveData<>(ipcConnection, klass);
			map.put(klass, liveData);
		}
		return liveData;
	}
}
