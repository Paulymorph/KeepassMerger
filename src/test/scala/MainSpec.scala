import java.io.File

import de.slackspace.openkeepass.KeePassDatabase
import de.slackspace.openkeepass.domain.{Entry, EntryBuilder, GroupBuilder, KeePassFileBuilder}
import org.scalatest.{FlatSpec, Matchers}

class MainSpec extends FlatSpec with Matchers {
  "main" should "merge 2 files and write result" in {
    val secret = "1fverevevdsdfewfsdfsxcvfrerefr"
    KeePassDatabase.write(first, secret, "1")
    KeePassDatabase.write(second, secret, "2")
    Main.main(Array("1", secret, "2", secret, "res", secret))
    new File("res").exists() shouldBe true
  }

  private def first = {
    val firstEntry = new EntryBuilder("First entry").username("Peter").password("Peters secret").build()
    val group = new GroupBuilder("Banking")
      .addEntry(new EntryBuilder("Second entry").username("Paul").password("secret").build())
      .build()

    val root = new GroupBuilder()
      .addEntry(firstEntry)
      .addGroup(group)
      .build()

    val keePassFile = new KeePassFileBuilder("writingDB")
      .addTopGroups(root)
      .build()

    keePassFile
  }

  private def second = {
    val group = new GroupBuilder("Banking")
      .addEntry(new EntryBuilder("Second entry").username("Paul").password("secrets").build())
      .addEntry(new EntryBuilder("Third entry").username("Paul").password("secrets").build())
      .build()

    val root = new GroupBuilder()
      .addGroup(group)
      .build()

    val keePassFile = new KeePassFileBuilder("writingDB2")
      .addTopGroups(root)
      .build()

    keePassFile
  }
}
