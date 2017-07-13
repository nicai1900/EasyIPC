package com.sensorberg.easyipc;

import android.os.Handler;
import android.os.Parcelable;

public interface IpcConnection {

	/**
	 * Adds a listener to this IPC-bus
	 *
	 * @param klazz    the object Class to receive
	 * @param listener the listener to receive events of the type of klazz
	 * @param handler  (optional) handler for the thread where events should be dispatched. (default is UI thread)
	 * @return the same IpcConnection connection object, for chaining calls
	 */
	<T extends Parcelable> IpcConnection addListener(Class<T> klazz, IpcListener<T> listener, Handler handler);

	/**
	 * Adds a listener to this IPC-bus. Calls will be made on the UI thread
	 *
	 * @param klazz    the object Class to receive
	 * @param listener the listener to receive events of the type of klazz
	 * @return the same IpcConnection connection object, for chaining calls
	 */
	<T extends Parcelable> IpcConnection addListener(Class<T> klazz, IpcListener<T> listener);

	/**
	 * Remove a listener from this IPC-bus.
	 *
	 * @param klazz    the object Class that we were receiving events
	 * @param listener the listener to be removed
	 * @return the same IpcConnection connection object, for chaining calls
	 */
	<T extends Parcelable> IpcConnection removeListener(Class<T> klazz, IpcListener<T> listener);

	/**
	 * Sends the object to the other process
	 *
	 * @param parcelable object to be sent.
	 * @return true, if successful, false otherwise.
	 */
	boolean dispatchEvent(Parcelable parcelable);

	/**
	 * If this IPC connection is alive.
	 * This represents no guarantees that a message will be properly dispatched,
	 * as IPC states can change at any time, but it's a good indication.
	 *
	 * @return true, if the connection seems to be alive, false otherwise.
	 */
	boolean isAlive();
}
