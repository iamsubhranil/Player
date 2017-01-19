/*
    Created By : iamsubhranil
    Date : 19/1/17
    Time : 1:22 PM
    Package : com.iamsubhranil.player
    Project : Player
*/
package com.iamsubhranil.player;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class Preparation {

    private static final String directoryToStore = "testindex";

    public static void main(String[] args) {
        //    store();
        load();
    }

    private static void load() {
        try {
            Directory index = FSDirectory.open(new File(directoryToStore).toPath());
            IndexReader indexReader = DirectoryReader.open(index);
            int docs = indexReader.numDocs();
            while (docs > 0) {
                Document loaded = indexReader.document(docs - 1);
                loaded.getFields().forEach(indexableField -> {
                    System.out.println(indexableField.name() + "\t" + indexableField.stringValue());
                });
                docs--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void store() {
        String directoryToSearch = "/media/iamsubhranil/Entertainment/Songs";

        System.out.println("Starting index..");

        StandardAnalyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try {
            Directory index = FSDirectory.open(new File(directoryToStore).toPath());
            IndexWriter w = new IndexWriter(index, config);
            System.out.println("Analyzing directory..");
            indexFolder(new File(directoryToSearch), w);
            System.out.println("Writing out..");
            w.commit();
            System.out.println("Closing index..");
            index.close();
            System.out.println("Done..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void indexFolder(File f, IndexWriter indexWriter) throws IOException {
        String[] files = f.list();
        for (String file : files) {
            File fl = new File(f + "/" + file);
            if (fl.isDirectory()) {
                indexFolder(fl, indexWriter);
            } else {
                Document document = new Document();
                Field field = new StringField("path", fl.getAbsolutePath(), Field.Store.YES);
                document.add(field);
                indexWriter.addDocument(document);
            }
        }
    }
}
