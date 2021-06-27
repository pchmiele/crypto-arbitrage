import $file.model, model.{ArbitrageOpportunities, Currency, Exchange, ExchangeRates}

object UI {
  def displayRates(exchangeRates: ExchangeRates): Unit = {
    println("Exchange rates:")
    println("---------------")
    exchangeRates.rates.foreach { case (Exchange(from, to), rate) =>
      println(s"$from -> $to, Rate: $rate")
    }
    println("---------------")
  }

  def displayArbitrageOpportunity(path: List[Currency], exchangeRates: ExchangeRates, startValue: Double): Unit = {
    println("\nThere is an arbitrage opportunity: " + path.mkString(" -> "))
    val exchangesWithRates = path.sliding(2).map { case List(from, to) =>
      val exchange = Exchange(from, to)
      (exchange, exchangeRates.rates(exchange))
    }
    val endValue = exchangesWithRates.foldLeft(startValue) {
      case (value, (Exchange(from, to), rate)) =>
        val exchangedValue = value * rate
        println(s"  Trade: $value $from to $exchangedValue $to")
        exchangedValue
    }
    val profit = ((endValue - startValue) / startValue) * 100.0
    println(s"Profit: $profit%")
  }

  def displayArbitrageOpportunities(arbitrageOpportunities: ArbitrageOpportunities, exchangeRates: ExchangeRates, startValue: Double = 100): Unit = {
    arbitrageOpportunities.opportunities match {
      case opportunities if opportunities.isEmpty =>
        println("There are no arbitrage opportunities")
      case opportunities =>
        println("\nList of arbitrage opportunities: ")
        opportunities.foreach(displayArbitrageOpportunity(_, exchangeRates, startValue))
    }
  }
}
