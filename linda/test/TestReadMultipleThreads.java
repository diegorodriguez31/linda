package linda.test;

import linda.Linda;
import linda.Tuple;

public class TestReadMultipleThreads {

    public static void main(String[] a) {

        final GenericTestMethod generic = new GenericTestMethod();
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        for (int i = 1; i <= 3; i++) {
            final int j = i;
            new Thread(() -> {

                generic.threadSleep(3000);

                Tuple motif = new Tuple(Integer.class, String.class);

                Tuple res = linda.read(motif);
                System.out.println("\n(" + j + ") Reading Int, String -> " + res);
                // expect [ 1 "foo1" ] for every thread

                linda.debug("(" + j + ")");

                // Waiting for the other threads print the debug
                generic.threadSleep(2000);

                // Thread 1 take the first one
                if (j == 1) {
                    Tuple res2 = linda.take(motif);
                    System.out.println("\n(" + j + ") Taking Int, String -> " + res2);
                    // expect [ 1 "foo1" ]
                }

                // Waiting thread 1 take the first one
                generic.threadSleep(2000);

                res = linda.read(motif);
                System.out.println("\n(" + j + ") Reading Int, String -> " + res);
                // expect [ 2 "foo2" ] for every thread

                // Waiting for the other threads read
                generic.threadSleep(2000);

                linda.debug("(" + j + ")");
            }).start();
        }

        new Thread(() -> {
            generic.createTuples(generic, linda);

            linda.debug("(0)");

        }).start();

    }


}
