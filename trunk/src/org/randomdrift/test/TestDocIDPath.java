package org.randomdrift.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class TestDocIDPath {
	
	public static void main(String[] args) throws CorruptIndexException, IOException{
		
		String luceneIndexPath = "E:\\Corpora\\Toi\\index";
		IndexReader luceneReader = IndexReader.open(FSDirectory.open(new File(luceneIndexPath)));
		
		String[] docsToBeClassified = new String[]{"out\\19288983.txt",
				"out\\19289044.txt",
				"out\\19289051.txt",
				"out\\19289084.txt",
				"out\\19289100.txt",
				"out\\19289137.txt",
				"out\\19289166.txt",
				"out\\19289243.txt",
				"out\\19289308.txt",
				"out\\19289323.txt",
				"out\\19289332.txt",
				"out\\19289378.txt",
				"out\\19289380.txt",
				"out\\19289385.txt",
				"out\\19289391.txt",
				"out\\19289400.txt",
				"out\\19289431.txt",
				"out\\19289445.txt",
				"out\\19289502.txt",
				"out\\19289532.txt",
				"out\\19289593.txt",
				"out\\19289622.txt",
				"out\\19289624.txt",
				"out\\19289647.txt",
				"out\\19289721.txt",
				"out\\19289742.txt",
				"out\\19289744.txt",
				"out\\19289763.txt",
				"out\\19289781.txt",
				"out\\19289806.txt",
				"out\\19289833.txt",
				"out\\19290055.txt",
				"out\\19290090.txt",
				"out\\19290093.txt",
				"out\\19290104.txt",
				"out\\19290117.txt",
				"out\\19290118.txt",
				"out\\19290157.txt",
				"out\\19290163.txt",
				"out\\19290176.txt",
				"out\\19290200.txt",
				"out\\19290202.txt",
				"out\\19313200.txt",
				"out\\19313227.txt",
				"out\\19313245.txt",
				"out\\19313260.txt",
				"out\\19313267.txt",
				"out\\19313272.txt",
				"out\\19313300.txt",
				"out\\19313320.txt",
				"out\\19313321.txt",
				"out\\19313334.txt",
				"out\\19313336.txt",
				"out\\19313349.txt",
				"out\\19313360.txt",
				"out\\19313370.txt",
				"out\\19313379.txt",
				"out\\19313381.txt",
				"out\\19313397.txt",
				"out\\19313398.txt",
				"out\\19313408.txt",
				"out\\19313411.txt",
				"out\\19313414.txt",
				"out\\19313423.txt",
				"out\\19313426.txt",
				"out\\19313429.txt",
				"out\\19313438.txt",
				"out\\19313439.txt",
				"out\\19313454.txt",
				"out\\19313456.txt",
				"out\\19313461.txt",
				"out\\19313469.txt",
				"out\\19313484.txt",
				"out\\19313492.txt",
				"out\\19313494.txt",
				"out\\19313497.txt",
				"out\\19313499.txt",
				"out\\19313502.txt",
				"out\\19313507.txt",
				"out\\19313508.txt",
				"out\\19313511.txt",
				"out\\19313512.txt",
				"out\\19313515.txt",
				"out\\19313517.txt",
				"out\\19313519.txt",
				"out\\19313523.txt",
				"out\\19313526.txt",
				"out\\19313529.txt",
				"out\\19313536.txt",
				"out\\19313545.txt",
				"out\\19313548.txt",
				"out\\19313552.txt",
				"out\\19313557.txt",
				"out\\19313559.txt",
				"out\\19313569.txt",
				"out\\19313576.txt",
				"out\\19313591.txt",
				"out\\19313597.txt",
				"out\\19313601.txt",
			"out\\19313646.txt"};
		
		int numDocs = luceneReader.numDocs();
		
		HashMap<String, Integer> pathDocIDMap = new HashMap<>();
		for(int i = 0; i < docsToBeClassified.length; i++){
			pathDocIDMap.put(docsToBeClassified[i], null);
		}
		for(int i = 0; i < numDocs; i++){
			Document doc = luceneReader.document(i);
			if(pathDocIDMap.containsKey(doc.get("path"))){
				pathDocIDMap.put(doc.get("path"), i);
			}
			//System.out.println("DocID : " + i + " Document Path: " + doc.get("path"));
		}
		
//		Iterator<String> pathIter = pathDocIDMap.keySet().iterator();
//		while(pathIter.hasNext()){
//			String path = pathIter.next();
//			System.out.println("Path: " + path + " Document Id: " + pathDocIDMap.get(path));
//		}
		
		for(int i = 0; i < docsToBeClassified.length; i++){
			System.out.println(docsToBeClassified[i] + ", " +pathDocIDMap.get(docsToBeClassified[i]));
		}
	}
}
