package com.sensorberg.easyipc.testapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.sensorberg.easyipc.ParcelableArrayList;

public class Data implements Parcelable {

	public final int val;
	public final String otherVal;

	public Data(int val, String otherVal) {
		this.val = val;
		this.otherVal = otherVal;
	}

	private Data(Parcel in) {
		val = in.readInt();
		otherVal = in.readString();
	}

	@Override public String toString() {
		return "Data{" + val + ", " + otherVal + "}";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(val);
		dest.writeString(otherVal);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
		@Override
		public Data createFromParcel(Parcel in) {
			return new Data(in);
		}

		@Override
		public Data[] newArray(int size) {
			return new Data[size];
		}
	};

	public static class List extends ParcelableArrayList<Data> {

	}
}