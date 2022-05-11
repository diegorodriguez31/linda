package linda.server;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.rmi.Naming;
import java.util.Collection;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
    private RemoteLinda linda;

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
            linda = (RemoteLinda) Naming.lookup("rmi://"+registryhost+"/MyLinda");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void write(Tuple t) {
        try {
            linda.write(t);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Tuple take(Tuple template) {
        try {
            return linda.take(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Tuple read(Tuple template) {
        try {
            return linda.read(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Tuple tryTake(Tuple template) {
        try {
            return linda.tryTake(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Tuple tryRead(Tuple template) {
        try {
            return linda.tryRead(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        try {
            return linda.takeAll(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        try {
            return linda.readAll(template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        try {
            linda.eventRegister(mode, timing, template, callback);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void debug(String prefix) {
        try {
            linda.debug(prefix);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
