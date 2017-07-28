package com.sensorberg.easyipc.testapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EspressoActivity1Test {

	@Rule
	public ActivityTestRule<EspressoActivity1> mActivityTestRule = new ActivityTestRule<>(EspressoActivity1.class);

	@Test
	public void espressoActivity1Test() {
		Robot.waitABit();
		Robot.checkText(EspressoActivity1.sentData);
	}
}
