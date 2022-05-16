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
        String backupURI = "localhost:1098";

        //  Création du serveur de noms
        try {
            dns = LocateRegistry.createRegistry(1099);

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
        /*BufferedWriter writer = null;
        try {
            File file = new File("linda/server/save/save.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream (buf);
            while((line = br.readLine()) != null)
            {
                try {
                    out.writeObject (line);
                    ObjectInputStream in = new ObjectInputStream (new ByteArrayInputStream (buf.toByteArray()));
                    linda.write((Tuple) in.readObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Load complete\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/

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
        boolean isMainServer = true;

        // essayer de se connecter
        // si on y arrive pas, essayer de se reconnecter toutes les 5 secondes
        lindaClient = new LindaClient(backupURI);

        while (true) {
            try{
                Thread.sleep(5000);
                if (isMainServer) {
                    sendCopyToBackUp(linda, lindaClient);
                    // TODO: gérer les callbacks

                    saveTuples(linda);
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
        BufferedWriter writer = null;
        try {
            /*writer = new BufferedWriter(new FileWriter("linda/server/save/save.txt"));

            List<Tuple> copyToBackup = (List<Tuple>) linda.readAll(null);
            for (Tuple tuple : copyToBackup) {
                writer.write(tuple.toString());
                writer.append("\n");
            }

            writer.close();*/

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("linda/server/save/save.txt"));

            List<Tuple> copyToBackup = (List<Tuple>) linda.readAll(null);
            for (Tuple tuple : copyToBackup) {
                out.writeObject(tuple);
            }

            //ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            //cp=(ConcretePage)in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
