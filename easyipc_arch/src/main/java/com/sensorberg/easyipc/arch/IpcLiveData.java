package com.sensorberg.easyipc.arch;

import android.arch.lifecycle.MutableLiveData;
import android.os.Looper;
import android.os.Parcelable;

import com.sensorberg.easyipc.IpcConnection;
import com.sensorberg.easyipc.IpcListener;

public class IpcLiveData<T extends Parcelable> extends MutableLiveData<T> {

	private final IpcConnection ipcConnection;
	private final Class<T> klass;

	public IpcLiveData(IpcConnection ipcConnection, Class<T> klass) {
		this.ipcConnection = ipcConnection;
		this.klass = klass;
	}

	@Override protected void onActive() {
		ipcConnection.addListener(klass, listener);
	}

	@Override protected void onInactive() {
		ipcConnection.removeListener(klass, listener);
	}

	private final IpcListener<T> listener = new IpcListener<T>() {
		@Override public void onEvent(T event) {
			if (Looper.getMainLooper() == Looper.myLooper()) {
				setValue(event);
			} else {
				postValue(event);
			}
		}
	};
}
