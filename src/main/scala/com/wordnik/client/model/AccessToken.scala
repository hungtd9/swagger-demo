package com.wordnik.client.model

/**
 * Created by hungtran on 4/10/16.
 */

import com.mongodb.casbah.Imports._
import com.novus.salat.dao.SalatDAO
import com.wordnik.client.mongoContext._
import org.joda.time.DateTime

case class AccessToken(
  objectId: Option[String] = None,
  updatedAt: Option[DateTime] = None,
  createdAt: Option[DateTime] = None,
  uid: Option[String] = None,
  accessToken: Option[String] = None,
  refreshToken: Option[String] = None,
  expiredAt: Option[DateTime] = None)

object AccessTokenDAO extends SalatDAO[AccessToken, ObjectId](collection = MongoConnection()("fossil_q_dev")("AccessToken")) {

  def findOneByToken(token: String): Option[AccessToken] = findOne(DBObject("accessToken" -> token))
}
