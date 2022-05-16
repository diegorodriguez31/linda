package linda.server;

import linda.Callback;
import linda.Tuple;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteCallbackImpl extends UnicastRemoteObject implements RemoteCallback{

    private Callback cb;

    public RemoteCallbackImpl(Callback cb) throws RemoteException {
        this.cb = cb;
    }

    @Override
    public void call(Tuple t) {
        cb.call(t);
    }
}
