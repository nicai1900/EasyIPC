package com.sensorberg.easyipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.sensorberg.easyipc.log.Log;

import java.util.ArrayList;
import java.util.List;

public class IpcActivityHelper extends IpcBase {

	private final String TAG = getClass().getName();

	private final Intent intent;
	private final Context context;
	private final int flags;
	private final List<ConnectionListener> listeners = new ArrayList<>();

	private boolean isStarted = false;
	private boolean isConnectionStatusDispatched = false;
	private ISender outgoing;

	public IpcActivityHelper(Context context, Intent intent, int flags) {
		super();
		this.context = context;
		this.intent = intent;
		this.flags = flags;
	}

	public IpcActivityHelper(Context context, Intent intent, int flags, int maxQueueSize) {
		super(maxQueueSize);
		this.context = context;
		this.intent = intent;
		this.flags = flags;
	}

	public void start() {
		isStarted = true;
		context.bindService(intent, serviceConnection, flags);
	}

	public void stop() {
		isStarted = false;
		ISender s = outgoing;
		if (s != null) {
			try {
				s.setReceiver(null);
			} catch (RemoteException e) { /* */ }
		}
		if (isConnectionStatusDispatched) {
			isConnectionStatusDispatched = false;
			for (int i = 0, size = listeners.size(); i < size; i++) {
				listeners.get(i).onServiceDisconnected();
			}
		}
		context.unbindService(serviceConnection);
	}

	@Override
	public boolean isAlive() {
		return outgoing != null;
	}

	public void addConnectionListener(ConnectionListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeConnectionListener(ConnectionListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override void sendTypesToOtherProcess(List<String> types) throws RemoteException {
		ISender s = outgoing;
		if (s != null) {
			s.setTypes(types);
		}
	}

	@Override boolean sendEventToOtherProcess(ParcelableEvent event) throws RemoteException {
		ISender s = outgoing;
		if (s != null) {
			s.onEvent(event);
			return true;
		} else {
			return false;
		}
	}

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d("%s.onServiceConnected", TAG);
			outgoing = ISender.Stub.asInterface(service);
			try {
				outgoing.setReceiver(incoming);
				updateTypes();
				dequeueIfNeeded();
			} catch (RemoteException e) {
				Log.e(e, "%s.onServiceConnected", TAG);
			}

			if (!isConnectionStatusDispatched) {
				isConnectionStatusDispatched = true;
				for (int i = 0, size = listeners.size(); i < size; i++) {
					listeners.get(i).onServiceConnected(IpcActivityHelper.this);
				}
			}

		}

		@Override public void onServiceDisconnected(ComponentName name) {
			Log.d("%s.onServiceDisconnected", TAG);
			if (isConnectionStatusDispatched) {
				isConnectionStatusDispatched = false;
				for (int i = 0, size = listeners.size(); i < size; i++) {
					listeners.get(i).onServiceDisconnected();
				}
			}
			outgoing = null;
			if (isStarted) { // re-bind
				context.bindService(intent, serviceConnection, flags);
			}
		}
	};

	private IReceiver.Stub incoming = new IReceiver.Stub() {
		@Override public void onEvent(ParcelableEvent event) throws RemoteException {
			sendToListeners(event);
		}

		@Override public void setTypes(List<String> types) throws RemoteException {
			setTypesThatTheOtherProcessIsListeningTo(types);
			dequeueIfNeeded();
		}
	};

	public interface ConnectionListener {
		void onServiceConnected(IpcActivityHelper ipcActivityHelper);

		void onServiceDisconnected();
	}

}
