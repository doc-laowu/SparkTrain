//package StructuredStreaming;
//
//
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.api.java.function.ForeachPartitionFunction;
//import org.apache.spark.broadcast.Broadcast;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.sql.streaming.StreamingQueryException;
//import org.apache.spark.sql.types.StructType;
//import scala.Tuple3;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.atomic.LongAccumulator;
//
///**
// * @Title: StructedStreamingTest
// * @ProjectName Real_Dws_Data_Statistics
// * @Description: TODO
// * @Author gaosen
// * @Date 2019/3/2616:18
// */
//public class StructedStreamingTest {
//
//    public static class IpDimTable {
//
//        private volatile Broadcast<List<Tuple3>> broadcastInstance = null;
//
//        private volatile LongAccumulator timeAcc = null;
//
//        /**
//          * @Author: yisheng.wu
//          * @Description TODO 初始化加载ip库文件
//          * @Date 14:31 2020/4/30
//          * @Param [spark]
//          * @return void
//          **/
//        public static void getInstance(SparkSession spark) {
//
//            spark.read()
//                    .textFile("")
//                    .flatMap((FlatMapFunction<Row, Tuple3<Integer, String, Timestamp>>) row -> {
//                                List<Tuple3<Integer, String, Timestamp>> result = new ArrayList<>();
//                                String line = row.getString(0);
//                                String[] arr = line.split(";");
//                                result.add(new Tuple3<Integer, String, Timestamp>(Integer.valueOf(arr[0]), arr[1], SocketWordCountTest.TimeUtil.formatTime2TimeStamp(arr[2])));
//                                return result.iterator();
//                            }, Encoders.tuple(Encoders.INT(), Encoders.STRING(), Encoders.TIMESTAMP())
//                    ).toDF("code", "app_id", "timestamp");
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
//
//
//        StructType userSchema = new StructType().add("ip", "string").add("timestamp", "timestamp");
//
//        Dataset<Row> stream = spark
//                .readStream()
//                .option("sep", ";")
//                .option("includeTimestamp", true)
//                .schema(userSchema)
//                .csv("E:\\csv");
//
//        // Group the data by window and word and compute the count of each group
////        Dataset<Row> windowedCounts = stream.foreachPartition(new ForeachPartitionFunction<Row>() {
////            @Override
////            public void call(Iterator<Row> iter) throws Exception {
////
////                iter.forEachRemaining(row->{
////
////                    String ip = row.getString(0);
////
////
////
////                });
////
////            }
////
////        });
//
//
////        stream.withWatermark("timestamp", "1 minutes").createOrReplaceTempView("table");
////        Dataset windowedCounts = stream.sparkSession().sql("select SUBSTRING(SUBSTRING_INDEX(window, ',', 1), 2),word, count(1) as count from table group by " +
////                "window(timestamp, '1 minutes', '1 minutes'), word");
//
//        // Start running the query that prints the windowed word counts to the console
////        StreamingQuery query = windowedCounts.writeStream()
////                .outputMode("append")
////                .format("console")
////                .option("truncate", "false")
////                .start();
////
////        query.awaitTermination();
//
//    }
//
//}