package koala.gui.cuisinier.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import koala.administration.Compte;
import koala.db.ConnectionManager;
import koala.gestionEmployes.Cuisiner;
import koala.gestionEmployes.EmployeManager;
import koala.gui.login.controller.CompteUIGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    @FXML
    private HBox compteInfo;
    @FXML
    public VBox queue;
    @FXML
    private Button proposer;
    @FXML
    private Button signaler;
    @FXML
    private StackPane rootContainer;
    @FXML
    private StackPane queueStack;
    @FXML
    private BorderPane container;
    @FXML
    private StackPane enCoursStack;
    @FXML
    public FlowPane enCours;


    public static Connection connection = ConnectionManager.getInstance().getConnection();

    TimerTask timerTask;
    static Timer timer ;
    @FXML
    public void initialize() {

        CommandeUIGenerator.lastUpdate = LocalDateTime.MIN;

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    CommandeUIGenerator.syncCommandes(queue, enCours);

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 500, 5000);

        FormUIGenerator.setProposerNdSignalerForms(signaler, proposer, null, null, rootContainer);

        try {
            System.out.println(Compte.getIdCompte());
            Cuisiner cuisiner = (Cuisiner) EmployeManager.getEmloye(Compte.getIdCompte(), "cuisiner");
            CompteUIGenerator.soutSignIn(compteInfo, cuisiner, "cuisiner");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public static void killAll() {


        if (timer != null ) {
            timer.cancel();
            timer.purge();
        }


    }


}
