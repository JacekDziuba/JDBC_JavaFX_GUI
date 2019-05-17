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
                Datasource.getInstance().addArtist(artist);
                tableView.getSelectionModel().select(artist);
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

    @FXML
    public void editArtistDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Artist");
        dialog.setHeaderText("Use this dialog to edit Artist");

        FXMLLoader fxmlLoader = new FXMLLoader();

        try {
            fxmlLoader.setLocation(getClass().getResource("artistDialog.fxml"));

            dialog.getDialogPane().setContent(fxmlLoader.load());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            DialogController editController = fxmlLoader.getController();
            Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
            editController.editArtist(artist);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                editController.updateArtist(artist);
                tableView.getSelectionModel().select(artist);
                Datasource.getInstance().updateArtistName(artist);
            }

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void deleteArtist() throws SQLException{
        Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
        if (artist == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No artist selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select artist to be deleted");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete artist");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete " + artist.getName() + " ?");

        Optional<ButtonType> results = alert.showAndWait();
        if (results.isPresent() && results.get() == ButtonType.OK) {
            Datasource.getInstance().deleteArtistFromDB(artist.getId());
            Datasource.getInstance().deleteArtist(artist);
        }
    }
}