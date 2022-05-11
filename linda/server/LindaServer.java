package linda.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/** Création d'un serveur de nom intégré et d'un objet accessible à distance.
 *  Si la créatipon du serveur de nom échoue, on suppose qu'il existe déjà (rmiregistry) et on continue. */
public class LindaServer {
    public static void main (String args[]) {
        Registry dns = null;
        //  Création du serveur de noms
        try {
            dns = LocateRegistry.createRegistry(1099);

            RemoteLinda linda = new RemoteLindaImpl();
            dns.bind("MyLinda", linda);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Service prêt : attente d'appels
        System.out.println ("Le systeme est pret.");
    }
}
