package eu.rd9.webencode.workers;

import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.config.Settings;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.util.FileHelper;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.apache.commons.io.FileUtils;

import javax.management.Notification;
import java.io.File;
import java.io.IOException;

/**
 * Created by renne on 17.12.2016.
 */
public class FFmpegWorker extends Worker {

    private Preset preset;
    private File inputFile;
    private double percentage = 0;
    private FFmpegWorker instance = this;

    public FFmpegWorker(Preset preset, File inputFile) {
        this.preset = preset;
        this.inputFile = inputFile;
    }

    @Override
    public void run() {
        super.run();
        try {

            File ffmpegFile = new File(Config.getInstance().getSetting(Settings.FFMPEG_PATH) + "/bin/ffmpeg.exe");
            if ( !ffmpegFile.exists() || !ffmpegFile.isFile())
                return;

            File ffprobeFile = new File(Config.getInstance().getSetting(Settings.FFMPEG_PATH) + "/bin/ffprobe.exe");
            if ( !ffprobeFile.exists() || !ffprobeFile.isFile())
                return;

            while (!FileHelper.isCompletelyWritten(this.inputFile))
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            FFmpeg ffmpeg = new FFmpeg(ffmpegFile.getAbsolutePath());
            FFprobe ffprobe = new FFprobe(ffprobeFile.getAbsolutePath());
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            FFmpegProbeResult probeResult = ffprobe.probe(this.inputFile.getAbsolutePath());

            FFmpegBuilder builder = this.preset.getFFpegBuilder(this.inputFile);
            executor.createJob(builder, new ProgressListener() {
                final double duration_us = probeResult.getFormat().duration * 1000000.0;

                @Override
                public void progress(Progress progress) {
                    double percentage = progress.out_time_ms / duration_us;
                    instance.percentage = percentage * 100;
                    String val = String.format("[%.0f%%] frame:%d time:%d ms fps:%.0f speed:%.2fx",
                            instance.percentage,
                            progress.frame,
                            progress.out_time_ms,
                            progress.fps.doubleValue(),
                            progress.speed
                    );
                    System.out.println(val);

                    if (instance.percentage == 100) {
                        state = WorkerState.FINISHED;
                    }
                }
            }).run();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return Workers.getWorker(this.getClass()).getWorkerName() + " (File: " + this.inputFile.getName() + ", " + Math.round(this.percentage) + "%)";
    }

}
