import $ivy.`com.softwaremill.sttp.client3::core:3.3.6`
import $ivy.`com.softwaremill.sttp.client3::circe:3.3.6`
import $file.model, model._
import sttp.client3._
import sttp.client3.circe._

object PriceonomicsClient {
  val backend = HttpURLConnectionBackend()
  val ratesUrl = "https://fx.priceonomics.com/v1/rates/"

  def getRates(): Either[String, ExchangeRates] = {
    basicRequest
      .get(uri"$ratesUrl")
      .response(asJson[Map[String, String]])
      .send(backend)
      .body.flatMap(ExchangeRates.create(_).toEither)
      .left
      .map(_.getMessage)
  }
}