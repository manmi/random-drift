package org.randomdrift.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.randomdrift.DocVectorIndexBuilder;
import org.randomdrift.TermVectorIndexBuilder;

public class TestNoiseTerms {
	public static void main(String[] args) throws CorruptIndexException,
			IOException {

		String luceneIndexPath = "E:\\Corpora\\Toi\\index";
		// Build the term vectors

//		TermVectorIndexBuilder termIndexBuilder = new TermVectorIndexBuilder(
//				luceneIndexPath);
//		long startTime = System.currentTimeMillis();
//		termIndexBuilder.buildTermVectorsAll();
//		long endTime = System.currentTimeMillis();
//		System.out.println("Time taken to build complete term vector: "
//				+ ((endTime - startTime) / 1000) + " seconds.");
//		// Build the document vectors
//		startTime = System.currentTimeMillis();
//		DocVectorIndexBuilder docIndexBuilder = new DocVectorIndexBuilder(
//				luceneIndexPath, termIndexBuilder.getTermVectors());
//		docIndexBuilder.buildDoctVectorsAll();
//		endTime = System.currentTimeMillis();
//		System.out.println("Time taken to build complete doc vectors: "
//				+ ((endTime - startTime) / 1000) + " seconds.");
//		// Classify documents
//		Map<String, Integer> termGlobalFreqMap = docIndexBuilder.getTermGlobalFreq();
//		
//		Iterator<String> termIterator = termGlobalFreqMap.keySet().iterator();
//		
//		while(termIterator.hasNext()){
//			String term = termIterator.next();
//			if(termGlobalFreqMap.get(term) < 2){
//				System.out.println(term + ", ");
//			}
//		}
//		
//		Map<Integer, Integer> termDistribution = new HashMap<>();
//		
//		while(termIterator.hasNext()){
//			String term = termIterator.next();
//			int termFreq = termGlobalFreqMap.get(term);
//			if(termDistribution.containsKey(termFreq)){
//				termDistribution.put(termFreq, termDistribution.get(termFreq) + 1);
//			}else{
//				termDistribution.put(termFreq, 1);
//			}	
//		}
//		
//		Iterator<Integer> distributionIterator = termDistribution.keySet().iterator();
//		while(distributionIterator.hasNext()){
//			int key = distributionIterator.next();
//			System.out.println(key + ", " + termDistribution.get(key));
//		}
	}

}
