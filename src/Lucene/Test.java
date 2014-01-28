package Lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

public class Test {

	public static void main(String[] args) throws IOException, ParseException {
		LuceneSearchIndex obj = new LuceneSearchIndex();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		float matching_factor = 0.7f;
		System.out.println("Search for dish");
		while ((line = br.readLine()) != null) {
			line = line + "~" + matching_factor;
			ArrayList<String> list = find(line, obj, 5);
			System.out.println(list);
		}
	}

	public static ArrayList<String> find(String q, LuceneSearchIndex obj, int k)
			throws IOException, ParseException {
		ArrayList<Document> list = obj.search(q, k);
		ArrayList<String> matches = new ArrayList<String>();
		int i = 0;
		for (Document d : list) {
			matches.add(d.get("title"));
		}
		return matches;
	}
}
