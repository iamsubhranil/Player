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

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

public class Preparation {

    private static final String directoryToStore = "testindex";
    private static final Tika tika = new Tika();
    private static final Mp3Parser parser = new Mp3Parser();
    private static final HashSet<String> metaSet = new HashSet<>();
    private static final HashSet<String> albumSet = new HashSet<>();
    private static final HashSet<String> artistSet = new HashSet<>();

    public static void main(String[] args) {
        //    store();
        load();
    }

    public static IndexReader getIndex() throws IOException {
        return DirectoryReader.open(FSDirectory.open(new File(directoryToStore).toPath()));
    }

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
                                //       System.out.println(indexableField.name()+"\t"+loaded.get(indexableField.name()));
                            }
                    );
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
                QueryParser queryParser = new QueryParser(s, analyzer);
                try {
                    TopDocs topDocs = indexSearcher.search(queryParser.parse(query), 100);
                    System.out.println("hits : " + topDocs.totalHits);
                    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                        Document doc = indexReader.document(scoreDoc.doc);
                        System.out.println("Path : " + doc.get("path"));
                        System.out.println(s + " : " + doc.get(s) + "\n");
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            });
            indexReader.close();
            loadedIndex.close();
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
                    TextField field = new TextField("path", fl.getAbsolutePath(), Field.Store.YES);
                    document.add(field);
                    String totalMeta = "";
                    for (String name : metadata.names()) {
                        String val = metadata.get(name);
                        name = name.contains(":") ? name.split(":")[1] : name;
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        metaSet.add(name);
                        if (val.equals("")) {
                            val = "Unknown";
                        }
                        totalMeta += name + "\t" + val + "\n";
                        TextField metaField = new TextField(name, val, Field.Store.YES);
                        document.add(metaField);
                    }
                    StringField hashField = new StringField("Hash", generateSHAString(totalMeta), Field.Store.YES);
                    document.add(hashField);
                    indexWriter.addDocument(document);
                }
            }
        }
    }

    private static String generateSHAString(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return DatatypeConverter.printHexBinary(hash); // make it printable
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
