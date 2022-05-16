package linda.server;

import linda.Tuple;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/** Création d'un serveur de nom intégré et d'un objet accessible à distance.
 *  Si la créatipon du serveur de nom échoue, on suppose qu'il existe déjà (rmiregistry) et on continue. */
public class LindaServer {
    public static void main (String args[]) {
        boolean isMainServer = true;

        Registry dns = null;
        RemoteLindaManager linda = null;
        String backupURI = "localhost:1098";


        //  Création du serveur de noms
        try {
            dns = LocateRegistry.createRegistry(1099);

            linda = new RemoteLindaManagerImpl();
            dns.bind("MyLinda", linda);

            loadSave(linda);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Service prêt : attente d'appels
        System.out.println ("Le systeme est pret.");

        LindaClient lindaClient = new LindaClient(backupURI);
        try {
            while (true) {
                Thread.sleep(5000);
                if (isMainServer) {
                    sendCopyToBackUp(linda, lindaClient);
                    // TODO: gérer les callbacks

                    saveTuples(linda);
                }
            }
        } catch (ConnectException e) {
            System.out.println("aaaaa");
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("linda/server/save/save.txt"));

            List<Tuple> copyToBackup = (List<Tuple>) linda.readAll(null);
            for (Tuple tuple : copyToBackup) {
                writer.write(tuple.toString());
                writer.append("\n");
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadSave(RemoteLindaManager linda) {
        BufferedWriter writer = null;
        try {
            File file = new File("linda/server/save/save.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null)
            {
                linda.write(Tuple.valueOf(line));
            }
            System.out.println("Load complete\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
