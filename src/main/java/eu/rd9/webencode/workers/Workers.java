package eu.rd9.webencode.workers;

/**
 * Created by renne on 17.12.2016.
 */
public enum Workers {

    FFMPEG("FFmpeg Worker", FFmpegWorker.class);

    private String workerName;
    private Class aClass;

    Workers(String workerName, Class aClass) {
        this.aClass = aClass;
        this.workerName = workerName;
    }

    public static Workers getWorker(Class aClass) {
        for (Workers worker : Workers.values()) {
            if (worker.aClass.equals(aClass))
                return worker;
        }

        return Workers.FFMPEG;
    }

    public Worker getWorker() {
        try {
            return (Worker) this.aClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getWorkerName() {
        return this.workerName;
    }
}
