package linda.test;

import linda.Linda;
import linda.Tuple;

public class GenericTestMethod {

    protected void threadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void createTuples(GenericTestMethod generic, Linda linda) {
        generic.threadSleep(1000);

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
