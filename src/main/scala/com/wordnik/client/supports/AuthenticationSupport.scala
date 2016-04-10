package com.wordnik.client.supports

import java.util.Locale
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

import authentikat.jwt.JsonWebToken
import org.scalatra.auth.{ ScentryConfig, ScentryStrategy, ScentrySupport }
import org.scalatra.{ ScalatraBase, Unauthorized }

/**
 * Authentication support for routes
 */
trait AuthenticationSupport extends ScentrySupport[User] {
  self: ScalatraBase =>

  protected def fromSession = { case id: String => User(id) }
  protected def toSession = { case usr: User => usr.id }

  val realm = "Bearer Authentication"
  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  override protected def configureScentry = {
    scentry.unauthenticated {
      scentry.strategies("Bearer").unauthenticated()
    }
  }

  override protected def registerAuthStrategies = {
    scentry.register("Bearer", app => new BearerStrategy(app, realm))
  }

  // verifies if the request is a Bearer request
  protected def auth()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    val baReq = new BearerAuthRequest(request)
    if (!baReq.providesAuth) {
      halt(401, "Unauthenticated")
    }
    if (!baReq.isBearerAuth) {
      halt(400, "Bad Request")
    }
    val user = scentry.authenticate("Bearer")
    if (user.isEmpty) halt(401, "Unauthenticated")
  }

}

class BearerStrategy(protected override val app: ScalatraBase, realm: String) extends ScentryStrategy[User] {

  implicit def request2BearerAuthRequest(r: HttpServletRequest): BearerAuthRequest = new BearerAuthRequest(r)

  protected def getUserId(user: User): String = user.id

  override def isValid(implicit request: HttpServletRequest) = request.isBearerAuth && request.providesAuth

  // catches the case that we got none user
  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    app halt Unauthorized()
  }

  // overwrite required authentication request
  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = validate(request.token)

  protected def validate(jwt: String): Option[User] = {
    jwt match {
      case JsonWebToken(header, claimsSet, signature) =>
        val claims = claimsSet.asSimpleMap.toOption
        Some(User(claims.get("id")))
      case x =>
        None
    }
  }

}

class BearerAuthRequest(r: HttpServletRequest) {

  private val AUTHORIZATION_KEYS = List("Authorization", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION")
  def parts = authorizationKey map { r.getHeader(_).split(" ", 2).toList } getOrElse Nil
  def scheme: Option[String] = parts.headOption.map(sch => sch.toLowerCase(Locale.ENGLISH))
  def token: String = parts.lastOption getOrElse ""

  private def authorizationKey = AUTHORIZATION_KEYS.find(r.getHeader(_) != null)

  def isBearerAuth = (false /: scheme) { (_, sch) => sch == "bearer" }
  def providesAuth = authorizationKey.isDefined

}

case class User(id: String)

