package connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, BeforeAndAfterEach}

class SkeletonWireMockSpec extends AsyncFlatSpec with BeforeAndAfterAll with BeforeAndAfterEach {

  private val server: WireMockServer = new WireMockServer(wireMockConfig.dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
  }

  override def afterAll(): Unit = {
    server.stop()
  }

}
