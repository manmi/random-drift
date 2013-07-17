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
import org.apache.lucene.store.FSDirectory;

public class DocVectorIndexBuilder {

	private HashMap<String, RandomVector> documentVectors0; // Map of docPath to
															// document random
															// vectors
	private HashMap<String, RandomVector> documentVectors1;
	
	
	private IndexReader luceneIndexReader;
	private final String luceneIndexPath;
	private RandomVectorFactory vectorFactory;
	private Map<String, RandomVector> termVectors;

	private Map<Integer, String> docIDPathMap;

	public DocVectorIndexBuilder(String luceneIndexPath,
			Map<String, RandomVector> termVectors,
			Map<Integer, String> docIDPathMap) throws CorruptIndexException,
			IOException {

		this.luceneIndexPath = luceneIndexPath;
		this.documentVectors0 = new HashMap<String, RandomVector>();
		this.documentVectors1 = new HashMap<>();
		
		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));
		vectorFactory = new RandomVectorFactory(256, 0.2f);
		this.termVectors = termVectors;
		this.docIDPathMap = docIDPathMap;
	}

	public void buildDocVectorsAll() throws IOException {
		// Iterate through the termVectors and get the TermFreqVector
		Iterator<String> termIterator = termVectors.keySet().iterator();
		while (termIterator.hasNext()) {
			String termString = termIterator.next();
			RandomVector termRandomVector = termVectors.get(termString);
			Term term = new Term("contents", termString);
			TermDocs termDocs = luceneIndexReader.termDocs(term);
			//***************************IDF Weighting*******************
			float globalTermWeight = 0.01f +(float) Math.log10(luceneIndexReader.numDocs()/luceneIndexReader.docFreq(term));
			//***********************************************************

			while (termDocs.next()) {
				RandomVector documentRandomVector = vectorFactory
						.getZeroVector();
				documentRandomVector.add(termRandomVector);
							
				//****************IDF Based Weighting final**********************************
				//Local_Weight x Global_Weight for Documents
				//Local weight = termDocs.freq(); 
				float localTermWeight = termDocs.freq();
				//**************************************************

				documentRandomVector.scaleVector(localTermWeight*globalTermWeight);
				int docID = termDocs.doc();
				
				String docPath = docIDPathMap.get(docID);
				if (documentVectors0.containsKey(docPath)) {
					RandomVector tmpRV = documentVectors0.get(docPath);
					tmpRV.add(documentRandomVector);
					documentVectors0.put(docPath, tmpRV);
				} else {
					documentVectors0.put(docPath, documentRandomVector);
				}
			}
		}
		normalizeDocVectors();
	}
	
	public void buildHaarDocVectorsAll(){
		Iterator<String> docVectorIterator = documentVectors0.keySet().iterator();
		while(docVectorIterator.hasNext()){
			String docPath = docVectorIterator.next();
			RandomVector docRVCopy = vectorFactory.getCopy(documentVectors0.get(docPath));
//			RandomVector haarForward1 = vectorFactory.getHaarForward(docRVCopy, 256);
//			RandomVector haarForward2 = vectorFactory.getHaarForward(haarForward1, 128);
//			RandomVector haarForward3 = vectorFactory.getHaarForward(haarForward2, 64);
//			RandomVector haarForward4 = vectorFactory.getHaarForward(haarForward3, 32);
//			RandomVector haarForward5 = vectorFactory.getHaarForward(haarForward4, 16);
//			RandomVector haarForward6 = vectorFactory.getHaarForward(haarForward5, 8);
//			//RandomVector haarForward7 = vectorFactory.getHaarForward(haarForward6, 4);
//			
//			RandomVector enhancedLat4 = vectorFactory.enhanceLatencyHaar(haarForward4, 16);
//			RandomVector enhancedLat3 = vectorFactory.enhanceLatencyHaar(haarForward3, 32);
//			RandomVector enhancedLat2 = vectorFactory.enhanceLatencyHaar(enhancedLat3, 64);
//			RandomVector enhancedLat1 = vectorFactory.enhanceLatencyHaar(enhancedLat2, 128);
//	
//			//RandomVector enhancedFea7 = vectorFactory.enhanceFeatureHaar(haarForward7, 2);
//			RandomVector enhancedFea6 = vectorFactory.enhanceFeatureHaar(haarForward6, 4);
//			RandomVector enhancedFea5 = vectorFactory.enhanceFeatureHaar(enhancedFea6, 8);
//			RandomVector enhancedFea4 = vectorFactory.enhanceFeatureHaar(enhancedFea5, 16);
//			RandomVector enhancedFea3 = vectorFactory.enhanceFeatureHaar(enhancedFea4, 32);
//			RandomVector enhancedFea2 = vectorFactory.enhanceFeatureHaar(enhancedFea3, 64);
//			RandomVector enhancedFea1 = vectorFactory.enhanceFeatureHaar(enhancedFea2, 128);
			
//			enhancedLat1.add(enhancedFea1);
			
			
			documentVectors1.put(docPath, docRVCopy);
		}
	}
	
	public Map<String, RandomVector> getDocVectors(){
		return documentVectors0;
	}
	
	public void normalizeDocVectors(){
		Iterator<String> docVectorIterator = documentVectors0.keySet().iterator();
		while(docVectorIterator.hasNext()){
			String docPath = docVectorIterator.next();
			RandomVector rv = documentVectors0.get(docPath);
			rv.normalize();
			documentVectors0.put(docPath, rv);
		}
	}

	

	public float compareDocuments(String docPath1, String docPath2) {
		if (documentVectors0.containsKey(docPath1)
				&& documentVectors0.containsKey(docPath2)) {
			RandomVector doc1RV = documentVectors0.get(docPath1);
			RandomVector doc2RV = documentVectors0.get(docPath2);
			//return doc1RV.dotProduct(doc2RV);
			float dotProduct = doc1RV.dotProduct(doc2RV);
			return (dotProduct/(doc1RV.norm()*doc2RV.norm()));
		}
		return 0.0f;
	}
	
	public float compareDocumentsInHaar(String docPath1, String docPath2) {
		if (documentVectors1.containsKey(docPath1)
				&& documentVectors1.containsKey(docPath2)) {
			RandomVector doc1RV = documentVectors1.get(docPath1);
			RandomVector doc2RV = documentVectors1.get(docPath2);
			//return doc1RV.dotProduct(doc2RV);
			float dotProduct = doc1RV.dotProduct(doc2RV);
			return (dotProduct/(doc1RV.norm()*doc2RV.norm()));
		}
		return 0.0f;
	}

	

	public RandomVector getDocRandomVectorHaar(String docPath){
		return documentVectors1.get(docPath);
	}

	public RandomVector getDocRandomVector(String docPath) {
		return documentVectors0.get(docPath);
	}
}
