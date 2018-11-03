package merger

import de.slackspace.openkeepass.domain._

class KeepassFileMerger {
  import merger.GroupsMerger._
  def merge(left: KeePassFile, right: KeePassFile): KeePassFile = {
    val mergedGroup = GroupsMerger.merge(Seq(left.getRoot), Seq(right.getRoot))

    new KeePassFileBuilder("").addTopGroups(mergedGroup:_*).build()
  }
}
