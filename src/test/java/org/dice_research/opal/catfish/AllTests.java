package org.dice_research.opal.catfish;

import org.dice_research.opal.catfish.service.EmptyBlankNodeCleanerTest;
import org.dice_research.opal.catfish.service.FormatCleanerTest;
import org.dice_research.opal.catfish.service.LiteralCleanerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

		FormatCleanerTest.class,

		EmptyBlankNodeCleanerTest.class,

		LiteralCleanerTest.class,

		CatfishTest.class

})
public class AllTests {
}