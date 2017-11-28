package koala.gui.admin.controller;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by bjama on 4/21/2017.
 */
public class CompteProperty {
    private SimpleStringProperty id;
    private SimpleStringProperty nom;
    private SimpleStringProperty login;
    private SimpleStringProperty password;
    private SimpleStringProperty pin;
    private SimpleStringProperty pinEnabled;



    public CompteProperty(String id, String nom, String login, String password, String pin, String pinEnabled) {
        this.id = new SimpleStringProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.login = new SimpleStringProperty(login);
        this.password = new SimpleStringProperty(password);
        this.pin =  new SimpleStringProperty(pin);;
        this.pinEnabled = new SimpleStringProperty(pinEnabled);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getNom() {
        return nom.get();
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getLogin() {
        return login.get();
    }

    public SimpleStringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getPin() {
        return pin.get();
    }

    public SimpleStringProperty pinProperty() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin.set(pin);
    }

    public String getPinEnabled() {
        return pinEnabled.get();
    }

    public SimpleStringProperty pinEnabledProperty() {
        return pinEnabled;
    }

    public void setPinEnabled(String pinEnabled) {
        this.pinEnabled.set(pinEnabled);
    }
}
