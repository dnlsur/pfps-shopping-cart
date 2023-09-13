package model

case object ModelTest

import bst3.app.CsTask
import bst3.app.Model.Id
import cats.effect.IO
import eu.timepit.refined.auto._
import io.circe.syntax.EncoderOps
import io.circe.{DecodingFailure, Encoder}
import io.circe.syntax._
import io.circe.parser._
import io.circe.derivation.renaming
import org.bson.BsonDocument
import org.mongodb.scala.bson.Document
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONLong, BSONReader, BSONString, document}
import weaver.{SimpleIOSuite, SourceLocation, TestName}
import weaver.scalacheck.Checkers

object ModelTest1 extends SimpleIOSuite with Checkers {

  test("json") {
    IO.delay {
      val d = CsTask(Id(1L))
      val dAsJson = d.asJson.noSpaces
      val json = """{"id":1}"""
      val decodedD = decode[CsTask](json)

      expect.same(d, decodedD)
      expect.same(json, dAsJson)
    }
  }

  test("bson") {
    IO.delay {
      val d = CsTask(Id(1L))
      val dAsBson: BSONDocument = implicitly[BSONDocumentWriter[CsTask]].write(d)
      val bson = document(
        "id" -> BSONLong(1L)
      )
      val decodedD: BSONDocument = implicitly[BSONDocumentReader[CsTask]].read(bson)
      expect.same(d, decodedD)
      expect.same(bson, dAsBson)
    }
  }

}
