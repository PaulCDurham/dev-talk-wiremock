package connectors

abstract class ConnectorException(message: String) extends Exception
case class NoSuchPersonException(id: Int) extends ConnectorException(s"No Person with Id $id")
case class ServerReturnedGibberishException() extends ConnectorException("The server returned gibberish and not JSON")
