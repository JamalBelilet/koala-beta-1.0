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
public class Restore implements Runnable{
    private String path;
    ProgressIndicator progressIndicator = new ProgressIndicator(-1);
    HBox openDatabaseAsFileHBox;
    StackPane rootContainer;

    public Restore(String path) {
        this.path = path;
    }
    @Override
    public void run() {
        String[] executeCmd = new String[]{"mysql", "--user=" + ConnectionManager.USERNAME, "-e", "source " + path};
        Process runtimeProcess;
        try {
            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                System.out.println("Backup restored successfully");
            } else {
                System.out.println("Could not restore the backup ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                openDatabaseAsFileHBox.getChildren().remove(progressIndicator);
                BackupUIGenerator.initBackupNotification(rootContainer, French.ADMIN_DONNEES_BACKUP_SUCCESS);
            }
        });
    }
    public void start(HBox openDatabaseAsFileHBox ,StackPane rootContainer) {
        progressIndicator.setMaxHeight(50);

        this.openDatabaseAsFileHBox = openDatabaseAsFileHBox;
        this.rootContainer = rootContainer;
        progressIndicator.setStyle("-fx-accent: #ffa000");
        new Thread(this).start();
        this.openDatabaseAsFileHBox.getChildren().add(progressIndicator);

    }
}