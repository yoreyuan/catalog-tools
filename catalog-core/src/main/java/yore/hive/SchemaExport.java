package yore.hive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import yore.common.RuntimeAnnotation;
import yore.common.RuntimeAspect;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yore Yuan
 */
@Order(value = 2)
@Component
public class SchemaExport extends yore.common.FileWriter implements CommandLineRunner {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HiveDbService hiveDbService;

    /**
     *
     * @param args 入参
     *        args[0] outpath，已通过脚本
     *        args[1] databases
     *        args[2] table name
     */
    @Override
    public void run(String... args) throws Exception {
        initWriter(args[0]);
        RuntimeAspect.printSpend(this, args);
        closeWriter();
    }


    @RuntimeAnnotation(descr = "Hive")
    public void start(String[] args) throws Exception {
        // value 格式为 db|table
        Set<String> hiveTableNameSet = new HashSet<>();
        String setValueTemp = "%s.%s";

        Map<String, Long> allHiveDBIdAndName = this.hiveDbService.queryAllHiveDBNameAndId();
        if (args.length > 1) {
            //LOG.warn("目前暂不支持指定 database 和 table 名");
            String dbName = args[1];
            if (allHiveDBIdAndName.get(dbName) == null) {
                LOG.error("指定的库{}不存在，或者当前为非 Hive 类型的库", dbName);
                return;
            }
            if (args.length == 2) {
                // 仅指定库时
                List<String> allInDbTableNameList = this.hiveDbService.queryAllHiveTableByDbId(allHiveDBIdAndName.get(dbName));
                for (String tblName : allInDbTableNameList) {
                    hiveTableNameSet.add(String.format(setValueTemp, dbName, tblName));
                }
            } else {
                // 指定了库和表时
                for (int i = 2; i < args.length; i++) {
                    boolean isExist = this.hiveDbService.queryAllHiveTableByDbIdAndTblName(
                            allHiveDBIdAndName.get(dbName), args[i]);
                    if (isExist) {
                        hiveTableNameSet.add(String.format(setValueTemp, dbName, args[i]));
                    } else {
                        LOG.error("指定的{}.{}不存在，或者当前为非 Hive 类型的表，已跳过", dbName, args[i]);
                    }
                }
            }
        } else {
            for (Map.Entry<String, Long> dbEntry : allHiveDBIdAndName.entrySet()) {
                List<String> allInDbTableNameList = this.hiveDbService.queryAllHiveTableByDbId(dbEntry.getValue());
                for (String tblName : allInDbTableNameList) {
                    hiveTableNameSet.add(String.format(setValueTemp, dbEntry.getKey(), tblName));
                }
            }
        }

        for (String dbTable : hiveTableNameSet) {
            try {
                String createSql = hiveDbService.showHiveCreateTable(
                        dbTable.split("\\.")[0],
                        dbTable.split("\\.")[1]
                );
                writer.write(createSql + "\n");
                writer.flush();
            } catch (Exception e) {
                LOG.error("执行表{}时发生了异常：{}", dbTable, e.getMessage());
            }
        }
    }

}
