package linda.server;

import linda.Tuple;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/** Création d'un serveur de nom intégré et d'un objet accessible à distance.
 *  Si la créatipon du serveur de nom échoue, on suppose qu'il existe déjà (rmiregistry) et on continue. */
public class LindaServer {
    static final int SERVER_PORT = 1099;
    static final String BACKUP_SERVER_URI = "localhost:1098";
    public static void main (String args[]) {
        Registry dns = null;
        RemoteLindaManager linda = null;

        //  Name server creation
        try {
            dns = LocateRegistry.createRegistry(SERVER_PORT);

            linda = new RemoteLindaManagerImpl();
            dns.bind("MyLinda", linda);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Load tuple space save
        loadSave(linda);

        // System ready, waiting for calls
        System.out.println ("Le systeme est pret, je suis le serveur principal.");

        // Establish link with the backup server
        linkWithBackupServer(linda);
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

    public static void linkWithBackupServer(RemoteLindaManager linda){
        LindaClient lindaClient = null;

        // The backup server has to be started before
        lindaClient = new LindaClient(BACKUP_SERVER_URI);

        while (true) {
            try{
                Thread.sleep(5000);
                sendCopyToBackUp(linda, lindaClient);
                saveTuples(linda);
                // TODO: gérer les callbacks
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
