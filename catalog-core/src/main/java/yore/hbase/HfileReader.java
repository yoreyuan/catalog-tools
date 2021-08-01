package yore.hbase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * HFile 解析工具
 *
 * @author Yore Yuan
 */
public class HfileReader {
    private static final Logger LOG = LoggerFactory.getLogger(HfileReader.class);

    /**
     *
     * 参见：
     * <a href='https://gitee.com/zhimashengu/hbase-release/blob/HDP-3.1.0.0-78-tag/hbase-server/src/main/java/org/apache/hadoop/hbase/io/hfile/HFilePrettyPrinter.java'>
     *   org.apache.hadoop.hbase.io.hfile.HFilePrettyPrinter
     * </a>
     * @param args 入参
     *        args[0] outpath，输出的本地路径
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
        Set<String> hfilePathSet = new HashSet<>();
        Properties properties = HFileProp.getHfileProperties();
        final String fileModel = properties.getProperty("file.model");
        final String outPrintType = properties.getProperty("out.print.type");

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                hfilePathSet.add(args[i]);
            }
        } else {
            String hfilesPath = properties.getProperty("hfiles.path").trim();
            for (String s : hfilesPath.split("\\s")) {
                if (StringUtils.isNotBlank(s)) {
                    hfilePathSet.add(s.trim());
                }
            }
        }

        try {
            Configuration configuration = HBaseConfiguration.create();
            FileSystem fileSystem = "hdfs".equalsIgnoreCase(fileModel)?
                    FileSystem.get(configuration) :
                    FileSystem.getLocal(configuration);
            //LOG.info("将解析{}个 hfile文件{}", hfilePathSet.size(), JSON.toJSON(hfilePathSet));
            System.out.printf("将解析%d个hfile文件:%s", hfilePathSet.size(), JSON.toJSON(hfilePathSet));
            for (String hfilePathStr : hfilePathSet) {
                HFile.Reader hReader = HFile.createReader(fileSystem, new Path(hfilePathStr), configuration);
                HFileScanner scanner = hReader.getScanner(true, true);
                boolean b = scanner.seekTo();
                while (scanner.next()) {
                    Cell cell = scanner.getCell();
                    byte[] rowBytes = cell.getRowArray(),
                           cfBytes = cell.getFamilyArray(),
                           qualifierBytes = cell.getQualifierArray(),
                           valueBytes = cell.getValueArray();
                    JSONObject json = new JSONObject();
                    if ("byte".equalsIgnoreCase(outPrintType)) {
                        json.put("rowkey", Bytes.toStringBinary(rowBytes, cell.getRowOffset(), cell.getRowLength()));
                        json.put("cf", Bytes.toStringBinary(cfBytes, cell.getFamilyOffset(), cell.getFamilyLength()));
                        json.put("K", Bytes.toStringBinary(qualifierBytes, cell.getQualifierOffset(), cell.getQualifierLength()));
                        json.put("V", Bytes.toStringBinary(valueBytes, cell.getValueOffset(), cell.getValueLength()));
                    } else if ("string".equalsIgnoreCase(outPrintType)) {
                        json.put("rowkey", new String(rowBytes, cell.getRowOffset(), cell.getRowLength()));
                        json.put("cf", new String(cfBytes, cell.getFamilyOffset(), cell.getFamilyLength()));
                        json.put("K", new String(qualifierBytes, cell.getQualifierOffset(), cell.getQualifierLength()));
                        json.put("V", new String(valueBytes, cell.getValueOffset(), cell.getValueLength()));
                    }
                    writer.write(json.toJSONString() + "\n");
                    writer.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("---------------- HFile");
        System.out.println(" 用时：" + ((end-start) / 1000.0) + " s");
    }

}
