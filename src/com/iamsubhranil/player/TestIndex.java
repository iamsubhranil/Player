/*
    Created By : iamsubhranil
    Date : 24/1/17
    Time : 3:50 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class TestIndex {


    private static final HashSet<String> metaSet = new HashSet<>();
    private static final HashSet<String> albumSet = new HashSet<>();
    private static final HashSet<String> artistSet = new HashSet<>();
    private static final String directoryToStore = "testindex";

    private static void load() {
        System.out.println("Starting load..");
        try {
            Directory loadedIndex = FSDirectory.open(new File(directoryToStore).toPath());
            System.out.println("Reading index..");
            IndexReader indexReader = DirectoryReader.open(loadedIndex);
            int docs = indexReader.numDocs();
            if (metaSet.size() == 0) {
                System.out.println("Populating metaset..");
                while (docs > 0) {
                    //    System.out.println("\n");
                    Document loaded = indexReader.document(docs - 1);
                    loaded.getFields().forEach(indexableField ->
                            {
                                metaSet.add(indexableField.name());
                            }
                    );
                    System.out.println("AlbumHash : " + loaded.get("AlbumHash"));
                    System.out.println("ArtistHash : " + loaded.get("ArtistHash"));
                    albumSet.add(loaded.get("Album"));
                    artistSet.add(loaded.get("Artist"));
                    docs--;
                }
            }
            System.out.println("Total " + artistSet.size() + " artists and " + albumSet.size() + " albums in " + indexReader.numDocs() + " songs..");
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            StandardAnalyzer analyzer = new StandardAnalyzer();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter search text : ");
            String query = reader.readLine();

            System.out.println("Searching..");
            metaSet.forEach(s -> {
                //  System.out.println("In " + s + "..");
                //    QueryParser queryParser = new QueryParser(s, analyzer);
                TermQuery termQuery = new TermQuery(new Term(s, query));
                try {
                    TopDocs topDocs = indexSearcher.search(termQuery, 100000);
                    System.out.println("hits : " + topDocs.totalHits);
                    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                        Document doc = indexReader.document(scoreDoc.doc);
                        System.out.println("Path : " + doc.get("path"));
                        System.out.println(s + " : " + doc.get(s) + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            indexReader.close();
            loadedIndex.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
