package com.sensorberg.testapp_foreground;

import android.os.Parcel;
import android.os.Parcelable;

import com.sensorberg.easyipc.ParcelableArrayList;

import java.util.Random;

public class Data implements Parcelable {

	private static final StringBuilder STRING_BUILDER = new StringBuilder();
	private static final Random RANDOM = new Random();
	private static final String CHARS = "1234567890qwertyuiopasdfghjklzxcvbnm ";

	public synchronized static Data getRandomData() {
		STRING_BUILDER.setLength(0);
		for (int i = 0; i < 16; i++) {
			STRING_BUILDER.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
		}
		return new Data(RANDOM.nextInt(), STRING_BUILDER.toString());
	}

	public synchronized static List getRandomDataList() {
		List l = new List();
		for (int i = 0; i < 29; i++) {
			l.add(getRandomData());
		}
		return l;
	}

	public final int val;
	public final String otherVal;

	private Data(int val, String otherVal) {
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
	public static final Creator<Data> CREATOR = new Creator<Data>() {
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