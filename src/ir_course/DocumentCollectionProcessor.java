
package ir_course;

import java.util.ArrayList;
import java.util.List;

public class DocumentCollectionProcessor {
	private List<DocumentInCollection> filteredDocuments;
	private Integer searchTask=13;
	private Integer docsInSearchTask = 0;
	private Integer relevantDocCount = 0;

	
	public DocumentCollectionProcessor(List<DocumentInCollection> documents, Integer searchTask) {
		this.searchTask = searchTask;
		this.filteredDocuments = getFilteredDocuments(documents, searchTask);
		updateCommonStats();
	}

	public List<DocumentInCollection> getFilteredDocuments() {
		return filteredDocuments;
	}
	

	private void updateCommonStats() {
		this.docsInSearchTask = filteredDocuments.size();
		this.relevantDocCount = getRelevantDocs(filteredDocuments);
	}

	private Integer getRelevantDocs(List<DocumentInCollection> docs) {
		Integer relevantDocs = 0;
		for (DocumentInCollection doc : docs) {
			if (doc.isRelevant()) {
				relevantDocs++;
			}
		}
		return relevantDocs;
	}

	private List<DocumentInCollection> getFilteredDocuments(List<DocumentInCollection> originalSet,
			Integer searchTask) {
		List<DocumentInCollection> filteredDocuments = new ArrayList<DocumentInCollection>();
		Integer docsInSearchTask = 0;
		for (DocumentInCollection doc : originalSet) {
			if (doc.getSearchTaskNumber() == searchTask) {
				filteredDocuments.add(doc);
				docsInSearchTask++;
			}
		}
		return filteredDocuments;
	}
	
	public LuceneStatistics getRankedSearchResultStats(List<DocumentInCollection> searchResults) {
		LuceneStatistics stats = new LuceneStatistics(searchResults, relevantDocCount);
		Integer relevantDocsInSearchCount = getRelevantDocs(searchResults);
		Integer searchResultCount = searchResults.size();
		stats.setRecall(relevantDocsInSearchCount / relevantDocCount.doubleValue());
		stats.setPrecision(relevantDocsInSearchCount / searchResultCount.doubleValue());
		stats.getElevenPointPR();
		return stats;
	}
}
