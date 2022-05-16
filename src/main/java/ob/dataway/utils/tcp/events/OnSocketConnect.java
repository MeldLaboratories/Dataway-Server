package ob.dataway.utils.tcp.events;

import ob.dataway.utils.tcp.TcpServer;
import ob.dataway.utils.tcp.events.args.OnSocketConnectArgs;

public interface OnSocketConnect {
  public void onConnect(TcpServer sender, OnSocketConnectArgs args);
}
