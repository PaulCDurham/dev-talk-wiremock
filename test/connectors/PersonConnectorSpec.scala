package connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models.Person
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, BeforeAndAfterEach, Matchers}
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.Upstream5xxResponse

class PersonConnectorSpec extends AsyncFlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  private val server: WireMockServer = new WireMockServer(wireMockConfig.port(9000))

  override def beforeAll(): Unit = {
    server.start()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
  }

  override def afterAll(): Unit = {
    server.stop()
  }

  private val app: Application = new GuiceApplicationBuilder().build()

  "PersonConnector.get" should "return the person retrieved by a valid request" in {

    val person = Person(1, "Joe", "Bloggs")

    server
      .stubFor(
        get(urlEqualTo("/person/1"))
          .withHeader("Accept", equalTo("application/json"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(person)))
          )
      )

    app.injector.instanceOf[PersonConnector].get(1) map {
      actual =>
        actual shouldBe person
    }

  }

  it should "throw NoSuchPersonException when a 404 Not Found response is received" in {

    server
      .stubFor(
        get(urlEqualTo("/person/1"))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

    recoverToSucceededIf[NoSuchPersonException] {
      app.injector.instanceOf[PersonConnector].get(1)
    }

  }

  it should "throw ServerReturnedGibberishException when non-valid JSON is received" in {

    server
      .stubFor(
        get(urlEqualTo("/person/1"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody("{}")
          )
      )

    recoverToSucceededIf[ServerReturnedGibberishException] {
      app.injector.instanceOf[PersonConnector].get(1)
    }

  }

  it should "demonstrate a characterisation rather then specification test" in {

    server
      .stubFor(
        get(urlEqualTo("/person/1"))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

    recoverToSucceededIf[Upstream5xxResponse] {
      app.injector.instanceOf[PersonConnector].get(1)
    }

  }

  "PersonConnector.post" should "handle a successful 201 Created response" in {

    val person = Person(1, "Joe", "Bloggs")
    val json = Json.stringify(Json.toJson(person))

    server
      .stubFor(
        post(urlEqualTo("/person"))
          .withHeader("Content-Type", equalTo("application/json"))
          .withRequestBody(equalToJson(json))
          .willReturn(
            aResponse()
              .withStatus(CREATED)
              .withHeader("Location", s"http://localhost:9000/person/${person.id}")
          )
      )

    app.injector.instanceOf[PersonConnector].post(person) map {
      _ =>
        succeed
    }

  }

}
