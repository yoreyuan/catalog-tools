package yore.hbase;

import org.apache.hadoop.hbase.TableName;
import yore.common.RuntimeAnnotation;
import yore.common.RuntimeAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * HBase 表结构备份
 *
 * @author Yore Yuan
 */
public class HBaseTable extends yore.common.FileWriter {

    /**
     *
     * @param args 入参
     *        args[0] outpath，备份的本地路径
     *        args[1] hfile path(local or hdfs)
     */
    public static void main(String[] args) throws Exception {
        initWriter(args[0]);
        RuntimeAspect.printSpend(HBaseTable.class, args);
        closeWriter();
    }


    @RuntimeAnnotation(descr = "HBase")
    public static void start(String[] args) throws Exception {
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
    }

}
