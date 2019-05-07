package application;

import application.Model.Album;
import application.Model.Artist;
import application.Model.Datasource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class Controller {

    // == fields ==

    @FXML
    private TableView tableView;

    @FXML
    private BorderPane mainBorderPane;

    // == methods ==

    @FXML
    public void initialize() {
        tableView.setItems(Datasource.getInstance().getArtists());
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

    @FXML
    public void addNewArtistDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new artist to the database");
        dialog.setHeaderText("Use this dialog to add new artist");

        FXMLLoader fxmlLoader = new FXMLLoader();

        try {
            fxmlLoader.setLocation(getClass().getResource("artistDialog.fxml"));
            dialog.getDialogPane().setContent(fxmlLoader.load());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DialogController controller = fxmlLoader.getController();
                Artist artist = controller.processResults();
                tableView.getSelectionModel().select(artist);

                Datasource.getInstance().insertNewArtist(artist);
            }

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.getMessage();
            return;
        } catch (SQLException s) {
            System.out.println("Couldn't load the artist");
            s.getMessage();
            return;
        }
    }
}