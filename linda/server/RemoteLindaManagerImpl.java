package linda.server;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.shm.CentralizedLinda;
import linda.test.BasicTestAsyncCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

public class RemoteLindaManagerImpl extends UnicastRemoteObject implements RemoteLindaManager {

    private CentralizedLinda linda;

    public RemoteLindaManagerImpl() throws RemoteException {
        linda = new CentralizedLinda();
    }

    @Override
    public void write(Tuple t) throws RemoteException {
        linda.write(t);
    }

    @Override
    public Tuple take(Tuple template) throws RemoteException {
        return linda.take(template);
    }

    @Override
    public Tuple read(Tuple template) throws RemoteException {
        return linda.read(template);
    }

    @Override
    public Tuple tryTake(Tuple template) throws RemoteException {
        return linda.tryTake(template);
    }

    @Override
    public Tuple tryRead(Tuple template) throws RemoteException {
        return linda.tryRead(template);
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
        return linda.takeAll(template);
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) throws RemoteException {
        return linda.readAll(template);
    }

    @Override
    public void eventRegister(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, RemoteCallback callback) throws RemoteException {
        IntermediateCallback interCallback = new IntermediateCallback(mode, timing, template, callback);
        linda.eventRegister(mode, timing, template, interCallback);
    }

    @Override
    public void debug(String prefix) throws RemoteException {
        linda.debug(prefix);
    }

    private static class IntermediateCallback implements Callback {
        private Linda.eventMode mode;
        private Linda.eventTiming timing;
        private Tuple template;
        private RemoteCallback callback;


        public IntermediateCallback(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, RemoteCallback callback) {
            this.mode = mode;
            this.timing = timing;
            this.template = template;
            this.callback = callback;
        }

        public void call(Tuple t) {
            try {
                callback.call(t);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
