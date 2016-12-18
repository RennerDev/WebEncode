package eu.rd9.webencode.workers;

/**
 * Created by renne on 16.12.2016.
 */
public class Worker implements Runnable {

    public WorkerState state = WorkerState.WAITING;

    @Override
    public void run() {
        state = WorkerState.RUNNING;
    }

    @Override
    public String toString() {
        return Workers.getWorker(this.getClass()).getWorkerName();
    }
}
