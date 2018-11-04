package merger

import de.slackspace.openkeepass.domain.{Entry, GroupBuilder, KeePassFileBuilder, EntryBuilder => KPEB}
import org.scalatest.{FlatSpec, Matchers}
class KeepassFileMergerSpec extends FlatSpec with Matchers {
  implicit lazy val leftEntriesConflictStrategy: ConflictStrategy[String, Entry] = (_: String, left: Entry, _: Entry) => left
  import KeepassFileMerger.groupConflictStrategy
  "Keepass file merger" should "merge two Keepass files" in {
    val merged = new KeepassFileMerger("").merge(first, second)
    merged.getEntries.size() shouldBe 3
    val banking = merged.getGroupByName("Banking")
    banking.getEntries.size() shouldBe 2
    banking.getEntryByTitle("Second entry").getPassword shouldBe "secret"
  }

  private def first = {
    val firstEntry = new KPEB("First entry").username("Peter").password("Peters secret").build()
    val group = new GroupBuilder("Banking")
      .addEntry(new KPEB("Second entry").username("Paul").password("secret").build())
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
      .addEntry(new KPEB("Second entry").username("Paul").password("secrets").build())
      .addEntry(new KPEB("Third entry").username("Paul").password("secrets").build())
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
