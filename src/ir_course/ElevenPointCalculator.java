package ir_course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ElevenPointCalculator {
	private Double precision = 0.0;
	private Double recall = 0.0;
	private List<Double> _11ptPR = new ArrayList<Double>();
	private Double avgPrecision;
	private List<Double> intermediatePr = new ArrayList<Double>();
	private List<Double> intermediateR = new ArrayList<Double>();
	private List<Boolean> relevanceOfResults = new ArrayList<Boolean>();
	private Integer relevantsInCorpus = 0;
	
	/**
	 * Constructor to get started in the middle.
	 * @param precision
	 * @param recall
	 * @param elevenPointPR
	 */
	public ElevenPointCalculator(Double precision, Double recall, List<Double> _11ptPR) {
		this.precision = precision;
		this.recall = recall;
		this._11ptPR = _11ptPR;
	}
	
	/**
	 * @param searchResults 
	 * @param relevantsInCorpus
	 */
	public ElevenPointCalculator(List<DocumentInCollection> searchResults, Integer relevantsInCorpus) {
		this.relevanceOfResults = searchResults.stream().map(item -> item.isRelevant()).collect(Collectors.toList());
		this.relevantsInCorpus = relevantsInCorpus;
		calculatePrecisionsAtK();
		calculateAveragePrecision();
		getElevenPointPR();
	}
	
	//Getters and setters of Precision, Recall and 11PointPR, PrecisionATK etc.

	public Double getPrecision() {
		return precision;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	public Double getRecall() {
		return recall;
	}

	public void setRecall(Double recall) {
		this.recall = recall;
	}

	public List<Double> getElevenPointPR() {
		return calculateElevenPointPR();
	}

	public void setElevenPointPR(List<Double> elevenPointPR) {
		this._11ptPR = elevenPointPR;
	}
	public Double getAvgPr() {
		return avgPrecision;
	}

	public void setAvgPr(Double avgPrecision) {
		this.avgPrecision = avgPrecision;
	}

	public List<Double> getPrecisionAtK() {
		return intermediatePr;
	}

	public void setintermediatePr(List<Double> intermediatePr) {
		this.intermediatePr = intermediatePr;
	}

	public List<Boolean> getRelevanceOfResults() {
		return relevanceOfResults;
	}

	public void setRelevanceOfResults(List<Boolean> relevanceOfResults) {
		this.relevanceOfResults = relevanceOfResults;
	}

	public List<Double> getIntermediateR() {
		return intermediateR;
	}

	public void setIntermediateR(List<Double> intermediateR) {
		this.intermediateR = intermediateR;
	}

	
	private List<Double> calculateElevenPointPR() {
		Double[] ElevenPointPR = new Double[11];
		Integer relevantsInsearchResult = (int) relevanceOfResults.stream().filter(p -> p == true).count();
		Double recallInPercent = (relevantsInsearchResult * 100) / relevantsInCorpus.doubleValue();
		Double sumRelevants = 0.0; 
		Integer sumResults = 1; 
		Double currentRecallPercent = 0.0;
		Integer recall10thsPlace = 0;
		sumResults = 0;
		sumRelevants = 0.0; 
		for (Boolean relevance : relevanceOfResults) {
			if (relevance) {
				sumRelevants++;
				currentRecallPercent = sumRelevants / relevantsInCorpus;
				// Getting index to update next precision
				recall10thsPlace = (int) (currentRecallPercent * 10);
				ElevenPointPR[recall10thsPlace] = intermediatePr
						.subList(sumRelevants.intValue() - 1, intermediatePr.size()).stream()
						.collect(Collectors.summarizingDouble(Double::doubleValue)).getMax();
			}
			sumResults++;
		}
		// Incase when the relevance is not 100%, it repeats the ending value. 
		for (int i=0; i<=10; i++){
			if(ElevenPointPR[i] == null && i != 0){
				ElevenPointPR[i] = ElevenPointPR[i-1];
			}
		}
		return Arrays.asList(ElevenPointPR);
	}

	/**
	 * Calculates the Average Precision value over all recall levels.
	 */
	private void calculateAveragePrecision() {
		avgPrecision = intermediatePr.stream().collect(Collectors.summarizingDouble(Double::doubleValue))
				.getAverage();
	}

	/**
	 * Calculates the precision at all recall levels.
	 */
	private void calculatePrecisionsAtK() {
		Double sumRelevants = 0.0; 
		Integer sumResults = 1;
		for (Boolean relevance : relevanceOfResults) {
			if (relevance) {
				sumRelevants++;
				intermediatePr.add(sumRelevants / sumResults.doubleValue());
				intermediateR.add(sumRelevants / relevantsInCorpus);
			}
			sumResults++;
		}
	}

	public Double getAveragePrecisionAtK(int k) {
		return intermediatePr.subList(0, Math.min(k, intermediatePr.size())).stream()
				.collect(Collectors.summarizingDouble(Double::doubleValue)).getAverage();
	}

}
