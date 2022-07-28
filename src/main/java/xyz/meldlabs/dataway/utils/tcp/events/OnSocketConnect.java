package xyz.meldlabs.dataway.utils.tcp.events;

import xyz.meldlabs.dataway.utils.tcp.TcpServer;
import xyz.meldlabs.dataway.utils.tcp.events.args.OnSocketConnectArgs;

public interface OnSocketConnect {
  public void onConnect(TcpServer sender, OnSocketConnectArgs args);
}
