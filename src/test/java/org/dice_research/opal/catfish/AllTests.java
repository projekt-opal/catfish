package org.dice_research.opal.catfish;

import org.dice_research.opal.catfish.service.impl.ThemeCleaner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

		FormatCleanerTest.class,

		EmptyBlankNodeCleanerTest.class,

		ThemeCleanerTest.class,

		LiteralCleanerTest.class

})
public class AllTests {
}