object Main extends App {

    var sum = 0.0
    for( a <- 0 to 17 ) {
      sum = sum + 500 * Math.pow(1.1, a)
      println(sum);
    }
}