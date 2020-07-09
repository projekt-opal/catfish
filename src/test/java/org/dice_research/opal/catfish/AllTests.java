package org.dice_research.opal.catfish;

import org.dice_research.opal.catfish.cleaner.DateFormatEqualizerTest;
import org.dice_research.opal.catfish.cleaner.EmptyBlankNodeCleanerTest;
import org.dice_research.opal.catfish.cleaner.FormatCleanerTest;
import org.dice_research.opal.catfish.cleaner.LiteralCleanerTest;
import org.dice_research.opal.catfish.cleaner.TitleLanguageFilterTest;
import org.dice_research.opal.catfish.cleaner.UriRewriterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

		DateFormatEqualizerTest.class,

		EmptyBlankNodeCleanerTest.class,

		FormatCleanerTest.class,

		LiteralCleanerTest.class,

		TitleLanguageFilterTest.class,

		UriRewriterTest.class,

		CatfishTest.class

})
public class AllTests {
}