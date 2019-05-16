package org.cosoc.diyconfig.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.yaml.snakeyaml.Yaml;

/**
 * @author cosoc
 *
 */
public class FileTypeConfigManage {

    /**
     * 返回读取到的文件
     * @param filePath 文件路径
     * @return 返回文件集
     */
    public static  Iterable<Object>  ymlReader(String filePath){
        //配置文件读取
        Yaml yaml = new Yaml();
        try {
            return yaml.loadAll(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
