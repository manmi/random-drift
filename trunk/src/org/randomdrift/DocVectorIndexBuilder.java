package org.randomdrift;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.FSDirectory;

public class DocVectorIndexBuilder {

	private HashMap<String, RandomVector> documentVectors; // Map of docPath to
															// document random
															// vectors
	private HashMap<String, RandomVector> documentHaarVectors;
	private HashMap<String, RandomVector> documentHaarVectors2;
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
		this.documentVectors = new HashMap<String, RandomVector>();
		this.documentHaarVectors = new HashMap<>();
		this.documentHaarVectors2 = new HashMap<>();
		this.luceneIndexReader = IndexReader.open(FSDirectory.open(new File(
				this.luceneIndexPath)));
		vectorFactory = new RandomVectorFactory(64, 0.2f);
		this.termVectors = termVectors;
		this.docIDPathMap = docIDPathMap;
	}

	public void buildDoctVectorsAll() throws IOException {
		// Iterate through the termVectors and get the TermFreqVector
		Iterator<String> termIterator = termVectors.keySet().iterator();
		while (termIterator.hasNext()) {
			String termString = termIterator.next();
			RandomVector termRandomVector = termVectors.get(termString);
			TermDocs termDocs = luceneIndexReader.termDocs(new Term("contents",
					termString));

			while (termDocs.next()) {
				RandomVector documentRandomVector = vectorFactory
						.getZeroVector();
				documentRandomVector.add(termRandomVector);
				float scaleFactor = (float) (1 + Math.log10(1 + Math
						.log10(termDocs.freq())));
				
//				float scaleFactor = (float)(1+Math.log10(termDocs.freq()));
				documentRandomVector.scaleVector(scaleFactor);
				documentRandomVector.normalize();
				int docID = termDocs.doc();
				String docPath = docIDPathMap.get(docID);
				if (documentVectors.containsKey(docPath)) {
					RandomVector tmpRV = documentVectors.get(docPath);
					tmpRV.add(documentRandomVector);
					tmpRV.normalize();
					documentVectors.put(docPath, tmpRV);
				} else {

					documentVectors.put(docPath, documentRandomVector);
				}
			}
		}
	}
	
	public void buildHaar1DIndex(){
		Iterator<String> docIterator = documentVectors.keySet().iterator();
		while(docIterator.hasNext()){
			String docPath = docIterator.next();
			RandomVector haarVector = (documentVectors.get(docPath)).getHaar1D();
			RandomVector haarVector2 = haarVector.getHaar1D();
			documentHaarVectors.put(docPath, haarVector);
			documentHaarVectors2.put(docPath, haarVector2);
		}
	}

	public float compareDocuments(String docPath1, String docPath2) {
		if (documentVectors.containsKey(docPath1)
				&& documentVectors.containsKey(docPath2)) {
			RandomVector doc1RV = documentVectors.get(docPath1);
			RandomVector doc2RV = documentVectors.get(docPath2);
			return doc1RV.dotProduct(doc2RV);
		}
		return 0.0f;
	}
	
	public float compareDocumentsInHaar(String docPath1, String docPath2){
		if (documentHaarVectors.containsKey(docPath1)
				&& documentHaarVectors.containsKey(docPath2)) {
			RandomVector doc1RV = documentHaarVectors.get(docPath1);
			RandomVector doc2RV = documentHaarVectors.get(docPath2);
			return doc1RV.dotProduct(doc2RV);
		}
		return 0.0f;
	}
	
	public float compareDocumentsInHaar2(String docPath1, String docPath2){
		if (documentHaarVectors2.containsKey(docPath1)
				&& documentHaarVectors2.containsKey(docPath2)) {
			RandomVector doc1RV = documentHaarVectors2.get(docPath1);
			RandomVector doc2RV = documentHaarVectors2.get(docPath2);
			return doc1RV.dotProduct(doc2RV);
		}
		return 0.0f;
	}

	public RandomVector getDocRandomVector(String docPath) {
		return documentVectors.get(docPath);
	}
}
