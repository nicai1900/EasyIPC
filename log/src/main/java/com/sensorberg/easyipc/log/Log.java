package com.sensorberg.easyipc.log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(
		value = "MS_SHOULD_BE_FINAL",
		justification = "It's not final to allow devs on runtime to enable/disable logs")
public final class Log {

	// utility class
	private Log() { /* */ }

	public static boolean ENABLED = false;
	private static final String TAG = "EasyIpc";
	private static final StringBuilder SB = new StringBuilder();

	public static void d(String message, Object... args) {
		if (ENABLED)
			android.util.Log.d(TAG, createMessage(message, args));
	}

	public static void e(Throwable throwable, String message, Object... args) {
		if (ENABLED)
			android.util.Log.e(TAG, createMessage(message, args), throwable);
	}

	public static void i(String message, Object... args) {
		if (ENABLED)
			android.util.Log.i(TAG, createMessage(message, args));
	}

	public static void v(String message, Object... args) {
		if (ENABLED)
			android.util.Log.v(TAG, createMessage(message, args));
	}

	public static void w(String message, Object... args) {
		if (ENABLED)
			android.util.Log.w(TAG, createMessage(message, args));
	}

	@SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop") // the break there makes sense
	private static synchronized String createMessage(String message, Object... args) {

		SB.setLength(0);
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		// find the first stack position that is from sensorberg and it's not from the logging classes
		for (int i = 0, size = trace.length; i < size; i++) {
			StackTraceElement element = trace[i];
			String className = element.getClassName();
			if (!className.startsWith("com.sensorberg")) {
				continue;
			}
			if (className.equals(Log.class.getCanonicalName())) {
				continue;
			}
			SB.append("[(");
			SB.append(element.getFileName());
			SB.append(':');
			SB.append(element.getLineNumber());
			SB.append(").");
			SB.append(element.getMethodName());
			SB.append("()] - ");
			break;
		}

		SB.append(args == null || args.length == 0 ? message : String.format(message, args));

		return SB.toString();
	}
}
