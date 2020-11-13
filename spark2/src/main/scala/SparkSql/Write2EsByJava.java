package SparkSql;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Title: Write2EsByJava
 * @ProjectName SparkTrain
 * @Description: TODO
 * @Author yisheng.wu
 * @Date 2020/5/1214:32
 */
public class Write2EsByJava {

    public static class My_test implements Serializable {

        public My_test() {
        }

        public My_test(String _id, Long test_integer) {
            this._id = _id;
            this.test_integer = test_integer;
        }

        private String _id;
        private Long test_integer;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public Long getTest_integer() {
            return test_integer;
        }

        public void setTest_integer(Long test_integer) {
            this.test_integer = test_integer;
        }
    }

    public static class myUdf implements UDF1<Long, List<Long>> {

        @Override
        public List<Long> call(Long aLong) throws Exception {

            List<Long> tag_seq = new ArrayList<>();

            tag_seq.add(10L);
            tag_seq.add(20L);
            tag_seq.add(30L);
            tag_seq.add(40L);
            tag_seq.add(50L);

            return tag_seq;
        }
    }

    public static void main(String[] args) {

        SparkSession sparkSession = SparkSession
                .builder()
                .master("local[4]")
                .appName("test")
                .getOrCreate();
        sparkSession.sparkContext().setLogLevel("WARN");

        List<My_test> my_tests = Arrays.asList(
                new My_test("46", 20L),
                new My_test("47", 30L)
        );

        Dataset<My_test> dataset = sparkSession.createDataset(my_tests, Encoders.bean(My_test.class));

        dataset.show();

        sparkSession.udf().register("Tags2TagIds", (Long age)->new myUdf().call(age), DataTypes.createArrayType(DataTypes.LongType, true));

        dataset.createOrReplaceTempView("my_test");

        Dataset<Row> ret_DS = sparkSession.sql("select _id, test_integer, Tags2TagIds(test_integer) as system_tags_array from my_test");

        ret_DS.show();

        String script = "ctx._source.system_tags_array = params.system_tags_array;ctx._source.test_integer = params.test_integer;";
        String params = "system_tags_array:system_tags_array,test_integer:test_integer";

        ret_DS.write()
                .format("org.elasticsearch.spark.sql")
                .option("es.mapping.id", "_id")
                .option("es.mapping.exclude", "_id")
                .option("es.nodes.wan.only", "true")
                .option("es.nodes", "es-cn-v0h1i3zti0007lgdp.elasticsearch.aliyuncs.com")
                .option("es.port", "9200")
                .option("es.update.script.inline", script)
                .option("es.update.script.params", params)
                .option("es.write.operation", "upsert")
                .option("es.net.http.auth.user", "elastic")
                .option("es.net.http.auth.pass", "Vhall2020")
                //      .option("es.read.field.as.array.include", "test_keyword_array") // 天坑：需要指定数组字段
                .option("es.batch.write.retry.count", 2)
                .mode("append")
                .save("test_array/test_array");

        //    df.show()

        sparkSession.stop();
    }

}
