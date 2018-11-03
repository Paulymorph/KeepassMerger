package merger

import de.slackspace.openkeepass.domain.{Entry, EntryBuilder}
import org.scalatest.{FlatSpec, Matchers}

class EntriesMergerSpec extends FlatSpec with Matchers {
  import EntriesMerger._

  private def entry(title: String, login: String, password: String) = {
    new EntryBuilder(title)
      .username(login)
      .password(password)
      .build()
  }

  "entries merger" should "merge a sequence and empty" in {
    val left = Seq(entry("1", "1", "1"))
    EntriesMerger.merge(left, Seq.empty) shouldBe left
  }

  it should "merge two entries with different titles" in {
    val left = Seq(entry("1", "1", "1"))
    val right = Seq(entry("2", "2", "2"))
    EntriesMerger.merge(left, right).sortBy(_.getUuid) shouldBe (left ++ right).sortBy(_.getUuid)
  }

  it should "merge two entries with same titles" in {
    val left = Seq(entry("1", "1", "1"))
    val right = Seq(entry("1", "2", "2"))
    EntriesMerger.merge(left, right) shouldBe left
  }

  it should "merge two identical entries" in {
    val left = Seq(entry("1", "1", "1"))
    val right = left.foldRight(Seq.empty[Entry])(_ +: _)
    EntriesMerger.merge(left, right) shouldBe left
  }

  it should "merge two entry seqs with different count" in {
    val left = Seq(entry("1", "1", "1"))
    val right = Seq(entry("1", "3", "2"), entry("2", "2", "2"))
    EntriesMerger.merge(left, right).sortBy(_.getUuid) shouldBe (left ++ right).filter(_.getUsername != 3).sortBy(_.getUuid)
  }
}
