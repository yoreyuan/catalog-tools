package yore.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Yore Yuan
 */
public abstract class FileWriter {
    private static final Logger LOG = LoggerFactory.getLogger(FileWriter.class);
    protected static BufferedWriter writer;

    /**
     * Initialize writer
     * @param outFilePath Output file path
     */
    protected static void initWriter(String outFilePath) {
        File outFile = new File(outFilePath);
        if (outFile.isDirectory()) {
            LOG.error("The specified {} is a folder, please specify the output file!", outFilePath);
        }
        if (!outFile.exists()) {
            LOG.warn("The output file {} does not exist, it will be created automatically.", outFilePath);
            String outFileDir = outFilePath.substring(0, outFilePath.lastIndexOf(File.separator) + 1);
            File outFileDirFile = new File(outFileDir);
            if (outFileDirFile.mkdirs()) {
                LOG.warn("{} Created successfully!", outFileDir);
            }
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Close writer
     */
    protected static void closeWriter() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
