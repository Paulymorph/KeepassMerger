package merger

trait KeyValueExtractor[Entry, Key, Value] {
  def key(instance: Entry): Key
  def value(instance: Entry): Value
}

trait Builder[Entry, K, V] {
  def build(key: K, value: V): Entry
}

trait Merger[T, Key, Value] {

  import Merger.KeyValueSyntax

  implicit def keyValueExtractor: KeyValueExtractor[T, Key, Value]
  implicit def entryBuilder: Builder[T, Key, Value]

  private def without(sequence: Seq[T], without: Seq[T]): Seq[T] = {
    sequence.filter { sourceElement =>
      without.forall(_.key != sourceElement.key)
    }
  }

  private def conflictedEntries(left: Seq[T], right: Seq[T]): Seq[(Key, Value, Value)] = {
    val commonKeys = left.map(_.key).intersect(right.map(_.key))
    commonKeys.map { key =>
      (key, left.find(_.key == key).get.value, right.find(_.key == key).get.value)
    }
  }

  def merge(
             leftEntries: Seq[T],
             rightEntries: Seq[T])
           (implicit strategy: ConflictStrategy[Key, Value]): Seq[T] = {
    val uniqueEntries = without(leftEntries, rightEntries) ++ without(rightEntries, leftEntries)

    val conflictEntries = conflictedEntries(leftEntries, rightEntries)

    val resolvedEntries = conflictEntries.map { case (key, left, right) =>
      val solution = strategy.resolveConflict(key, left, right)
      entryBuilder.build(key, solution)
    }

    uniqueEntries ++ resolvedEntries
  }
}

object Merger {

  implicit class KeyValueSyntax[T](instance: T) {
    def key[K, V](implicit keyValueExtractor: KeyValueExtractor[T, K, V]): K = keyValueExtractor.key(instance)

    def value[K, V](implicit keyValueExtractor: KeyValueExtractor[T, K, V]): V = keyValueExtractor.value(instance)
  }

}
