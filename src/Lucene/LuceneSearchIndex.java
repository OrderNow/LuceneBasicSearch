package Lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

public class LuceneSearchIndex {

	private final static String indexDir = "data/index1";

	StandardAnalyzer analyzer;
	Directory index;

	public LuceneSearchIndex() throws IOException {

		analyzer = new StandardAnalyzer(Version.LUCENE_44);
		index = MMapDirectory.open(new File(indexDir));
	}

	public ArrayList<Document> search(String querystr, int k)
			throws IOException, ParseException {
		Query q = new QueryParser(Version.LUCENE_44, "title", analyzer)
				.parse(querystr);
		int hitsPerPage = k;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		ArrayList<Document> docArr = new ArrayList<Document>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			docArr.add(d);
		}
		return docArr;
	}

	/**
	 * Doc[title,]
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private static void indexAllProducts(String dir) throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);

		File d1 = new File(indexDir);
		for (File f : d1.listFiles()) {
			f.delete();
		}

		Directory index = new MMapDirectory(new File(indexDir));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		File d = new File(dir);
		for (File f : d.listFiles()) {
			System.out.println("Processing " + f.getName());
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = br.readLine()) != null) {
				addDoc(w, line, f.getName());
			}
			br.close();
		}
		w.close();
		index.close();
	}

	private static void addDoc(IndexWriter w, String title, String subCat)
			throws IOException {
		Document doc = new Document();
		/**
		 * add here if we want to add something else in the document
		 */
		doc.add(new TextField("title", title, Field.Store.YES));
		w.addDocument(doc);
	}

	public static void main(String[] args) throws ParseException, IOException {
		// input directory
		LuceneSearchIndex.indexAllProducts("data/train1");
	}

}
