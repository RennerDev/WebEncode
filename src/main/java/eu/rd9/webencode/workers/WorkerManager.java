package eu.rd9.webencode.workers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by renne on 16.12.2016.
 */
public class WorkerManager extends Thread{

    private static WorkerManager workerManager = null;
    public static WorkerManager getInstance()
    {
        if ( workerManager == null)
        {
            workerManager = new WorkerManager();
            workerManager.start();
        }

        return workerManager;
    }



    private ExecutorService executor;
    private List<Worker> workerList = new ArrayList<>();

    public WorkerManager()
    {
        this.executor = Executors.newFixedThreadPool(4);
    }

    public WorkerManager(int tCount)
    {
        this.executor = Executors.newFixedThreadPool(tCount);
    }

    public void run ()
    {

    }

    public void startWorker (Worker worker)
    {
        this.workerList.add(worker);
        this.executor.submit(worker);
    }

    public List<Worker> getWorkers()
    {
        return this.workerList;
    }

    public void addJob (Worker worker)
    {
        this.executor.submit(worker);
    }
}
