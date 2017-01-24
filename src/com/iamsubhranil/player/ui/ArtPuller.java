/*
    Created By : iamsubhranil
    Date : 22/1/17
    Time : 3:29 PM
    Package : com.iamsubhranil.player.ui
    Project : Player
*/
package com.iamsubhranil.player.ui;

import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.PaginatedResult;
import javafx.scene.image.Image;
//import radams.gracenote.webapi.GracenoteWebAPI;

public class ArtPuller {

    private static final String lastFMAPIKey = "4bddef13a7f66d97f103b01145e9212f";
    private static final String lastFMAPIKey2 = "d482b6bc8b52269c9cbd2f1a3700c195";
    private static final String lastFMSharedSecret = "6e61bcb4a0e4e956ddf6157dbbfe5bcb";
    private static final String lastFMUsername = "iamsubhranil";
    private static final String lastFMApplicationName = "Player";
    private static final String defaultImage = ArtPuller.class.getResource("noart.png").toExternalForm();
    //  private static GracenoteWebAPI gracenoteWebAPI = null;

    static {
        //  String token = Authenticator.getToken(lastFMAPIKey);
        // Session session = Authenticator.getSession(token,lastFMAPIKey,lastFMSharedSecret);
    }

   /* public static void setGracenoteWebAPI(GracenoteWebAPI gwapi) {
        gracenoteWebAPI = gwapi;
    }

    public static String pullArtistArt(String artistName) {
        String URL = ArtPuller.class.getResource("noart.png").toExternalForm();
        try {
            URL = gracenoteWebAPI.searchArtist(artistName).getURL("artist_image_url");
        } catch (Exception gne) {
        }
        return URL;
    }*/

    public static void main(String[] args) {
        System.out.println(Artist.getInfo("habijabi", lastFMAPIKey2).getImageURL(ImageSize.SMALL));
    }

    public static String getImageURLForArtist(String artistName) {
        String ret = defaultImage;

        try {
            ret = Artist.getInfo(artistName, lastFMAPIKey2).getImageURL(ImageSize.LARGE);
        } catch (Exception e) {
        }
        if (ret.equals(""))
            ret = defaultImage;
        return ret;
    }

    public static String getDefaultImage() {
        return defaultImage;
    }

    public static Image getImageForArtist(String artistName) {
        Image grabbedImage = new Image(defaultImage);
        PaginatedResult<de.umass.lastfm.Image> images = Artist.getImages(artistName, lastFMAPIKey);
        if (!images.isEmpty()) {
            de.umass.lastfm.Image im = images.getPageResults().iterator().next();
            grabbedImage = new Image(im.getUrl());
        }
        return grabbedImage;
    }

/*    public static String pullAlbumArt(String artist, String album) {
        String URL = ArtPuller.class.getResource("noart.png").toExternalForm();
        try {
            URL = gracenoteWebAPI.searchAlbum(artist, album).getURL("album_coverart");
        } catch (Exception e) {
        }
        return URL;
    }
*/
}
