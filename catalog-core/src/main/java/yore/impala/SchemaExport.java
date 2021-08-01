package yore.impala;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yore Yuan
 */
@Order(value = 3)
@Component
public class SchemaExport implements CommandLineRunner {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ImpalaDbService impalaDbService;

    /**
     *
     * @param args 入参
     *        args[0] outpath，已通过脚本
     *        args[1] databases
     *        args[2] table name
     */
    @Override
    public void run(String... args) throws Exception {
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
        // value 格式为 db|table
        Set<String> impalaTableNameSet = new HashSet<>();
        String setValueTemp = "%s.%s";

        Map<String, Long> allDBIdAndName = this.impalaDbService.queryAllImpalaDBNameAndId();
        if (args.length > 1) {
            String dbName = args[1];
            if (allDBIdAndName.get(dbName) == null) {
                LOG.error("指定的库{}不存在，请确认库名", dbName);
                return;
            }
            if (args.length == 2) {
                List<String> allInDbTableNameList = this.impalaDbService.queryAllImpalaTableByDbId(allDBIdAndName.get(dbName));
                for (String tblName : allInDbTableNameList) {
                    impalaTableNameSet.add(String.format(setValueTemp, dbName, tblName));
                }
            } else {
                for (int i = 2; i < args.length; i++) {
                    boolean isExist = this.impalaDbService.queryAllImpalaTableByDbIdAndTblName(
                            allDBIdAndName.get(dbName), args[i]);
                    if (isExist) {
                        impalaTableNameSet.add(String.format(setValueTemp, dbName, args[i]));
                    } else {
                        LOG.error("指定的{}.{}不存在，已自动跳过", dbName, args[i]);
                    }
                }
            }
        } else {
            for (Map.Entry<String, Long> dbEntry : allDBIdAndName.entrySet()) {
                List<String> allInDbTableNameList = this.impalaDbService.queryAllImpalaTableByDbId(dbEntry.getValue());
                for (String tblName : allInDbTableNameList) {
                    impalaTableNameSet.add(String.format(setValueTemp, dbEntry.getKey(), tblName));
                }
            }
        }

        for (String dbTable : impalaTableNameSet) {
            try {
                String createSql = impalaDbService.showImpalaCreateTable(
                        dbTable.split("\\.")[0],
                        dbTable.split("\\.")[1]
                );
                writer.write(createSql + "\n");
                writer.flush();
            } catch (Exception e) {
                LOG.error("执行表{}是发生了异常：{}", dbTable, e.getMessage());
            }
        }

        writer.close();
        long end = System.currentTimeMillis();
        System.out.println("---------------- Hive");
        System.out.println(" 用时：" + ((end-start) / 1000.0) + " s");
    }
}
