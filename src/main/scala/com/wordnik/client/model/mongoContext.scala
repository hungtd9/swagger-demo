package com.wordnik.client

import com.mongodb.casbah.commons.conversions.scala.{ RegisterConversionHelpers, RegisterJodaTimeConversionHelpers }
import com.novus.salat.json.{ JSONConfig, StringDateStrategy }
import com.novus.salat.{ Context, StringTypeHintStrategy, TypeHintFrequency }
import org.joda.time.format.{ DateTimeFormat, ISODateTimeFormat }

/**
 * Created by hungtran on 3/24/16.
 */
package object mongoContext {

  val dateTimeFormat = ISODateTimeFormat.dateTime.withZoneUTC()
  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC()

  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()

  implicit val context = {
    val context = new Context {
      val name = "global"

      override val typeHintStrategy = StringTypeHintStrategy(
        when = TypeHintFrequency.WhenNecessary,
        typeHint = "_t"
      )
      override val jsonConfig = JSONConfig(
        dateStrategy = StringDateStrategy(dateFormatter = dateTimeFormat)
      )
    }
    //    context.registerClassLoader()
    context
  }

}
