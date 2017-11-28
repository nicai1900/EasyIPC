package com.sensorberg.easyipc.testapp;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class Robot {
	public static void clickButton() {
		onView(allOf(withId(R.id.button), isDisplayed())).perform(click());
	}

	public static void clickText() {
		onView(allOf(withId(R.id.text), isDisplayed())).perform(click());
	}

	public static void checkText(String s) {
		onView(allOf(withId(R.id.text), isDisplayed())).check(matches(withText(s)));
	}

	public static void waitABit() {
		try {
			Thread.sleep(75);
		} catch (InterruptedException e) {  }
	}
}
