package org.randomdrift;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class TermVectorIndexBuilder {

	// private HashMap<Term, RandomVector> termVectors;
	private HashMap<String, RandomVector> termVectors;
	private HashMap<String, RandomVector> contextVectors;
	private IndexReader luceneIndexReader;
	private final String luceneIndexPath;
	private RandomVectorFactory vectorFactory;
	private Map<Integer, Integer> numTermsInDoc;
	private Map<String, Integer> termGlobalFreq;
	private Map<Integer, String> docIDPathMap;
	private int totalNumberOfTerms;

	public TermVectorIndexBuilder(String luceneIndexPath, int totalNumberOfTerms,
			Map<Integer, Integer> numTermsInDoc,
			Map<String, Integer> termGlobalFreq,
			Map<Integer, String> docIDPathMap) throws CorruptIndexException,
			IOException {
		this.luceneIndexPath = luceneIndexPath;
		this.termVectors = new HashMap<>();
		this.contextVectors = new HashMap<>();
		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));
		this.numTermsInDoc = numTermsInDoc;
		this.termGlobalFreq = termGlobalFreq;
		this.docIDPathMap = docIDPathMap;
		this.totalNumberOfTerms = totalNumberOfTerms;

		vectorFactory = new RandomVectorFactory(100, 0.2f);
	}

	public Map<String, RandomVector> getTermVectors() {
		return termVectors;
	}

	public void buildTermVectorsAll() throws CorruptIndexException, IOException {

		Iterator<Integer> docIDIterator = docIDPathMap.keySet().iterator();
		while (docIDIterator.hasNext()) {
			int docID = docIDIterator.next();
			RandomVector contextVector = vectorFactory.getOneVector();
			contextVectors.put(docIDPathMap.get(docID), contextVector);
		}

		TermEnum termEnum = luceneIndexReader.terms();

		// while (termEnum.next()) {
		// Term term = termEnum.term();
		// if (term.field() == "contents" && term.text().length() > 3) {
		// // System.out.println("Term : Field: " + term.field() +
		// // " Text: "
		// // + term.text());
		// TermDocs termDocs = luceneIndexReader.termDocs(term);
		// while (termDocs.next()) {
		// int docID = termDocs.doc();
		// if (termVectors.containsKey(term)) {
		// termVectors.put(term.text(), termVectors.get(term)
		// .addOneVector(contextVectors.get(docID)));
		// } else {
		// termVectors.put(term.text(), contextVectors.get(docID));
		// }
		// }
		// } else {
		// continue;
		// }
		// }

		Iterator<String> termIterator = termGlobalFreq.keySet().iterator();
		while (termIterator.hasNext()) {
			String termString = termIterator.next();
			Term term = new Term("contents", termString);
			TermDocs termDocs = luceneIndexReader.termDocs(term);
			float scaleFactor1 = (float) Math.log10(totalNumberOfTerms
					/ termGlobalFreq.get(termString));
			while (termDocs.next()) {
				float scaleFactor2 = (float) Math.log10((numTermsInDoc
						.get(termDocs.doc())) / termDocs.freq());
				String docPath = docIDPathMap.get(termDocs.doc());
				RandomVector contextVector = contextVectors.get(docPath);
				contextVector.scaleVector(scaleFactor1 + scaleFactor2);
				contextVector.normalize();
				if (termVectors.containsKey(termString)) {
					RandomVector termVector = termVectors.get(termString);
					termVector.add(contextVector);
					termVector.normalize();
					termVectors.put(termString, termVector);
				} else {
					termVectors.put(termString, contextVector);
				}
			}
		}
	}

	public String[] getTopNSimilarTerms(String term, int n) {
		float[] similarity = new float[n];
		String[] terms = new String[n];

		RandomVector queryTerm = termVectors.get(term);

		if (queryTerm != null) {
			for (String targetTerm : termVectors.keySet()) {
				float dotProduct = queryTerm.dotProduct(termVectors
						.get(targetTerm));

				for (int i = 0; i < n; i++) {
					if (dotProduct > similarity[i]) {
						float tmp = similarity[i];
						similarity[i] = dotProduct;
						dotProduct = tmp;
						String tmpTerm = terms[i];
						terms[i] = targetTerm;
						targetTerm = tmpTerm;
						//continue;
					}
				}
			}
			return terms;
		} else {
			return null;
		}
	}
}
