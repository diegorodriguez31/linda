package linda.server;

import linda.Tuple;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class BackupLindaServer {
    static final int SERVER_PORT = 1098;
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

        // Service ready : waiting for calls
        System.out.println ("Le systeme est pret.");

        try {
            while (true) {
                Thread.sleep(5000);
                System.out.println("----------New Tuple Space state------------");
                for (Tuple t : linda.readAll(null)){
                    System.out.println(t.toString());
                }
                System.out.println("----------------end state------------------\n\n\n\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
