package org.cosoc.diyconfig.entity;



import java.util.List;


/**
 * 自身配置文件信息
 * 所有字段以层次关系命名
 * @author cosoc
 */
public class DIYConfigInfo {


    //版本号
    public static Double version;

    //狩猎标记
    public static String R_Prey;
    //狩猎类型
    public static String R_PreyTarget;
    //文件需要截取掉的字符串
    public static List<String> R_fileMapFlagKey;
    //配置文件主目录
    public static List<String> R_ConfigFilePath;
    //文件目录排除正则表达式
    public static List<String> R_E_Regex;
    //文件目录排除特定文件
    public static List<String> R_E_SpecialFile;

    //测试配置文件是否正确读取的toString
    public static String testToString() {
       return  "version:" + version  + "\n"
               + "R_Prey:" + R_Prey + "\n"
                + "R_PreyTarget:" + R_PreyTarget + "\n"
                + "R_ConfigPath:" + R_ConfigFilePath + "\n"
                + "R_S_Read:" + R_E_Regex + "\n"
                + "R_E_Regex:" + R_E_SpecialFile + "\n"
                + "R_fileMapFlagKey" + R_fileMapFlagKey + "\n";
    }
}
