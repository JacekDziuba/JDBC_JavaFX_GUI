package application.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Album {

    // == fields ==

    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleIntegerProperty artistId;

    // == constructors ==

    public Album() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.artistId = new SimpleIntegerProperty();
    }

    // == getters and setters ==

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getArtistId() {
        return artistId.get();
    }

    public SimpleIntegerProperty artistIdProperty() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId.set(artistId);
    }
}
