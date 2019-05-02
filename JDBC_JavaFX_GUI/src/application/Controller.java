package application;

import application.Model.Album;
import application.Model.Artist;
import application.Model.Datasource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

public class Controller {

    // == fields ==

    @FXML
    private TableView tableView;

    // == methods ==

    @FXML
    public void listArtists() {
        ObservableList<Artist> artists = FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
        tableView.setItems(artists);
    }

    @FXML
    public void listAlbumsForArtist() throws SQLException {
        final Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
        if (artist == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("NO ARTIST SELECTED");
            alert.setContentText("PLEASE SELECT AN ARTIST");
            alert.showAndWait();
            return;
        }

        ObservableList<Album> albums = FXCollections.observableArrayList(Datasource.getInstance().queryAlbumsByArtist_ID(artist.getId()));
        tableView.setItems(albums);
    }
}