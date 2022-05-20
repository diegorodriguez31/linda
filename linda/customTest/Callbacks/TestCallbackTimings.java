package linda.customTest.Callbacks;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class TestCallbackTimings {

    private static Linda linda;
    private static Tuple cbMotif;

    private static class MyImmediateCallback implements Callback {
        public void call(Tuple t) {
            System.out.println("Immediate callback CB got "+t);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Immediate callback CB done with "+t);
        }
    }

    private static class MyFutureCallback implements Callback {
        public void call(Tuple t) {
            System.out.println("Future callback CB got "+t);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Future callback CB done with "+t);
        }
    }

    public static void main(String[] a) {
        linda = new linda.shm.CentralizedLinda();
        // linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        cbMotif = new Tuple(Integer.class, String.class);
        linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE, cbMotif, new TestCallbackTimings.MyImmediateCallback());
        linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, cbMotif, new TestCallbackTimings.MyFutureCallback());

        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "foo");
        System.out.println("(2) write: " + t3);
        linda.write(t3);

        Tuple t4 = new Tuple(5, "should not be taken or read");
        System.out.println("(2) write: " + t4);
        linda.write(t4);

        linda.debug("(2)");
    }
}
