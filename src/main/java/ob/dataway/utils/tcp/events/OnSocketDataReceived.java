package ob.dataway.utils.tcp.events;

import ob.dataway.utils.tcp.TCPConnection;

public interface OnSocketDataReceived {
  public void onDataReceived(byte[] data);
}
