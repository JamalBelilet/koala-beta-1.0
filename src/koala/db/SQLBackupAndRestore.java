package koala.db;

public class SQLBackupAndRestore {
    private String path;

    public SQLBackupAndRestore(String path) {
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public class restore implements Runnable {
        Process process;


        public void start() {
            new Thread(this).start();

        }

        @Override
        public void run() {

            String[] restoreCmd = new String[]{"C:\\Program Files\\MySQL\\MySQL Workbench 6.3 CE\\mysql.exe ", "--user=" + ConnectionManager.USERNAME, "--password=" + ConnectionManager.PASSWORD, "-e", "source " + path};
            Process process;
            try {
                process = Runtime.getRuntime().exec(restoreCmd);
                int processComplete = process.waitFor();

                if (processComplete==0) {
                    System.out.println("Restored Succuss");
                }else{
                    System.out.println("Can't Restored");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




//
//    public void start() {
//        new Thread(this).start();
//
//    }
//    @Override
//    public void run() {
//        Process p;
//
//        try {
//
//
////            ProcessBuilder builder = new ProcessBuilder("mysqldump -u " + ConnectionManager.USERNAME  + " --databases " + ConnectionManager.DBName + " > " + path);
////            p = builder.start();
//
//            Runtime runtime = Runtime.getRuntime();
////            p=runtime.exec("C:\\xampp\\mysql\\bin\\mysqldump.exe -"+ ConnectionManager.USERNAME +" -"+ ConnectionManager.PASSWORD +" --add-drop-database -B "+ ConnectionManager.DBName +" -r"+path);
//
////            System.out.println("mysqldump -u " + ConnectionManager.USERNAME  + " --databases " + ConnectionManager.DBName + " > jamaljamljfldsajffdasfdsafasd.sql");
////            p =runtime.exec("mysqldump -u " + ConnectionManager.USERNAME  + " --databases " + ConnectionManager.DBName + " > jamaljamljfldsajffdasfdsafasd.sql");
//            p =runtime.exec("mysqldump -v -v -v --host=localhost --user="+ ConnectionManager.USERNAME+ " --port=3306 --protocol=tcp --force --allow-keywords --compress --add-drop-table --result-file=" + path + " --databases " + ConnectionManager.DBName);
//
////            Scanner scanner = new Scanner(p.getInputStream());
////
////            System.out.println();
//
////            System.out.println(path);while (scanner.hasNext())
////                System.out.println(scanner.next());
////            mysqldump -u root --databases koala > dump.sql
//
////            ProcessBuilder procBuilder = new ProcessBuilder(new String[]{"C:\Program Files (x86)\Nmap\nmap.exe", "-T4", "-A"}); //add all params
////            procBuilder.start();
////
//
//            int processComplete = p.waitFor();
//            if (processComplete==0) {
//                System.out.println("Backup Created Succuss");
//            }else {
//                System.out.println("Can't Create backup");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
// -u username -p password -B dataBase name