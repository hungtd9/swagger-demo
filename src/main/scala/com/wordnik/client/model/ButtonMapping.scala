package com.wordnik.client.model

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global._
import org.joda.time.{ DateTime, DateTimeZone }

case class ButtonMapping(
  objectId: String,
  deviceId: String,
  gesture: Int,
  action: Int,
  updatedAt: DateTime = new DateTime(0, DateTimeZone.UTC),
  createdAt: DateTime = new DateTime(0, DateTimeZone.UTC))

object ButtonMappingDAO extends SalatDAO[ButtonMapping, ObjectId](collection = MongoConnection()("fossil_q_dev")("Mapping")) {

  val DefaultOffset = 0
  val DefaultLimit = 20

  // Indexes
  collection.createIndex(DBObject("deviceId" -> 1))
  collection.createIndex(DBObject("objectId" -> 1))

  def findByDevice(deviceId: String, limit: Int = this.DefaultLimit, offset: Int = this.DefaultOffset): List[ButtonMapping] = {
    find(DBObject("deviceId" -> deviceId)).limit(limit).skip(offset).toList
  }

  def findOneByMode(deviceId: String, gestureId: Int): Option[ButtonMapping] = {
    findOne(DBObject("deviceId" -> deviceId, "gesture" -> gestureId))
  }

  def update(deviceId: String, gestureId: Int, updatedObject: ButtonMapping): Unit = {
    update(
      q = DBObject("deviceId" -> deviceId, "gesture" -> gestureId),
      o = DBObject("$set" -> toDBObject(updatedObject))
    )
  }

  def delete(deviceId: String, gestureId: Option[Int] = None) = {
    gestureId match {
      case Some(m) => remove(q = DBObject("deviceId" -> deviceId, "gesture" -> gestureId))
      case None => remove(q = DBObject("deviceId" -> deviceId))
    }
  }
}
