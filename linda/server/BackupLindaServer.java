package linda.server;

import linda.Tuple;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/** Création d'un serveur de nom intégré et d'un objet accessible à distance.
 *  Si la créatipon du serveur de nom échoue, on suppose qu'il existe déjà (rmiregistry) et on continue. */
public class BackupLindaServer {
    public static void main (String args[]) {
        boolean isMainServer = false;

        Registry dns = null;
        RemoteLindaManager linda = null;
        String backupURI = "localhost:1099";


        //  Création du serveur de noms
        try {
            dns = LocateRegistry.createRegistry(1098);

            linda = new RemoteLindaManagerImpl();
            dns.bind("MyLinda", linda);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Service prêt : attente d'appels
        System.out.println ("Le systeme est pret.");

        //LindaClient lindaClient = new LindaClient(backupURI);

        try {
            while (true) {
                Thread.sleep(5000);
                //TODO: isMainServer = lindaClient == null || !lindaClient.checkStatus();

                if(isMainServer) {
                    System.out.println("\n\n\nI am the main server now !!!");
                } else {
                    System.out.println("----------new state------------");
                    for (Tuple t : linda.readAll(null)){
                        System.out.println(t.toString());
                    }
                    System.out.println("----------end state------------\n\n\n\n");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sendCopyToBackUp(RemoteLindaManager linda, LindaClient lindaClient, int time) {
        try {
            Thread.sleep(time);
            if (linda != null) {
                lindaClient.takeAll(null);
                List<Tuple> copyToBackup = (List<Tuple>) linda.readAll(null);

                for (Tuple tuple : copyToBackup) {
                    lindaClient.write(tuple);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
