package org.randomdrift;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class TermVectorIndexBuilder {

	// private HashMap<Term, RandomVector> termVectors;
	private HashMap<String, RandomVector> termVectors;
	private HashMap<Integer, RandomVector> contextVectors;
	private IndexReader luceneIndexReader;
	private final String luceneIndexPath;
	private RandomVectorFactory vectorFactory;

	public TermVectorIndexBuilder(String luceneIndexPath)
			throws CorruptIndexException, IOException {
		this.luceneIndexPath = luceneIndexPath;
		this.termVectors = new HashMap<String, RandomVector>();
		this.contextVectors = new HashMap<Integer, RandomVector>();
		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));

		vectorFactory = new RandomVectorFactory(64, 0.2f);
	}

	public void buildTermVectorsAll() throws CorruptIndexException, IOException {

		for (int i = 0; i < luceneIndexReader.numDocs(); i++) {
			RandomVector contextVector = vectorFactory.getOneVector();
			contextVectors.put(i, contextVector);
		}

		TermEnum termEnum = luceneIndexReader.terms();

		while (termEnum.next()) {
			Term term = termEnum.term();
			if (term.field() == "contents") {
				// System.out.println("Term : Field: " + term.field() +
				// " Text: "
				// + term.text());
				TermDocs termDocs = luceneIndexReader.termDocs(term);
				while (termDocs.next()) {
					int docID = termDocs.doc();
					if (termVectors.containsKey(term)) {
						termVectors.put(term.text(), termVectors.get(term)
								.addOneVector(contextVectors.get(docID)));
					} else {
						termVectors.put(term.text(), contextVectors.get(docID));
					}
				}
			} else {
				continue;
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
						continue;
					}
				}
			}
			return terms;
		} else {
			return null;
		}
	}
}
