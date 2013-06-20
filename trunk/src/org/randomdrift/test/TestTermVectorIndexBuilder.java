package org.randomdrift.test;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.randomdrift.LuceneIndexProfiler;
import org.randomdrift.TermVectorIndexBuilder;

public class TestTermVectorIndexBuilder {

	public static void main(String[] args) throws CorruptIndexException,
			IOException {
		String luceneIndexPath = "E:\\Corpora\\Toi\\index";

		LuceneIndexProfiler indexProfiler = new LuceneIndexProfiler(
				luceneIndexPath);
		long startTime = System.currentTimeMillis();
		indexProfiler.profile();
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to profile lucene index: "
				+ ((endTime - startTime) / 1000) + " seconds.");

		TermVectorIndexBuilder termIndexBuilder = new TermVectorIndexBuilder(
				luceneIndexPath, indexProfiler.getTotalNumberOfTerms(),
				indexProfiler.getNumTermsInDoc(),
				indexProfiler.getTermGlobalFreq(),
				indexProfiler.getDocIDPathMap());
		startTime = System.currentTimeMillis();
		termIndexBuilder.buildTermVectorsAll();
		endTime = System.currentTimeMillis();
		System.out.println("Time taken to build complete term vector: "
				+ ((endTime - startTime) / 1000) + " seconds.");

		String[] searchResult = termIndexBuilder.getTopNSimilarTerms(
				"aandhi", 30);
		System.out.println("Search result follows*************************");
		for (String result : searchResult) {
			System.out.println(result);
		}
	}
}
