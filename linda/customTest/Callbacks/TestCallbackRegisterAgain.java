package linda.customTest.Callbacks;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class TestCallbackRegisterAgain {

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
        linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, cbMotif, new TestCallbackRegisterAgain.MyReadCallback());
        linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, cbMotif, new TestCallbackRegisterAgain.MyTakeCallback());

        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "should be read and taken");
        System.out.println("(2) write: " + t3);
        linda.write(t3);

        Tuple t4 = new Tuple(5, "should be read and taken");
        System.out.println("(2) write: " + t4);
        linda.write(t4);

        Tuple t5 = new Tuple(6, 6, "nothing happends");
        System.out.println("(2) write: " + t5);
        linda.write(t5);

        Tuple t6 = new Tuple(7, "should be read and taken");
        System.out.println("(2) write: " + t6);
        linda.write(t6);

        linda.debug("(2)");
    }
}
