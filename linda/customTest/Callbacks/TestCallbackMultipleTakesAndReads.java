package linda.customTest.Callbacks;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class TestCallbackMultipleTakesAndReads {

    private static Linda linda;
    private static Tuple cbMotif;

    private static class MyReadCallback implements Callback {
        public void call(Tuple t) {
            System.out.println("Reading callback CB got "+t);
            linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, cbMotif, this);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Reading callback CB done with "+t);
        }
    }

    private static class MyTakeCallback implements Callback {
        public void call(Tuple t) {
            System.out.println("Taking callback CB got "+t);
            linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, cbMotif, this);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Taking callback CB done with "+t);
        }
    }

    public static void main(String[] a) {
        linda = new linda.shm.CentralizedLinda();
        // linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        cbMotif = new Tuple(Integer.class, String.class);
        linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, cbMotif, new TestCallbackMultipleTakesAndReads.MyReadCallback());
        linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, cbMotif, new TestCallbackMultipleTakesAndReads.MyReadCallback());
        linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, cbMotif, new TestCallbackMultipleTakesAndReads.MyTakeCallback());
        linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, cbMotif, new TestCallbackMultipleTakesAndReads.MyTakeCallback());

        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "should be read twice and taken once");
        System.out.println("(2) write: " + t3);
        linda.write(t3);

        Tuple t4 = new Tuple(5, "should be read twice and taken once");
        System.out.println("(2) write: " + t4);
        linda.write(t4);

        linda.debug("(2)");
    }
}
