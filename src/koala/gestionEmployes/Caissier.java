package koala.gestionEmployes;

import koala.db.ConnectionManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionPlats.plat.PlatManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Caissier extends Employe {
    private int idCaissier;

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static List<Employe> loadTableToList() throws SQLException {
        return EmployeManager.loadTableToList("caissier");
    }

    @Override
    public int getIdSpecial() {
        return idCaissier;
    }

    @Override
    public void setIdSpecial(int idSpecial) {
        this.idCaissier = idSpecial;
    }

    public int getIdCaissier() {
        return idCaissier;
    }

    public void setIdCaissier(int idCaissier) {
        this.idCaissier = idCaissier;
    }

    public static HashMap<Integer, ArrayList<CommandeUnitaire>> commandeClients(LocalDateTime lastUpdate){
        List<Notification> notifications;
        try {
            notifications=NotificationManager.getNotificationCaissier(lastUpdate);
        } catch (Exception e) {
            notifications=null;
        }
        if (notifications==null) return null;
        HashMap<Integer,ArrayList<CommandeUnitaire>> commandes=new HashMap<>();

        for (Notification notification : notifications) {
            ArrayList<CommandeUnitaire> commandeUnitaires = new ArrayList<CommandeUnitaire>();
            String commandeComposantes;
            commandeComposantes = notification.getText();
            System.out.println("_______________________________________________________");
            System.out.println(commandeComposantes.trim().split("\n")[0] + "=====split /n====");
//            System.out.println(commandeComposantes.split("/n")[1] + "=====split / n====");
            System.out.println("_______________________________________________________");
            System.out.println(commandeComposantes.split(":").length + "=====split :====");
//            System.out.println("_______________________________________________________");
//            System.out.println(commandeComposantes.split("/n") + "=====split / n====");
            System.out.println("_______________________________________________________");

            for (String commandeText :
                    commandeComposantes.trim().split("\n")) {
                if (commandeText.length() > 2 ) {
                    String[] text_int = commandeText.trim().split(":");

                    try {

                        commandeUnitaires.add(new CommandeUnitaire(-1, PlatManager.getPlat(text_int[0].toString().trim()), Integer.parseInt(text_int[1])));
                    } catch (SQLException e) {
                    } catch (StringIndexOutOfBoundsException e)  {
                        System.out.println("lenth");
                    }
                }



            }

//            String nomPlat = "";
//            String nbrPlat = "";
//            boolean remplirPlat = true;
//            for (int i = 0; i < commandeComposantes.length(); i++) {
//                if (commandeComposantes.charAt(i) == '\n' &&  !(i==commandeComposantes.length()-1)) {
//                    try {
////                        nbrPlat=String.valueOf(commandeComposantes.charAt(i));
//                        System.out.println(nomPlat.toString().trim() + "-----------------------------");
//                        System.out.println(PlatManager.getPlat(nomPlat.toString().trim()) + "-----------------------------");
//                        commandeUnitaires.add(new CommandeUnitaire(-1, PlatManager.getPlat(nomPlat.toString().trim()), Integer.parseInt(nbrPlat)));
//                    } catch (SQLException e) {
//                    } catch (StringIndexOutOfBoundsException e)  {
//                        System.out.println("lenth");
//                    }
//                    nomPlat="";
//                    nbrPlat="";
//                    remplirPlat=true;
//                }else if(commandeComposantes.charAt(i)==':') {
//                    remplirPlat=false;
//                }else if (remplirPlat) nomPlat += commandeComposantes.charAt(i);
//                else nbrPlat += commandeComposantes.charAt(i);
//            }
//
            commandes.put(notification.getIdClient(),commandeUnitaires);
        }


        return commandes;
    }

    public static class Notification {


        private int idNotification;
//        private int idCaissier;
        private int idClient;

//        private boolean read;
//        private boolean ignore;
        private String text;
        private LocalDateTime time;

        public Notification() {
        }

        public Notification(int idNotification, int idCaissier, boolean read, boolean ignore, String text) {
            this.idNotification = idNotification;
//            this.idCaissier = idCaissier;
//            this.read = read;
//            this.ignore = ignore;
            this.text = text;

        }

        public Notification(int idNotification, int idClient, String text, LocalDateTime time) {
            this.idNotification = idNotification;
            this.idClient = idClient;
            this.text = text;
            this.time = time;
        }

        public int getIdNotification() {
            return idNotification;
        }

        public void setIdNotification(int idNotification) {
            this.idNotification = idNotification;
        }

        public int getIdClient() {
            return idClient;
        }

        public void setIdClient(int idClient) {
            this.idClient = idClient;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }

    public static class NotificationManager{
//        static String sql_loadNReadNIgnoreTableToList = "SELECT * FROM caissierNotification as cN WHERE cN.read = FALSE AND cN.ignore = FALSE ";
//        static String sql_loadNReadIgnoreTableToList = "SELECT * FROM caissierNotification as cN WHERE cN.read = FALSE";
//        static String sql_loadReadTableToList = "SELECT * FROM caissierNotification as cN WHERE cN.read = TRUE";


//        public static void loadObjectToRow(Magasinier.Notification notification) {
//            try (
//                    Statement stmt = connection.createStatement();
//            ){
//                stmt.executeUpdate("INSERT INTO magasiniernotification (idMagasinier, idIngredient, idCuisiner, magasiniernotification.read, magasiniernotification.ignore) VALUES(" + notification.idMagasinier + ", "+ notification.idIngredient + ", "+ notification.idCuisinier + ", "+ notification.isRead() + ", "+ notification.isIgnore() +")");
//
//
//
//            } catch(Exception ex){
//                ex.printStackTrace();
//            }
//
//        }

//        public static List<Notification> loadNReadNIgnoreTableToList() throws SQLException {
//            return loadTableToList(sql_loadNReadNIgnoreTableToList);
//        }
//
//        public static List<Notification> loadNReadIgnoreTableToList() throws SQLException {
//            return loadTableToList(sql_loadNReadIgnoreTableToList);
//        }
//
//        public static List<Notification> loadReadTableToList() throws SQLException {
//            return loadTableToList(sql_loadReadTableToList);
//        }

        private static Notification loadCurrentRowToObject(ResultSet rs) throws SQLException {
            Notification notification = new Notification();

            notification.setIdNotification(rs.getInt("idcommadeclient"));
            notification.setIdClient(rs.getInt("idClient"));
//            notification.setIdCaissier(rs.getInt("idCaissier"));
//            notification.setRead(rs.getBoolean("read"));
//            notification.setIgnore(rs.getBoolean("ignore"));
            notification.setText(rs.getString("content"));
            notification.setTime(rs.getTimestamp("time").toLocalDateTime());

            return notification;
        }

        public static List<Notification> loadTableToList(String sql) throws SQLException {
            List<Notification> list = new ArrayList<>();

            if (sql==null) sql="SELECT * FROM commadeclient";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                list.add(loadCurrentRowToObject(rs));
            }
            statement.close();
            rs.close();

            return list;
        }

        public static List<Notification> getNotificationCaissier(LocalDateTime lastUpdate) throws Exception{
            return loadTableToList("SELECT * from commadeclient WHERE  commadeclient.time > '" + Timestamp.valueOf(lastUpdate)+"'");
        }

        public static void remove(int idNotification) {
            try (
                    Statement stmt = connection.createStatement();
            ){
                stmt.executeUpdate("DELETE FROM commadeclient WHERE idcommadeclient = " + idNotification);
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

}
