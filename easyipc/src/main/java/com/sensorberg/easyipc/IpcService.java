package com.sensorberg.easyipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;

public class IpcService extends Service {

	private final IpcServiceHelper ipcConnection = new IpcServiceHelper();

	protected <T extends Parcelable> IpcService addListener(Class<T> klazz, IpcListener<T> listener) {
		ipcConnection.addListener(klazz, listener);
		return this;
	}

	protected <T extends Parcelable> IpcService removeListener(Class<T> klazz, IpcListener<T> listener) {
		ipcConnection.removeListener(klazz, listener);
		return this;
	}

	protected IpcConnection getIpcConnection() {
		return ipcConnection;
	}

	@Override public void onDestroy() {
		ipcConnection.clearAll();
		super.onDestroy();
	}

	protected final void dispatchEvent(Parcelable event) {
		ipcConnection.dispatchEvent(event);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return ipcConnection.getBinder();
	}

	@Override public void onRebind(Intent intent) {
		ipcConnection.setBound(true);
	}

	@Override public boolean onUnbind(Intent intent) {
		ipcConnection.setBound(false);
		return true;
	}
}
