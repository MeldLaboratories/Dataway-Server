package ob.dataway.utils.tcp.events;

import ob.dataway.utils.tcp.TcpConnection;
import ob.dataway.utils.tcp.events.args.OnSocketDataReceivedArgs;

public interface OnSocketDataReceived {
  public void onDataReceived(TcpConnection sender, OnSocketDataReceivedArgs args);
}
