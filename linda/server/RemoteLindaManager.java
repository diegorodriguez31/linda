package linda.server;

import linda.Linda;
import linda.Tuple;

import java.rmi.RemoteException;
import java.util.Collection;

public interface RemoteLindaManager extends java.rmi.Remote {

    public void write(Tuple t) throws RemoteException;

    public Tuple take(Tuple template) throws RemoteException;

    public Tuple read(Tuple template) throws RemoteException;

    public Tuple tryTake(Tuple template) throws RemoteException;

    public Tuple tryRead(Tuple template) throws RemoteException;

    public Collection<Tuple> takeAll(Tuple template) throws RemoteException;

    public Collection<Tuple> readAll(Tuple template) throws RemoteException;

    void eventRegister(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, RemoteCallback callback) throws RemoteException;

    public void debug(String prefix) throws RemoteException;
    public boolean checkStatus() throws RemoteException;
}
