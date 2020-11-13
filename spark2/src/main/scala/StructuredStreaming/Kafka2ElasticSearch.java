package StructuredStreaming;

import com.alibaba.fastjson.JSON;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Title: Kafka2ElasticSearch
 * @ProjectName SparkTrain
 * @Description: TODO kafka流数据写入elasticsearch
 * @Author yisheng.wu
 * @Date 2019/12/1313:32
 */
public class Kafka2ElasticSearch {

    public static class PersonBean {
        private String name;
        private String surname;
        private int age;

        public PersonBean() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "PersonBean{" +
                    "name='" + name + '\'' +
                    ", surname='" + surname + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static void main(String[] args) throws StreamingQueryException {

        SparkSession spark = SparkSession.builder()
                .master("local[*]")
                .appName("Kafka2ElasticSearch")
                .getOrCreate();

        Dataset<Row> dataset = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "192.168.52.129:9092,192.168.52.130:9092,192.168.52.131:9092")
                .option("subscribe", "test_new_client")
                .option("startingOffsets", "earliest")
                .load();

        StreamingQuery query = dataset.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .mapPartitions(new MapPartitionsFunction<Row, PersonBean>() {
                    public Iterator<PersonBean> call(final Iterator<Row> input) throws Exception {
                        List<PersonBean> list = new ArrayList<PersonBean>();
                        input.forEachRemaining(new Consumer<Row>() {
                            public void accept(Row row) {
                                list.add(JSON.parseObject(row.getString(1), PersonBean.class));
                            }
                        });
                        return list.iterator();
                    }
                },Encoders.<PersonBean>bean(PersonBean.class))
                .writeStream()
                .option("checkpointLocation", "/save/location")
                .format("es")
                .trigger(Trigger.ProcessingTime(30000))
                .option("es.index.auto.create", "true")
                .option("es.nodes", "192.168.1.171")
                .option("es.port", "9200")
                .option("es.nodes.wan.only", "true")
                .option("es.write.operation", "upsert")
                .option("es.mapping.id", "name")
                .start("test_new_client");

        query.awaitTermination();
    }

}
