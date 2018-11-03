package merger

trait ConflictStrategy[K, V] {
  def resolveConflict(commonKey: K, left: V, right: V): V
}
