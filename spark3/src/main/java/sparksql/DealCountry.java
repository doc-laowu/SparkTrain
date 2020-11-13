package sparksql;

import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @Title: DealCountry
 * @ProjectName SparkTrain
 * @Description: TODO
 * @Author yisheng.wu
 * @Date 2020/10/1215:12
 */
public class DealCountry {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("JavaStructuredNetworkWordCountWindowed")
                .master("local[*]")
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        String filepath = "E:\\ip20200314\\home\\codebase\\17mon\\mydata4vipday2.txtx";

        Dataset<Row> rowDataset = spark.read()
                .textFile(filepath)
                .mapPartitions(new MapPartitionsFunction<String, Tuple2<String, String>>() {

                    @Override
                    public Iterator<Tuple2<String, String>> call(Iterator<String> iterator) throws Exception {

                        ArrayList<Tuple2<String, String>> fastlist = new ArrayList<>();

                        iterator.forEachRemaining(line -> {
                            String[] split = line.split("\t");
                            Tuple2<String, String> tuple2 = new Tuple2<>(split[2], split[3]);
                            fastlist.add(tuple2);
                        });

                        return fastlist.iterator();
                    }

                }, Encoders.tuple(Encoders.STRING(), Encoders.STRING()))
                .toDF("country", "province");

        rowDataset.createOrReplaceTempView("source_table");

        Dataset<Row> sql = spark.sql("select distinct(country) as country from source_table where country != '中国' " +
                "union all select distinct(province) as country from source_table where country = '中国'");

        sql.show(100);

        spark.stop();
    }

}
