package bst3.app

import bst3.app.Helpers.GenericObservable
import bst3.app.Model.Id
import com.mongodb.{ServerApi, ServerApiVersion}
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{ConnectionString, FindObservable, MongoClient, MongoClientSettings, MongoCollection, MongoCredential, MongoDatabase, SingleObservable}
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter}
import org.mongodb.scala.bson._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.Using

object MongoClientConnectionExample {

  def main(args: Array[String]): Unit = {

    // Replace the placeholder with your Atlas connection string
    val connectionString = "mongodb://localhost:27017"

    val credentials = MongoCredential.createCredential("root", "admin", "example".toCharArray)

    // Construct a ServerApi instance using the ServerApi.builder() method
    val serverApi = ServerApi.builder.version(ServerApiVersion.V1).build()

    val settings = MongoClientSettings
      .builder()
      .applyConnectionString(ConnectionString(connectionString))
      .serverApi(serverApi)
      .credential(credentials)
      .build()

    // Create a new client and connect to the server
    Using(MongoClient(settings)) { mongoClient =>
      // Send a ping to confirm a successful connection
      val database = mongoClient.getDatabase("admin")
      val ping = database.runCommand(Document("ping" -> 1)).head()

      Await.result(ping, 10.seconds)
      System.out.println("Pinged your deployment. You successfully connected to MongoDB!")

      val testDb: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(Model.codecRegistry)

      val coll: MongoCollection[Document] = testDb.getCollection("mycoll")
      val tskColl: MongoCollection[CsTask] = testDb.getCollection("tasks")


      // insert a document
      val document = BsonDocument("{x: 1}")
      coll.insertOne(document).printResults()

      document.append("x", BsonInt32(2)).append("y", BsonInt32(3))

      // replace a document
      coll.replaceOne(Filters.equal("_id", document.get("_id")), document)
        .printResults()

      // find documents
      coll.find().printResults()


      val l = tskColl.insertOne(CsTask(Id(20L))).results()
      val f1: SingleObservable[CsTask] = tskColl.find[CsTask]().first()
      val f1r = f1.results()
      f1.printResults()
      val f2: SingleObservable[CsTask] = tskColl.find[CsTask]()
      val f2r = f2.results()
      f2.printResults()
      ()
    }
    ()
  }
}
