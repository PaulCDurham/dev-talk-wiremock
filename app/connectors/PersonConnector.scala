package connectors

import com.google.inject.Inject
import models.Person
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, NotFoundException}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PersonConnector @Inject()(httpClient: HttpClient) {

  def get(id: Int): Future[Person] = {

    implicit val headerCarrier: HeaderCarrier = HeaderCarrier().withExtraHeaders("Accept" -> "application/json")

    httpClient.GET[HttpResponse]("http://localhost:9000/person/1") map {
      response =>
        assert(response.status == OK)

        response.json.validate[Person].fold(
          _ => throw ServerReturnedGibberishException(),
          person => person
        )
    } recoverWith {
      case _: NotFoundException => throw NoSuchPersonException(id)
    }

  }

  def post(person: Person): Future[Unit] = {

    implicit val headerCarrier: HeaderCarrier = HeaderCarrier().withExtraHeaders("Content-Type" -> "application/json")

    httpClient.POST[Person, HttpResponse]("http://localhost:9000/person", person) map {
      response =>
        assert(response.status == CREATED)
    }

  }

}
