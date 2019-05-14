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
     * @param filePath
     * @return
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
