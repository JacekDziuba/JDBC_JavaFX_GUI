package application;

import application.Model.Artist;
import application.Model.Datasource;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class DialogController {

    // == fields ==

    @FXML
    private TextField artistNameField;

    // == methods ==

    public Artist processResults() throws SQLException {
        String firstName = artistNameField.getText().trim();
        Artist artist = new Artist();
        artist.setName(firstName);

        Datasource.getInstance().insertNewArtist(artist);

        return artist;
    }

    public void editArtist(Artist artist) {
        artistNameField.setText(artist.getName());
    }

    public void updateArtist(Artist artist) {
        artist.setName(artistNameField.getText());

    }

}
