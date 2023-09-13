package bst3.app

import derevo.derive
import derevo.cats._
import derevo.reactivemongo._
import derevo.circe.magnolia.{decoder, encoder}
import io.circe.{DecodingFailure, Encoder}
import io.circe.syntax._
import io.circe.parser._
import io.circe.derivation.renaming
import io.estatico.newtype.macros.newtype
import reactivemongo.bson._
import reactivemongo.bson.DefaultBSONHandlers._
import reactivemongo.bson.BSONLong
import reactivemongo.bson.BSONLongHandler._

import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}


@derive(eqv, order, show, encoder, decoder, bsonDocumentWriter, bsonDocumentReader)
case class CsTask(id: Model.Id)

object Model {

  @derive(eqv, order, show, encoder, decoder, bsonNewtypeWriter, bsonNewtypeReader)
  @newtype
  case class Id(value: Long)

  val codecRegistry = fromRegistries(fromProviders(classOf[CsTask]), DEFAULT_CODEC_REGISTRY)

}
