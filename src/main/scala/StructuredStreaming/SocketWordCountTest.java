package StructuredStreaming;//package StructuredStreaming;
//
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.sql.*;
//import org.apache.spark.sql.streaming.StreamingQuery;
//import org.apache.spark.sql.streaming.StreamingQueryException;
//import scala.Tuple2;
//
//import java.sql.Timestamp;
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
//                .option("host", "myMaster01")
//                .option("port", 9999)
////                .option("includeTimestamp", true)
//                .load();
//
//        // Split the lines into words, retaining timestamps
//        Dataset<Row> words = lines
//                .flatMap(new FlatMapFunction<Row, Tuple2<String, Timestamp>>(){
//                             @Override
//                             public Iterator<Tuple2<String, Timestamp>> call(Row row) throws Exception {
//                                 List<Tuple2<String, Timestamp>> result = new ArrayList<>();
//                                 String line = row.getString(0);
//                                 String[] arr = line.split(";");
//                                 result.add(new Tuple2<String, Timestamp>(arr[0], MyTimeUtil.formatTime2TimeStamp(arr[1])));
//                                 return result.iterator();
//                             }
//                         }, Encoders.tuple(Encoders.STRING(), Encoders.TIMESTAMP())
//                ).toDF("word", "timestamp");
//
//
//        // Group the data by window and word and compute the count of each group
////        Dataset<Row> windowedCounts = words.groupBy(
////                functions.window(words.col("timestamp"), "1 minutes", "1 minutes"),
////                words.col("word")
////        ).count();
//
//
////        words.withWatermark("timestamp", "1 minutes").createOrReplaceTempView("table");
////        Dataset windowedCounts = words.sparkSession().sql("select window, word, count(1) as count from table group by " +
////                "window(timestamp, '1 minutes', '1 minutes'), word");
//
////        words.withWatermark("timestamp", "1 minutes").createOrReplaceTempView("table");
////        Dataset windowedCounts = words.sparkSession().sql("select cast(window.start as string) as timestamp_1m, window, word, " +
////                "count(1) as count from table group by window(timestamp, '1 minutes', '1 minutes'), word");
//
//        words.withWatermark("timestamp", "1 minutes").createOrReplaceTempView("table");
//        Dataset windowedCounts = words.sparkSession().sql("select word, count(1) as count from table where word < 999" +
//                "group by window(timestamp, '1 minutes', '1 minutes'), word").as(Encoders.bean(WordBean.class));
//
////        Dataset maxLenWord = words.sparkSession().sql("select word, count(1) as count from table " +
////                "group by window(timestamp, '1 minutes', '1 minutes'), word").as(Encoders.bean(WordBean.class));
//
//        // Start running the query that prints the windowed word counts to the console
//        StreamingQuery query = windowedCounts.writeStream()
//                .outputMode("append")
//                .format("console")
//                .option("truncate", "false")
//                .start();
//
////        StreamingQuery query2 = maxLenWord.writeStream()
////                .outputMode("append")
////                .format("console")
////                .option("truncate", "false")
////                .start();
//
////        Properties props = new Properties();
////        props.setProperty("data.sink.table", "wordcount");
//
////        StreamingQuery query = windowedCounts.writeStream()
////                .outputMode("append")
////                .foreach(new JdbcForeachWriter(props))
////                .start();
//
//        query.awaitTermination();
//
////        query2.awaitTermination();
//
//    }
//
//}
