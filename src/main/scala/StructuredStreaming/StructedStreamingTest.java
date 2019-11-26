package StructuredStreaming;//package StructuredStreaming;
//
//
//import org.apache.spark.sql.*;
//import org.apache.spark.sql.streaming.StreamingQuery;
//import org.apache.spark.sql.streaming.StreamingQueryException;
//import org.apache.spark.sql.types.StructType;
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
//        StructType userSchema = new StructType().add("word", "string").add("timestamp", "timestamp");
//
//        Dataset<Row> stream = spark
//                .readStream()
//                .option("sep", ";")
//                .option("includeTimestamp", true)
//                .schema(userSchema)
//                .csv("E:\\csv");
//
//        // Group the data by window and word and compute the count of each group
////        Dataset<Row> windowedCounts = stream
////                .withWatermark("timestamp", "1 minutes")
////                .groupBy(functions.window(stream.col("timestamp"), "1 minutes", "1 minutes"),
////                stream.col("word")
////        ).count().as("counter");
//
//
//        stream.withWatermark("timestamp", "1 minutes").createOrReplaceTempView("table");
//        Dataset windowedCounts = stream.sparkSession().sql("select SUBSTRING(SUBSTRING_INDEX(window, ',', 1), 2),word, count(1) as count from table group by " +
//                "window(timestamp, '1 minutes', '1 minutes'), word");
//
//        // Start running the query that prints the windowed word counts to the console
//        StreamingQuery query = windowedCounts.writeStream()
//                .outputMode("append")
//                .format("console")
//                .option("truncate", "false")
//                .start();
//
//        query.awaitTermination();
//
//    }
//
//}
