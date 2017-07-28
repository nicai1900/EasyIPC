package com.sensorberg.easyipc.testapp;

import android.os.Parcel;
import android.os.Parcelable;

public class EarlyData implements Parcelable {

	public final String value;

	public EarlyData(String value) {
		this.value = value;
	}

	protected EarlyData(Parcel in) {
		value = in.readString();
	}

	@Override public String toString() {
		return "EarlyData(" + value + ")";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(value);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<EarlyData> CREATOR = new Parcelable.Creator<EarlyData>() {
		@Override
		public EarlyData createFromParcel(Parcel in) {
			return new EarlyData(in);
		}

		@Override
		public EarlyData[] newArray(int size) {
			return new EarlyData[size];
		}
	};
}