//package AvroDemo;
//
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.api.java.function.MapPartitionsFunction;
//import org.apache.spark.broadcast.Broadcast;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.sql.execution.QueryExecution;
//import org.apache.spark.sql.streaming.StreamingQuery;
//import org.apache.spark.sql.streaming.StreamingQueryException;
//import org.apache.spark.sql.streaming.StreamingQueryListener;
//import org.apache.spark.sql.util.QueryExecutionListener;
//import org.apache.spark.util.LongAccumulator;
//import scala.Tuple2;
//import scala.Tuple5;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @Title: IpAnalysisDemo
// * @ProjectName structured_streaming_etl_service_interaction
// * @Description: TODO
// * @Author yisheng.wu
// * @Date 2020/4/3015:25
// */
//public class IpAnalysisDemo {
//
//    public static class IpDim{
//
//        private volatile static List<Tuple5<String, String, String, String, String>> broadcast = null;
//
//        private volatile static Long longAccumulator = null;
//
//        public static long ipToLong(String strIp) {
//            String[] ip = strIp.split("\\.");
//            return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) +
//                    Long.parseLong(ip[3]);
//
//        }
//
//        public static String binarySearch(List<Tuple5<String, String, String, String, String>> array, String ip) {
//
//            Long dstIp = ipToLong(ip);
//
//            int low = 0, high = array.size()-1;
//
//            int index = -1;
//
//            while (low <= high) {
//
//                int mid = (high + low) / 2;
//
//                String ip_1S = array.get(mid)._1();
//                long ip_1L = ipToLong(ip_1S);
//
//                String ip_2S = array.get(mid)._2();
//                long ip_2L = ipToLong(ip_2S);
//
//                if (dstIp >= ip_1L && dstIp <= ip_2L) {
//                    index = mid;
//                    break;
//                } else if (dstIp < ip_1L) {
//                    high = mid - 1;
//                } else if (dstIp > ip_2L) {
//                    low = mid + 1;
//                }
//
//            }
//
//            if (index == -1) {
//                return new String("*#*#*");
//            } else {
//
//                Tuple5<String, String, String, String, String> tuple5 = array.get(index);
//                String country = tuple5._3();
//                String province = tuple5._4();
//                String city = tuple5._5();
//
//                StringBuilder append = new StringBuilder()
//                        .append(country).append("#")
//                        .append(province).append("#")
//                        .append(city);
//
//                return append.toString();
//            }
//
//        }
//
//        /**
//         * @Author: yisheng.wu
//         * @Description TODO 加载ip库文件和并且广播
//         * @Date 16:41 2020/4/30
//         * @Param [spark, filepath]
//         * @return org.apache.spark.broadcast.Broadcast<java.util.List<scala.Tuple5<java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String>>>
//         **/
//        private static List<Tuple5<String, String, String, String, String>> LoadAndBroadcast(String filepath) throws IOException {
//
//            System.out.println("start load the data from:"+filepath);
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"UTF-8"));
//            String line = null;
//            ArrayList<Tuple5<String, String, String, String, String>> tuple5s = new ArrayList<>();
//
//            while(null != (line = br.readLine())){
//                String[] split = line.split("\t");
//                Tuple5<String, String, String, String, String> tuple5 = new Tuple5<>(split[0], split[1], split[2], split[3], split[4]);
//                tuple5s.add(tuple5);
//            }
//
//            broadcast = tuple5s;
//
//            return broadcast;
//        }
//
//
//        /**
//         * @Author: yisheng.wu
//         * @Description TODO 初始化加载ip库文件
//         * @Date 14:31 2020/4/30
//         * @Param [spark]
//         * @return void
//         **/
//        public static List<Tuple5<String, String, String, String, String>> getBroadcast(String filepath) throws IOException {
//
//            if(null == broadcast) {
//                synchronized (IpDim.class) {
//                    if (null == broadcast) {
//                        broadcast = LoadAndBroadcast(filepath);
//                    }
//                }
//            }
//            return broadcast;
//        }
//
//        /**
//         * @Author: yisheng.wu
//         * @Description TODO 重新加载ip库文件
//         * @Date 16:35 2020/4/30
//         * @Param [spark]
//         * @return void
//         **/
//        public static void updateBroadcast(String filepath) throws IOException {
//
//            if(null != broadcast){
//                broadcast.clear();
//                synchronized (IpDim.class) {
//                    broadcast = LoadAndBroadcast(filepath);
//                }
//            }
//        }
//
//        /**
//         * @Author: yisheng.wu
//         * @Description TODO 获取累加器的值
//         * @Date 16:46 2020/4/30
//         * @Param [spark]
//         * @return void
//         **/
//        public static Long getAccumulator(){
//
//            if(null == longAccumulator){
//                synchronized (IpDim.class) {
//                    if (null == longAccumulator) {
//                        // 初始化当时的时间点
//                        longAccumulator = new Date().getTime();
//                        System.out.println("init the time:"+longAccumulator);
//                    }
//                }
//            }
//            return longAccumulator;
//        }
//
//        public static Long updateAccumulator(Long time){
//
//            if(null != longAccumulator){
//                synchronized (IpDim.class) {
//                    if (null != longAccumulator) {
//                        // 初始化当时的时间点
//                        longAccumulator = time;
//                        System.out.println("update the time:"+longAccumulator);
//                    }
//                }
//            }
//            return longAccumulator;
//        }
//
//    }
//
//    public static class IpDimTable {
//
//        private volatile static Broadcast<List<Tuple5<String, String, String, String, String>>> broadcast = null;
//
//        private volatile static LongAccumulator longAccumulator = null;
//
//        public static long ipToLong(String strIp) {
//            String[] ip = strIp.split("\\.");
//            return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) +
//                    Long.parseLong(ip[3]);
//
//        }
//
//        public static String binarySearch(List<Tuple5<String, String, String, String, String>> array, String ip) {
//
//            Long dstIp = ipToLong(ip);
//
//            int low = 0, mid, high = array.size();
//
//            int dstIndex = -1;
//
//            while (low <= high) {
//
//                mid = (high + low) / 2;
//
//                String ip_1S = array.get(mid)._1();
//                long ip_1L = ipToLong(ip_1S);
//
//                String ip_2S = array.get(mid)._2();
//                long ip_2L = ipToLong(ip_2S);
//
//                if (dstIp >= ip_1L && dstIp <= ip_2L) {
//                    dstIndex = mid;
//                    break;
//                } else if (dstIp < ip_1L) {
//                    high = mid - 1;
//                } else if (dstIp > ip_2L) {
//                    low = mid + 1;
//                }
//
//            }
//
//            if (dstIndex == -1) {
//                return new String("*#*#*");
//            } else {
//
//                Tuple5<String, String, String, String, String> tuple5 = array.get(dstIndex);
//                String country = tuple5._3();
//                String province = tuple5._3();
//                String city = tuple5._3();
//
//                StringBuilder append = new StringBuilder()
//                        .append(country).append("#")
//                        .append(province).append("#")
//                        .append(city);
//
//                return append.toString();
//            }
//
//        }
//
//        /**
//          * @Author: yisheng.wu
//          * @Description TODO 加载ip库文件和并且广播
//          * @Date 16:41 2020/4/30
//          * @Param [spark, filepath]
//          * @return org.apache.spark.broadcast.Broadcast<java.util.List<scala.Tuple5<java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String>>>
//          **/
//        private static Broadcast<List<Tuple5<String, String, String, String, String>>> LoadAndBroadcast(SparkSession spark, String filepath){
//
//            System.out.println("load the ip file: "+filepath);
//
//            Dataset<String> stringDataset = spark.read().textFile(filepath);
//
//            List<Tuple5<String, String, String, String, String>> collect = stringDataset.mapPartitions(new MapPartitionsFunction<String, Tuple5<String, String, String, String, String>>() {
//
//                        @Override
//                        public Iterator<Tuple5<String, String, String, String, String>> call(Iterator<String> input) throws Exception {
//
//                            ArrayList<Tuple5<String, String, String, String, String>> tuple5s = new ArrayList<>();
//
//                            input.forEachRemaining(line -> {
//                                String[] split = line.split("\t");
//
//                                Tuple5<String, String, String, String, String> tuple5 = new Tuple5<>(split[0], split[1], split[2], split[3], split[4]);
//
//                                tuple5s.add(tuple5);
//                            });
//                            return tuple5s.iterator();
//                        }
//                    }, Encoders.tuple(Encoders.STRING(), Encoders.STRING(), Encoders.STRING(), Encoders.STRING(), Encoders.STRING()))
//                    .toJavaRDD()
//                    .cache()
//                    .collect();
//
//            JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());
//            Broadcast<List<Tuple5<String, String, String, String, String>>> broadcast = sc.broadcast(collect);
//
//            return broadcast;
//        }
//
//
//        /**
//         * @Author: yisheng.wu
//         * @Description TODO 初始化加载ip库文件
//         * @Date 14:31 2020/4/30
//         * @Param [spark]
//         * @return void
//         **/
//        public static void initBroadcast(SparkSession spark, String filepath) {
//
//            if(null == broadcast) {
//                synchronized (IpDimTable.class) {
//                    if (null == broadcast) {
//                        broadcast = LoadAndBroadcast(spark, filepath);
//                        System.out.println("init load the ip file");
//                    }
//                }
//            }
//        }
//
//        public static Broadcast<List<Tuple5<String, String, String, String, String>>> getBroadcast(){
//            return broadcast;
//        }
//
//        /**
//          * @Author: yisheng.wu
//          * @Description TODO 重新加载ip库文件
//          * @Date 16:35 2020/4/30
//          * @Param [spark]
//          * @return void
//          **/
//        public static void updateBroadcast(SparkSession spark, String filepath){
//
//            if(null != broadcast){
//                broadcast.unpersist(false);
//                synchronized (IpDimTable.class) {
//                    broadcast = LoadAndBroadcast(spark, filepath);
//                    System.out.println("reload the ip file");
//                }
//            }
//        }
//
//        /**
//          * @Author: yisheng.wu
//          * @Description TODO 获取累加器的值
//          * @Date 16:46 2020/4/30
//          * @Param [spark]
//          * @return void
//          **/
//        public static LongAccumulator initAccumulator(SparkSession spark){
//
//            if(null == longAccumulator){
//                synchronized (IpDimTable.class) {
//                    JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());
//                    if (null == longAccumulator) {
//                        longAccumulator = sc.sc().longAccumulator("UpdateTimeCount");
//                        // 初始化当时的时间点
//                        longAccumulator.add(new Date().getTime());
//                        System.out.println("update the long acc value");
//                    }
//                }
//            }
//            return longAccumulator;
//        }
//
//    }
//
//    public static void main(String[] args) throws StreamingQueryException {
//
//        SparkSession spark = SparkSession
//                .builder()
//                .appName("JavaStructuredNetworkWordCountWindowed")
//                .master("local[*]")
//                .getOrCreate();
//
//        spark.sparkContext().setLogLevel("WARN");
//
//        String filepath = "E:\\iplist.txt";
//
//
//
//        Dataset<Row> lines = spark
//                .readStream()
//                .format("socket")
//                .option("host", "192.168.1.171")
//                .option("port", 9999)
//                .option("includeTimestamp", true)
//                .load();
//
//        Dataset<Row> words = lines
//                .flatMap((FlatMapFunction<Row, Tuple2<String, Timestamp>>) row -> {
//                            List<Tuple2<String, Timestamp>> result = new ArrayList<>();
//                            String line = row.getString(0);
//                            String[] arr = line.split(";");
//                            result.add(new Tuple2<String, Timestamp>(arr[0], TimeUtil.formatStr2TimeStamp(arr[1])));
//                            return result.iterator();
//                        }, Encoders.tuple(Encoders.STRING(), Encoders.TIMESTAMP())
//                ).toDF("ip", "timestamp");
//
//
//        // 初始化广播变量和迭代器
////        IpDimTable.initBroadcast(spark, filepath);
////        IpDimTable.initAccumulator(spark);
//
//        // Group the data by window and word and compute the count of each group
//        Dataset<Row> rowDataset = words.mapPartitions(new MapPartitionsFunction<Row, Tuple5<String, Timestamp, String, String, String>>() {
//            @Override
//            public Iterator<Tuple5<String, Timestamp, String, String, String>> call(Iterator<Row> input) throws Exception {
//
//                ArrayList<Tuple5<String, Timestamp, String, String, String>> arrayList = new ArrayList<>();
//
//                input.forEachRemaining(row -> {
//
//                    String ip = row.getAs("ip");
//                    Timestamp timestamp = row.getAs("timestamp");
//
//                    List<Tuple5<String, String, String, String, String>> value = IpDimTable.getBroadcast().getValue();
//                    String location = IpDimTable.binarySearch(value, ip);
//                    String[] split = location.split("#");
//                    Tuple5<String, Timestamp, String, String, String> tuple
//                            = new Tuple5<>(ip, timestamp, split[0], split[1], split[2]);
//                    arrayList.add(tuple);
//
////                    List<Tuple5<String, String, String, String, String>> broadcast = null;
////                    try {
////                        broadcast = IpDim.getBroadcast(filepath);
////                        Long value = IpDim.getAccumulator();
////                        Long curDateTime = new Date().getTime();
////                        // 如果距离上一次加载已经操作一分钟就重新加载一下
////                        if (curDateTime.longValue() - value.longValue() > 60000) {
////                            IpDim.updateBroadcast(filepath);
////                            IpDim.updateAccumulator(new Date().getTime());
////                        }
////
////                        String location = IpDim.binarySearch(broadcast, ip);
////
////                        String[] split = location.split("#");
////
////                        Tuple5<String, Timestamp, String, String, String> tuple
////                                = new Tuple5<>(ip, timestamp, split[0], split[1], split[2]);
////
////                        arrayList.add(tuple);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
//                });
//
//                return arrayList.iterator();
//            }
//        }, Encoders.tuple(Encoders.STRING(), Encoders.TIMESTAMP(), Encoders.STRING(), Encoders.STRING(), Encoders.STRING()))
//                .toDF("ip", "timestamp", "country", "province", "city");
//
//        // Start running the query that prints the windowed word counts to the console
//        StreamingQuery query = rowDataset.writeStream()
//                .outputMode("append")
//                .format("console")
//                .option("truncate", "false")
//                .queryName("ipdimtable")
//                .start();
//
//        spark.streams().addListener(new StreamingQueryListener(){
//
//            @Override
//            public void onQueryStarted(QueryStartedEvent event) {
//                IpDimTable.initBroadcast(spark,filepath);
//                LongAccumulator longAccumulator = IpDimTable.initAccumulator(spark);
//                // 如果距离上一次加载已经操作一分钟就重新加载一下
//                if (new Date().getTime() - longAccumulator.value() > 60000) {
//                    IpDimTable.updateBroadcast(spark, filepath);
//                    longAccumulator.setValue(new Date().getTime());
//                }
//                System.out.println("start the brodcast and the time acc"+new Date().getTime());
//            }
//
//            @Override
//            public void onQueryProgress(QueryProgressEvent event) {
//                System.out.println("process the brodcast and the time acc:"+new Date().getTime());
//            }
//
//            @Override
//            public void onQueryTerminated(QueryTerminatedEvent event) {
//                System.out.println("end the brodcast and the time acc"+new Date().getTime());
//            }
//        });
//
//        spark.listenerManager().register(new QueryExecutionListener(){
//
//            @Override
//            public void onSuccess(String funcName, QueryExecution qe, long durationNs) {
//                LongAccumulator longAccumulator = IpDimTable.initAccumulator(spark);
//                // 如果距离上一次加载已经操作一分钟就重新加载一下
//                if (new Date().getTime() - longAccumulator.value() > 60000) {
//                    IpDimTable.updateBroadcast(spark, filepath);
//                    longAccumulator.setValue(new Date().getTime());
//                }
//                System.out.println("querry success!"+new Date().getTime());
//            }
//
//            @Override
//            public void onFailure(String funcName, QueryExecution qe, Exception exception) {
//
//            }
//        });
//
//        query.awaitTermination();
//
//    }
//
//}
