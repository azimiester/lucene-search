package ir_course;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.EnglishStemmer;

class Settings {
	private String _stemmer;
	public boolean _removeStopWords;
	public String _similarity;
	public Analyzer getAnalyzer() {
		String _stemmer = this._stemmer;
		boolean _removeStopWords =  this._removeStopWords;
		Analyzer analyzer = new Analyzer() {
			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
			    Tokenizer source = new LetterTokenizer();              
			    TokenStream filter = new LowerCaseFilter(source);
			    filter = new StandardFilter(filter);
			    switch(_stemmer){
			    	case CONSTANTS.PORTER:
			    		filter = new PorterStemFilter(filter);
			    		break;
			    	case CONSTANTS.K:
			    		filter = new KStemFilter(filter);
			    	break;
			    	case CONSTANTS.ENG:
						filter = new SnowballFilter(filter, new org.tartarus.snowball.ext.EnglishStemmer());
					break;
			    	case CONSTANTS.ENG_M:
		    		default:
			    		filter = new EnglishMinimalStemFilter(filter);
		    		break;
			    }
			    if (_removeStopWords){
				    filter = new StopFilter(filter, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
			    }
			    return new TokenStreamComponents(source, filter);
			}
		};
	return analyzer;
	}
	
	
	public Similarity getSimilarity(){
		return this._similarity.equals(CONSTANTS.BM25.toString()) ? new BM25Similarity() : new ClassicSimilarity();
	}
	
	public Settings(String _stemmer, boolean _removeStopWords, String _similarity){
		this._stemmer=_stemmer;
		this._removeStopWords = _removeStopWords;
		this._similarity = _similarity;
	}
	
	@Override
	public String toString() {
		return "==>Settings: Stop words removed = " + this._removeStopWords + ", Stemmer = " + this._stemmer + ", Similarity = "
				+ this._similarity + "<==";
	}
	
}
