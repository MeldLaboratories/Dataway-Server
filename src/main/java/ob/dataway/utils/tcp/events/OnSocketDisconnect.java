package ob.dataway.utils.tcp.events;

import ob.dataway.utils.tcp.TcpConnection;
import ob.dataway.utils.tcp.events.args.OnSocketDisconnectArgs;

public interface OnSocketDisconnect {
  public void onDisconnect(TcpConnection sender, OnSocketDisconnectArgs args);
}
