package com.sensorberg.easyipc;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.sensorberg.easyipc.log.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class IpcBase implements IpcConnection {

	private static final int EVENT_TIMEOUT = 4000;
	private static final int DEFAULT_QUEUE_SIZE = 30;
	private final String TAG = getClass().getSimpleName();

	private final Pools.SynchronizedPool<TimeStamped<Parcelable>> timeStampedPool;
	private final ArrayDeque<TimeStamped<Parcelable>> queue = new ArrayDeque<>();
	private Map<String, Set<IpcListener>> listeners = new HashMap<>();
	private Handler defaultHandler = new Handler(Looper.getMainLooper());
	private Map<IpcListener, Handler> handlers = new HashMap<>();
	private List<String> types;
	private final Object lock = new Object();
	private final int maxQueueSize;

	IpcBase() {
		this(DEFAULT_QUEUE_SIZE);
	}

	IpcBase(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
		this.timeStampedPool = new Pools.SynchronizedPool<>(maxQueueSize);
	}

	/**
	 * ULTRA-VERBOSE method name, I know, it's better like this.
	 * Makes clear what that is about.
	 *
	 * @param types list of types the other process is currently listening to.
	 */
	void setTypesThatTheOtherProcessIsListeningTo(List<String> types) {
		Log.d("%s.receivedTypesThatTheOtherProcessIsListeningTo %s", TAG, types);
		this.types = types;
	}

	abstract void sendTypesToOtherProcess(List<String> types) throws RemoteException;

	abstract boolean sendEventToOtherProcess(ParcelableEvent event) throws RemoteException;

	@Override
	public <T extends Parcelable> IpcConnection addListener(@NonNull Class<T> klazz, @NonNull IpcListener<T> listener, Handler handler) {
		if (handler != null) {
			handlers.put(listener, handler);
		}
		addListener(klazz, listener);
		return this;
	}

	@Override
	public <T extends Parcelable> IpcConnection addListener(@NonNull Class<T> klazz, @NonNull IpcListener<T> listener) {
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
	public <T extends Parcelable> IpcConnection removeListener(@NonNull Class<T> klazz, @NonNull IpcListener<T> listener) {
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
		if (types != null) {
			types.clear();
		}
	}

	final void updateTypes() {
		try {
			sendTypesToOtherProcess(new ArrayList<>(listeners.keySet()));
		} catch (RemoteException e) {
			Log.e(e, "%s.failedToSendTypesToOtherProcess", TAG);
		}
	}

	@Override
	public final boolean dispatchEvent(@NonNull Parcelable event) {
		if (internalDispatchEvent(event)) {
			return true;
		} else {
			Log.v("%s.queueing event (%s)%s", TAG, event.getClass().getCanonicalName(), event);

			TimeStamped<Parcelable> tsp = timeStampedPool.acquire();
			if (tsp == null) {
				tsp = new TimeStamped<>();
			}
			queue.add(tsp.init(event));
			if (queue.size() > maxQueueSize) {
				timeStampedPool.release(queue.remove());
			}
			return false;
		}
	}

	private boolean internalDispatchEvent(Parcelable event) {
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

	final void dequeueIfNeeded() {
		if (!queue.isEmpty()) {
			Log.d("%s.deque %s events", TAG, queue.size());
		}
		while (!queue.isEmpty()) {
			TimeStamped<Parcelable> tsp = queue.peek();

			// if not timed out and dispatch successfully
			if (tsp.elapsed() <= EVENT_TIMEOUT && internalDispatchEvent(tsp.t) ||
				// or timed out
				tsp.elapsed() > EVENT_TIMEOUT) {

				// remove from queue, return to the pool
				timeStampedPool.release(queue.remove());
			}
		}
	}

	final void sendToListeners(ParcelableEvent event) {
		Log.v("%s.sendToListeners(%s)", TAG, event.parcelable == null ? event.className : event.parcelable.toString());
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

	private static class TimeStamped<T> {
		private long timestamp;
		private T t;

		private TimeStamped init(T t) {
			this.timestamp = System.currentTimeMillis();
			this.t = t;
			return this;
		}

		private long elapsed() {
			return System.currentTimeMillis() - timestamp;
		}
	}

}
