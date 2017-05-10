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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;

public class LuceneSearchApp {
	private Directory index;
	private Analyzer analyzer;
	private IndexWriterConfig config;
	private IndexWriter writer;
	private int count = 0;
	private Settings settings;
	private IndexSearcher searcher;
	private IndexReader reader;

	public LuceneSearchApp(Settings settings) throws IOException {
		this.settings = settings;
		this.index =  new RAMDirectory();
		this.analyzer = settings.getAnalyzer();
		this.config = new IndexWriterConfig(analyzer);
		this.config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.writer = new IndexWriter(this.index, this.config); 
	}
	
	public void index(List<DocumentInCollection> docs) throws IOException {
		for (DocumentInCollection doc : docs){
	      Document document = new Document();
	      document.add(new TextField(CONSTANTS.TITLE, doc.getTitle(), Field.Store.YES));
	      document.add(new TextField(CONSTANTS.ABSTRACT, doc.getAbstractText(), Field.Store.YES));
	      document.add(new StringField(CONSTANTS.RELEVANCE, Boolean.toString(doc.isRelevant()) ,Field.Store.YES));
	      this.writer.addDocument(document);
		}
        this.writer.close();
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		searcher.setSimilarity(settings.getSimilarity());

	}
	public List<DocumentInCollection> search(String queryString, int maxHits) throws IOException{
		List<DocumentInCollection> results = new LinkedList<DocumentInCollection>();
		try {
			QueryParser parser = new QueryParser(CONSTANTS.ABSTRACT, analyzer);
			Query query = parser.parse(queryString);
			System.out.println(query);
			TopDocs hits = searcher.search(query, maxHits);
			System.out.println("Found: " + hits.totalHits);
			ScoreDoc[] scoreDocs = hits.scoreDocs;
			for (int n = 0; n < scoreDocs.length; ++n) {
				int docid = scoreDocs[n].doc;
				Document doc = searcher.doc(docid);
				results.add(new DocumentInCollection(doc.get(CONSTANTS.TITLE), doc.get(CONSTANTS.ABSTRACT),
						13, queryString, Boolean.parseBoolean(doc.get(CONSTANTS.RELEVANCE))));
			}
			reader.close();

		} catch (Exception e) {
			System.out.println("Error in search" + e);
		}

		return results;

	}
	private void addToMapByConfiguration(Map<String, List<Double>> mapByConfiguration, String Key, Double data) {
		if (!mapByConfiguration.containsKey(settings.toString())) {
			mapByConfiguration.put(settings.toString(), new ArrayList<Double>());
		}
		mapByConfiguration.get(settings.toString()).add(data);
	}

	private void addToAvg11ptPRByConfig(Map<String, List<Double>> avg11ptPRByConfig, String Key,
			List<Double> elevenPointPR) {
		if (!avg11ptPRByConfig.containsKey(Key)) {
			avg11ptPRByConfig.put(Key, new ArrayList<Double>());
		}
		avg11ptPRByConfig.get(Key).addAll(elevenPointPR);
	}

	public static void printMapWithSettings(Map<String, List<Double>>  map){
		System.out.println();
		System.out.println("§§§§--M.A.P--§§§§");
		System.out.println();
		map.forEach((j, k) -> System.out.println(j + "\n" + "MAP: " + k.stream().collect(Collectors.summarizingDouble(Double::doubleValue)).getAverage()  ));
	}
	
	public static void print11PointsForSettings(Map<String, List<Double>> result, int queryStrSize){
		System.out.println();
		System.out.println("§§§§--Eleven Point PR--§§§§");
		System.out.println();
		
		// Getting Average for all queries and finding MAP for each Config. 
		for (Map.Entry<String, List<Double>> entry : result.entrySet()) {
			System.out.print(entry.getKey() + "\n");
			double[] sum = new double[11];
			for (int idx = 0; idx < entry.getValue().size(); idx++) {
				sum[idx % 11] += entry.getValue().get(idx);
			}
			System.out.print("[");
			for (Double val : sum) {
				System.out.print(val / queryStrSize);
				System.out.print(", ");
			}
			System.out.println("]");
		}
	}
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			DocumentCollectionParser parser = new DocumentCollectionParser();
			parser.parse(args[0]);
			List<DocumentInCollection> docs = parser.getDocuments(13);
			DCHelper dcHelper = new DCHelper(docs, 13);
			// Get tasks for information visualization only.
			docs = dcHelper.getFilteredDocuments();
			// Getting queries and settings from Constants.
			List<String> queryStrings = CONSTANTS.getQueries();
			List<Settings> settings = CONSTANTS.getSettings();
			// Storing each of the MAP and 11 Point in a Hashmap using config string as Key.
			Map<String, List<Double>> resultForSettings = new HashMap<String, List<Double>>();
			Map<String, List<Double>> mapForSetting = new HashMap<String, List<Double>>();
			for (String queryString : queryStrings) {
				System.out.println("Query:" + queryString);
				for (Settings setting : settings) {
					LuceneSearchApp engine = new LuceneSearchApp(setting);
					engine.index(docs);
					List<DocumentInCollection> searchResults = engine.search(queryString,
							docs.size());
					ElevenPointCalculator _11Pt = dcHelper.getRankedSearchResultStats(searchResults);
					// Get Map for each setting / Query Pair.
					engine.addToMapByConfiguration(mapForSetting, setting.toString(),_11Pt.getAvgPr());
					// Get 11 pt for each setting / Query Pair.
					engine.addToAvg11ptPRByConfig(resultForSettings, setting.toString(), _11Pt.getElevenPointPR());
				}
			}
			// Print 11 PT
			print11PointsForSettings(resultForSettings, queryStrings.size());
			// printMap
			printMapWithSettings(mapForSetting);
		}
		else
			System.out.println("ERROR: the path of a RSS Feed file has to be passed as a command line argument.");
	}
}
