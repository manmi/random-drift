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
	private HashMap<String, RandomVector> termVectors0;
	private HashMap<String, RandomVector> termVectors1;
	private HashMap<String, RandomVector> termVectors2;
	private HashMap<String, RandomVector> termVectors3;
	private HashMap<String, RandomVector> termVectors4;
	private HashMap<String, RandomVector> termVectors5;
	private HashMap<String, RandomVector> termVectors6;
	private HashMap<String, RandomVector> contextVectors;
	private IndexReader luceneIndexReader;
	private final String luceneIndexPath;
	private RandomVectorFactory vectorFactory;
	private Map<Integer, Integer> numTermsInDoc;
	private Map<String, Integer> termGlobalFreq;
	private Map<Integer, String> docIDPathMap;
	private int totalNumberOfTerms;
	private int totalNumberOfDocs;

	public TermVectorIndexBuilder(String luceneIndexPath, int totalNumberOfTerms,
			Map<Integer, Integer> numTermsInDoc,
			Map<String, Integer> termGlobalFreq,
			Map<Integer, String> docIDPathMap) throws CorruptIndexException,
			IOException {
		this.luceneIndexPath = luceneIndexPath;
		this.termVectors0 = new HashMap<>();
		this.termVectors1 = new HashMap<>();
		this.termVectors2 = new HashMap<>();
		this.termVectors3 = new HashMap<>();
		this.termVectors4 = new HashMap<>();
		this.termVectors5 = new HashMap<>();
		this.termVectors6 = new HashMap<>();
		this.contextVectors = new HashMap<>();
		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));
		this.numTermsInDoc = numTermsInDoc;
		this.termGlobalFreq = termGlobalFreq;
		this.docIDPathMap = docIDPathMap;
		this.totalNumberOfTerms = totalNumberOfTerms;
		this.totalNumberOfDocs = docIDPathMap.size();

		vectorFactory = new RandomVectorFactory(64, 0.2f);
	}
	
	public void buildTermHaarVectors(){
		Iterator<String> termIterator = termVectors0.keySet().iterator();
		while(termIterator.hasNext()){
			String term = termIterator.next();
			RandomVector haarVector1 = (termVectors0.get(term)).getHaar1D();
			RandomVector haarVector2 = haarVector1.getHaar1D();
			RandomVector haarVector3 = haarVector2.getHaar1D();
			RandomVector haarVector4 = haarVector3.getHaar1D();
			RandomVector haarVector5 = haarVector4.getHaar1D();
			RandomVector haarVector6 = haarVector5.getHaar1D();
			termVectors1.put(term, haarVector1);
			termVectors2.put(term, haarVector2);
			termVectors3.put(term, haarVector3);
			termVectors4.put(term, haarVector4);
			termVectors5.put(term, haarVector5);
			termVectors6.put(term, haarVector6);
		}
	}

	public Map<String, RandomVector> getTermVectors() {
		return termVectors0;
	}
	
	public Map<String, RandomVector> getTermVectors(int order){
		switch (order) {
		case 0:
			return termVectors0;
		case 1:
			return termVectors1;
		case 2:
			return termVectors2;
		case 3:
			return termVectors3;
		case 4:
			return termVectors4;
		case 5:
			return termVectors5;
		case 6:
			return termVectors6;
		default:
			return null;
		}
	}
	


	public void buildTermVectorsAll() throws CorruptIndexException, IOException {

		Iterator<Integer> docIDIterator = docIDPathMap.keySet().iterator();
		while (docIDIterator.hasNext()) {
			int docID = docIDIterator.next();
			RandomVector contextVector = vectorFactory.getOneVector();
			contextVectors.put(docIDPathMap.get(docID), contextVector);
		}

		Iterator<String> termIterator = termGlobalFreq.keySet().iterator();
		while (termIterator.hasNext()) {
			String termString = termIterator.next();
			Term term = new Term("contents", termString);
			TermDocs termDocs = luceneIndexReader.termDocs(term);
			int totalNumberOfTermsI = termGlobalFreq.get(termString);
			int smalln = luceneIndexReader.docFreq(term);
			float scaleFactor = (float)((Math.log(1+totalNumberOfDocs/smalln))/(Math.log(2)));//IDF
			//float scaleFactor1 = (float) Math.log10(totalNumberOfTerms
			//		/ termGlobalFreq.get(termString));
			while (termDocs.next()) {
				//float scaleFactor2 = (float) Math.log10((numTermsInDoc
				//		.get(termDocs.doc())) / termDocs.freq());
				//float scaleFactor = Math.log(totalNumberOfDocs/)
				String docPath = docIDPathMap.get(termDocs.doc());
				RandomVector contextVector = contextVectors.get(docPath);
				RandomVector contextVectorCopy = vectorFactory.getCopy(contextVector);
				
				contextVectorCopy.scaleVector(scaleFactor);//for IDF
				//contextVectorCopy.scaleVector(scaleFactor1 + scaleFactor2);
				//contextVectorCopy.normalize();

				if (termVectors0.containsKey(termString)) {
					RandomVector termVector = termVectors0.get(termString);
					termVector.add(contextVectorCopy);
					//termVector.normalize();
					termVectors0.put(termString, termVector);
				} else {
					termVectors0.put(termString, contextVectorCopy);
				}
			}
		}
		normalizeTermVectors0();
	}
	
	public void normalizeTermVectors0(){	
		Iterator<String> termVectorIterator = termVectors0.keySet().iterator();
		while(termVectorIterator.hasNext()){
			String term = termVectorIterator.next();
			RandomVector rv = termVectors0.get(term);
			rv.normalize();
			termVectors0.put(term, rv);
		}
	}
	
	

	public String[] getTopNSimilarTerms(String term, int n) {
		float[] similarity = new float[n];
		String[] terms = new String[n];

		RandomVector queryTerm = termVectors0.get(term);

		if (queryTerm != null) {
			for (String targetTerm : termVectors0.keySet()) {
				float dotProduct = queryTerm.dotProduct(termVectors0
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
