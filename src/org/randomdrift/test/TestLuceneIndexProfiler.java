package org.randomdrift.test;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.randomdrift.LuceneIndexProfiler;

public class TestLuceneIndexProfiler {
	
	public static void main(String[] args) throws CorruptIndexException, IOException{
		String luceneIndexPath = "E:\\Corpora\\Toi\\index";
		LuceneIndexProfiler indexProfiler = new LuceneIndexProfiler(luceneIndexPath);
		long startTime = System.currentTimeMillis();
		indexProfiler.profile();
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to profile lucene index: "
				+ ((endTime - startTime) / 1000) + " seconds.");
		
		System.out.println("Number of documents in the index: " + indexProfiler.getDocIDPathMap().keySet().size());
		System.out.println("Number of documents in the index: " + indexProfiler.getNumTermsInDoc().keySet().size());
		System.out.println("Number of terms in the index: " + indexProfiler.getTermGlobalFreq().keySet().size());
	}
}
