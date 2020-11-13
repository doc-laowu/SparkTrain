package DeltaLake

import org.apache.spark.sql.{Dataset, Row, SparkSession}

/**
  * @Title: ReadWriteDemo
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/11/2616:26
  */
object ReadWriteDemo {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    // 写入数据
    spark.range(0, 20)
      .write
      .format("delta")
      .save("/deltalake/delta-table")

    // 读取数据
    spark.read
      .format("delta")
      .load("/deltalake/delta-table")
      .show()

    // 更新数据
    spark.range(5, 10)
      .write
      .format("delta")
      .mode("overwrite")
      .save("/deltalake/delta-table")

    spark.stop()

  }

}
