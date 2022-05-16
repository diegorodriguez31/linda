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

    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
        if (serverURI.length() >= 1) {
            registryhost = serverURI;
        } else {
            registryhost = "localhost:1099";
        }

        //  Connexion au serveur de noms (obtention d'un handle)
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
        registryhost = registryhost.equals("localhost:1098") ? "localhost:1099" : "localhost:1098";
        boolean needSeverSwitch = false;

        try {
            lindaManager.checkStatus();
        } catch (RemoteException e) {
            needSeverSwitch = true;
        }

        if (needSeverSwitch){
            try {
                lindaManager = (RemoteLindaManager) Naming.lookup("rmi://"+registryhost+"/MyLinda");
                System.out.println("Server changed to rmi://"+registryhost+"/MyLinda");
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

    public boolean checkStatus() throws RemoteException {
        return lindaManager.checkStatus();
    }
}
