package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
    private List<Tuple> tuplesSpace;
    private List<CallBackInfo> callBackInfos;
    private final Lock lock ;


    public CentralizedLinda() {
        tuplesSpace = new ArrayList<>();
        callBackInfos = new ArrayList<>();
        lock = new ReentrantLock(); // true to be fair & can be changed
    }

    // TO BE COMPLETED

    @Override
    public void write(Tuple t) {
        lock.lock();
        tuplesSpace.add(t);
        lock.unlock();

        for (CallBackInfo callBackInfo: callBackInfos) {
            Tuple tuple = tryRead(callBackInfo.getTemplate());
            if(tuple != null){
                if (callBackInfo.getMode() == eventMode.READ) {
                    callBackInfo.getCallback().call(tuple);
                }
                else if (callBackInfo.getMode() == eventMode.TAKE) {
                    callBackInfo.getCallback().call(take(callBackInfo.getTemplate()));
                }
            }
        }
    }

    /** Returns a tuple matching the template and removes it from the tuplespace.
     * Blocks if no corresponding tuple is found. */
    @Override
    public Tuple take(Tuple template) {
        Tuple res = null;
        while (res == null) {
            lock.lock();
            for (Tuple tuple : tuplesSpace) {
                if(tuple.matches(template)) {
                    tuplesSpace.remove(tuple);
                    res = tuple;
                    break;
                }
            }
            lock.unlock();
        }
        return res;
    }

    @Override
    public Tuple read(Tuple template) {
        Tuple res = null;
        while (res == null) {
            lock.lock();
            for (Tuple tuple : tuplesSpace) {
                if(tuple.matches(template)) {
                    res = tuple.deepclone();
                    break;
                }
            }
            lock.unlock();
        }
        return res;
    }

    @Override
    public Tuple tryTake(Tuple template) {
        lock.lock();
        Tuple res = null;
        for (Tuple tuple : tuplesSpace) {
            if(tuple.matches(template)) {
                res = tuple;
                tuplesSpace.remove(tuple);
            }
        }
        lock.unlock();
        return res;
    }

    @Override
    public Tuple tryRead(Tuple template) {
        lock.lock();
        Tuple res = null;
        for (Tuple tuple : tuplesSpace) {
            if(tuple.matches(template)) {
                res = tuple.deepclone();
            }
        }
        lock.unlock();
        return res;
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        List<Tuple> res = new ArrayList<>();
        lock.lock();
        for (Tuple tuple : tuplesSpace) {
            if(tuple.matches(template)) {
                res.add(tuple);
                tuplesSpace.remove(tuple);
            }
        }
        lock.unlock();
        return res;
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        List<Tuple> res = new ArrayList<>();
        lock.lock();
        for (Tuple tuple : tuplesSpace) {
            if(tuple.matches(template)) {
                res.add(tuple.deepclone());
            }
        }
        lock.unlock();
        return res;
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
         if (timing == eventTiming.IMMEDIATE) {
             Tuple tuple = (mode == eventMode.READ ? tryRead(template) : tryTake(template));

             if(tuple != null){
                 callback.call(tuple);
             } else {
                 callBackInfos.add(new CallBackInfo(mode, template, callback));
             }

         } else if (timing == eventTiming.FUTURE) {
             callBackInfos.add(new CallBackInfo(mode, template, callback));
         }
    }

    @Override
    public void debug(String prefix) {
        lock.lock();
        System.out.println("\nStart Debugging " + prefix);
        for (Tuple tuple : tuplesSpace) {
            System.out.println(tuple.toString());
        }
        System.out.println("Debugging Finished " + prefix + "\n");
        lock.unlock();
    }
}
