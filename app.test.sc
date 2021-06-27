import $ivy.`org.scalatest::scalatest:3.2.9`
import $file.arbitrage, arbitrage.Arbitrage
import $file.model, model._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ArbitrageSpec extends AnyFlatSpec with Matchers {
  "Arbitrage.findArbitrageOpportunities" should "not find arbitrage opportunities on empty input" in {
    val exchangeRates = ExchangeRates(Map())
    val directedGraph = DirectedGraph(exchangeRates)
    Arbitrage.findArbitrageOpportunities(directedGraph) shouldBe ArbitrageOpportunities(Set())
  }

  it should "not find arbitrage opportunities if there are no negative cycles (only negative rates)" in {
    val exchangeRates = ExchangeRates(
      Map(
        Exchange(EUR, USD) -> 0.1,
        Exchange(USD, USD) -> 0.2,
        Exchange(BTC, USD) -> 0.3,
        Exchange(USD, EUR) -> 0.4,
        Exchange(EUR, BTC) -> 0.2,
        Exchange(EUR, JPY) -> 0.3,
        Exchange(BTC, BTC) -> 0.2,
        Exchange(BTC, JPY) -> 0.1,
        Exchange(JPY, BTC) -> 0.2,
        Exchange(JPY, EUR) -> 0.4,
        Exchange(BTC, EUR) -> 0.3,
        Exchange(JPY, JPY) -> 0.2,
        Exchange(EUR, EUR) -> 0.1,
        Exchange(USD, BTC) -> 0.4,
        Exchange(USD, JPY) -> 0.2,
        Exchange(JPY, USD) -> 0.1
      )
    )

    val directedGraph = DirectedGraph(exchangeRates)
    Arbitrage.findArbitrageOpportunities(directedGraph) shouldBe ArbitrageOpportunities(Set())
  }

  it should "not find arbitrage opportunities if there are no negative cycles" in {
    val exchangeRates = ExchangeRates(
      Map(
        Exchange(EUR, USD) -> 0.5,
        Exchange(USD, USD) -> 1.0,
        Exchange(BTC, USD) -> 0.5,
        Exchange(USD, EUR) -> 2.0,
        Exchange(EUR, BTC) -> 1.0,
        Exchange(EUR, JPY) -> 1.0,
        Exchange(BTC, BTC) -> 1.0,
        Exchange(BTC, JPY) -> 1.0,
        Exchange(JPY, BTC) -> 1.0,
        Exchange(JPY, EUR) -> 1.0,
        Exchange(BTC, EUR) -> 1.0,
        Exchange(JPY, JPY) -> 1.0,
        Exchange(EUR, EUR) -> 1.0,
        Exchange(USD, BTC) -> 1.0,
        Exchange(USD, JPY) -> 1.0,
        Exchange(JPY, USD) -> 0.5
      )
    )

    val directedGraph = DirectedGraph(exchangeRates)
    Arbitrage.findArbitrageOpportunities(directedGraph) shouldBe ArbitrageOpportunities(Set())
  }

  it should "find arbitrage opportunities if there is negative cycle (long)" in {
    val exchangeRates = ExchangeRates(
      Map(
        Exchange(EUR, USD) -> 2.0,
        Exchange(USD, USD) -> 1.0,
        Exchange(BTC, USD) -> 2.0,
        Exchange(USD, EUR) -> 1.7,
        Exchange(EUR, BTC) -> 1.5,
        Exchange(EUR, JPY) -> 1.6,
        Exchange(BTC, BTC) -> 1.0,
        Exchange(BTC, JPY) -> 2.0,
        Exchange(JPY, BTC) -> 2.0,
        Exchange(JPY, EUR) -> 2.0,
        Exchange(BTC, EUR) -> 1.9,
        Exchange(JPY, JPY) -> 1.0,
        Exchange(EUR, EUR) -> 1.0,
        Exchange(USD, BTC) -> 2.0,
        Exchange(USD, JPY) -> 2.0,
        Exchange(JPY, USD) -> 1.3
      )
    )

    val directedGraph = DirectedGraph(exchangeRates)
    Arbitrage.findArbitrageOpportunities(directedGraph) shouldBe ArbitrageOpportunities(
      Set(
        List(BTC, JPY, EUR, USD, BTC),
        List(USD, BTC, JPY, EUR, USD),
        List(EUR, USD, BTC, JPY, EUR),
        List(JPY, EUR, USD, BTC, JPY)
      )
    )
  }

  it should "find arbitrage opportunities if there is negative cycle (short)" in {
    val exchangeRates = ExchangeRates(
      Map(
        Exchange(EUR, USD) -> 1.1280049,
        Exchange(USD, USD) -> 1.0,
        Exchange(BTC, USD) -> 135.1249417,
        Exchange(USD, EUR) -> 0.8308155,
        Exchange(EUR, BTC) -> 0.0098759,
        Exchange(EUR, JPY) -> 115.5731644,
        Exchange(BTC, BTC) -> 1.0,
        Exchange(BTC, JPY) -> 13832.2287711,
        Exchange(JPY, BTC) -> 0.0000755,
        Exchange(JPY, EUR) -> 0.0070704,
        Exchange(BTC, EUR) -> 99.6715878,
        Exchange(JPY, JPY) -> 1.0,
        Exchange(EUR, EUR) -> 1.0,
        Exchange(USD, BTC) -> 0.0088666,
        Exchange(USD, JPY) -> 109.4286271,
        Exchange(JPY, USD) -> 0.009089
      )
    )

    val directedGraph = DirectedGraph(exchangeRates)
    Arbitrage.findArbitrageOpportunities(directedGraph) shouldBe ArbitrageOpportunities(
      Set(
        List(USD, BTC, USD),
        List(BTC, USD, BTC)
      )
    )
  }

  it should "find same arbitrage opportunities no matter where it starts" in {
    val exchangeRates = ExchangeRates(
      Map(
        Exchange(EUR, USD) -> 1.1280049,
        Exchange(USD, USD) -> 1.0,
        Exchange(BTC, USD) -> 135.1249417,
        Exchange(USD, EUR) -> 0.8308155,
        Exchange(EUR, BTC) -> 0.0098759,
        Exchange(EUR, JPY) -> 115.5731644,
        Exchange(BTC, BTC) -> 1.0,
        Exchange(BTC, JPY) -> 13832.2287711,
        Exchange(JPY, BTC) -> 0.0000755,
        Exchange(JPY, EUR) -> 0.0070704,
        Exchange(BTC, EUR) -> 99.6715878,
        Exchange(JPY, JPY) -> 1.0,
        Exchange(EUR, EUR) -> 1.0,
        Exchange(USD, BTC) -> 0.0088666,
        Exchange(USD, JPY) -> 109.4286271,
        Exchange(JPY, USD) -> 0.009089
      )
    )

    val directedGraph = DirectedGraph(exchangeRates)
    val usdResult = Arbitrage.findArbitrageOpportunities(directedGraph, USD)
    val eurResult = Arbitrage.findArbitrageOpportunities(directedGraph, EUR)
    val jpyResult = Arbitrage.findArbitrageOpportunities(directedGraph, JPY)
    val btcResult = Arbitrage.findArbitrageOpportunities(directedGraph, BTC)
    usdResult shouldBe eurResult
    usdResult shouldBe jpyResult
    usdResult shouldBe btcResult
  }
}

(new ArbitrageSpec).execute()