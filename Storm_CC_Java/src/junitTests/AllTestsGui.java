package junitTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestLog_Gui.class, TestGraph_Gui.class, TestMainGui.class })
public class AllTestsGui {

}
