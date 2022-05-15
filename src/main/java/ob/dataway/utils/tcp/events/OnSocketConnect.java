package ob.dataway.utils.tcp.events;

import ob.dataway.utils.tcp.TcpConnection;

public interface OnSocketConnect {
  public void onConnect(TcpConnection connection);
}
