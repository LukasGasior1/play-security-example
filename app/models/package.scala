import com.mongodb.casbah.MongoClient
import com.novus.salat.Context
import play.api.Play.current

package object models {

  implicit val ctx = new Context {
    val name = "Custom Context"
  }

  ctx.registerGlobalKeyOverride(remapThis = "id", toThisInstead = "_id")

  val host = current.configuration.getString("mongo.host").get
  val port = current.configuration.getInt("mongo.port").get
  val dbName = current.configuration.getString("mongo.dbname").get

  val mongoClient = MongoClient(host, port)
  val db = mongoClient(dbName)
}
