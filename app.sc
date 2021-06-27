import $file.client, client.PriceonomicsClient
import $file.ui, ui.UI
import $file.model, model._
import $file.arbitrage, arbitrage.Arbitrage

PriceonomicsClient.getRates()
  .fold(
    exception => println(s"Couldn't fetch exchange rates because of: $exception"),
    exchangeRates => {
      UI.displayRates(exchangeRates)
      val graph = DirectedGraph(exchangeRates)
      val arbitrageOpportunities = Arbitrage.findArbitrageOpportunities(graph)
      UI.displayArbitrageOpportunities(arbitrageOpportunities, exchangeRates)
    }
  )