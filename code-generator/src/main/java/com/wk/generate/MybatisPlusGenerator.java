package com.wk.generate;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class MybatisPlusGenerator {

    /**
     * JDBC相关配置
     */
    private static final String DATABASE_NAME="changgou_seckill";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://192.168.0.167:3306/"+DATABASE_NAME+"?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    /**
     * 数据库表名,多个表名用逗号隔开,如果生成整个数据库的表就不写
     */
    private static final String[] TABLE_NAME = {};

    /**
     * 数据库表前缀,多个前缀用逗号隔开,没有前缀就不写
     */
    private static final String TABLE_PREFIX = "tb_";

    /**
     * 包名
     */
    private static final String PARENT_PACKAGE = "com.wk.seckill";

    /**
     * 设置生成路径
     * System.getProperty("user.dir"):本项目生成
     * 生成到其他项目，路径到项目的根目录即可D:\工作空间\项目所在文件夹
     */
    //private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String PROJECT_PATH = "E:\\ideaspace\\cg-v3\\parent\\service\\seckill";

    /**
     * 代码生成者
     */
    private static final String AUTHOR = "wk";

    /**
     * 模块名,java代码和mapper文件的模块名保持一致,空串不影响生成结果
     */
    private static final String MODULE_NAME = "";

    /**
     * 接口生成位置
     */
    private static final String INTERFACE_PATH = "/src/main/resources/mapper/"+MODULE_NAME+"/";

    /**
     * 代码生成完毕后是否打开生成目录
     */
    private static final boolean OPEN_FILE = false;

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        /*
         * 配置路径
         */
        String objPath = PROJECT_PATH + "/src/main/java";       // 获取java目录

        /*
         * 全局配置
         */
        GlobalConfig gc = new GlobalConfig()
            .setOutputDir(objPath)                  // 配置路径
            .setOpen(OPEN_FILE)                     // 是否打开生成的文件夹
            .setAuthor(AUTHOR) // author
            /* 自定义文件命名，注意 %s 会自动填充表实体属性！ */
            .setControllerName("%sController")      // 设置controller类后缀
            .setServiceName("%sService")            // 设置Service接口后缀
            .setServiceImplName("%sServiceImpl")    // 设置Service实现类后缀
            .setMapperName("%sMapper")              // 设置mapper接口后缀
            .setXmlName("%sMapper")                 // 设置sql映射文件后缀
            .setFileOverride(true)                  // 是否覆盖同名文件，默认是false
            .setActiveRecord(false)                 // 不需要ActiveRecord特性的请改为false
            .setEnableCache(false)                  // XML 二级缓存
            .setBaseResultMap(true)                 // XML ResultMap
            .setBaseColumnList(true)                // XML columList
            .setIdType(IdType.INPUT)                 // 主键ID类型
            .setDateType(DateType.ONLY_DATE);       // 时间类型
        mpg.setGlobalConfig(gc);

        /*
         * 数据源配置
         */
        DataSourceConfig dsc = new DataSourceConfig()
                .setDbType(DbType.MYSQL)
                .setDriverName(DRIVER)
                .setUrl(URL)
                .setUsername(USERNAME)
                .setPassword(PASSWORD)
                .setTypeConvert(new MySqlTypeConvert());    // 设置数据库字段类型转换类
        mpg.setDataSource(dsc);

        /*
         * 策略配置
         */
        StrategyConfig strategy = new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)       //数据库表映射到实体的命名策略
                .setColumnNaming(NamingStrategy.underline_to_camel) //字段下划线驼峰命名
                .setEntityLombokModel(true)                         //是否使用lombok
                .setRestControllerStyle(true)                       // 设置controller自动加RestController注解
                .setInclude(TABLE_NAME)                             //修改替换成你需要的表名，多个表名传数组,如果注释掉就生成库中所有的表
                .setTablePrefix(TABLE_PREFIX.split(","));     // 此处可以修改为您的表前缀
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig packageConfig = new PackageConfig()
                .setModuleName(MODULE_NAME)
                .setParent(PARENT_PACKAGE)       //父包名
                /* 默认设置 如有特殊需要可以自行更改
                .setController("controller")
                .setService("service") //服务接口 
                .setServiceImpl("service.impl") //服务实现
                .setMapper("mapper") //dao层 
                .setXml("mapper") //dao层对应的xml文件
                .setEntity("entity")*/; // pojo对象
        mpg.setPackageInfo(packageConfig);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };


        // 自定义xml的存放路径
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义Mapper.xml文件存放的路径
                return PROJECT_PATH + INTERFACE_PATH + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置生成的资源模板
        TemplateConfig templateConfig = new TemplateConfig()
                .setXml(null)           //此处设置为null，就不会再java下创建xml的文件夹
                .setController("/templates/myController.java")      //指定自定义模板
                .setMapper("/templates/myMapper.java")
                .setService("/templates/myService.java")
                .setServiceImpl("/templates/myServiceImpl.java");

        mpg.setTemplate(templateConfig);
        mpg.execute();              // 执行生成
    }

}
