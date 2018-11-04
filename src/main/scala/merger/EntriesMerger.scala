package merger

import de.slackspace.openkeepass.domain.Entry

class EntriesMerger(implicit conflictStrategy: ConflictStrategy[String, Entry]) extends Merger[Entry, String, Entry] {
  override implicit lazy val keyValueExtractor: KeyValueExtractor[Entry, String, Entry] =
    new KeyValueExtractor[Entry, String, Entry] {
    override def key(instance: Entry): String = instance.getTitle

    override def value(instance: Entry): Entry = instance
  }

  implicit lazy val entryBuilder: Builder[Entry, String, Entry] = (_: String, value: Entry) => value
}