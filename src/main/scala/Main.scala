import de.slackspace.openkeepass.KeePassDatabase
import de.slackspace.openkeepass.domain.KeePassFile
import merger.KeepassFileMerger

object Main {
  import KeepassFileMerger.{chooseEntryConflictStrategy, groupConflictStrategy}
  def readKeepassFile(file: String, secret: String): KeePassFile = {
    KeePassDatabase.getInstance(file).openDatabase(secret)
  }

  def main(args: Array[String]): Unit = {
    args match {
      case Array(leftFile, leftSecret, rightFile, rightSecret, resultName, resultSecret) =>
        val left = readKeepassFile(leftFile, leftSecret)
        val right = readKeepassFile(rightFile, rightSecret)
        val merged = new KeepassFileMerger(resultName).merge(left, right)
        KeePassDatabase.write(merged, resultSecret, s"$resultName.kdbx")
      case _ => throw new IllegalArgumentException("The input should be next: left file, left secret, right file, right secret, result file, result secret")
    }
  }
}
