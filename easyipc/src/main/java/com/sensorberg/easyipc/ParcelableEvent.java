package com.sensorberg.easyipc;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableEvent implements Parcelable {

	final String className;
	final Class klazz;
	final Parcelable parcelable;

	public ParcelableEvent(Parcelable event) {
		klazz = event.getClass();
		className = klazz.getCanonicalName();
		parcelable = event;
	}

	private ParcelableEvent(Parcel in) {
		klazz = (Class) in.readSerializable();
		if (klazz == null) {
			parcelable = null;
			className = "wtf?";
		} else {
			className = klazz.getCanonicalName();
			parcelable = in.readParcelable(klazz.getClassLoader());
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(klazz);
		dest.writeParcelable(parcelable, 0);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<ParcelableEvent> CREATOR = new Parcelable.Creator<ParcelableEvent>() {
		@Override
		public ParcelableEvent createFromParcel(Parcel in) {
			return new ParcelableEvent(in);
		}

		@Override
		public ParcelableEvent[] newArray(int size) {
			return new ParcelableEvent[size];
		}
	};
}
