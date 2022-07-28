package xyz.meldlabs.dataway.utils.tcp.events;

import xyz.meldlabs.dataway.utils.tcp.TcpConnection;
import xyz.meldlabs.dataway.utils.tcp.events.args.OnSocketDataReceivedArgs;

public interface OnSocketDataReceived {
  public void onDataReceived(TcpConnection sender, OnSocketDataReceivedArgs args);
}
