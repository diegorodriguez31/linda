package linda.server;

import linda.Tuple;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/** Création d'un serveur de nom intégré et d'un objet accessible à distance.
 *  Si la créatipon du serveur de nom échoue, on suppose qu'il existe déjà (rmiregistry) et on continue. */
public class LindaServer {
    public static void main (String args[]) {
        Registry dns = null;
        RemoteLindaManager linda = null;
        String serverURI = args[0];
        String backupURI = serverURI.equals("localhost:1099") ? "localhost:1098" : "localhost:1099";
        int port = serverURI.equals("localhost:1099") ? 1099 : 1098;

        //  Création du serveur de noms
        try {
            dns = LocateRegistry.createRegistry(port);

            linda = new RemoteLindaManagerImpl();
            dns.bind("MyLinda", linda);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Charger les tuples depuis la sauvegarde
        loadSave(linda);

        // Service prêt : attente d'appels
        System.out.println ("Le systeme est pret.");

        // setup du serveur de backup
        linkWithBackupServer(backupURI, linda);
    }

    public static void loadSave(RemoteLindaManager linda) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("linda/server/save/save.txt"));

            Set<Tuple> result = new HashSet<>();
            try {
                for (;;) {
                    result.add((Tuple) in.readObject());
                }
            } catch (EOFException e) {
                System.out.println(e.getMessage());
            }

            for(Tuple t : result) {
                linda.write(t);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void linkWithBackupServer(String backupURI, RemoteLindaManager linda){
        LindaClient lindaClient = null;
        boolean isMainServer;

        // essayer de se connecter
        // si on y arrive pas, essayer de se reconnecter toutes les 5 secondes
        lindaClient = new LindaClient(backupURI);

        while (true) {
            try{
                Thread.sleep(5000);

                isMainServer = backupURI.equals("localhost:1098") || !lindaClient.checkStatus();

                if (isMainServer) {
                    System.out.println("----------Main server------------");
                    sendCopyToBackUp(linda, lindaClient);
                    // TODO: gérer les callbacks

                    saveTuples(linda);
                } else {
                    System.out.println("----------Server de backup------------");
                    System.out.println("----------new state------------");
                    for (Tuple t : linda.readAll(null)){
                        System.out.println(t.toString());
                    }
                    System.out.println("----------end state------------\n\n\n\n");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void sendCopyToBackUp(RemoteLindaManager linda, LindaClient lindaClient) throws RemoteException {
        if (linda != null) {
            // clean the backup server
            lindaClient.takeAll(null);

            // send copy to the backup server
            List<Tuple> copyToBackup = (List<Tuple>) linda.readAll(null);
            for (Tuple tuple : copyToBackup) {
                lindaClient.write(tuple);
            }
        }
    }

    public static void saveTuples(RemoteLindaManager linda) throws RemoteException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("linda/server/save/save.txt"));

            List<Tuple> copyToBackup = (List<Tuple>) linda.readAll(null);
            for (Tuple tuple : copyToBackup) {
                out.writeObject(tuple);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
