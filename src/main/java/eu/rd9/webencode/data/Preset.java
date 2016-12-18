package eu.rd9.webencode.data;

import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.config.Settings;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by renne on 16.12.2016.
 */
public class Preset {

    public String Preset_Name;
    public String FFmpeg_Parameters;


    public FFmpegBuilder getFFpegBuilder(File inputFile)
    {
        return  new FFmpegBuilder()
                .setInput(inputFile.getAbsolutePath())
                .addOutput(Config.getInstance().getSetting(Settings.CONVERTED_FILES_OUTPUT_PATH) + "/" + FilenameUtils.removeExtension(inputFile.getName()) + "_converted.mp4")
                .setVideoCodec("libx264")
                .setFormat("mp4").done();
    }

    @Override
    public String toString()
    {
        return this.Preset_Name;
    }

}
