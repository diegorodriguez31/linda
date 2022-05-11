package linda.customTest;

import linda.Linda;
import linda.Tuple;

public class GenericTestMethod {

    public void threadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createTuples(Linda linda) {
        threadSleep(1000);

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
    }

    public void createTuplesForMultipleThreads(Linda linda) {
        threadSleep(1000);

        Tuple t1 = new Tuple(1, "foo1");
        System.out.println("(0) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple(2, "foo2");
        System.out.println("(0) write: " + t2);
        linda.write(t2);

        Tuple t3 = new Tuple(3, "foo3");
        System.out.println("(0) write: " + t3);
        linda.write(t3);
    }
}