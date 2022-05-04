package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class CallBackInfo {
    private Linda.eventMode mode;
    private Tuple template;
    private Callback callback;

    public CallBackInfo(Linda.eventMode mode, Tuple template, Callback callback) {
        this.mode = mode;
        this.template = template;
        this.callback = callback;
    }

    public Linda.eventMode getMode() {
        return mode;
    }

    public Tuple getTemplate() {
        return template;
    }

    public Callback getCallback() {
        return callback;
    }
}
