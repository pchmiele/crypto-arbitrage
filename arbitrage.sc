import $file.model, model._
import scala.annotation.tailrec

object Arbitrage {
  def findArbitrageOpportunities(graph: DirectedGraph, source: Currency = USD): ArbitrageOpportunities = {
    val initialState: Map[Currency, Distance] = graph.vertices.map {
      case c if c.currency == source => c.currency -> Distance(0.0)
      case c => c.currency -> Distance(Double.MaxValue)
    }.toMap

    val updatedState = relax(graph.edges, initialState, graph.size - 1)
    val changedNodes = lastRelaxation(graph.edges, updatedState)
    ArbitrageOpportunities(changedNodes.map(retraceNegativeLoop(_, updatedState)).filter(_.nonEmpty))
  }

  private def retraceNegativeLoop(start: Currency, distances: Map[Currency, Distance], path: List[Currency] = List.empty): List[Currency] = {
    def retrace(previous: Currency, path: List[Currency]): List[Currency] = {
      distances(previous).prev match {
        case None =>
          List.empty
        case Some(prev) if prev == start =>
          (prev :: path) :+ start
        case Some(prev) if path.contains(prev) =>
          List.empty
        case Some(prev) =>
          retrace(prev, prev :: path)
      }
    }
    retrace(start, path)
  }

  private def lastRelaxation(edges: List[Edge], distances: Map[Currency, Distance]): Set[Currency]  = {
    val candidates = Set.empty[Currency]
    edges.foldLeft(candidates) {
      case (acc, Edge(Vertex(from), Vertex(to), cost)) =>
        val fromCost = distances(from).cost
        val toCost = distances(to).cost
        val newCost = fromCost + cost

        if(toCost > newCost) {
          distances(to).prev.map(prev => acc + prev).getOrElse(acc)
        } else {
          acc
        }
    }
  }

  @tailrec
  private def relax(edges: List[Edge], distances: Map[Currency, Distance], times: Int): Map[Currency, Distance]  = {
    if(times == 0) {
      distances
    } else {
      val updatedDistances = edges.foldLeft(distances) {
        case (acc, Edge(Vertex(from), Vertex(to), cost)) =>
          val fromCost = distances(from).cost
          val toCost = acc(to).cost
          val newCost = fromCost + cost

          if(toCost > newCost) {
            acc.updated(to, Distance(newCost, Some(from)))
          } else {
            acc
          }
      }

      if(distances == updatedDistances) {
        updatedDistances
      } else {
        relax(edges, updatedDistances, times - 1)
      }
    }
  }
}