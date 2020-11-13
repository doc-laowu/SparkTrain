package StructuredStreaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Title: wordcount
 * @ProjectName SparkTrain
 * @Description: TODO
 * @Author yisheng.wu
 * @Date 2020/8/2714:36
 */
public class wordcount {

    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: JavaWordCount <file>");
            System.exit(1);
        }

        SparkConf conf = new SparkConf();
        String[] paths = {"D:\\workspace\\git\\SparkTrain\\spark3\\target\\spark3-1.0-SNAPSHOT.jar"};
        conf.setAppName("JavaWordCount")
                .setJars(paths).setMaster("spark://mymaster01:7077");

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .appName("JavaWordCount")
                .getOrCreate();

        JavaRDD<String> lines = spark.read().textFile(args[0]).javaRDD();

        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());

//        JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<String, Integer>(s, 1));

        JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        });

        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);

        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<?,?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
        spark.stop();

    }

}
