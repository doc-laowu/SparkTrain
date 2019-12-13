package StructuredStreaming;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Title: HotItems
 * @ProjectName SparkTrain
 * @Description: TODO
 * @Author yisheng.wu
 * @Date 2019/12/1318:52
 */
public class HotItems {

    public static class UserBehavior{

        public long userId;         // 用户ID
        public long itemId;         // 商品ID
        public int categoryId;      // 商品类目ID
        public String behavior;     // 用户行为, 包括("pv", "buy", "cart", "fav")
        public Timestamp timestamp;      // 行为发生的时间戳，单位秒

        public UserBehavior(long userId, long itemId, int categoryId, String behavior, Timestamp timestamp) {
            this.userId = userId;
            this.itemId = itemId;
            this.categoryId = categoryId;
            this.behavior = behavior;
            this.timestamp = timestamp;
        }

        public static UserBehavior of(String userId, String itemId, String categoryId, String behavior, String timestamp){

            long _userId = Long.parseLong(userId);
            long _itemId = Long.parseLong(itemId);
            int _categoryId = Integer.parseInt(categoryId);
            Timestamp _timestamp = new Timestamp(Long.parseLong(timestamp));
            UserBehavior userBehavior = new UserBehavior(_userId, _itemId, _categoryId, behavior, _timestamp);

            return userBehavior;
        }

    }

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("JavaStructuredNetworkWordCountWindowed")
                .master("local[*]")
                .getOrCreate();
        spark.sparkContext().setLogLevel("WARN");
        // Create DataFrame representing the stream of input lines from connection to host:port
        Dataset<Row> lines = spark
                .readStream()
                .format("socket")
                .option("host", "myMaster01")
                .option("port", 9999)
                .load();

        Dataset<UserBehavior> userBehaviorDataset = lines.flatMap(new FlatMapFunction<Row, UserBehavior>() {

            @Override
            public Iterator<UserBehavior> call(Row row) throws Exception {

                List<UserBehavior> result = new ArrayList<>();

                String line = row.getString(0);
                String[] split = line.split(",");
                UserBehavior userBehavior = UserBehavior.of(split[0], split[1], split[2], split[3], split[4]);
                result.add(userBehavior);

                return result.iterator();
            }
        }, Encoders.<UserBehavior>bean(UserBehavior.class));

        userBehaviorDataset.groupByKey(new MapFunction<UserBehavior, UserBehavior>(){

            @Override
            public UserBehavior call(UserBehavior value) throws Exception {



                return null;
            }
        }, Encoders.bean(UserBehavior.class));



    }

}
