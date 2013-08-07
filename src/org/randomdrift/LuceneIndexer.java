package org.randomdrift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneIndexer {
	
	Directory luceneDirectory;
	Analyzer analyzer;
	IndexWriterConfig writerConfig;
	IndexWriter indexWriter;
	
	public LuceneIndexer(String indexPath) throws IOException{
		
		this.luceneDirectory = FSDirectory.open(new File(indexPath));
		this.analyzer = new StandardAnalyzer(Version.LUCENE_31);
		this.writerConfig = new IndexWriterConfig(Version.LUCENE_31, analyzer);
		this.writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.indexWriter = new IndexWriter(luceneDirectory, writerConfig);
		
	}
	
	public void indexDocDirectory(String dirPath) throws CorruptIndexException, IOException{
		File dirFile = new File(dirPath);
		if(dirFile.isDirectory()){
			String[] files = dirFile.list();
			if(files != null){
				for(int i = 0; i < files.length; i++){
					indexDoc(files[i]);
					System.out.println("Index file: " + files[i]);
				}
			}
		}
	}
	
	
	public void indexDoc(String path) throws CorruptIndexException, IOException{
		File docFile = new File(path);
		
		if(docFile.canRead()){
			if(docFile.isDirectory())
				indexDocDirectory(path);
			else{
				FileInputStream docFIStream = new FileInputStream(docFile);
				Document document = new Document();
				Field pathField = new Field("path", docFile.getPath(), Store.YES, Index.NOT_ANALYZED_NO_NORMS);
				document.add(pathField);
				Field contentField = new Field("contents",new BufferedReader(new InputStreamReader(docFIStream, "UTF-8")), TermVector.YES);
				document.add(contentField);
				indexWriter.updateDocument(new Term("path", docFile.getPath()), document);
				//to close or not to close, that is the question
				//indexWriter.close();
			}
		}
	}
	
	public int getDocIDFromPath(String docPath) throws CorruptIndexException, IOException{
		IndexReader indexReader = IndexReader.open(luceneDirectory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		Query query = new WildcardQuery(new Term("path", docPath));
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query, BooleanClause.Occur.SHOULD);
		
		TopDocs topDocs = indexSearcher.search(booleanQuery, 5);
		for(int i = 0; i < topDocs.scoreDocs.length; i++){
			int docID = topDocs.scoreDocs[i].doc;
			if(docPath.equals((indexReader.document(docID).get("path"))))
				return docID;
		}
		return -1;
	}
	
	public TermFreqVector getTermFreqVectorFromPath(String docPath) throws IOException{
		IndexReader indexReader = IndexReader.open(luceneDirectory);
		int docID = getDocIDFromPath(docPath);
		if(docID == -1)
			return null;
		else{
			return indexReader.getTermFreqVector(docID, "contents");
		}
	}
	
	public void closeIndexWriter() throws CorruptIndexException, IOException{
		indexWriter.close();
	}
}
