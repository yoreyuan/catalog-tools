package yore.impala;

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
@Order(value = 3)
@Component
public class SchemaExport extends yore.common.FileWriter implements CommandLineRunner {
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
        initWriter(args[0]);
        RuntimeAspect.printSpend(this, args);
        closeWriter();
    }

    @RuntimeAnnotation(descr = "Impala")
    public void start(String[] args) throws Exception {
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
    }

}
