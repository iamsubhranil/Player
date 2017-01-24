/*
    Created By : iamsubhranil
    Date : 24/1/17
    Time : 3:12 PM
    Package : com.iamsubhranil.player.db
    Project : Player
*/
package com.iamsubhranil.player.db;

import java.io.*;
import java.util.ArrayList;

public class SearchScope {

    private static final File fileToStore = new File("search.scopes");
    private static final ArrayList<File> scopes = new ArrayList<>();

    static boolean loadSearchScopes() {
        scopes.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileToStore));
            String scope;
            while ((scope = reader.readLine()) != null) {
                File sf = new File(scope);
                if (sf.isDirectory())
                    scopes.add(sf);
            }
            reader.close();
            return scopes.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

    public static ArrayList<File> getSearchScopes() {
        return scopes;
    }

    public static boolean addScope(String s) {
        File newScope = new File(s);
        for (File scope : scopes) {
            if (newScope.getAbsolutePath().contains(scope.getAbsolutePath()))
                return false;
        }
        scopes.add(newScope);
        return true;
    }

    public static boolean storeSearchScopes() {
        if (fileToStore.exists())
            fileToStore.delete();
        try {
            PrintWriter writer = new PrintWriter(fileToStore);
            scopes.forEach(scope -> writer.println(scope.getAbsolutePath()));
            writer.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
