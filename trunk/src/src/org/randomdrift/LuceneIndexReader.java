package org.randomdrift;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexReader {

	public final String luceneIndexPath;
	public IndexReader indexReader;

	public LuceneIndexReader(String luceneIndexPath)
			throws CorruptIndexException, IOException {
		this.luceneIndexPath = luceneIndexPath;
		this.indexReader = IndexReader.open(FSDirectory.open(new File(
				luceneIndexPath)));
	}

	public String getLuceneIndexPath() {
		return luceneIndexPath;
	}

	public void close() throws IOException {
		this.indexReader.close();
	}

	// Lookup a document in the index from path

	public Document getDocument(int docId) throws CorruptIndexException,
			IOException {
		return indexReader.document(docId);
	}

	public int numDocs() {
		return indexReader.numDocs();
	}

	public List<Document> getAllDocuments() {
		return null;
	}

}
