package ir_course;

import java.util.ArrayList;
import java.util.List;

public class CONSTANTS {
	   public static final String PORTER="PORTER_STEMMER";
	   public static final String K="K_STEMMER";
	   public static final String ENG_M="ENGLISH_MIN_STEMMER";
	   public static final boolean REMOVE_STOP = true;
	   public static final boolean DONT_REMOVE_STOP = false;
	   public static final String ENG="ENGLISH_STEMMER";
	   public static final String TITLE="Title";
	   public static final String ABSTRACT="abstract";
	   public static final String RELEVANCE = "REL";
	   public static final String VSM = "vsm";
	   public static final String BM25 = "bm25";
	   
	   public static List<Settings> getSettings() {
		   List<Settings> settings = new ArrayList<Settings>();
			settings
					.add(new Settings(CONSTANTS.ENG, CONSTANTS.REMOVE_STOP, CONSTANTS.BM25 ));
			settings
					.add(new Settings(CONSTANTS.ENG_M, CONSTANTS.REMOVE_STOP, CONSTANTS.BM25 ));
			settings
					.add(new Settings(CONSTANTS.K, CONSTANTS.REMOVE_STOP, CONSTANTS.VSM ));
			settings
					.add(new Settings(CONSTANTS.PORTER, CONSTANTS.REMOVE_STOP, CONSTANTS.VSM ));
			settings
					.add(new Settings(CONSTANTS.ENG, CONSTANTS.DONT_REMOVE_STOP, CONSTANTS.BM25 ));
			settings
					.add(new Settings(CONSTANTS.ENG_M, CONSTANTS.DONT_REMOVE_STOP, CONSTANTS.BM25 ));
			settings
					.add(new Settings(CONSTANTS.K, CONSTANTS.DONT_REMOVE_STOP, CONSTANTS.VSM ));
			settings
					.add(new Settings(CONSTANTS.PORTER, CONSTANTS.DONT_REMOVE_STOP, CONSTANTS.VSM ));
			return settings;
	   }
	   
	   public static List<String> getQueries() {
			List<String> queryStrings = new ArrayList<String>();
			queryStrings.add("visualizing dataset");
			queryStrings.add("large data set visualization");
			queryStrings.add("huge data sets visualization");
			queryStrings.add("visualizing high-dimensional dataset");
			return queryStrings;
	   }
}
