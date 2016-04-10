package com.wordnik.client.supports

import javax.servlet.http.{ HttpServletResponse, HttpServletRequest }

import org.scalatra.ScalatraBase

/**
 * Created by hungtran on 4/10/16.
 */
trait MediaTypeSupport {
  self: ScalatraBase =>

  def checkJsonRequest()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    if (request.getHeader("Content-Type") != "application/json") {
      halt(415, "Only the Content-Type of application/json and its extensions can be accepted.")
    }
  }
}
