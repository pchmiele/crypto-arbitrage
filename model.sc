import $ivy.`org.typelevel::cats-core:2.3.0`

import scala.util.Try
import cats.implicits._

sealed trait Currency

case object USD extends Currency
case object EUR extends Currency
case object JPY extends Currency
case object BTC extends Currency

object Currency {
  def apply(currency: String): Currency = currency match {
    case "USD" => USD
    case "EUR" => EUR
    case "JPY" => JPY
    case "BTC" => BTC
  }
}

case class Exchange(from: Currency, to: Currency)
case class ExchangeRates(rates: Map[Exchange, Double])

object ExchangeRates {
  def create(in: Map[String, String]): Try[ExchangeRates] = {
    Try {
      val exchangeRates = in.map { case (key, value) =>
        val a :: b :: Nil = key.split("_").toList
        Exchange(Currency(a), Currency(b)) -> value.toDouble
      }

      ExchangeRates(exchangeRates)
    }.adaptErr(_ => new Exception("Malformed input. Could not parse exchange rates. Please check if data model has changed."))
  }
}

case class Vertex(currency: Currency)
case class Edge(from: Vertex, to: Vertex, cost: Double) {
  def fromCurrency: Currency = from.currency
  def toCurrency: Currency = to.currency
}

object Edge {
  def apply(exchange: Exchange, cost: Double): Edge =
    Edge(Vertex(exchange.from), Vertex(exchange.to), -Math.log(cost))
}

case class DirectedGraph(graphRepr: Map[Vertex, List[Edge]]) {
  lazy val size: Int = graphRepr.keys.size

  lazy val vertices: List[Vertex] = graphRepr.keys.toList
  lazy val edges: List[Edge] = graphRepr.values.flatten.toList

  def addEdge(newEdge: Edge): DirectedGraph = {
    newEdge match {
      case Edge(from, to, value) if value == 0 && from == to =>
        DirectedGraph(graphRepr)
      case _ =>
        graphRepr.get(newEdge.from) match {
          case Some(edges) =>
            DirectedGraph(graphRepr + (newEdge.from -> (newEdge :: edges)))

          case None =>
            DirectedGraph(graphRepr + (newEdge.from -> List(newEdge)))
        }
    }

  }
}

object DirectedGraph {
  def empty: DirectedGraph = DirectedGraph(Map.empty[Vertex, List[Edge]])

  def apply(exchangeRates: ExchangeRates): DirectedGraph = {
    exchangeRates.rates.foldLeft(DirectedGraph.empty){ case (graph, (exchange, cost)) =>
      graph.addEdge(Edge(exchange, cost))
    }
  }
}

case class Distance(cost: Double, prev: Option[Currency] = None)

case class ArbitrageOpportunities(opportunities: Set[List[Currency]] = Set.empty)
