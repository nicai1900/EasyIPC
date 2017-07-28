package com.sensorberg.testapp_foreground;

import android.os.Parcel;
import android.os.Parcelable;

public class Command implements Parcelable {
	public final boolean stream;

	public Command(boolean stream) {
		this.stream = stream;
	}

	protected Command(Parcel in) {
		stream = in.readByte() != 0x00;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (stream ? 0x01 : 0x00));
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Command> CREATOR = new Parcelable.Creator<Command>() {
		@Override
		public Command createFromParcel(Parcel in) {
			return new Command(in);
		}

		@Override
		public Command[] newArray(int size) {
			return new Command[size];
		}
	};
}