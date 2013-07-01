package org.randomdrift;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexProfiler {

	private IndexReader luceneIndexReader;
	private Map<Integer, Integer> numTermsInDoc;
	private Map<String, Integer> termGlobalFreq;
	private Map<Integer, String> docIDPathMap;
	private String luceneIndexPath;
	private int totalNumberOfTerms;

	public LuceneIndexProfiler(String luceneIndexPath)
			throws CorruptIndexException, IOException {
		this.luceneIndexPath = luceneIndexPath;
		this.numTermsInDoc = new HashMap<>();
		this.termGlobalFreq = new HashMap<>();
		this.docIDPathMap = new HashMap<>();
		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));
	}

	public void profile() throws IOException {
		TermEnum termEnum = luceneIndexReader.terms();
		setTotalNumberOfTerms(0);

		while (termEnum.next()) {
			Term term = termEnum.term();
			if (term.field().equals("contents") && term.text().length() > 3 && stringIsAlpha(term.text())) {
				TermDocs termDocs = luceneIndexReader.termDocs(term);
				int intTermGlobalFreq = 0;
				while (termDocs.next()) {
					int docID = termDocs.doc();
					Document document = luceneIndexReader.document(docID);
					String documentPath = document.get("path");
					if (!docIDPathMap.containsKey(docID)) {
						docIDPathMap.put(docID, documentPath);
					}
					if (numTermsInDoc.containsKey(docID))
						numTermsInDoc.put(docID, numTermsInDoc.get(docID)
								+ termDocs.freq());
					else
						numTermsInDoc.put(docID, termDocs.freq());
					intTermGlobalFreq += termDocs.freq();
				}
				termGlobalFreq.put(term.text(), intTermGlobalFreq);
				setTotalNumberOfTerms(getTotalNumberOfTerms() + intTermGlobalFreq);
			}
		}
	}
	
	public boolean stringIsAlpha(String term){
		char[] chars = term.toCharArray();
		for(char c:chars){
			if(!Character.isAlphabetic(c))
				return false;
		}
		return true;
	}

	public Map<Integer, Integer> getNumTermsInDoc() {
		return numTermsInDoc;
	}

	public Map<String, Integer> getTermGlobalFreq() {
		return termGlobalFreq;
	}

	public String getLuceneIndexPath() {
		return luceneIndexPath;
	}

	public Map<Integer, String> getDocIDPathMap() {
		return docIDPathMap;
	}

	public int getTotalNumberOfTerms() {
		return totalNumberOfTerms;
	}

	private void setTotalNumberOfTerms(int totalNumberOfTerms) {
		this.totalNumberOfTerms = totalNumberOfTerms;
	}
}
