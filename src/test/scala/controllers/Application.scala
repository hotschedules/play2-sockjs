package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.iteratee._
import play.api.mvc._
import play.sockjs.api._

abstract class SockJSTestRouter(websocket: Boolean = true, cookies: Boolean = false) extends SockJSRouter with Controller {
  override def server = {
    val settings = SockJSSettings(
      websocket = websocket,
      cookies = if (cookies) Some(SockJSSettings.CookieCalculator.jsessionid) else None,
      streamingQuota = 4096)
    SockJSServer(settings)
  }
}

/**
 * responds with identical data as received
 */
class Echo extends SockJSTestRouter {

  def sockjs = SockJS.using { req =>
    Concurrent.joined[String]
  }

}

/**
 * identical to echo, but with websockets disabled
 */
class DisabledWebSocketEcho extends SockJSTestRouter(false) {

  def sockjs = SockJS.using { req =>
    Concurrent.joined[String]
  }

}

/**
 * identical to echo, but with JSESSIONID cookies sent
 */
class CookieNeededEchoController extends SockJSTestRouter(true, false) {

  def sockjs = SockJS.using { req =>
    Concurrent.joined[String]
  }

}

/**
 * server immediately closes the session
 */
class Closed extends SockJSTestRouter {

  def sockjs = SockJS.using { req =>
    (Iteratee.ignore[String], Enumerator.eof[String])
  }

}