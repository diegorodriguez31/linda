package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
    private List<Tuple> tuplesSpace;
    private List<CallBackInfo> callBackInfos;

    private final Lock callBackLock;

    private List<Request> requests;
    private Lock moniteur;


    public CentralizedLinda() {
        tuplesSpace = new ArrayList<>();
        callBackInfos = new ArrayList<>();
        callBackLock = new ReentrantLock();
        moniteur = new ReentrantLock();
        requests = new ArrayList<>();
    }

    @Override
    public void write(Tuple t) {
        moniteur.lock();

        tuplesSpace.add(0, t);

        List<Request> temp = new ArrayList<>(requests);
        for (Request req : temp) {
            if (t.matches(req.getTuple())) {
                requests.remove(req);
                req.getCondition().signal();
            }
        }

        moniteur.unlock();

        manageCallbacks();
    }

    /** Returns a tuple matching the template and removes it from the tuplespace.
     * Blocks if no corresponding tuple is found. */
    @Override
    public Tuple take(Tuple template) {
        Tuple res = null;

        moniteur.lock();
        if (!isTemplateOccurrence(template)) {
            Request request = new Request(template, moniteur.newCondition(), eventMode.TAKE);
            requests.add(request);
            try {
                request.getCondition().await();

                for (Tuple tuple : tuplesSpace) {
                    if (tuple.matches(template)) {
                        res = tuple;
                        break;
                    }
                }

                // bloquer si le tuple n'est plus présent => retour en début de boucle
                // priorité au signaleur
                if (res == null) {
                    moniteur.unlock();
                    return take(template);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            for (Tuple tuple : tuplesSpace) {
                if (tuple.matches(template)) {
                    res = tuple;
                    break;
                }
            }
        }
        tuplesSpace.remove(res);
        moniteur.unlock();
        return res;
    }

    @Override
    public Tuple read(Tuple template) {
        Tuple res = null;

        moniteur.lock();
        if (!isTemplateOccurrence(template)) {
            Request request = new Request(template, moniteur.newCondition(), eventMode.READ);
            requests.add(request);
            try {
                request.getCondition().await();

                for (Tuple tuple : tuplesSpace) {
                    if (tuple.matches(template)) {
                        res = tuple.deepclone();
                        break;
                    }
                }

                // bloquer si le tuple n'est plus présent => retour en début de boucle
                // priorité au signaleur
                if (res == null) {
                    moniteur.unlock();
                    return read(template);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            for (Tuple tuple : tuplesSpace) {
                if (tuple.matches(template)) {
                    res = tuple.deepclone();
                    break;
                }
            }
        }
        moniteur.unlock();
        return res;
    }

    @Override
    public Tuple tryTake(Tuple template) {
        moniteur.lock();
        Tuple res = null;
        for (Tuple tuple : tuplesSpace) {
            if(tuple.matches(template)) {
                res = tuple;
                break;
            }
        }
        tuplesSpace.remove(res);
        moniteur.unlock();
        return res;
    }

    @Override
    public Tuple tryRead(Tuple template) {
        moniteur.lock();
        Tuple res = null;
        for (Tuple tuple : tuplesSpace) {
            if(tuple.matches(template)) {
                res = tuple.deepclone();
                break;
            }
        }
        moniteur.unlock();
        return res;
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        boolean takeAllTuples = template == null;
        List<Tuple> res = new ArrayList<>();
        moniteur.lock();
        for (Tuple tuple : tuplesSpace) {
            if(takeAllTuples || tuple.matches(template)) {
                res.add(tuple);
            }
        }
        tuplesSpace.removeAll(res);
        moniteur.unlock();
        return res;
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        boolean readAllTuples = template == null;
        List<Tuple> res = new ArrayList<>();
        moniteur.lock();
        for (Tuple tuple : tuplesSpace) {
            if(readAllTuples || tuple.matches(template)) {
                res.add(tuple.deepclone());
            }
        }
        moniteur.unlock();
        return res;
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        boolean addCallback = timing == eventTiming.FUTURE;
         if (timing == eventTiming.IMMEDIATE) {
             Tuple tuple = null;
             if (mode == eventMode.READ) {
                 tuple = tryRead(template);
             } else if (mode == eventMode.TAKE) {
                 tuple = tryTake(template);
             }

             if(tuple != null){
                 callback.call(tuple);
             } else {
                 addCallback = true;
             }
         }

         if (addCallback) {
             callBackLock.lock();
             callBackInfos.add(new CallBackInfo(mode, template, callback));
             callBackLock.unlock();
         }
    }

    @Override
    public void debug(String prefix) {
        moniteur.lock();
        System.out.println("\nTuple Space content " + prefix + "--------------");
        for (Tuple tuple : tuplesSpace) {
            System.out.println(tuple.toString());
        }
        System.out.println("-----------" + prefix + "--------------");

        moniteur.unlock();
    }

    public void manageCallbacks(){
        List<CallBackInfo> callBacksToTrigger = new ArrayList<>();

        callBackLock.lock();

        // get the list of all the callBacks that need to be triggered
        for (CallBackInfo callBackInfo : callBackInfos) {
            if (isTemplateOccurrence(callBackInfo.getTemplate())) {
                callBacksToTrigger.add(callBackInfo);
            }
        }

        // remove them from the list of callbacks
        callBackInfos.removeAll(callBacksToTrigger);
        callBackLock.unlock();

        // call the callbacks
        for (CallBackInfo callBackInfo : callBacksToTrigger) {
            Tuple tuple = null;
            if (callBackInfo.getMode() == eventMode.READ) {
                tuple = tryRead(callBackInfo.getTemplate());
            } else if (callBackInfo.getMode() == eventMode.TAKE) {
                tuple = tryTake(callBackInfo.getTemplate());
            }

            // sometimes, 2 callbacks may wanna take the only occurence of a tuple
            // the tuple would be null for the second callback
            // so:
            if(tuple != null) {
                // call the callback
                callBackInfo.getCallback().call(tuple);
            } else {
                // add back the callback into the list
                callBackLock.lock();
                callBackInfos.add(new CallBackInfo(callBackInfo.getMode(), callBackInfo.getTemplate(), callBackInfo.getCallback()));
                callBackLock.unlock();
            }
        }
    }

    public boolean isTemplateOccurrence(Tuple template){
        return tryRead(template) != null;
    }
}
