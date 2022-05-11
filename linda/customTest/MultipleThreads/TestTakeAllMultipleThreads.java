package linda.customTest.MultipleThreads;

import linda.Linda;
import linda.Tuple;
import linda.customTest.GenericTestMethod;

import java.util.Collection;

public class TestTakeAllMultipleThreads {

    public static void main(String[] a) {

        final GenericTestMethod generic = new GenericTestMethod();
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        for (int i = 1; i <= 3; i++) {
            final int j = i;
            new Thread(() -> {

                generic.threadSleep(3000);

                Tuple motif = new Tuple(Integer.class, String.class);

                Collection<Tuple> res = linda.takeAll(motif);
                System.out.println("\n(" + j + ") Taking All Int, String -> " + res);
                // expect [[ 1 "foo1" ], [ 2 "foo2" ], [ 3 "foo3" ]] for the first thread

                linda.debug("(" + j + ")");

                // Waiting for the other threads print the debug
                generic.threadSleep(2000);

                res = linda.takeAll(motif);
                System.out.println("\n(" + j + ") Taking All Int, String -> " + res);
                // expect null for every thread

                // Waiting for the other threads read
                generic.threadSleep(2000);

                linda.debug("(" + j + ")");
            }).start();
        }

        new Thread(() -> {
            generic.createTuplesForMultipleThreads(linda);
            linda.debug("(0)");
        }).start();

    }


}
