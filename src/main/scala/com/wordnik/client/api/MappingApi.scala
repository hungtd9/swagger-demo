package com.wordnik.client.api

import com.wordnik.client.model.{ ButtonMappingDAO, ButtonMapping, MappingInsertParams, MappingUpdateParams }
import com.wordnik.client.supports.{ MediaTypeSupport, AuthenticationSupport }
import org.json4s._
import org.scalatra.{ Ok, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.FileUploadSupport
import org.scalatra.swagger._

trait BaseApi extends ScalatraServlet
    with FileUploadSupport
    with JacksonJsonSupport
    with SwaggerSupport
    with AuthenticationSupport
    with MediaTypeSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    checkJsonRequest()
    auth()
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }
}

class MappingApi(implicit val swagger: Swagger) extends BaseApi {

  protected val applicationDescription: String = "MappingApi"
  override protected val applicationName: Option[String] = Some("Mapping")

  val insertOperation = (apiOperation[ButtonMapping]("insert")
    summary "Insert mapping"
    parameters (pathParam[String]("deviceId").description(""),
      bodyParam[MappingInsertParams]("body").description("").optional)
  )

  post("/device/:deviceId/mapping", operation(insertOperation)) {

    val deviceId = params.getOrElse("deviceId", halt(400))
    bodyParam[MappingInsertParams]("body").description("").optional

    println("deviceId: " + deviceId)
    println("body: ")
  }

  val listOperation = (apiOperation[List[ButtonMapping]]("list")
    summary "List button"
    parameters (pathParam[String]("deviceId").description(""),
      queryParam[Int]("limit").description("").optional.defaultValue(20),
      queryParam[Int]("offset").description("").optional.defaultValue(0))
  )

  get("/device/:deviceId/mapping", operation(listOperation)) {

    val deviceId = params.getOrElse("deviceId", halt(400))
    val limit = params.getAs[Int]("limit").getOrElse(20)
    val offset = params.getAs[Int]("offset").getOrElse(0)

    val mappings = ButtonMappingDAO.findByDevice(deviceId, limit, offset)
    Ok(mappings)
  }

  val removeOperation = (apiOperation[Unit]("remove")
    summary "Remove mapping"
    parameters (pathParam[String]("deviceId").description(""),
      pathParam[Int]("gestureId").description(""))
  )

  delete("/device/:deviceId/mapping/:gestureId", operation(removeOperation)) {
    val deviceId = params.getOrElse("deviceId", halt(400))
    val gestureId = params.getOrElse("gestureId", halt(400))

    println("deviceId: " + deviceId)
    println("gestureId: " + gestureId)
  }

  val removeAllOperation = (apiOperation[Unit]("removeAll")
    summary "Remove mapping"
    parameters (pathParam[String]("deviceId").description(""))
  )

  delete("/device/:deviceId/mapping", operation(removeAllOperation)) {
    val deviceId = params.getOrElse("deviceId", halt(400))
    println("deviceId: " + deviceId)
  }

  val showOperation = (apiOperation[ButtonMapping]("show")
    summary "Show mapping"
    parameters (pathParam[String]("deviceId").description(""),
      pathParam[Int]("gestureId").description(""))
  )

  get("/device/:deviceId/mapping/:gestureId", operation(showOperation)) {

    val deviceId = params.getOrElse("deviceId", halt(400))
    val gestureId = params.getOrElse("gestureId", halt(400))

    println("deviceId: " + deviceId)
    println("gestureId: " + gestureId)
  }

  val updateOperation = (apiOperation[ButtonMapping]("update")
    summary "Update mapping"
    parameters (pathParam[String]("deviceId").description(""),
      pathParam[Int]("gestureId").description(""),
      bodyParam[MappingUpdateParams]("body").description("").optional)
  )

  post("/device/:deviceId/mapping/:gestureId", operation(updateOperation)) {

    val deviceId = params.getOrElse("deviceId", halt(400))
    val gestureId = params.getOrElse("gestureId", halt(400))
    bodyParam[MappingUpdateParams]("body").description("").optional

    println("deviceId: " + deviceId)
    println("gestureId: " + gestureId)
    println("body: ")
  }

}