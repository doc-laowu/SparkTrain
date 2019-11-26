package SparkCore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.MutableAggregationBuffer
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction
import org.apache.spark.sql.types._

/**
  * 自定义的无类型的聚合函数
  */
object myAvg extends UserDefinedAggregateFunction {
  //输入参数的类型
  override def inputSchema: StructType = StructType(Array(StructField("inputColumn", LongType)))

  //中间缓存区的数据类型
  override def bufferSchema: StructType = StructType(Array(StructField("sum", LongType), StructField("count", LongType)))

  //最后的返回结果的类型
  override def dataType: DataType = StructType(Array(StructField("result", DoubleType)))

  //这个函数是否总是对相同的输入返回相同的输出
  override def deterministic: Boolean = true

  //初始化缓冲区的值
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0L
  }

  //使用输入数据的值来跟新我们的缓冲区
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    //判断输入的值是否为空
    if(!input.isNullAt(0)){
      buffer(0) = buffer.getLong(0) + input.getLong(0)
      buffer(1) = buffer.getLong(1) + 1
    }

  }

  //合并两个缓冲区的值
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {

    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)

  }

  //计算最后的结果
  override def evaluate(buffer: Row): Any = buffer.getLong(0).toDouble / buffer.getLong(1)

}

object myAvgTest{

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("wordcount").setMaster("local[2]")
    val sc = SparkContext.getOrCreate(conf)
    val spark = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._

    spark.udf.register("myAverage", myAvg)

    val df = spark.read.json("employees.json")
    df.createOrReplaceTempView("employees")
    df.show()

    val result = spark.sql("SELECT myAverage(salary) as average_salary FROM employees")
    result.show()

    sc.stop()

  }

}