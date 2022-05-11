package linda.customTest.SingleThread;

import linda.Linda;
import linda.Tuple;
import linda.customTest.GenericTestMethod;

import java.util.Collection;

public class TestTakeAll {

    public static void main(String[] a) {

        final GenericTestMethod generic = new GenericTestMethod();
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/aaa");

        new Thread(() -> {

            generic.threadSleep(3000);

            Tuple motif = new Tuple(Integer.class, String.class);

            Collection<Tuple> res = linda.takeAll(motif);
            System.out.println("\n(1) Taking All Int, String -> " + res);
            // expect [[ 4 "foo" ], [ 19 "foo2" ]]

            linda.debug("(1)");

            Tuple res2 = linda.tryRead(motif);
            System.out.println("\n(1) Try Reading Int, String -> " + res2);
            // expect null

            res = linda.takeAll(motif);
            System.out.println("\n(1) Taking All Int, String -> " + res);
            // expect []

            linda.debug("(1)");
        }).start();

        new Thread(() -> {
            generic.createTuples(linda);

            Tuple t5 = new Tuple("foo", "foo2");
            System.out.println("(2) write " + t5);
            linda.write(t5);

            linda.debug("(2)");
        }).start();
    }
}
