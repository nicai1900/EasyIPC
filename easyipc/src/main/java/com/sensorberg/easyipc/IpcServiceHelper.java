package com.sensorberg.easyipc;

import android.os.IBinder;
import android.os.RemoteException;

import com.sensorberg.easyipc.log.Log;

import java.util.List;

public class IpcServiceHelper extends IpcBase {

	private IReceiver outgoing;
	private boolean isBound = false;

	public IpcServiceHelper(int maxQueueSize) {
		super(maxQueueSize);
	}

	public IpcServiceHelper() {
		super();
	}

	@Override public boolean isAlive() {
		return isBound;
	}

	public IBinder getBinder() {
		isBound = true;
		return incoming;
	}

	public void setBound(boolean isBound) {
		this.isBound = isBound;
		if (isBound) {
			dequeueIfNeeded();
		}
	}

	@Override void sendTypesToOtherProcess(List<String> types) throws RemoteException {
		IReceiver r = outgoing;
		if (isBound && r != null) {
			r.setTypes(types);
		}
	}

	@Override boolean sendEventToOtherProcess(ParcelableEvent event) throws RemoteException {
		IReceiver r = outgoing;
		if (isBound && r != null) {
			r.onEvent(event);
			return true;
		} else {
			return false;
		}
	}

	private final ISender.Stub incoming = new ISender.Stub() {
		@Override public void setReceiver(IReceiver receiver) throws RemoteException {
			IpcServiceHelper.this.outgoing = receiver;
			updateTypes();
			dequeueIfNeeded();
			Log.d("IpcServiceHelper.setReceiver %s", receiver);
		}

		@Override public void setTypes(List<String> types) throws RemoteException {
			IpcServiceHelper.this.setTypesThatTheOtherProcessIsListeningTo(types);
			dequeueIfNeeded();
		}

		@Override public void onEvent(ParcelableEvent event) throws RemoteException {
			sendToListeners(event);
		}
	};
}
