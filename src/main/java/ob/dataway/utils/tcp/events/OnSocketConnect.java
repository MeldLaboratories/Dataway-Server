package ob.dataway.utils.tcp.events;

import ob.dataway.utils.tcp.TCPConnection;

public interface OnSocketConnect {
  public void onConnect(TCPConnection connection);
}
