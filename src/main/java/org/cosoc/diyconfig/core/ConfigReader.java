package org.cosoc.diyconfig.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cosoc.diyconfig.common.DIYConfigKeyWord;
import org.cosoc.diyconfig.entity.DIYConfigInfo;
import org.cosoc.diyconfig.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 配置文件读取核心类
 * @author cosoc
 */
public class ConfigReader {

    @SuppressWarnings("unused")
	private Class<?> userSpaceClass;
    private PathUtil pathUtil;
    @SuppressWarnings("unused")
	private Logger logger;

    public ConfigReader(Class<?> userSpaceClass) {
        this.userSpaceClass = userSpaceClass;
        this.pathUtil = new PathUtil(userSpaceClass);
        this.logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * 经典配置文件读取
     * 所有配置文件放置主配置目录下或者子目录下
     * 该方法将根据配置文件规则一一的读取
     * @param path 读取的目录
     * @param allConfigFile 所有的文件集合
     */
    public void readClassicConfig(String path, Map<String,File> allConfigFile){
        //提取符合规则的配置文件
        File rootDir = new File(path);
        if (!rootDir.exists()) {
            throw new RuntimeException("指定的:" + path + " 不存在");
        }
        //列出所有配置文件
        File[] files = rootDir.listFiles();
        //找出符合规则的文件
        for (File file : files) {
            if(file.isFile()) {
                //如果配置文件不是排除的文件,将进行添加
                if(isReadFile(file)){
                    allConfigFile.put(getFileKey(file),file);
                }
            //如果是目录且需要遍历子目录
            }else if(file.isDirectory()){
                if(isReadFile(file)){ //如果该目录不是排除目录
                    readClassicConfig(path + file.getName() + pathUtil.getFileSeparator(),allConfigFile);
                }
            }
        }
    }

    /**
     * 判断是否是需要读取该文件
     * 首先判断是否是排除文件
     * 然后判断读取规则
     * 如果读取返回true
     * 如果不读取返回 false
     * @param file 需要判断的文件
     */
    private boolean isReadFile(File file){


        /**
         * 首先检测文件排除项的统配符是否设置
         * 如果已经设置则处理是否需要读取忘记
         */
        if(DIYConfigInfo.R_E_Regex != null
                && !DIYConfigInfo.R_E_Regex.isEmpty()) {
            for(String regexStr : DIYConfigInfo.R_E_Regex){
                // 编译正则表达式
                Pattern pattern = Pattern.compile(regexStr);
                //如果文件中包含
                String fileName = file.getName();
                Matcher matcher = pattern.matcher(fileName);
                //如果文件名匹配正则表达式
                if(matcher.matches()) {
                    //剔除这个不需要的文件
                    return false;
                }
            }
        }

        /**
         * 其次判断特殊列表中的的排除文件或者文件
         * 这里的算法是:
         *   获取排除项中的所有文件或者目录
         *   创建一个文件后获取他的路径
         *   与当前文件路径进行比较
         *   如果相等就是同一个文件
         */
        List<String> fileList = DIYConfigInfo.R_E_SpecialFile;
        //创建文件
        for(String filePath: fileList){
            File excludeFile = new File(filePath);
            if(!excludeFile.exists()){
                throw new RuntimeException("指定的:" + filePath + "不存在");
            }
            try {
                //如果是需要排除的文件
                if(excludeFile.getCanonicalPath().equals(file.getCanonicalPath())){
                    return false;
                }
            } catch (IOException e) {
                //文件路径获取异常
                e.printStackTrace();
            }
        }


        /**
         * 全面读取文件
         * DIYConfigInfo.R_Prey为null
         * 或者
         * DIYConfigInfo.R_PreyTarget为null
         * 视为读取主配置目录下视为所有文件
         * 适合项目上线发布使用
         */
        if(DIYConfigInfo.R_Prey == null || DIYConfigInfo.R_PreyTarget == null){
            return true;
        }

        /**
         * 如果是统配的方式读取配置文件
         * 这里需要处理文件是否符合统配的要求
         */
        if(DIYConfigInfo.R_Prey != null
                && DIYConfigInfo.R_PreyTarget.equals(DIYConfigKeyWord.ENV_TYPE_F)
                ){

            //如果是目录则需要继续读取
            if(file.isDirectory()){
                return true;
            }
            // 编译正则表达式
            Pattern pattern = Pattern.compile(DIYConfigInfo.R_Prey);
            //如果文件中包含
            String fileName = file.getName();
            Matcher matcher = pattern.matcher(fileName);
            //如果文件名匹配正则表达式
            if(matcher.matches()) {
                //如果匹配说需要读取
                return true;
            }
        }

        /**
         * 如果是目录组织读取配置文件配置文件
         * 这里使用路劲相等的算法进行处理
         */
        if(DIYConfigInfo.R_Prey != null
                && DIYConfigInfo.R_PreyTarget.equals(DIYConfigKeyWord.ENV_TYPE_D)){
            for(String pathName : DIYConfigInfo.R_ConfigFilePath){
                try {
                    if(file.getCanonicalPath().contains(pathName + DIYConfigInfo.R_Prey)){
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  false;
        }

        /**
         * 以上都没有匹配视为不读取
         */
        return false;
    }


    /**
     * 获取文件的map中的key
     * 这个方法返回的是相对于主配置目录下的路径作为文件的key
     * 如果这个文件不在config路径之下，将在前加&以做标识
     * @param file 文件原始名称
     * @return 返回还原后的文件名(可能改变也可能不变)
     */
    private String getFileKey(File file) {
        String path = null;
        //获取真实文件名
        String realFileName = file.getName();
        for(String flagStr : DIYConfigInfo.R_fileMapFlagKey){
            realFileName = file.getName();
            if(file.getName().contains(flagStr)){
                realFileName = file.getName().replace(flagStr,"");
                break;
            }
        }

        //获取路径
        try {
            for(String pathStr : DIYConfigInfo.R_ConfigFilePath) {
                if(file.getCanonicalPath().contains(pathStr)){
                    File parentDir = new File(pathStr);
                    if(!((file.getParentFile().getCanonicalPath() + pathUtil.getFileSeparator()).equals(pathStr ))){
                        path = parentDir.getName()
                                + pathUtil.getFileSeparator()
                                + file.getParentFile().getCanonicalPath().replace(pathStr,"")
                                + pathUtil.getFileSeparator()
                                + realFileName;
                    }else{
                        path  = parentDir.getName()
                                + pathUtil.getFileSeparator()
                                + realFileName;
                    }
                    return path;
                }
            }
            //若只是单独的文件(没有主目录孤立的文件)
            File parentDir = file.getParentFile();
            path  = parentDir.getName() + pathUtil.getFileSeparator() + realFileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
