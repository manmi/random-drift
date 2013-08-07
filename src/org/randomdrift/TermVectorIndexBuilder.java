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
	
	private HashMap<String, RandomVector> contextVectors;
	private IndexReader luceneIndexReader;
	private final String luceneIndexPath;
	private RandomVectorFactory vectorFactory;
	private Map<Integer, Integer> numTermsInDoc;
	private Map<String, Integer> termGlobalFreq;
	private Map<Integer, String> docIDPathMap;
	private HaarTransformer haarTransformer;
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
		this.contextVectors = new HashMap<>();
		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));
		this.numTermsInDoc = numTermsInDoc;
		this.termGlobalFreq = termGlobalFreq;
		this.docIDPathMap = docIDPathMap;
		this.totalNumberOfTerms = totalNumberOfTerms;
		this.totalNumberOfDocs = docIDPathMap.size();
		this.haarTransformer = new HaarTransformer();

		vectorFactory = new RandomVectorFactory(2048, 0.005f);
	}
	
	public void buildTermHaarVectors(){
		Iterator<String> termIterator = termVectors0.keySet().iterator();
		while(termIterator.hasNext()){
			String term = termIterator.next();
			RandomVector termRV = vectorFactory.getCopy(termVectors0.get(term));
			RandomVector haarForwardRV_2 = vectorFactory.getHaarForward(termRV, 2048);
			RandomVector haarForwardRV_1 = vectorFactory.getHaarForward(haarForwardRV_2, 1024);
			RandomVector haarForwardRV0 = vectorFactory.getHaarForward(haarForwardRV_1, 512);
			RandomVector haarForwardRV1 = vectorFactory.getHaarForward(haarForwardRV0, 256);
			RandomVector haarForwardRV2 = vectorFactory.getHaarForward(haarForwardRV1, 128);
			RandomVector haarForwardRV3 = vectorFactory.getHaarForward(haarForwardRV2, 64);
			RandomVector haarForwardRV4	= vectorFactory.getHaarForward(haarForwardRV3, 32);
			RandomVector haarForwardRV5 = vectorFactory.getHaarForward(haarForwardRV4, 16);
			RandomVector haarForwardRV6 = vectorFactory.getHaarForward(haarForwardRV5,8);
			//RandomVector haarForwardRV7 = vectorFactory.getHaarForward(haarForwardRV6, 4);

			//RandomVector lat6 = vectorFactory.enhanceLatencyHaar(haarForwardRV7, 2);
			RandomVector lat5 = vectorFactory.enhanceLatencyHaar(haarForwardRV6, 4);
			RandomVector lat4 = vectorFactory.enhanceLatencyHaar(lat5, 8);
			RandomVector lat3 = vectorFactory.enhanceLatencyHaar(lat4, 16);
			RandomVector lat2 = vectorFactory.enhanceLatencyHaar(lat3, 32);
			RandomVector lat1 = vectorFactory.enhanceLatencyHaar(lat2, 64);
			RandomVector lat0 = vectorFactory.enhanceLatencyHaar(lat1, 128);
			RandomVector lat = vectorFactory.enhanceLatencyHaar(lat0, 256);
			RandomVector lat_1 = vectorFactory.enhanceLatencyHaar(lat, 512);
			RandomVector lat_2 = vectorFactory.enhanceLatencyHaar(lat_1, 1024);
			
			termRV.add(lat_2);	
			termRV.normalize();
		
			termVectors1.put(term, termRV);
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
//		case 2:
//			return termVectors2;
//		case 3:
//			return termVectors3;
//		case 4:
//			return termVectors4;
//		case 5:
//			return termVectors5;
//		case 6:
//			return termVectors6;
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
					
			while (termDocs.next()) {
				String docPath = docIDPathMap.get(termDocs.doc());
				RandomVector contextVector = contextVectors.get(docPath);
				RandomVector contextVectorCopy = vectorFactory.getCopy(contextVector);
				
				float localTermWeight = termDocs.freq();
				contextVectorCopy.scaleVector(localTermWeight);//for IDF
				if (termVectors0.containsKey(termString)) {
					RandomVector termVector = termVectors0.get(termString);
					termVector.add(contextVectorCopy);
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
			float test = rv.getRandomArray()[2];
			if(Float.isNaN(test))
				System.out.println("Problemo!!!!" + term);
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
