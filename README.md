# 
DIYConfig是一个配置文件管理的Java工具包。他能任意的配置不同环境下的配置文件读取策略。
几乎所有的开发我们都要配置文件，我们不得不为每个项目写特定的组织结构。
DIYConfig工具包为了解决这些重复的工作。他可以轻而易举的获取你不同环境的配置文件，
让你得心应手的随心所欲组织配置文件。
# 安装
## 方法一
下载jar包在jar包目录下执行
```
mvn install:install-file -Dfile=./DIYConfig-2021.04.07.jar -DgroupId=org.cosoc.DIYConfig -DartifactId=DIYConfig -Dversion=2021.04.07 -Dpackaging=jar
```
## 方法二
下载源文件在目录下执行
```
mvn clean install
```
## 方法三
直接使用中央仓库

## 添加依赖包
```
<dependency>
	<groupId>org.cosoc.DIYConfig</groupId>
	<artifactId>DIYConfig</artifactId>
	<version>2021.04.07</version>
</dependency>
```
# 使用
把DIYConfig.yml配置文件复制到资源目录下，进行配置后。配置完成，创建对象 DIYConfigClient,该对象将读取需要的配置文件.
使用getAllConfigFile()方法获取所有文件！返回的是 Map<String,File>,key是文件的主配置目录下的相对路径加主配置目录
