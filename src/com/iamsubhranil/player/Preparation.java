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
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashSet;

public class Preparation {

    private static final String directoryToStore = "testindex";
    private static final Tika tika = new Tika();
    private static final Mp3Parser parser = new Mp3Parser();
    private static final HashSet<String> metaSet = new HashSet<>();

    public static void main(String[] args) {
        //   store();
        load();
    }

    private static void load() {
        System.out.println("Starting load..");
        try {
            Directory index = FSDirectory.open(new File(directoryToStore).toPath());
            System.out.println("Reading index..");
            IndexReader indexReader = DirectoryReader.open(index);
            int docs = indexReader.numDocs();
            if (metaSet.size() == 0) {
                System.out.println("Populating metaset..");
                while (docs > 0) {
                    Document loaded = indexReader.document(docs - 1);
                    loaded.getFields().forEach(indexableField -> metaSet.add(indexableField.name()));
                    docs--;
                }
            }
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            StandardAnalyzer analyzer = new StandardAnalyzer();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter search text : ");
            String query = reader.readLine();

            System.out.println("Searching..");
            metaSet.forEach(s -> {
                System.out.println("In " + s + "..");
                QueryParser queryParser = new QueryParser(s, analyzer);
                try {
                    TopDocs topDocs = indexSearcher.search(queryParser.parse(query), 100);
                    System.out.println("hits : " + topDocs.totalHits);
                    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                        Document doc = indexReader.document(scoreDoc.doc);
                        System.out.println(doc.get(s));
                        System.out.println("Path : " + doc.get("path"));
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            });
            indexReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void store() {
        String directoryToSearch = "/media/iamsubhranil/Entertainment/Songs/English Songs/Enrique Iglesias";

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
            w.close();
            index.close();
            System.out.println("Done..");
        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }
    }

    private static void indexFolder(File f, IndexWriter indexWriter) throws IOException, TikaException, SAXException {
        String[] files = f.list();
        for (String file : files) {
            File fl = new File(f + "/" + file);
            if (fl.isDirectory()) {
                indexFolder(fl, indexWriter);
            } else {
                if (tika.detect(fl).startsWith("audio")) {
                    BodyContentHandler handler = new BodyContentHandler();
                    Metadata metadata = new Metadata();
                    ParseContext context = new ParseContext();
                    parser.parse(new FileInputStream(fl), handler, metadata, context);

                    Document document = new Document();
                    Field field = new StringField("path", fl.getAbsolutePath(), Field.Store.YES);
                    document.add(field);
                    for (String name : metadata.names()) {
                        String bak = name;
                        name = name.contains(":") ? name.split(":")[1] : name;
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        metaSet.add(name);
                        TextField metaField = new TextField(name, metadata.get(bak), Field.Store.YES);
                        document.add(metaField);
                    }
                    indexWriter.addDocument(document);
                }
            }
        }
    }
}
