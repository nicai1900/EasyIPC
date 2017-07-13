package com.sensorberg.easyipc;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;

import com.sensorberg.easyipc.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class IpcBase implements IpcConnection {

	private final String TAG = getClass().getName();

	private Map<String, Set<IpcListener>> listeners = new HashMap<>();
	private Handler defaultHandler = new Handler(Looper.getMainLooper());
	private Map<IpcListener, Handler> handlers = new HashMap<>();
	private List<String> types;
	private final Object lock = new Object();

	void setLocalTypes(List<String> types) {
		Log.d("%s.setLocalTypes %s", TAG, types);
		this.types = types;
	}

	abstract void sendTypesToOtherProcess(List<String> types) throws RemoteException;

	abstract boolean sendEventToOtherProcess(ParcelableEvent event) throws RemoteException;

	@Override
	public <T extends Parcelable> IpcConnection addListener(Class<T> klazz, IpcListener<T> listener, Handler handler) {
		if (handler != null) {
			handlers.put(listener, handler);
		}
		addListener(klazz, listener);
		return this;
	}

	@Override
	public <T extends Parcelable> IpcConnection addListener(Class<T> klazz, IpcListener<T> listener) {
		Set<IpcListener> set = listeners.get(klazz.getCanonicalName());
		if (set == null) {
			set = new HashSet<>();
			listeners.put(klazz.getCanonicalName(), set);
		}
		synchronized (lock) {
			set.add(listener);
		}
		updateTypes();
		return this;
	}

	@Override
	public <T extends Parcelable> IpcConnection removeListener(Class<T> klazz, IpcListener<T> listener) {
		Set<IpcListener> set = listeners.get(klazz.getCanonicalName());
		if (set != null) {
			synchronized (lock) {
				set.remove(listener);
			}
			handlers.remove(listener);
			if (set.isEmpty()) {
				listeners.remove(klazz.getCanonicalName());
			}
		}
		updateTypes();
		return this;
	}

	final void clearAll() {
		listeners.clear();
		handlers.clear();
		types.clear();
	}

	final void updateTypes() {
		try {
			sendTypesToOtherProcess(new ArrayList<>(listeners.keySet()));
		} catch (RemoteException e) {
			Log.e(e, "%s.failedToSendTypesToOtherProcess", TAG);
		}
	}

	final boolean sendToOtherProcess(Parcelable event) {
		Log.d("IpcBase.send(%s)", event.getClass().getCanonicalName());
		List<String> t = types;
		if (t != null && t.contains(event.getClass().getCanonicalName())) {
			try {
				return sendEventToOtherProcess(new ParcelableEvent(event));
			} catch (RemoteException e) {
				Log.e(e, "%s.failedToSendEventToOtherProcess", TAG);
			}
		}
		return false;
	}

	final void sendToListeners(ParcelableEvent event) {
		Log.d("%s.receive(%s)", TAG, event.className);
		Set<IpcListener> set = listeners.get(event.className);
		if (set != null) {
			for (IpcListener l : set) {
				Handler handler = handlers.get(l);
				if (handler == null) {
					handler = defaultHandler;
				}
				EventDispatcher dispatcher = dispatchers.acquire();
				if (dispatcher == null) {
					dispatcher = new EventDispatcher(dispatchers, lock);
				}
				dispatcher.dispatch(handler, l, event.parcelable, set);
			}
		}
	}

	private final Pools.SynchronizedPool<EventDispatcher> dispatchers =
			new Pools.SynchronizedPool<>(50);

	private static class EventDispatcher implements Runnable {

		private final Pools.SynchronizedPool<EventDispatcher> dispatchers;
		private final Object lock;
		private IpcListener listener;
		private Parcelable event;
		private Set<IpcListener> set;

		EventDispatcher(Pools.SynchronizedPool<EventDispatcher> dispatchers, Object lock) {
			this.dispatchers = dispatchers;
			this.lock = lock;
		}

		@Override public void run() {

			synchronized (lock) {
				if (set.contains(listener)) {
					listener.onEvent(event);
				}
			}

			this.listener = null;
			this.event = null;
			this.set = null;
			dispatchers.release(this);
		}

		void dispatch(Handler handler, IpcListener listener, Parcelable event, Set<IpcListener> set) {
			this.listener = listener;
			this.event = event;
			this.set = set;
			if (handler.getLooper() == Looper.myLooper()) {
				run();
			} else {
				handler.post(this);
			}
		}
	}

}
