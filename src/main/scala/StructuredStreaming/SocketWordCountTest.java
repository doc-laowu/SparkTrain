//package StructuredStreaming;//package StructuredStreaming;
//
//import org.apache.commons.lang.time.FastDateFormat;
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.sql.streaming.StreamingQuery;
//import org.apache.spark.sql.streaming.StreamingQueryException;
//import scala.Tuple3;
//
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @Title: SocketWordCountTest
// * @ProjectName Real_Dws_Data_Statistics
// * @Description: TODO
// * @Author gaosen
// * @Date 2019/3/2617:06
// */
//public class SocketWordCountTest {
//
//    public static class TimeUtil{
//
//        private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
//
//        public static Timestamp formatTime2TimeStamp(Long timestamp) throws Exception {
//
//            try {
//
//                Timestamp ts = new Timestamp(timestamp);
//                return ts;
//            }catch (Exception e){
//                throw new Exception("transfer the unix timestamp to the sql timestamp error!");
//            }
//        }
//
//        /**
//         * @Author: yisheng.wu
//         * @Description 将字符串的时间转为时间戳
//         * @Date 10:37 2019/3/27
//         * @Param [timestamp]
//         * @return java.sql.Timestamp
//         **/
//        public static Timestamp formatTime2TimeStamp(String timestamp) throws Exception {
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            return formatTime2TimeStamp(sdf.parse(timestamp).getTime());
//        }
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
//        // Create DataFrame representing the stream of input lines from connection to host:port
//        Dataset<Row> lines = spark
//                .readStream()
//                .format("socket")
//                .option("host", "192.168.1.171")
//                .option("port", 9999)
////                .option("includeTimestamp", true)
//                .load();
//
//        // Split the lines into words, retaining timestamps
//        Dataset<Tuple3<Integer, String, Timestamp>> words = lines
//                .flatMap((FlatMapFunction<Row, Tuple3<Integer, String, Timestamp>>) row -> {
//                    List<Tuple3<Integer, String, Timestamp>> result = new ArrayList<>();
//                    String line = row.getString(0);
//                    String[] arr = line.split(";");
//                    result.add(new Tuple3<Integer, String, Timestamp>(Integer.valueOf(arr[0]), arr[1], TimeUtil.formatTime2TimeStamp(arr[2])));
//                    return result.iterator();
//                }, Encoders.tuple(Encoders.INT(), Encoders.STRING(), Encoders.TIMESTAMP())
//                ).toDF("code", "app_id", "timestamp");
//
//
//        words.withWatermark("timestamp", "1 minutes").createOrReplaceTempView("table");
//
//
//        Dataset windowedCounts_appid = words.sparkSession().sql("select window, app_id, code, count(1) as count from table where code < 999 " +
//                "group by window(timestamp, '1 minutes', '1 minutes'), app_id, code");
//
//        windowedCounts_appid.explain();
////                .filter("code < 999");
//
//
//        StreamingQuery query_02 = windowedCounts_appid.writeStream()
//                .outputMode("append")
//                .format("console")
//                .option("truncate", "false")
//                .start();
//
//        spark.streams().awaitAnyTermination();
//
//    }
//
//}
