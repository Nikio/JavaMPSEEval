package junitTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AllTestsControllers.class, AllTestsGui.class,
		AllTestsModels.class, TestDatabaseConnection.class })
public class AllTests {

}
