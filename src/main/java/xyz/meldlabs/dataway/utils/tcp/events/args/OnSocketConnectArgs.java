package xyz.meldlabs.dataway.utils.tcp.events.args;

import java.util.List;

import xyz.meldlabs.dataway.utils.tcp.TcpConnection;
import xyz.meldlabs.dataway.utils.tcp.events.OnSocketConnect;

public class OnSocketConnectArgs {
  /**
   * The new socket connection.
   */
  public final TcpConnection socket;

  private final List<OnSocketConnect> listeners;
  private final OnSocketConnect listener;

  public OnSocketConnectArgs(OnSocketConnect listener, List<OnSocketConnect> listeners, TcpConnection socket) {
    this.listeners = listeners;
    this.listener = listener;
    this.socket = socket;
  }

  /**
   * Unsubscribes the listener from the event.
   */
  public void unsubscribe() {
    if (listeners != null) {
      listeners.remove(listener);
    }
  }
}
