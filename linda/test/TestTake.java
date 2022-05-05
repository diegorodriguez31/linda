package linda.test;

import linda.Linda;
import linda.Tuple;

public class TestTake {

    public static void main(String[] a) {

        final GenericTestMethod generic = new GenericTestMethod();
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/aaa");

        new Thread(() -> {

            generic.threadSleep(3000);

            Tuple motif = new Tuple(Integer.class, String.class);

            Tuple res = linda.take(motif);
            System.out.println("\n(1) Taking Int, String -> " + res);
            // expect [ 4 "foo" ]

            linda.debug("(1)");

            Tuple res2 = linda.read(motif);
            System.out.println("\n(1) Reading Int, String -> " + res2);
            // expect [ 19 "foo2" ]

            res = linda.take(motif);
            System.out.println("\n(1) Taking Int, String -> " + res);
            // expect [ 19 "foo2" ]

            linda.debug("(1)");
        }).start();

        new Thread(() -> {

            generic.threadSleep(1000);


            Tuple t1 = new Tuple(4, 5);
            System.out.println("(2) write: " + t1);
            linda.write(t1);

            Tuple t11 = new Tuple(4, 5);
            System.out.println("(2) write: " + t11);
            linda.write(t11);

            Tuple t2 = new Tuple("hello", 15);
            System.out.println("(2) write: " + t2);
            linda.write(t2);

            Tuple t3 = new Tuple(4, "foo");
            System.out.println("(2) write: " + t3);
            linda.write(t3);

            Tuple t4 = new Tuple(19, "foo2");
            System.out.println("(2) write " + t4);
            linda.write(t4);

            linda.debug("(2)");

        }).start();
    }
}
