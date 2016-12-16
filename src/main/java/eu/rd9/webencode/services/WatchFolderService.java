package eu.rd9.webencode.services;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Created by renne on 16.12.2016.
 */
public class WatchFolderService extends Thread {

    private List<String> objects = new ArrayList<>();
    private String watchFolderPath;
    private boolean run = true;

    public WatchFolderService(String path) {
        this.watchFolderPath = path;
        this.start();
    }

    public String getWatchFolderPath() {
        return this.watchFolderPath;
    }

    public List<String> getFiles() {
        return this.objects;
    }

    @Override
    public void run() {
        watchDirectoryPath(Paths.get(this.watchFolderPath));
    }

    public void watchDirectoryPath(Path path) {
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path,
                    "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path + " is not a folder");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        System.out.println("Watching path: " + path);
        FileSystem fs = path.getFileSystem();
        try (WatchService service = fs.newWatchService()) {
            path.register(service, ENTRY_CREATE);
            WatchKey key = null;
            while (this.run) {
                key = service.take();
                WatchEvent.Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                    } else if (ENTRY_CREATE == kind) {
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        objects.add(newPath.toString());
                    }

                    System.out.println(kind.toString());

                }
                if (!key.reset()) {
                    break;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex2) {
            ex2.printStackTrace();
        }
    }

    public void wStop ()
    {
        this.run = false;
        super.stop();
        super.interrupt();
    }

}
