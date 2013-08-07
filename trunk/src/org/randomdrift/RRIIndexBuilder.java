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
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.FSDirectory;

public class RRIIndexBuilder {
	// The training cycles are fixed to three

	private HashMap<String, RandomVector> termVectors;
	private HashMap<String, RandomVector> documentVectors;

	private String luceneIndexPath;
	private IndexReader luceneIndexReader;
	private Map<Integer, String> docIDPathMap;
	private Map<String, Integer> termGlobalFreq;
	private RandomVectorFactory vectorFactory;

	public RRIIndexBuilder(String luceneIndexPath,
			Map<Integer, String> docIDPathMap,
			Map<String, Integer> termGlobalFreq) throws CorruptIndexException,
			IOException {

		this.luceneIndexPath = luceneIndexPath;
		this.docIDPathMap = docIDPathMap;
		this.termGlobalFreq = termGlobalFreq;

		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));
		this.vectorFactory = new RandomVectorFactory(256, 0.2f);
	}

	public void buildRRITermIndex(
			Map<String, RandomVector> fromTermVectors,
			Map<String, RandomVector> fromDocumentVectors)
			throws IOException {

		this.termVectors = new HashMap<>();

		Iterator<String> termIterator = termGlobalFreq.keySet().iterator();
		while (termIterator.hasNext()) {
			String termString = termIterator.next();
			Term term = new Term("contents", termString);
			TermDocs termDocs = luceneIndexReader.termDocs(term);
			while (termDocs.next()) {
				String docPath = docIDPathMap.get(termDocs.doc());
				RandomVector contextVector = fromDocumentVectors.get(docPath);
				RandomVector contextVectorCopy = vectorFactory
						.getCopy(contextVector);

				float localTermWeight = termDocs.freq();
				contextVectorCopy.scaleVector(localTermWeight);// for IDF
				if (termVectors.containsKey(termString)) {
					RandomVector termVector = termVectors.get(termString);
					termVector.add(contextVectorCopy);
					termVectors.put(termString, termVector);
				} else {
					termVectors.put(termString, contextVectorCopy);
				}
			}
		}
		normalizeTermVectors();
	}
	
	public void normalizeTermVectors(){	
		Iterator<String> termVectorIterator = termVectors.keySet().iterator();
		while(termVectorIterator.hasNext()){
			String term = termVectorIterator.next();
			RandomVector rv = termVectors.get(term);	
			rv.normalize();
			float test = rv.getRandomArray()[2];
			if(Float.isNaN(test))
				System.out.println("Problemo!!!!" + term);
			termVectors.put(term, rv);
		}
	}

	public void buildRRTDocumentIndex(
			Map<String, RandomVector> fromTermVectors)
			throws IOException {

		this.documentVectors = new HashMap<>();
		// Iterate through the termVectors and get the TermFreqVector
		Iterator<String> termIterator = fromTermVectors.keySet().iterator();
		while (termIterator.hasNext()) {
			String termString = termIterator.next();
			RandomVector termRandomVector = fromTermVectors.get(termString);
			Term term = new Term("contents", termString);
			TermDocs termDocs = luceneIndexReader.termDocs(term);
			// ***************************IDF Weighting*******************
			float globalTermWeight = 0.01f + (float) Math
					.log10(luceneIndexReader.numDocs()
							/ luceneIndexReader.docFreq(term));
			// ***********************************************************

			while (termDocs.next()) {
				RandomVector documentRandomVector = vectorFactory
						.getZeroVector();
				documentRandomVector.add(termRandomVector);

				// ****************IDF Based Weighting
				// final**********************************
				// Local_Weight x Global_Weight for Documents
				// Local weight = termDocs.freq();
				float localTermWeight = termDocs.freq();
				// **************************************************

				documentRandomVector.scaleVector(localTermWeight
						* globalTermWeight);
				int docID = termDocs.doc();

				String docPath = docIDPathMap.get(docID);
				if (documentVectors.containsKey(docPath)) {
					RandomVector tmpRV = documentVectors.get(docPath);
					tmpRV.add(documentRandomVector);
					documentVectors.put(docPath, tmpRV);
				} else {
					documentVectors.put(docPath, documentRandomVector);
				}
			}
		}
		normalizeDocVectors();
	}
	


	public void normalizeDocVectors() {
		Iterator<String> docVectorIterator = documentVectors.keySet()
				.iterator();
		while (docVectorIterator.hasNext()) {
			String docPath = docVectorIterator.next();
			RandomVector rv = documentVectors.get(docPath);
			rv.normalize();
			documentVectors.put(docPath, rv);
		}
	}
	
	public void indexDocument(String docPath, TermFreqVector termFreqVector) throws IOException{
		
		RandomVector docVector = vectorFactory.getZeroVector();
		String[] terms = termFreqVector.getTerms();
		int[] termFreq = termFreqVector.getTermFrequencies();
		
		for(int i = 0; i < terms.length; i++){
			if(termVectors.containsKey(terms[i])){
				docVector.add(termVectors.get(terms[i]));
				float globalTermWeight = 0.01f + (float) Math
						.log10(luceneIndexReader.numDocs()
								/ luceneIndexReader.docFreq(new Term("contents", terms[i])));
				docVector.scaleVector(globalTermWeight * termFreq[i]);
			}
		}
		docVector.normalize();
		documentVectors.put(docPath, docVector);
	}
	
	public boolean stringIsAlpha(String term){
		char[] chars = term.toCharArray();
		for(char c:chars){
			if(!Character.isAlphabetic(c))
				return false;
		}
		return true;
	}
	
	public float compareDocuments(String docPath1, String docPath2) {
		if (documentVectors.containsKey(docPath1)
				&& documentVectors.containsKey(docPath2)) {
			RandomVector doc1RV = documentVectors.get(docPath1);
			RandomVector doc2RV = documentVectors.get(docPath2);
			//return doc1RV.dotProduct(doc2RV);
			float dotProduct = doc1RV.dotProduct(doc2RV);
			return (dotProduct/(doc1RV.norm()*doc2RV.norm()));
		}
		return 0.0f;
	}

	public Map<String, RandomVector> getTermVectors() {
		return termVectors;
	}

	public Map<String, RandomVector> getDocVectors() {
		return documentVectors;
	}
	
	public RandomVector getDocRandomVector(String docPath){
		return documentVectors.get(docPath);
	}
}
