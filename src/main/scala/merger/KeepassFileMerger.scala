package merger

import de.slackspace.openkeepass.domain._
import merger.GroupsMerger.GroupContents

import scala.io.StdIn

class KeepassFileMerger(resultName: String)(implicit conflictStrategy: ConflictStrategy[String, GroupContents]) {
  def merge(left: KeePassFile, right: KeePassFile): KeePassFile = {
    val mergedGroup = (new GroupsMerger).merge(Seq(left.getRoot), Seq(right.getRoot))

    new KeePassFileBuilder(resultName).addTopGroups(mergedGroup: _*).build()
  }
}

object KeepassFileMerger {
  implicit lazy val leftEntriesConflictStrategy: ConflictStrategy[String, Entry] = (_: String, left: Entry, _: Entry) => left

  implicit lazy val chooseEntryConflictStrategy: ConflictStrategy[String, Entry] =
    (key: String, left: Entry, right: Entry) => {
      def choose: Entry = {
        println()
        println(s"Conflict for key '$key'")
        println(s"Write 'l' to choose left '$left'")
        println(s"Write 'r' to choose right '$right'")

        StdIn.readLine() match {
          case "l" => left
          case "r" => right
          case _ => choose
        }
      }

      choose
    }

  implicit def groupConflictStrategy(implicit entriesConflictStrategy: ConflictStrategy[String, Entry]): ConflictStrategy[String, GroupContents] =
    (_: String, left: GroupContents, right: GroupContents) => {
      val groupsConflict = groupConflictStrategy(entriesConflictStrategy)
      val mergedEntries = new EntriesMerger()(entriesConflictStrategy).merge(left._1, right._1)(entriesConflictStrategy)
      val mergedGroups = new GroupsMerger()(groupsConflict).merge(left._2, right._2)(groupsConflict)
      (mergedEntries, mergedGroups)
    }
}
