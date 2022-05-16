package linda.server;

import linda.Callback;
import linda.Tuple;

import java.rmi.RemoteException;

public interface RemoteCallback extends java.rmi.Remote {

    /** Callback when a tuple appears.
     * See Linda.eventRegister for details.
     *
     * @param t the new tuple
     */
    public void call(Tuple t) throws RemoteException;
}
