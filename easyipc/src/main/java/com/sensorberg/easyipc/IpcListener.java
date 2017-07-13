package com.sensorberg.easyipc;

import android.os.Parcelable;

public interface IpcListener<T extends Parcelable> {
	void onEvent(T event);
}
