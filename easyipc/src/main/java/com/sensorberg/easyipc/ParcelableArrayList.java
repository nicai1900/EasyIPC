package com.sensorberg.easyipc;

import android.os.Parcel;
import android.os.Parcelable;

import com.sensorberg.easyipc.log.Log;

import java.util.ArrayList;
import java.util.Collection;

public class ParcelableArrayList<T extends Parcelable> extends ArrayList<T> implements Parcelable {

	public ParcelableArrayList() {
		super();
	}

	public ParcelableArrayList(Collection<T> c) {
		super(c);
	}

	public ParcelableArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public static final Creator<ParcelableArrayList> CREATOR = new Creator<ParcelableArrayList>() {
		@Override
		public ParcelableArrayList createFromParcel(Parcel in) {
			Class k = (Class) in.readSerializable();
			ParcelableArrayList l = null;
			try {
				l = (ParcelableArrayList) k.newInstance();
				l.addAll(in.readArrayList(k.getClassLoader()));
			} catch (InstantiationException e) {
				Log.e(e, "ParcelableArrayList.CREATOR.InstantiationException");
			} catch (IllegalAccessException e) {
				Log.e(e, "ParcelableArrayList.CREATOR.IllegalAccessException");
			}
			return l;
		}

		@Override
		public ParcelableArrayList[] newArray(int size) {
			return new ParcelableArrayList[size];
		}
	};

	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(getClass());
		dest.writeList(this);
	}
}
