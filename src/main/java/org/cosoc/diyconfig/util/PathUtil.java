package org.cosoc.diyconfig.util;

import java.io.File;

/**
 * 这个类获取路基相关的类
 * @author cosoc
 */
public class PathUtil {

    private Class<?> userSpaceClass;

    /**
     * 当创建这个类是传入一个Class
     * 这个Class是任意用户空间的类
     * 为什么这样做：
     *    因为这个类打包以后获取的路径是他自己的jar包路径
     *    而不是应用环境的路径,所以传入Class做为参考
     * @param userSpaceClass 用户空间任意Class
     */
    public PathUtil(Class<?> userSpaceClass) {
        this.userSpaceClass = userSpaceClass;
    }

    /**
     * 检查当前是什么环境
     * 该方法的逻辑：
     *    算法是这样的,当打包jar包后获取的类路径都会
     *    jar包明加！符号！
     *    以这个为切入点就是检测是否存在这个！符号
     *    有还有检测是否当前目录存在这么一个jar包名
     *    后得出结果
     *
     *    这个方法是基于打jar包后会带一个特殊符号!
     *
     *    如果这个！符号不存在那么这个方法彻底的崩塌了
     *
     * @return 返回当前真实的路径
     */
    public String getDefaultPath() {

        String projectRoot = System.getProperty("user.dir") + System.getProperty("file.separator");

        String path = userSpaceClass.getResource("").getPath();

        //如果不包含！则是开发环境
        if (!path.contains("!")) {
            return getClass().getResource("/").getPath();
        }

        /**
         * 判断是否已经打了jar包
         * 把整体路径打散放入数组
         * 找出数组中含有！字符的字符串
         * 然后用这个字符检测当前路径是否包含该包
         * 如果包含说明是打包的
         * 如果没有默认任务是测试环境
         */
        String[] patches = path.split("/");
        //反向遍历更快拿到！符号
        for (int i = patches.length - 1; i >= 0; i--) {
            //找到!符号
            if (patches[i].contains("!")) {
                File file = new File(projectRoot);
                if (file.exists()) {
                    File[] files = file.listFiles();
                    //文件夹是空的
                    if (null == files || files.length == 0) {
                        return path;
                    } else {
                        for (File file2 : files) {
                            if ((file2.getName() + "!").equals(patches[i])) {
                                return projectRoot;
                            }
                        }
                    }
                }
            }
        }
        return path;
    }

    /**
     * 获取java包的路径
     * @return java路径加上 /
     */
    public String getJarPath() {
        return System.getProperty("user.dir") + System.getProperty("file.separator");
    }

    /**
     * 获取系统文件分割符
     * @return 返回分隔符
     */
    public String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * 获取系统路径分割符
     * @return 返回分隔符
     */
    public String getPathSeparator() {
        return System.getProperty("path.separator");
    }

}
