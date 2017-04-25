package ir_course;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.TypeTokenFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

class PorterAnalyzer extends Analyzer{
	@Override
	protected TokenStreamComponents createComponents(String arg0) {

	    Tokenizer source = new LetterTokenizer();              
	    TokenStream filter = new LowerCaseFilter(source);
	    filter = new StopFilter(filter, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	    //filter = new PorterStemFilter(filter);
	    filter = new EnglishMinimalStemFilter(filter);
	    return new TokenStreamComponents(source, filter);
    } 
  }