package junitTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestAttackController.class, TestClientController.class,
		TestConnectionManager.class })
public class AllTestsControllers {

}
