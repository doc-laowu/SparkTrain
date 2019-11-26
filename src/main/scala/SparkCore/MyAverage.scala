package SparkCore

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import org.apache.spark.sql.expressions.Aggregator

case class Employee(name: String, salary: Long)
case class Average(var sum: Long, var count: Long)

object MyAverage extends Aggregator[Employee, Average, Double] {

  //此聚合的值为零。是否满足任意b + 0 = b的性质
  def zero: Average = Average(0L, 0L)

  //将两个值组合生成一个新值。为了提高性能，该函数可以修改“buffer”
  //返回它，而不是构造一个新对象
  def reduce(buffer: Average, employee: Employee): Average = {
    buffer.sum += employee.salary
    buffer.count += 1
    buffer
  }

  // 合并两个中间值
  def merge(b1: Average, b2: Average): Average = {
    b1.sum += b2.sum
    b1.count += b2.count
    b1
  }

  //转换reduce的输出
  def finish(reduction: Average): Double = reduction.sum.toDouble / reduction.count

  //指定中间值类型的编码器
  def bufferEncoder: Encoder[Average] = Encoders.product

  //指定最终输出值类型的编码器
  def outputEncoder: Encoder[Double] = Encoders.scalaDouble

}


object MyAverageTest{

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("wordcount").setMaster("local[2]")
    val sc = SparkContext.getOrCreate(conf)
    val spark = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._

    val ds = spark.read.json("employees.json").as[Employee]
    ds.show()


    // Convert the function to a `TypedColumn` and give it a name
    val averageSalary = MyAverage.toColumn.name("average_salary")
    val result = ds.select(averageSalary)
    result.show()

  }

}