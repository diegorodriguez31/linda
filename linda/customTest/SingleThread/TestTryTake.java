package linda.customTest.SingleThread;

import linda.Linda;
import linda.Tuple;
import linda.customTest.GenericTestMethod;

import java.util.Collection;

public class TestTryTake {

    public static void main(String[] a) {

        final GenericTestMethod generic = new GenericTestMethod();
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/aaa");

        new Thread(() -> {

            generic.threadSleep(3000);

            Tuple motif = new Tuple(Integer.class, String.class);

            Tuple res = linda.tryTake(motif);
            System.out.println("\n(1) Try Taking Int, String -> " + res);
            // expect [ 4 "foo" ]

            res = linda.tryTake(motif);
            System.out.println("\n(1) Try Taking Int, String -> " + res);
            // expect [ 19 "foo2" ]

            linda.debug("(1)");

            res = linda.tryTake(motif);
            System.out.println("\n(1) Try Taking Int, String -> " + res);
            // expect null

            res = linda.tryRead(motif);
            System.out.println("\n(1) Try Reading Int, String -> " + res);
            // expect null

            linda.debug("(1)");
        }).start();

        new Thread(() -> {
            generic.createTuples(linda);

            linda.debug("(2)");
        }).start();
    }
}
