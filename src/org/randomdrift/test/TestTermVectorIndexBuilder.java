package org.randomdrift.test;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.randomdrift.TermVectorIndexBuilder;

public class TestTermVectorIndexBuilder {

	public static void main(String[] args) throws CorruptIndexException,
			IOException {
		String luceneIndexPath = "E:\\Corpora\\Toi\\index";

		TermVectorIndexBuilder termIndexBuilder = new TermVectorIndexBuilder(
				luceneIndexPath);
		long startTime = System.currentTimeMillis();
		termIndexBuilder.buildTermVectorsAll();
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to build complete term vector: "
				+ ((endTime - startTime) / 1000) + " seconds.");

		String[] searchResult = termIndexBuilder.getTopNSimilarTerms(
				"waterborne", 30);
		System.out.println("Search result follows*************************");
		for (String result : searchResult) {
			System.out.println(result);
		}
	}
}
