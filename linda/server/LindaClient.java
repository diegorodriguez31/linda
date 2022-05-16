package linda.server;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
    private RemoteLindaManager lindaManager;

    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
        String registryhost;
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
                        checkServerStatus();
                    } catch (ConnectException e) {
                        System.out.println("server out");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }.start();
    }

    public boolean checkServerStatus() throws MalformedURLException, NotBoundException, RemoteException {
        String backupURI = "localhost:1098";
        try {
            if (lindaManager == null || !lindaManager.checkStatus()) {
                lindaManager = (RemoteLindaManager) Naming.lookup("rmi://"+backupURI+"/MyLinda");
                System.out.println("Server changed to rmi://"+backupURI+"/MyLinda");
                return false;
            }
        } catch (Exception e) {
            lindaManager = (RemoteLindaManager) Naming.lookup("rmi://"+backupURI+"/MyLinda");
            System.out.println("Server changed to rmi://"+backupURI+"/MyLinda");
        }
        return true;
    }

    @Override
    public void write(Tuple t) {
        try {
            lindaManager.write(t);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Tuple take(Tuple template) {
        try {
            return lindaManager.take(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Tuple read(Tuple template) {
        try {
            return lindaManager.read(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Tuple tryTake(Tuple template) {
        try {
            return lindaManager.tryTake(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Tuple tryRead(Tuple template) {
        try {
            return lindaManager.tryRead(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        try {
            return lindaManager.takeAll(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        try {
            return lindaManager.readAll(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        try {
            lindaManager.eventRegister(mode, timing, template, new RemoteCallbackImpl(callback));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void debug(String prefix) {
        try {
            lindaManager.debug(prefix);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean checkStatus() throws RemoteException {
        return lindaManager.checkStatus();
    }
}
