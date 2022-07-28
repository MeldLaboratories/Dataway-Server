package xyz.meldlabs.dataway.utils.tcp.events;

import xyz.meldlabs.dataway.utils.tcp.TcpConnection;
import xyz.meldlabs.dataway.utils.tcp.events.args.OnSocketDisconnectArgs;

public interface OnSocketDisconnect {
  public void onDisconnect(TcpConnection sender, OnSocketDisconnectArgs args);
}
