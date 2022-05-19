package linda.server;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Collection;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
    private RemoteLindaManager lindaManager;
    private String registryhost;
    final String SERVER_URI = "localhost:1099";
    final String BACKUP_SERVER_URI = "localhost:1098";

    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
        if (serverURI.length() >= 1) {
            registryhost = serverURI;
        } else {
            registryhost = SERVER_URI;
        }

        //  Connexion to the name server to get a handle on the linda Manager
        try {
            lindaManager = (RemoteLindaManager) Naming.lookup("rmi://"+registryhost+"/MyLinda");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        checkServerStatus();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }.start();
    }

    public boolean checkServerStatus() {
        String newRegistryHost = registryhost.equals(BACKUP_SERVER_URI) ? SERVER_URI : BACKUP_SERVER_URI;
        boolean needSeverSwitch = false;

        try {
            lindaManager.checkStatus();
        } catch (RemoteException e) {
            needSeverSwitch = true;
        }

        if (needSeverSwitch){
            try {
                registryhost = newRegistryHost;
                lindaManager = (RemoteLindaManager) Naming.lookup("rmi://"+newRegistryHost+"/MyLinda");
                System.out.println("Server changed to rmi://"+newRegistryHost+"/MyLinda");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public void write(Tuple t) {
        try {
            lindaManager.write(t);
        } catch (RemoteException e) {
            checkServerStatus();
        }
    }

    @Override
    public Tuple take(Tuple template) {
        try {
            return lindaManager.take(template);
        } catch (RemoteException e) {
            checkServerStatus();
            return null;
        }
    }

    @Override
    public Tuple read(Tuple template) {
        try {
            return lindaManager.read(template);
        } catch (RemoteException e) {
            checkServerStatus();
            return null;
        }
    }

    @Override
    public Tuple tryTake(Tuple template) {
        try {
            return lindaManager.tryTake(template);
        } catch (RemoteException e) {
            checkServerStatus();
            return null;
        }
    }

    @Override
    public Tuple tryRead(Tuple template) {
        try {
            return lindaManager.tryRead(template);
        } catch (RemoteException e) {
            checkServerStatus();
            return null;
        }
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        try {
            return lindaManager.takeAll(template);
        } catch (RemoteException e) {
            checkServerStatus();
            return null;
        }
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        try {
            return lindaManager.readAll(template);
        } catch (RemoteException e) {
            checkServerStatus();
            return null;
        }
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        try {
            lindaManager.eventRegister(mode, timing, template, new RemoteCallbackImpl(callback));
        } catch (RemoteException e) {
            checkServerStatus();
        }
    }

    @Override
    public void debug(String prefix) {
        try {
            lindaManager.debug(prefix);
        } catch (RemoteException e) {
            checkServerStatus();
        }
    }
}
