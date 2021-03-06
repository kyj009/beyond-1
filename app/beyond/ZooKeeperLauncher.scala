package beyond

import akka.actor.Actor
import org.apache.zookeeper.server.ServerConfig
import org.apache.zookeeper.server.ZooKeeperServerMain

object ZookeeperLauncherCommand extends Enumeration {
  val Launch, Shutdown = Value
}

// FIXME: Shutdown the server and crash this actor if the underlying ZooKeeper
// server does not respond.
class ZooKeeperLauncher extends Actor {
  class BeyondZooKeeperServerMain extends ZooKeeperServerMain {
    // Make shutdown() public because it is protected in ZooKeeperServerMain.
    override def shutdown() {
      super.shutdown()
    }
  }

  class ZooKeeperThread extends Thread {
    override def run() {
      zkServer.runFromConfig(config)
    }

    def shutdown() {
      zkServer.shutdown()
    }
  }

  // FIXME: Currently, ZooKeeperLauncher launches a standalone ZooKeeper server.
  // Setup a ZooKeeper cluster instead.
  private val zkServer: BeyondZooKeeperServerMain = new BeyondZooKeeperServerMain
  private val config: ServerConfig = new ServerConfig

  // FIXME: Don't hardcode the configuration file.
  config.parse("zoo.cfg")

  private val zkServerThread: ZooKeeperThread = new ZooKeeperThread

  override def receive: Receive = {
    // FIXME: Check if this beyond instance should launch a ZooKeeperServer.
    case ZookeeperLauncherCommand.Launch =>
      zkServerThread.start()
    case ZookeeperLauncherCommand.Shutdown =>
      zkServerThread.shutdown()
  }
}

