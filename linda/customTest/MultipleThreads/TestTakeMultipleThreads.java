package linda.customTest.MultipleThreads;

import linda.Linda;
import linda.Tuple;
import linda.customTest.GenericTestMethod;

public class TestTakeMultipleThreads {

    public static void main(String[] a) {

        final GenericTestMethod generic = new GenericTestMethod();
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        for (int i = 1; i <= 3; i++) {
            final int j = i;
            new Thread(() -> {

                generic.threadSleep(3000);

                Tuple motif = new Tuple(Integer.class, String.class);

                Tuple res = linda.take(motif);
                System.out.println("\n(" + j + ") Taking Int, String -> " + res);
                // expect each thread taking one of the three first tuples

                linda.debug("(" + j + ")");

                // Waiting for the other threads print the debug
                generic.threadSleep(2000);


                res = linda.read(motif);
                System.out.println("\n(" + j + ") Reading Int, String -> " + res);
                // expect [ "foo4" "foo4" ] for every thread

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
