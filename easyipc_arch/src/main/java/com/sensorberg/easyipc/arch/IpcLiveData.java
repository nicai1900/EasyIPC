package com.sensorberg.easyipc.arch;

import android.arch.lifecycle.MutableLiveData;
import android.os.Looper;
import android.os.Parcelable;

import com.sensorberg.easyipc.IpcBase;
import com.sensorberg.easyipc.IpcListener;

public class IpcLiveData<T extends Parcelable> extends MutableLiveData<T> {

	private final IpcBase base;
	private final Class<T> klass;

	public IpcLiveData(IpcBase base, Class<T> klass) {
		this.base = base;
		this.klass = klass;
	}

	@Override protected void onActive() {
		base.addListener(klass, listener);
	}

	@Override protected void onInactive() {
		base.removeListener(klass, listener);
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
