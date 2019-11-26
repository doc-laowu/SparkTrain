package DeltaLake

import org.apache.spark.sql.SparkSession

/**
  * @Title: ReadDataByVersion
  * @ProjectName SparkTrain
  * @Description: TODO
  * @Author yisheng.wu
  * @Date 2019/11/2616:59
  */
object ReadDataByVersion {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .getOrCreate()

    // 读取指定版本的数据
    spark.read
      .format("delta")
      .option("versionAsOf", 0)
      .load("/deltalake/delta-table").show()

  }

}
