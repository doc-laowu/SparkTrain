package SparkSql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object DataSource {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("DataSource")
    val spark = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._

    //直接从文件读取数据进行显示
    val sqlDF = spark.sql("SELECT * FROM parquet.`src/main/resources/users.parquet`").show()

    //创建一个DaSet[String]的里面的一列存储了一个json的对象
    val otherPeopleDataset = spark.createDataset(
      """{"name":"Yin","address":{"city":"Columbus","state":"Ohio"}}"""::Nil)
    //使用读取json的方式去读取DataSet
    val otherPeople = spark.read.json(otherPeopleDataset)
    otherPeopleDataset.persist()
    otherPeopleDataset.unpersist()
    otherPeople.show()

    spark.stop()
  }

}
