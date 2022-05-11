package linda.shm;

import linda.Linda;
import linda.Tuple;

import java.util.concurrent.locks.Condition;

public class Request {
    private Tuple tuple;
    private Condition condition;
    private Linda.eventMode requestType;

    public Request(Tuple tuple, Condition condition, Linda.eventMode requestType) {
        this.tuple = tuple;
        this.condition = condition;
        this.requestType = requestType;
    }

    public Tuple getTuple() {
        return tuple;
    }

    public Condition getCondition() {
        return condition;
    }

    public Linda.eventMode getRequestType() {
        return requestType;
    }
}
