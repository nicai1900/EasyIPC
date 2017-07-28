package com.sensorberg.easyipc.testapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.sensorberg.testapp_foreground.EspressoActivity0;
import com.sensorberg.testapp_foreground.MainActivity;

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

		for (int j = 0; j < 10; j++) {
			// MainActivity -> click button to Activity0
			Robot.clickButton();
			// test Activity0
			for (int i = 0; i < 3; i++) {
				Robot.checkText(EspressoActivity0.lastSent);
				Robot.waitABit();
				Robot.clickButton();
			}

			pressBack(); // back

			// MainActivity -> click button to Activity1
			Robot.clickText();
			Robot.waitABit();
			Robot.checkText(MainActivity.sentData);

			pressBack(); // back
		}

	}
}
