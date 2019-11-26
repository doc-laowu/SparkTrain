package ScalaTest

object myTest {

  def main(args: Array[String]): Unit = {

    //fold操作
//    val list = List(1,2,3,4,5)
//    println(list.fold(0)(_ + _))
//    println(list.foldLeft(0)(_ - _))
//    println(list.foldRight(1)(_ + _))

    //基于scala的单机版的wordcount操作
//    val file = new File("E:/myfile/sbt_build_model.txt")
//    val wordsMap=scala.collection.mutable.Map[String,Int]()
//    val result = Source.fromFile(file).getLines().flatMap(_.split(" ")).map((_, 1)).toList
//      .groupBy(_._1).mapValues(_.reduce((x, y)=> (x._1, x._2 + y._2))).foreach(println)

    //简单匹配
//    val colorNum = 4
//    val colorStr = colorNum match {
//      case 1 => "red"
//      case 2 => "green"
//      case 3 => "yellow"
////      case _ => " Not Allowed"
//      case unexpected  => s"$unexpected is Not Allowed"
//    }
//    println(colorStr)

    //类型模式
//    for (elem <- List(9,12.3,"Spark","Hadoop",'Hello)){
//      val str  = elem match{
//        case i: Int => i + " is an int value."
//        case d: Double => d + " is a double value."
//        case "Spark"=> "Spark is found."
//        case s: String => s + " is a string value."
//        case _ => "This is an unexpected value."
//      }
//      println(str)
//    }

//    //case类的匹配
//    case class Car(brand: String, price: Int)
//    val myBYDCar = new Car("BYD", 89000)
//    val myBMWCar = new Car("BMW", 1200000)
//    val myBenzCar = new Car("Benz", 1500000)
//    for (car <- List(myBYDCar, myBMWCar, myBenzCar)) {
//      car match{
//        case Car("BYD", 89000) => println("Hello, BYD!")
//        case Car("BMW", 1200000) => println("Hello, BMW!")
//        case Car(brand, price) => println("Brand:"+ brand +", Price:"+price+", do you want it?")
//      }
//    }


    val m = Map(
      "name" -> "john doe",
      "age" -> 18,
      "hasChild" -> true,
      "childs" -> List(
        Map("name" -> "dorothy", "age" -> 5, "hasChild" -> false),
        Map("name" -> "bill", "age" -> 8, "hasChild" -> false)
      )
    )
//    implicit val formats = Serialization.formats(NoTypeHints)
//    println((write(m)))

    import org.codehaus.jackson.map.ObjectMapper
    val mapper = new ObjectMapper()
    // convert to json formatted string
    val jstring  = mapper.writeValueAsString(m)
    println(jstring)

  }

}
