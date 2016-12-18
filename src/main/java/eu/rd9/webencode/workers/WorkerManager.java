package eu.rd9.webencode.workers;

import eu.rd9.webencode.data.Rule;
import eu.rd9.webencode.page.WebEncodeUI;
import eu.rd9.webencode.services.RulesService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by renne on 16.12.2016.
 */
public class WorkerManager extends Thread {

    private static WorkerManager workerManager = null;
    private ExecutorService executor;
    private List<Worker> workerList = new ArrayList<>();
    public WorkerManager() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public WorkerManager(int tCount) {
        this.executor = Executors.newFixedThreadPool(tCount);
    }

    public static WorkerManager getInstance() {
        if (workerManager == null) {
            workerManager = new WorkerManager();
            workerManager.start();
        }

        return workerManager;
    }

    public void run() {

    }

    public void startWorker(Worker worker) {
        this.workerList.add(worker);
        this.executor.submit(worker);
    }

    public List<Worker> getWorkers() {
        return this.workerList;
    }

    public void doWork(File file) {
        Rule rule = RulesService.getInstance().getRuleForFile(file.getName());
        Worker worker = new FFmpegWorker(rule.Preset, file);
        this.startWorker(worker);
    }
}
