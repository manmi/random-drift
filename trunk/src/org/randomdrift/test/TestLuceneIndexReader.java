package org.randomdrift.test;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.randomdrift.LuceneIndexReader;

public class TestLuceneIndexReader {

	public static void main(String[] args) throws CorruptIndexException,
			IOException {

		String luceneIndexPath = "E:\\Corpora\\Toi\\index";

		LuceneIndexReader luceneReader = new LuceneIndexReader(luceneIndexPath);

		Document doc = luceneReader.getDocument(100);
		System.out.println(doc.get("path"));
		System.out.println(doc.toString());
		System.out.println("Number of documents in the index: "
				+ luceneReader.numDocs());

		System.out.println("*******************************************");

		for (int i = 0; i < 10; i++) {
			System.out.println("DocID: " + i + "\t Doc Path:\t"
					+ luceneReader.getDocument(i).get("path"));
		}
		luceneReader.close();
	}

}
