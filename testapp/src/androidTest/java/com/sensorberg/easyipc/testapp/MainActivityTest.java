package com.sensorberg.easyipc.testapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.pressBack;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

	@Rule
	public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void mainActivityTest() {

		for (int j = 0; j < 5; j++) {
			// MainActivity -> click button to Activity0
			Robot.clickButton();
			// test Activity0
			for (int i = 0; i < 5; i++) {
				Robot.checkText(EspressoActivity0.lastSent);
				Robot.clickButton();
				Robot.waitABit();
			}

			pressBack(); // back

			// MainActivity -> click button to Activity1
			Robot.clickText();
			Robot.waitABit();
			Robot.checkText(EspressoActivity1.sentData);

			pressBack(); // back
		}

	}
}
