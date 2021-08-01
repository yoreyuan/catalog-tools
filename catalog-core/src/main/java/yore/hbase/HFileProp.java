package yore.hbase;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author Yore Yuan
 */
public class HFileProp {


    /**
     * 根据不同环境，获取配置文件的 Properties
     */
    public static Properties getHfileProperties() {
        String osName = System.getProperty("os.name");
        Properties properties = new Properties();
        if (osName.contains("Linux")) {
            String confPath = System.getProperty("my.hfile.config.path");
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(confPath), StandardCharsets.UTF_8));
                properties.load(br);
            } catch (Exception ignored) {
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            //String confFileName = confPath.substring(confPath.lastIndexOf("/") + 1);
            String confFileName = "HFilePath.properties";
            // 通过读取编译后的 classes 中文件加载
            InputStream in = HFileProp.class.getClassLoader().getResourceAsStream(confFileName);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)) ) {
                properties.load(br);
            } catch (IOException ignored) {
            }
        }
        return properties;
    }

    public static void main(String[] args) {
        Properties properties = HFileProp.getHfileProperties();
        String hfilesPath = properties.getProperty("hfiles.path").trim();
        for (String s : hfilesPath.split("\\s")) {
            if (StringUtils.isNotBlank(s)) {
                System.out.println("> " + s);
            }
        }
    }

}
