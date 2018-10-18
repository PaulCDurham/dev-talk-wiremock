# WireMock Dev Forum Talk

## What is WireMock?
http://wiremock.org/

- A simulator for HTTP-based APIs
- A mock HTTP server
- A web server that serves canned responses
- An HTTP interaction recorder

## Use Cases
- In-process unit testing
- Standalone integration testing
- Recording and playback of HTTP interactions
- Simulation of faults
  - Delays
  - Chunked responses
  - Bad responses

## Setup
- Choice of two JARs
- Standard
- Standalone
  - Includes all dependencies
  - Used in example project

```
libraryDependencies += "com.github.tomakehurst" % "wiremock" % "2.19.0"
libraryDependencies += "com.github.tomakehurst" % "wiremock-standalone" % "2.19.0"
```

## Configuration
- WireMockServer
- WireMockConfiguration.wireMockConfig
- Ports
  - port(8000)
  - dynamicPort()
  - httpsPort(8443)
  - dynamicHttpsPort()
- Proxy settings
- File locations

```
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
```

## Stubbing
- Matches request to response
- Pretty much like Mockito
- Matchers exist or URsL, headers, and body
  - Be very strict for a happy path GET
  - Be lax for an exceptional response
- DSL
- JSON mapping files
- No match results in 404 Not Found

```
server
  .stubFor(
    get("/hello")
      .willReturn(
        aResponse()
          .withStatus(200)
          .withBody("Hello, world!")
      )
  )
```

## Verifying
- Verrify that a request was received
- Again similar to Mockito
- Again uses pattern matching
- Failures throw VerificationException

```
verify(getRequestedFor(urlEqualTo("/hello")))
```

## Example REST API
This is the REST interface for the example connector

```
GET /person/:id
```
Request
- Headers: Accept = application/json

Responses

- 200 OK
  - Header: Content-Type = application/json
  - Body: JSON representation of a person
- 404 Not Found

```
POST /person
```
Request
- Header: Content-Type = application/json

Responses
- 201 Created
  - Header: Location = /person/:id

## HttpClient
- Mockito or WireMock?
- Don't mock what you don't own
  - https://github.com/mockito/mockito/wiki/How-to-write-good-tests#dont-mock-type-you-dont-own
- HttpClient has complex behaviours
  - Adds headers
  - Converts to/from JSON
  - But doesn't (easily) let you handle an invalid response
  - Throws HttpException
- WireMock
  - Tests the behaviour of the whole connector stack
  - Does add some overhead
