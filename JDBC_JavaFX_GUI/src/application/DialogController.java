package application;

import application.Model.Artist;
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
        return artist;
    }
}
