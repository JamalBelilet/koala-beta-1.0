package koala.db;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import koala.gui.admin.controller.BackupUIGenerator;
import strings.French;

/**
 * Created by bjama on 4/23/2017.
 */
public class Backup implements Runnable{
    private String path;
    ProgressIndicator progressIndicator = new ProgressIndicator(-1);
    HBox saveDatabaseAsFileHBox;
    StackPane rootContainer;

    public Backup(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        Process p;

        try {

            Runtime runtime = Runtime.getRuntime();
            p =runtime.exec("mysqldump -v -v -v --host=localhost --user="+ ConnectionManager.USERNAME+ " --port=3306 --protocol=tcp --force --allow-keywords --compress --add-drop-table --result-file=" + path + " --databases " + ConnectionManager.DBName);

            int processComplete = p.waitFor();
            if (processComplete==0) {
                System.out.println("Backup Created Succuss");
            }else {
                System.out.println("Can't Create backup");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                saveDatabaseAsFileHBox.getChildren().remove(progressIndicator);
                BackupUIGenerator.initBackupNotification(rootContainer, French.ADMIN_DONNEES_BACKUP_SAVE_SUCCESS);


            }
        });

    }

    public void start(HBox saveDatabaseAsFileHBox, StackPane rootContainer) {
        progressIndicator.setMaxHeight(50);

        this.saveDatabaseAsFileHBox = saveDatabaseAsFileHBox;
        this.rootContainer = rootContainer;
        progressIndicator.setStyle("-fx-accent: #ffa000");
        new Thread(this).start();
        this.saveDatabaseAsFileHBox.getChildren().add(progressIndicator);

    }
}