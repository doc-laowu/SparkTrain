//package RealTimeDial;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.commons.lang.Validate;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.StorageLevels;
//import org.apache.spark.api.java.function.Function0;
//import org.apache.spark.broadcast.Broadcast;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SaveMode;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.streaming.Durations;
//import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
//import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import org.apache.spark.util.LongAccumulator;
//import org.apache.spark.util.SizeEstimator;
//import org.apache.spark.streaming.Time;
//
///**
// * BillConsumer 大屏实时计算
// *         1、hdfs dfs -rm -r hdfs://bd001:8020/data/consumer/bill/checkpoint/*
// *         2、hdfs dfs -rm -r hdfs://bd001:8020/data/dashboard/ruian/sdt=curTime
// *         3、spark-submit --class com.mengyao.graph.etl.apps.commons.datasource.bill.BillConsumer --master yarn --deploy-mode cluster --driver-memory 4g --executor-cores 6 --executor-memory 5g --queue default --verbose data-graph-etl.jar
// *
// * @author mengyao
// *
// */
//public class BillConsumer {
//
//    private static final ThreadLocal<SimpleDateFormat> FORMATTER_YMD = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));
//    private static final ThreadLocal<SimpleDateFormat> FORMATTER_YMDHMS = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));
//    private static final ThreadLocal<SimpleDateFormat> FORMATTER_YMDHMSS = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmssSSS"));
//    private static final String BASE_PATH = "hdfs://bd001:8020/data/dashboard/ruian/";
//    private static final String TMP_PATH = "/merge";
//    private static String appName = "BillConsumer";
//    private static String logLevel = "ERROR";
//
//
//    public static void main(String[] args) {
//        args = new String[] {"hdfs://bd001:8020/data/consumer/bill/checkpoint", "10", "base.mq.goo.com:9877", "Bill", "bill_dev_group_00013"};
//        if (args.length < 5) {
//            System.err.println("Usage: "+appName+" <checkpointDir> <milliseconds> <namesrvAddr> <groupId> <topic>");
//            System.exit(1);
//        }
//
//        String checkPointDir = args[0];
//        int second = Integer.parseInt(args[1]);
//        String namesrv = args[2];
//        Validate.notNull(namesrv, "RocketMQ namesrv not null!");
//        String topic = args[3];
//        Validate.notNull(topic, "RocketMQ topic not null!");
//        String group = args[4];
//        Validate.notNull(group, "RocketMQ group not null!");
//
//        Function0<JavaStreamingContext> jscFunc = () -> createJavaSparkStreamingContext(checkPointDir, second, namesrv, topic, group);
//        JavaStreamingContext jssc = JavaStreamingContext.getOrCreate(checkPointDir, jscFunc, new Configuration());
//        jssc.sparkContext().setLogLevel(logLevel);
//
//        try {
//            jssc.start();
//            jssc.awaitTermination();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            jssc.stop(false, true);//关闭StreamingContext但不关闭SparkContext，同时等待数据处理完成
//        }
//    }
//
//    /**
//     * 获取当前时间yyyyMMdd，匹配账单数据saleTime
//     * @param curTime
//     * @return
//     */
//    static String getCurrentDate(long curTime) {
//        return FORMATTER_YMD.get().format(new Date(curTime));
//    }
//
//    /**
//     * 打印bill RDD<BCD>的分区及占用空间大小，debug方法
//     * @param rdd
//     * @param time
//     */
//    static void printStream(JavaRDD<BCD> rdd, Time time) {rdd.id();
//        System.out.println("==== time: "+FORMATTER_YMDHMSS.get().format(new Date(time.milliseconds()))+", rdd: partitions="+rdd.getNumPartitions()+", space="+SizeEstimator.estimate(rdd)/1048576+"mb");
//    }
//
//    /**
//     * 合并parquet小文件
//     * @param session
//     * @param dfsDir
//     */
//    static void mergeSmallFiles(Dataset<Row> fulls, SparkSession session, String dfsDir) {
//        fulls.coalesce(1).write().mode(SaveMode.Overwrite).parquet(BASE_PATH+TMP_PATH);
//        session.read().parquet(BASE_PATH+TMP_PATH).coalesce(1).write().mode(SaveMode.Overwrite).parquet(dfsDir);
//    }
//
//    /**
//     * 创建DFS Dir
//     * @param session
//     * @param dfsDir
//     */
//    static void mkDfsDir(SparkSession session, String dfsDir) {
//        FileSystem fs = null;
//        try {
//            fs = FileSystem.get(session.sparkContext().hadoopConfiguration());
//            Path path = new Path(dfsDir);
//            if(!fs.exists(path)) {
//                fs.mkdirs(path);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (null != fs) {fs.close();}
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 容错Driver
//     * @param checkPointDir
//     * @param second
//     * @param namesrv
//     * @param topic
//     * @param group
//     * @return
//     */
//    static JavaStreamingContext createJavaSparkStreamingContext(String checkPointDir, int second, String namesrv, String topic, String group) {
//        try {
//            SparkConf conf = new SparkConf()
//                    //==== Enable Back Pressure
//                    .set("spark.streaming.backpressure.enabled", "true")//启用被压机制
//                    .set("spark.streaming.backpressure.initialRate", "50000")//初始接收数据条数，如该值为空时使用spark.streaming.backpressure.initialRate为默认值
//                    .set("spark.streaming.receiver.maxRate", "100")//每秒接收器可接收的最大记录数
//                    //==== Enable Dynamic Resource Allocation
//                    .set("spark.dynamicAllocation.enabled", "false")//禁用spark动态资源分配
//                    .set("spark.streaming.dynamicAllocation.enabled", "true")//启用SparkStreaming动态资源分配，该配置和spark动态资源分配存在冲突，只能使用一个
//                    .set("spark.streaming.dynamicAllocation.minExecutors", "2")//启用SparkStreaming动态资源分配后的给应用使用的最小executor数
//                    .set("spark.streaming.dynamicAllocation.maxExecutors", "3")//启用SparkStreaming动态资源分配后的给应用使用的最大executor数
//                    //==== Spark Streaming Parallelism and WAL
//                    .set("spark.streaming.concurrentJobs", "1")//并行job数量，默认1
//                    .set("spark.streaming.blockInterval", "5000")//SparkStreaming接收器接收数据后5000毫秒生成block，默认200毫秒
//                    .set("spark.streaming.receiver.writeAheadLog.enable", "true")//开启SparkStreaming接收器的WAL来确保接收器实现至少一次的容错语义
//                    .set("spark.streaming.driver.writeAheadLog.allowBatching", "true")//driver端WAL
//                    .set("spark.streaming.driver.writeAheadLog.batchingTimeout", "15000")
//                    //==== Hive on Spark
//                    .set("hive.execution.engine", "spark")//设置hive引擎为spark，hdp-2.6.1.0默认支持tez、mr，可通过应用级别配置使用Hive on Spark
//                    .set("hive.enable.spark.execution.engine", "true")//启用Hive on Spark
//                    .set("spark.driver.extraJavaOptions", "-Dhdp.version=2.6.1.0-129")//hdp-2.6.1.0中要求Hive on Spark必须指定driver的jvm参数
//                    .set("spark.yarn.am.extraJavaOptions", "-Dhdp.version=2.6.1.0-129")//hdp-2.6.1.0中要求Hive on Spark必须指定ApplicationMaster的jvm参数
//                    //==== Hive Merge Small Files
//                    .set("hive.metastore.uris", "thrift://bd001:9083")//hive ThriftServer
//                    .set("hive.merge.sparkfiles", "true")//合并spark小文件
//                    .set("hive.merge.mapfiles", "true")//在只有map任务的作业结束时合并小文件。
//                    .set("hive.merge.mapredfiles", "true")//在mapreduce作业结束时合并小文件。
//                    .set("hive.merge.size.per.task", "268435456")//作业结束时合并文件的大小。
//                    .set("hive.merge.smallfiles.avgsize", "100000")//当作业的平均输出文件大小小于此数量时，Hive将启动另一个map-reduce作业，以将输出文件合并为更大的文件。如果hive.merge.mapfiles为true，则仅对仅map作业执行此操作;对于hive.merge.mapredfiles为true，仅对map-reduce作业执行此操作。
//                    //==== Spark SQL Optimizer
//                    .set("spark.sql.warehouse.dir", "hdfs://bd001:8020/apps/hive/warehouse")//SparkSQL依赖的hive仓库地址
//                    .set("spark.sql.files.maxPartitionBytes", "134217728")//SparkSQL读取文件数据时打包到一个分区的最大字节数
//                    .set("spark.sql.files.openCostInBytes", "134217728")//当SparkSQL读取的文件中有大量小文件时，小于该值的文件将被合并处理，默认4M，此处设置为128M
//                    .set("spark.sql.shuffle.partitions", "600")//SparkSQL运行shuffle的并行度
//                    .set("spark.sql.autoBroadcastJoinThreshold", "67108864")//设置为64M，执行join时自动广播小于该值的表，默认10M
//                    //==== Spark Core Configure
//                    .set("spark.rdd.compress","true")//开启rdd压缩以节省内存
//                    .set("spark.default.parallelism", "600")//并行任务数
//                    .set("spark.rpc.askTimeout", "300")//spark rpc超时时间
//                    .set("spark.eventLog.enabled", "true")//开启eventLog
//                    //==== Application Configure
//                    .set("spark.app.name", appName)//Spark Application名称
//                    .set("spark.master", "yarn")//运行模式为Spark on YARN
//                    .set("spark.deploy.mode", "cluster")//部署模式为yarn-cluster
//                    .set("spark.driver.memory", "4g")//driver内存4g
//                    .set("spark.driver.cores", "1")//driver计算vcore数量为1
//                    .set("spark.executor.memory", "5g")//executor内存为4g
//                    .set("spark.executor.heartbeatInterval", "20000")//executor心跳间隔20秒，默认10秒
//                    .set("spark.yarn.archive", "hdfs://bd001:8020/hdp/apps/2.6.1.0-129/spark2")//spark依赖jar存档到hdfs指定位置
//                    .set("spark.executor.extraJavaOptions", "-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseConcMarkSweepGC")//打印GC详情和耗时
//                    .set("spark.jars", "/usr/hdp/2.6.1.0-129/sqoop/lib/mysql-connector-java.jar")//如果使用了数据库驱动，则通过此配置即可
//                    //==== Serialized Configure
//                    .set("spark.kryoserializer.buffer", "512k")//默认64k，设置为256k
//                    .set("spark.kryoserializer.buffer.max", "256m")//默认64m，设置为256m
//                    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")//使用kryo序列化库
//                    .registerKryoClasses(new Class[]{HashMap.class, BCD.class})
//                    ;
//
//            //构建MQ配置
//            @SuppressWarnings("serial")
//            HashMap<String, String> mqConf = new HashMap<String, String>() {{
//                put(RocketMQConfig.NAME_SERVER_ADDR, namesrv);
//                put(RocketMQConfig.CONSUMER_TOPIC, topic);
//                put(RocketMQConfig.CONSUMER_GROUP, group);
//            }};
//
//            JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(second));
//            jssc.checkpoint(checkPointDir);
//            jssc.remember(Durations.minutes(1440));
//
//            //接収RocketMQ账单
//            JavaReceiverInputDStream<BCD> billListRDD = jssc.receiverStream(new RocketMQReceiver(mqConf, StorageLevels.MEMORY_AND_DISK_SER));
//
//            billListRDD
//                    .foreachRDD((rdd, time) -> {
//                        boolean isEmpty = rdd.partitions().isEmpty();
//                        if (!isEmpty) {
//                            printStream(rdd, time);
//
//                            long start = System.currentTimeMillis();
//                            String curTime = getCurrentDate(start).intern();
//                            String dfsDir = (BASE_PATH+"sdt="+curTime+"/").intern();
//
//                            //账单计数器
//                            //LongAccumulator billAccumulator = BillAccumulator.getInstance(JavaSparkContext.fromSparkContext(rdd.context()), appName);
//                            //billAccumulator.add(rdd.count());
//
//                            //初始化SparkSession
//                            SparkSession session = HiveSession.getInstance(conf);
//
//                            //维表等广播
//                            BroadcastDIM dim = BroadcastWrapper.getInstance(JavaSparkContext.fromSparkContext(rdd.context())).getValue();
//                            SaleAnalysisService service = dim.getService();
//                            Dataset<Row> shop = dim.getShop();
//                            Dataset<Row> type = dim.getType();
//                            Dataset<Row> ruian = dim.getRuian();
//                            String mallIdStr = dim.getMallIdStr();
//
//                            Set<String> areaSet=dim.getAreaSet();
//                            Map<String, Set<String>> areaMall=dim.getAreaMall();
//                            Set<String> mallSet=dim.getMallSet();
//                            Set<String> typeSet=dim.getTypeSet();
//
//
//                            //如果时间为00:00:00时则认为是新的一天，更新广播数据
//                            if ((curTime+"000000").equals(FORMATTER_YMDHMS.get().format(new Date(time.milliseconds())))) {
//                                BroadcastWrapper.update(session, JavaSparkContext.fromSparkContext(rdd.context()));//更新dim表数据
//                                mkDfsDir(session, dfsDir);//初始化dfsDir
//                            }
//
//                            //先持久化本次接收到的账单（写入当日）
//                            session.createDataFrame(rdd, BCD.class)
//                                    .filter("sdt = "+curTime)
//                                    .write()
//                                    .mode("append")
//                                    .parquet(dfsDir);
//
//                            //再读取全量账单（读取当日）
//                            Dataset<Row> bills = session.read().parquet(dfsDir)
//                                    .filter("shopId in ("+mallIdStr+")")
//                                    .dropDuplicates("billId")
//                                    //.coalesce(1)
//                                    .cache();
//
//                            //计算大屏指标
//                            System.out.println("==== 计算指标：时间条件："+curTime+" ====");
//                            service.totalSale(bills);
//                            service.totalRefund(bills);
//                            service.peakTime(bills);
//                            service.areaSaleTrendForAll(bills, ruian,areaSet, curTime);
//                            service.projectContrast(bills, ruian,areaMall);
//                            service.saleForShopTop10(bills, shop, ruian);
//                            service.projectTypeSaleContrast(bills, shop, type, ruian,areaSet,mallSet,typeSet);
//                            long end = System.currentTimeMillis();
//                            System.out.println("==== 计算指标：耗时："+(end-start)+"/ms ====");
//                            bills.unpersist();
//                            //每天最多存6个文件
//                            if (bills.inputFiles().length > 6) {
//                                mergeSmallFiles(bills, session, dfsDir);
//                            }
//                        } else {//如果SparkStreaming接收的Batch为空，则不做处理
//                            System.out.println("==== rdd is null! ");
//                        }
//                    });
//            return jssc;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//}
//
///**
// * 广播DIM相关数据
// * @author mengyao
// *
// */
//class BroadcastWrapper {
//    private static volatile Broadcast<BroadcastDIM> instance = null;
//
//    public static Broadcast<BroadcastDIM> getInstance(JavaSparkContext jsc) {
//        if (instance == null) {
//            synchronized (BroadcastWrapper.class) {
//                if (instance == null) {
//                    SparkSession session = HiveSession.getInstance(jsc.getConf());
//                    BroadcastDIM dim = new BroadcastDIM(session);
//                    dim.assign();
//                    instance = jsc.broadcast(dim);
//                }
//            }
//        }
//        return instance;
//    }
//
//    /**
//     * 每日更新数据
//     * @param batchTime    batch发生时间
//     * @param dayES    每日开始时间
//     */
//    public static void update(SparkSession session, JavaSparkContext jsc) {
//        BroadcastDIM dim = instance.getValue();
//        if (null!=dim) {
//            dim.assign();
//            jsc.broadcast(dim);
//        }
//    }
//}
//
//class BroadcastDIM {
//    private SparkSession session;
//    private SaleAnalysisService service = new SaleAnalysisService();
//    private Dataset<Row> shop;
//    private Dataset<Row> type;
//    private Dataset<Row> ruian;
//    private String mallIdStr;
//    private List<RuianMall> ruianList ;
//    private Set<String>  areaSet;
//    private Set<String>  typeSet;
//    private Set<String> mallSet;
//    private Map<String, Set<String>> areaMall;
//    public BroadcastDIM(SparkSession session) {
//        this.session = session;
//    }
//    public void assign() {
//        ruian = session.sql("select id,item,name,area_en,area_cn,channel,mid,rmid from tbl_dim_ruian").cache();
//        Row[] ruianRows = (Row[])ruian.collect();
//        setRuianList(ruianRows);
//        setAreaSet();
//        setMallIdStr();
//        setRuianMallAll();
//        setMallSet();
//        shop = session.sql("select id,shop_entity_id,shop_entity_name,shop_id,shop_entity_type_root,shop_entity_type_leaf,bill_match_mode,leasing_model,shop_entity_status,open_time,close_time,open_time_list,close_time_list,monite_begin_time_list,monite_end_time_list,marketing_copywriter,marketing_image,font_style,font_size,contract_area,province,city,area,billhint,is_del,brand,brand_code,classify,in_aera,storey,leasing_resource,shop_entity_source,create_time,c_time_stamp,shop_entity_img,business_area_id,source,status,logo,door_head_photo,business_license,certificate,coordinates,consume_per,brand_name,brand_log,alipay,process  "
//                + "from tbl_ods_shop where shop_id in ("+mallIdStr+")").cache();
//        type = session.sql("select * from tbl_ods_type").cache();
//        setRuianType();
//    }
//    public void  setRuianList(Row[] rows) {
//        if(rows.length>0){
//            ruianList=new ArrayList<>();
//            for(Row row:rows){
//                RuianMall rm=new RuianMall();
//                if(!row.isNullAt(0)){//id
//                    rm.setId(row.getInt(0));
//                }
//                if(!row.isNullAt(1)){//item
//                    rm.setItem(row.getString(1));
//                }
//                if(!row.isNullAt(2)){//name
//                    rm.setName(row.getString(2));
//                }
//                if(!row.isNullAt(3)){//area_en
//                    rm.setAreaEn(row.getString(3));
//                }
//                if(!row.isNullAt(4)){//area_cn
//                    rm.setArenCn(row.getString(4));
//                }
//                if(!row.isNullAt(5)){//channel
//                    rm.setChannel(row.getString(5));
//                }
//                if(!row.isNullAt(6)){//mid
//                    rm.setMid(row.getInt(6));
//                }
//                if(!row.isNullAt(7)){//rmid
//                    rm.setRmid(row.getString(7));
//                }
//                ruianList.add(rm);
//            }
//        }
//    }
//    /**
//     * 提取rmid 拼接成字符串
//     * @param rows
//     */
//    public void setMallIdStr() {
//        if(ruianList.size()>0){
//            StringBuilder sbStr=new StringBuilder();
//            for(RuianMall row : ruianList){
//                sbStr.append("'").append(row.getRmid()).append("',");
//            }
//            String tmpValue = sbStr.toString();
//            mallIdStr=tmpValue.substring(0, tmpValue.length()-1);
//        }
//    }
//    /**
//     * 提取非空唯一中文区域名称
//     * @return
//     */
//    public void setAreaSet() {
//        if(ruianList.size()>0){
//            areaSet=new java.util.HashSet<>();
//            for(RuianMall row:ruianList){
//                areaSet.add(row.getArenCn());
//            }
//        }
//    }
//    /**
//     * 提取瑞安mall 14个机构中文名
//     *
//     * @return
//     * Map<String,Set<String>>
//     * key:areaCn value :  Set<mallName>
//     */
//    public void setRuianMallAll( ){
//        areaMall=new HashMap<>();
//        ruianList.forEach(rm->{
//            if(areaMall.containsKey(rm.getArenCn())){//存在，更新mall的列表
//                areaMall.get(rm.getArenCn()).add(rm.getName());
//            }else{//新的区域，新的mall
//                Set<String> mallSet=new HashSet<>();
//                mallSet.add(rm.getName());
//                areaMall.put(rm.getArenCn(),mallSet);
//            }
//        });
//    }
//    /**
//     * 提取瑞安mall 14个机构中文名
//     *
//     * @return
//     */
//    public void setMallSet(){
//        mallSet=new java.util.HashSet<>();
//        ruianList.forEach(rm->{
//            if(!mallSet.contains(rm.getName())){//存在，更新mall的列表
//                mallSet.add(rm.getName());
//            }
//        });
//    }
//    /**
//     * 性能、性能、性能 考虑，先写死
//     * 如果用账单、店铺、业态关联查询，效率会很慢
//     *
//     * @return
//     */
//    public void setRuianType(){
//        //TODO 目前写死，需要改
//        typeSet=new HashSet<>();
//        typeSet.add("服务");
//        typeSet.add("零售");
//        typeSet.add("娱乐");
//        typeSet.add("主力店");
//        typeSet.add("餐饮");
//        typeSet.add("其它");
//    }
//    public SaleAnalysisService getService() {
//        return service;
//    }
//    public void setService(SaleAnalysisService service) {
//        this.service = service;
//    }
//    public Dataset<Row> getShop() {
//        return shop;
//    }
//    public void setShop(Dataset<Row> shop) {
//        this.shop = shop;
//    }
//    public Dataset<Row> getType() {
//        return type;
//    }
//    public void setType(Dataset<Row> type) {
//        this.type = type;
//    }
//    public Dataset<Row> getRuian() {
//        return ruian;
//    }
//    public void setRuian(Dataset<Row> ruian) {
//        this.ruian = ruian;
//    }
//    public String getMallIdStr() {
//        return mallIdStr;
//    }
//    public void setMallIdStr(String mallIdStr) {
//        this.mallIdStr = mallIdStr;
//    }
//    public List<RuianMall> getRuianList() {
//        return ruianList;
//    }
//    public Set<String> getAreaSet() {
//        return this.areaSet;
//    }
//    public Map<String, Set<String>> getAreaMall() {
//        return this.areaMall;
//    }
//    public Set<String> getMallSet() {
//        return this.mallSet;
//    }
//    public Set<String> getTypeSet() {
//        return this.typeSet;
//    }
//}
//
///**
// * 账单累加器
// * @author mengyao
// *
// */
//class BillAccumulator {
//    private static volatile LongAccumulator instance = null;
//
//    public static LongAccumulator getInstance(JavaSparkContext jsc, String name) {
//        if (instance == null) {
//            synchronized (BillAccumulator.class) {
//                if (instance == null) {
//                    instance = jsc.sc().longAccumulator(name);
//                }
//            }
//        }
//        return instance;
//    }
//}
//
///**
// * SparkSQL的Hive数据源
// * @author mengyao
// *
// */
//class HiveSession {
//    private static transient SparkSession instance = null;
//
//    public static SparkSession getInstance(SparkConf conf) {
//        if (instance == null) {
//            instance = SparkSession.builder()
//                    .config(conf)
//                    .enableHiveSupport()
//                    .getOrCreate();
//        }
//        return instance;
//    }
//}
