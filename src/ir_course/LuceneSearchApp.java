/*
 * Skeleton class for the Lucene search program implementation
 *
 * Created on 2011-12-21
 * * Jouni Tuominen <jouni.tuominen@aalto.fi>
 * 
 * Modified on 2015-30-12
 * * Esko Ikkala <esko.ikkala@aalto.fi>
 * 
 */
package ir_course;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;

public class LuceneSearchApp {
	private Directory index;
	private PorterAnalyzer analyzer;
	private IndexWriterConfig config;
	private IndexWriter writer;
	private int count = 0;
	public class LuceneConstants {
		   public static final String TITLE="Title";
		   public static final String ABSTRACT="abstract";
	}
	public LuceneSearchApp() throws IOException {
		this.index =  new RAMDirectory();
		this.analyzer = new PorterAnalyzer();
		this.config = new IndexWriterConfig(analyzer);
		this.writer = new IndexWriter(this.index, this.config);
	}
	
	public void index(List<DocumentInCollection> docs) throws IOException {
		for (DocumentInCollection doc : docs){
	      Document document = new Document();
	      document.add(new TextField("textContent", doc.getTitle()+doc.getAbstractText(), Field.Store.YES));
	      //document.add(new TextField(LuceneConstants.ABSTRACT, doc.getAbstractText(), Field.Store.YES));
	      //document.add(new StringField(LuceneConstants.QUERY, doc.getQuery(),Field.Store.YES));
	      this.writer.addDocument(document);
	      this.count++;
		}
        this.writer.close();
	}
	public BooleanQuery.Builder createQuery( List<String> in, List<String> notIn, String type, BooleanQuery.Builder qb){
		QueryParser qp;
		if (in != null){
			for(String s:in){
				Query query;
				qp = new QueryParser(type, this.analyzer);
				try {
					query = qp.parse(s);
					qb.add(query,BooleanClause.Occur.MUST);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}	
		}
		if (notIn != null){
			for (String s:notIn)
			{
				qp = new QueryParser(type, this.analyzer);
				Query query;
				try {
					query = qp.parse(s);
					qb.add(query, BooleanClause.Occur.MUST_NOT);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}	
		}
		return qb;
	}
	public List<String> search(String query) throws IOException{
		List<String> results = new LinkedList<String>();
		
		/*
		BooleanQuery.Builder qb = new BooleanQuery.Builder();
		this.createQuery(LuceneConstants.TITLE, qb);
		this.createQuery(LuceneConstants.ABSTRACT, qb);
        BooleanQuery fq = qb.build();
		IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(fq, this.count);
        ScoreDoc[] hits = docs.scoreDocs;
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            results.add(d.get(LuceneConstants.TITLE) + "\t" + d.get(LuceneConstants.DESCRIPTION)  + "\t" + d.get(LuceneConstants.DATE) );
        }
		*/
		return results;

	}
	 
	public void printQuery(List<String> inTitle, List<String> notInTitle, List<String> inDescription, List<String> notInDescription, String startDate, String endDate) {
		System.out.print("Search (");
		if (inTitle != null) {
			System.out.print("in title: "+inTitle);
			if (notInTitle != null || inDescription != null || notInDescription != null || startDate != null || endDate != null)
				System.out.print("; ");
		}
		if (notInTitle != null) {
			System.out.print("not in title: "+notInTitle);
			if (inDescription != null || notInDescription != null || startDate != null || endDate != null)
				System.out.print("; ");
		}
		if (inDescription != null) {
			System.out.print("in description: "+inDescription);
			if (notInDescription != null || startDate != null || endDate != null)
				System.out.print("; ");
		}
		if (notInDescription != null) {
			System.out.print("not in description: "+notInDescription);
			if (startDate != null || endDate != null)
				System.out.print("; ");
		}
		if (startDate != null) {
			System.out.print("startDate: "+startDate);
			if (endDate != null)
				System.out.print("; ");
		}
		if (endDate != null)
			System.out.print("endDate: "+endDate);
		System.out.println("):");
	}
	
	public void printResults(List<String> results) {
		if (results.size() > 0) {
			Collections.sort(results);
			for (int i=0; i<results.size(); i++)
				System.out.println(" " + (i+1) + ". " + results.get(i));
		}
		else
			System.out.println(" no results");
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			LuceneSearchApp engine = new LuceneSearchApp();
			DocumentCollectionParser parser = new DocumentCollectionParser();
			parser.parse(args[0]);
			List<DocumentInCollection> docs = parser.getDocuments();
			
			for (Iterator<DocumentInCollection> iter = docs.listIterator(); iter.hasNext(); ) {
				DocumentInCollection a = iter.next();
			    if (a.getSearchTaskNumber() != 13) {
			        iter.remove();
			    }
			}
			engine.index(docs);
			
			System.out.print(docs);
			
			/*

			List<String> inTitle;
			List<String> notInTitle;
			List<String> inDescription;
			List<String> notInDescription;
			List<String> results;
			
			// 1) search documents with words "kim" and "korea" in the title
			inTitle = new LinkedList<String>();
			inTitle.add("kim");
			inTitle.add("korea");
			results = engine.search(inTitle, null, null, null, null, null);
			engine.printResults(results);
			
			// 2) search documents with word "kim" in the title and no word "korea" in the description
			inTitle = new LinkedList<String>();
			notInDescription = new LinkedList<String>();
			inTitle.add("kim");
			notInDescription.add("korea");
			results = engine.search(inTitle, null, null, notInDescription, null, null);
			engine.printResults(results);

			// 3) search documents with word "us" in the title, no word "dawn" in the title and word "" and "" in the description
			inTitle = new LinkedList<String>();
			inTitle.add("us");
			notInTitle = new LinkedList<String>();
			notInTitle.add("dawn");
			inDescription = new LinkedList<String>();
			inDescription.add("american");
			inDescription.add("confession");
			results = engine.search(inTitle, notInTitle, inDescription, null, null, null);
			engine.printResults(results);
			
			// 4) search documents whose publication date is 2011-12-18
			results = engine.search(null, null, null, null, "2011-12-18", "2011-12-18");
			engine.printResults(results);
			
			// 5) search documents with word "video" in the title whose publication date is 2000-01-01 or later
			inTitle = new LinkedList<String>();
			inTitle.add("video");
			results = engine.search(inTitle, null, null, null, "2000-01-01", null);
			engine.printResults(results);
			
			// 6) search documents with no word "canada" or "iraq" or "israel" in the description whose publication date is 2011-12-18 or earlier
			notInDescription = new LinkedList<String>();
			notInDescription.add("canada");
			notInDescription.add("iraq");
			notInDescription.add("israel");
			results = engine.search(null, null, null, notInDescription, null, "2011-12-18");
			engine.printResults(results);
			*/
		}
		else
			System.out.println("ERROR: the path of a RSS Feed file has to be passed as a command line argument.");
	}
}
