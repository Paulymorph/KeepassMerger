package merger

import de.slackspace.openkeepass.domain.{Entry, Group, GroupBuilder}

import collection.JavaConverters._
import scala.collection.mutable

import GroupsMerger.GroupContents

class GroupsMerger(implicit conflictStrategy: ConflictStrategy[String, GroupContents]) extends Merger[Group, String, GroupContents] {

  override implicit lazy val keyValueExtractor: KeyValueExtractor[Group, String, GroupContents] = new KeyValueExtractor[Group, String, GroupContents] {

    import de.slackspace.openkeepass.domain.Group

    override def key(instance: Group): String = instance.getName

    override def value(instance: Group): (mutable.Buffer[Entry], mutable.Buffer[Group]) =
      (instance.getEntries.asScala, instance.getGroups.asScala)
  }

  override implicit lazy val entryBuilder: Builder[Group, String, GroupContents] =
    (key: String, value: GroupContents) => {
      val builder = new GroupBuilder(key).addEntries(value._1.asJava)
      value._2.foreach(builder.addGroup)
      builder.build()
    }
}

object GroupsMerger {
  type GroupContents = (Seq[Entry], Seq[Group])
}