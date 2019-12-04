package DeltaLake

import io.delta.tables.DeltaTable
import org.apache.spark.sql.SparkSession

/**
  * @Title: DelUpdateMergeDemo
  * @ProjectName SparkTrain
  * @Description: TODO deltalake表的删除、更新和合并
  * @Author yisheng.wu
  * @Date 2019/11/2711:50
  */
object DelUpdateMergeDemo {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .config("spark.sql.shuffle.partitions", 12)
      .master("local[*]")
      .getOrCreate();

    import spark.implicits._

    // 加载表数据
    val deltaTable = DeltaTable.forPath("/deltalake/events/")

    // 删除指定条件的数据
    deltaTable.delete("date < '2017-01-01'")

    // 根据条件更新指定字段的值
    deltaTable.updateExpr(
      "eventType = 'clck'",
      Map("eventType" -> "'click'")
    )

    // 加载要更新的数据
    //创建一个DaSet[String]的里面的一列存储了一个json的对象
    val otherPeopleDataset = spark.createDataset("""{"date":"2019-11-27 16:53:24", "eventId":1234, "data":"thi is the rule"}"""::Nil)
    //使用读取json的方式去读取DataSet
    val updatesDF = spark.read.json(otherPeopleDataset)

    // 使用合并向上插入表

    /**
      * 当表中存在该条数据的key时就更新，否则就插入新数据
      */
    deltaTable.as("events")
      .merge(
        updatesDF.as("updates"),
        "events.eventId = updates.eventId"
      ).whenMatched()
      .updateExpr(
        Map("data" -> "updates.data")
      ).whenNotMatched
      .insertExpr(
        Map(
        "date" -> "updates.date",
        "eventId" -> "updates.eventId",
        "data" -> "updates.data"
        )
      ).execute()

    /**
      * 写入Delta表时的重复数据删除
      */
    deltaTable.as("logs")
      .merge(
        updatesDF.as("updates"),
        "logs.uniqueId = updates.uniqueId"
      ).whenNotMatched
      .insertAll()
      .execute()

  }

}
