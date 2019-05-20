package application.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {

    // == constants ==

    public static final String DB_NAME = "music.db";

    public static final String CONNECTION_STRING = "jdbc:sqlite:/Users/martakepa/Desktop/" + DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    // SELECT albums.name FROM albums INNER JOIN artists ON albums.artist = artists._id WHERE artists.name =
    public static final String QUERY_ALBUMS_BY_ARTIST_START = "SELECT " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + " FROM " + TABLE_ALBUMS + " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID + " WHERE " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + " = \"";
    // ORDER BY albums.name COLLATE NOCASE
    public static final String QUERY_ALBUMS_BY_ARTIST_SORT = " ORDER BY " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";

    public static final String INSERT_ARTIST = "INSERT INTO " + TABLE_ARTISTS + '(' + COLUMN_ARTIST_NAME + ") VALUES(?)";
    public static final String INSERT_ALBUMS = "INSERT INTO " + TABLE_ALBUMS + '(' + COLUMN_ALBUM_NAME + ", " + COLUMN_ALBUM_ARTIST + ") VALUES(?, ?)";

    public static final String QUERY_ARTIST = "SELECT " + COLUMN_ARTIST_ID + " FROM " + TABLE_ARTISTS + " WHERE " + COLUMN_ARTIST_NAME + " = ?";
    public static final String QUERY_ALBUM = "SELECT " + COLUMN_ALBUM_ID + " FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_NAME + " = ?";

    // SELECT * FROM albums WHERE artist = ? ORDER BY name COLLATE NOCASE
    public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT * FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_ARTIST + " = ? ORDER BY " + COLUMN_ALBUM_NAME + " COLLATE NOCASE";

    // UPDATE artists SET artist.name = ? WHERE artist._id = ?
    public static final String UPDATE_ARTIST_NAME = "UPDATE " + TABLE_ARTISTS + " SET " + COLUMN_ARTIST_NAME + " = ? WHERE " + COLUMN_ARTIST_ID + " = ?";

    // DELETE FROM artists WHERE _id = ?
    public static final String DELETE_ARTIST = "DELETE FROM " + TABLE_ARTISTS + " WHERE " + COLUMN_ARTIST_ID + " = ?";

    // == fields ==

    private Connection conn;

    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertIntoAlbums;

    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;

    private PreparedStatement queryAlbumsByArtistID;
    private PreparedStatement updateArtistName;
    private PreparedStatement deleteArtist;

    private ObservableList<Artist> artistObservableList;

    // == private constructor ==

    public static Datasource instance = new Datasource();

    public static Datasource getInstance() {
        return instance;
    }

    private Datasource() {

    }

    // == getter ==

    public ObservableList<Artist> getArtists() {
        return artistObservableList;
    }

    // == methods ==

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            insertIntoArtists = conn.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS);
            insertIntoAlbums = conn.prepareStatement(INSERT_ALBUMS, Statement.RETURN_GENERATED_KEYS);
            queryArtist = conn.prepareStatement(QUERY_ARTIST);
            queryAlbum = conn.prepareStatement(QUERY_ALBUM);
            queryAlbumsByArtistID = conn.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            updateArtistName = conn.prepareStatement(UPDATE_ARTIST_NAME);
            deleteArtist = conn.prepareStatement(DELETE_ARTIST);

            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (insertIntoArtists != null) {
                insertIntoArtists.close();
            }
            if (insertIntoAlbums != null) {
                insertIntoAlbums.close();
            }
            if (queryArtist != null) {
                queryArtist.close();
            }
            if (queryAlbum != null) {
                queryAlbum.close();
            }
            if (queryAlbumsByArtistID != null) {
                queryAlbumsByArtistID.close();
            }
            if (updateArtistName != null) {
                updateArtistName.close();
            }
            if (deleteArtist != null) {
                deleteArtist.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public void queryArtists(int sortOrder) {
        artistObservableList = FXCollections.observableArrayList();
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        if (sortOrder != ORDER_BY_NONE) {
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");
            if (sortOrder == ORDER_BY_DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())) {

            while (results.next()) {
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artistObservableList.add(artist);
            }

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    public void insertNewArtist(@NotNull Artist artist) throws SQLException {
        queryArtist.setString(1, artist.getName());
        ResultSet results = queryArtist.executeQuery();
        if (results.next()) {
            throw new SQLException("Couldn't load the artist");
        } else {
            // Insert the artist
            insertIntoArtists.setString(1, artist.getName());
            int affectedRows = insertIntoArtists.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert artist!");
            }
        }
        System.out.println("New artist added");
    }

    private int insertAlbum(String name, int artistId) throws SQLException {

        queryAlbum.setString(1, name);
        ResultSet results = queryAlbum.executeQuery();
        if (results.next()) {
            return results.getInt(1);
        } else {
            // Insert the album
            insertIntoAlbums.setString(1, name);
            insertIntoAlbums.setInt(2, artistId);
            int affectedRows = insertIntoAlbums.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert album!");
            }

            ResultSet generatedKeys = insertIntoAlbums.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Couldn't get _id for album");
            }
        }
    }

    public List<Album> queryAlbumsByArtist_ID(int artistId) throws SQLException {
        queryAlbumsByArtistID.setInt(1, artistId);
        ResultSet results = queryAlbumsByArtistID.executeQuery();

        List<Album> albumList = new ArrayList<>();
        while (results.next()) {
            Album album = new Album();
            album.setId(results.getInt(1));
            album.setName(results.getString(2));
            album.setArtistId(artistId);
            albumList.add(album);
        }
        return albumList;
    }

    public void addArtist(Artist item) {
        artistObservableList.add(item);
    }

    public void updateArtistName(Artist artist) throws SQLException{
        updateArtistName.setString(1, artist.getName());
        updateArtistName.setInt(2, artist.getId());
        int affectedRows = updateArtistName.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update artist!");
        }
    }

    public void deleteArtist(Artist item) { artistObservableList.remove(item); }

    public void deleteArtistFromDB(int artistId) throws SQLException{
        deleteArtist.setInt(1, artistId);
        int affectedRows = deleteArtist.executeUpdate();

        if (affectedRows != 1) {
            throw  new SQLException("Couldn't delete the artist");
        }
        System.out.println("Artist deleted");
    }
}