package com.project.json

import com.project.model.TriState
import spray.json.{DefaultJsonProtocol, JsNull, JsString, JsValue, JsonFormat}

object TriStateJsonFormat extends DefaultJsonProtocol {
  implicit def triStateFormat[A: JsonFormat]: JsonFormat[TriState[A]] = new JsonFormat[TriState[A]] {
    override def write(obj: TriState[A]): JsValue = obj match {
      case TriState.Unset => JsNull
      case TriState.Null => JsNull
      case TriState.Set(v) => implicitly[JsonFormat[A]].write(v)
    }

    override def read(json: JsValue): TriState[A] = json match {
      case JsNull => TriState.Null
      case JsString("") => TriState.Null
      case value => try {
        TriState.Set(value.convertTo[A])
      } catch {
        case _: Exception => TriState.Unset
      }
      case _ => TriState.Unset
    }
  }
}
