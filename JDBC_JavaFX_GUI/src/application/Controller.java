package application;

import application.Model.Album;
import application.Model.Artist;
import application.Model.Datasource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class Controller {

    // == fields ==

    @FXML
    private TableView tableView;

    // == methods ==

    @FXML
    public void listArtists() {
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        tableView.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void listAlbumsForArtist() {
        final Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
        if (artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }
        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(Datasource.getInstance().queryAlbumsByArtist_ID(artist.getId()));
            }
        };
        tableView.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

}

class GetAllArtistsTask extends Task {

    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
    }
}