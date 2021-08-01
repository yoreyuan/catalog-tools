package yore.hbase;

import org.apache.hadoop.hbase.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * HBase 表结构备份
 *
 * @author Yore Yuan
 */
public class HBaseTable {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseTable.class);

    /**
     *
     * @param args 入参
     *        args[0] outpath，备份的本地路径
     *        args[1] hfile path(local or hdfs)
     */
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        String outFilePath = args[0];
        File outFile = new File(outFilePath);
        if (outFile.isDirectory()) {
            LOG.error("指定的{}为文件夹，请指定输出的文件！", outFilePath);
        }
        if (!outFile.exists()) {
            LOG.warn("输出文件 {} 不存在，将手动创建", outFilePath);
            String outFileDir = outFilePath.substring(0, outFilePath.lastIndexOf(File.separator) + 1);
            File outFileDirFile = new File(outFileDir);
            if (outFileDirFile.mkdirs()) {
                LOG.warn("{}创建成功", outFileDir);
            }
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true), StandardCharsets.UTF_8));
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();
        List<String> createCmdList = new ArrayList<>();

        if (args.length > 1) {
            List<TableName> tableNames = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                tableNames.add(TableName.valueOf(args[i]));
            }
            createCmdList = hbaseUtil.getHBaseCreateCmd(tableNames);
        } else {
            createCmdList = hbaseUtil.getHBaseCreateCmd(null);
        }

        for (String createCmd : createCmdList) {
            writer.write(createCmd + "\n");
            writer.flush();
        }
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println("---------------- HBase");
        System.out.println(" 用时：" + ((end-start) / 1000.0) + " s");
    }

}
