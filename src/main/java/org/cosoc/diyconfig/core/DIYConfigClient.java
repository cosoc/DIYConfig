package org.cosoc.diyconfig.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cosoc.diyconfig.entity.DIYConfigInfo;
import org.cosoc.diyconfig.util.FileTypeConfigManage;
import org.cosoc.diyconfig.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cosoc
 *
 */
public class DIYConfigClient {

    @SuppressWarnings("unused")
	private Class<?> userSpaceClass;
    private ConfigReader configReader;
    private Logger logger;
    private PathUtil pathUtil;
    private Map<String,File> allConfigFile;


    /**
     * 默认构造方法
     * @param userSpaceClass
     */
    public DIYConfigClient(Class<?> userSpaceClass) {
        this.userSpaceClass = userSpaceClass;
        this.configReader = new ConfigReader(userSpaceClass);
        this.logger = LoggerFactory.getLogger(getClass());
        this.pathUtil = new PathUtil(userSpaceClass);
        //默认配置文件位置
        String defaultConfigPath = pathUtil.getDefaultPath();
        String defaultConfigFile = defaultConfigPath + "DIYConfig.yml";
        readDIYConfig(defaultConfigFile);
        this.allConfigFile = readConfig();
    }

    /**
     * 给出自定义路径构造方法
     * @param userSpaceClass
     * @param configPath
     */
    public DIYConfigClient(Class<?> userSpaceClass,String configPath) {
        this.userSpaceClass = userSpaceClass;
        this.configReader = new ConfigReader(userSpaceClass);
        this.logger = LoggerFactory.getLogger(getClass());
        this.pathUtil = new PathUtil(userSpaceClass);
        readDIYConfig(configPath);
        this.allConfigFile = readConfig();
    }

    /**
     * 获取所有的配置文件
     * 此方法不需要每次读取
     * @return
     */
    public Map<String, File> getAllConfigFile() {
        return allConfigFile;
    }

    /**
     * 读取所有配置文件
     * 这个文件提供用户调用
     * 他会返回所有符合条件的File列表
     * 此方法需要每次获取都读取
     * @return
     */
    public Map<String, File> readConfig(){

        Map<String,File> configMap = new HashMap<String,File>();
        for (String path : DIYConfigInfo.R_ConfigFilePath) {
            configReader.readClassicConfig(path,configMap);
        }
        return configMap;
    }

    /**
     * 读取自身的配置文件
     * @param filePath
     */
    @SuppressWarnings("unchecked")
    private void readDIYConfig(String filePath) {
        //判断一个文件配置文件是否存在
        File diyConfig = new File(filePath);
        if (!diyConfig.exists()) {
            logger.error("默认配置" + filePath + "文件没有被发现");
            return;
        }
        //读取配置文件内容
        Iterable<Object> ret = FileTypeConfigManage.ymlReader(filePath);
        for (Object o : ret) {
            Map<String,Object> fileData = (Map<String,Object>)o;

            //版本信息
            DIYConfigInfo.version = (Double) fileData.get("version");
            //版本信息逻辑处理

            //基础base配置
			Map<String,Object> readRule = (Map<String,Object>)fileData.get("readRule");

            //初始化
            DIYConfigInfo.R_ConfigFilePath = new ArrayList<String>();
            //如果设置为null时这里将读取不到值
            if(readRule.get("configFilePath") != null){
                List<String> configPaths = (List<String>)readRule.get("configFilePath");
                if(configPaths.isEmpty()) {
                    //默认的配置文件位置既主配置目录下
                    DIYConfigInfo.R_ConfigFilePath.add(pathUtil.getDefaultPath());
                }else{
                    for (String path : configPaths){
                        if(path.contains("classpath:")){
                            String[] rPath = path.split("\\:");
                            path = pathUtil.getDefaultPath() + rPath[rPath.length-1];
                        }
                        if(path.endsWith(pathUtil.getFileSeparator())){
                            DIYConfigInfo.R_ConfigFilePath.add(path);
                        }else {
                            DIYConfigInfo.R_ConfigFilePath.add(path + pathUtil.getFileSeparator());

                        }
                    }
                }
            }

            //狩猎标记
            if(readRule.get("prey") != null) {
                DIYConfigInfo.R_Prey = readRule.get("prey").toString();
            }
            //狩猎类型
            if(readRule.get("preyTarget") != null) {
                DIYConfigInfo.R_PreyTarget = readRule.get("preyTarget").toString();
            }


            //文件排除规则
            Map<String,Object> exclude = (Map<String,Object>)readRule.get("exclude");
            //排除正则表达式
            if(exclude.get("regex") != null) {
                DIYConfigInfo.R_E_Regex = (List<String>) exclude.get("regex");
            }
            //排除的特殊文件
            DIYConfigInfo.R_E_SpecialFile = new ArrayList<String>();
            if(exclude.get("specialFile") != null){
                List<String> pathList = (List<String>) exclude.get("specialFile");
                for(String path : pathList) {
                    if(path.contains("classpath:")){
                        String[] rPath = path.split("\\:");
                        path = pathUtil.getDefaultPath() + rPath[rPath.length-1];
                    }
                    if(path.endsWith(pathUtil.getFileSeparator())){
                        DIYConfigInfo.R_E_SpecialFile.add(path);
                    }else {
                        DIYConfigInfo.R_E_SpecialFile.add(path + pathUtil.getFileSeparator());
                    }
                }
            }
        }
    }


}
