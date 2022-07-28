package xyz.meldlabs.dataway.utils.tcp.events.args;

import java.util.List;

import xyz.meldlabs.dataway.utils.tcp.events.OnSocketDataReceived;

public class OnSocketDataReceivedArgs {
  /**
   * The data received from the socket.
   */
  public final byte[] data;

  private final List<OnSocketDataReceived> listeners;
  private final OnSocketDataReceived listener;

  public OnSocketDataReceivedArgs(OnSocketDataReceived listener, List<OnSocketDataReceived> listeners, byte[] data) {
    this.listeners = listeners;
    this.listener = listener;

    this.data = data;
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
