package eu.rd9.webencode.data;

import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.config.Settings;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by renne on 16.12.2016.
 */
public class Preset {

    public String Preset_Name;
    public PresetParameter presetParameter = new PresetParameter();
    private UUID uuid = UUID.randomUUID();

    public void setUuid(String uuidStr) {
        this.uuid = UUID.fromString(uuidStr);
    }

    public String getUUIDStr() {
        return this.uuid.toString();
    }

    public FFmpegBuilder getFFpegBuilder(File inputFile) {

        FFmpegBuilder fFmpegBuilder = new FFmpegBuilder();
        fFmpegBuilder.setInput(inputFile.getAbsolutePath());
        FFmpegOutputBuilder fFmpegOutputBuilder = fFmpegBuilder.addOutput(Config.getInstance().getSetting(Settings.CONVERTED_FILES_OUTPUT_PATH) + "/" + FilenameUtils.removeExtension(inputFile.getName()) + "_converted.mp4");

        for (PresetOption option : PresetOption.values()) {
            String val = this.presetParameter.getOptionValue(option);
            if (val == null) {
             System.out.println(option + " is null!");
                continue;
            }

            switch (option) {
                case VIDEO_OUTPUT_BITRATE:
                    System.out.println("Set " + option.toString() + " to " + val);
                    fFmpegOutputBuilder.setVideoBitRate(Long.parseLong(val));
                    break;
                case VIDEO_OUTPUT_FORMAT:
                    System.out.println("Set " + option.toString() + " to " + val);
                    fFmpegOutputBuilder.setFormat(val);
                    break;
                case VIDEO_CODEC:
                    System.out.println("Set " + option.toString() + " to " + val);
                    fFmpegOutputBuilder.setVideoCodec(val);
                    break;
                default:
                    break;
            }
        }
        fFmpegOutputBuilder.done();
        return fFmpegBuilder;
    }

    @Override
    public String toString() {
        return this.Preset_Name;
    }

}
