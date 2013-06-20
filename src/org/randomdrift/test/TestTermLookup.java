package org.randomdrift.test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;


public class TestTermLookup {
	
	public static void main(String[] args) throws CorruptIndexException, IOException{
		String luceneIndexPath = "E:\\Corpora\\Toi\\index";
		IndexReader luceneReader = IndexReader.open(FSDirectory.open(new File(luceneIndexPath)));
		
		TermDocs termDocs = luceneReader.termDocs(new Term("contents", "water"));
		if(termDocs != null){
			System.out.println("So far so good. Term is indexed");
		}
		
		while(termDocs.next()){
			System.out.println("Document: " + termDocs.doc() + " | Frequency: " + termDocs.freq());
		}
		
		TermEnum termEnum = luceneReader.terms();
		long totalNumberOfTerms = 0;
		while(termEnum.next()){
			Term term = termEnum.term();
			if (term.field() == "contents") {
				TermDocs termDocs2 = luceneReader.termDocs(term);
				while (termDocs2.next())
					totalNumberOfTerms = totalNumberOfTerms + termDocs2.freq();
			}
		}
		System.out.println("Total Number of Terms: " + totalNumberOfTerms);
	}

}
