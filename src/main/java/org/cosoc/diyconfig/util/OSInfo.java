package org.cosoc.diyconfig.util;

public class OSInfo {

    public static final String OS_NAME = System.getProperty("os.name");

    public static boolean isWindows() {
        return OS_NAME.toLowerCase().indexOf("windows") >= 0;
    }
}
