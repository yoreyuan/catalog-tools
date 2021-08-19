package yore.common.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author Yore Yuan
 */
public class SysProperties {


    /**
     * 根据不同环境，获取配置文件的 Properties
     * <p>默认以系统环境变量中的 my.config.path 指定的配置文件 </p>
     *
     * @param propFileName 用于非 Linux 是运行时加载的配置
     */
    public static Properties getComponeProperties(String propFileName) {
        return getProperties("my.config.path", propFileName);
    }


    /**
     * 获取系统配置文件的 Properties
     * @return Properties
     */
    public static Properties getSysProperties() {
        return getProperties("sys.config.path", "application.properties");
    }


    /**
     * 获取指定的配置文件 Properties
     * @param configPath 组件/插件配置文件路径属性名
     * @param propFileName 配置文件名，本地运行是加载的
     * @return Properties
     */
    private static Properties getProperties(String configPath, String propFileName) {
        String osName = System.getProperty("os.name");
        Properties properties = new Properties();
        if (osName.contains("Linux")) {
            String confPath = System.getProperty(configPath);
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
            // 通过读取编译后的 classes 中文件加载
            if (!propFileName.endsWith(".properties")) {
                propFileName += ".properties";
            }
            InputStream in = SysProperties.class.getClassLoader().getResourceAsStream(propFileName);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)) ) {
                properties.load(br);
            } catch (IOException ignored) {
            }
        }
        return properties;
    }

}
