package ob.dataway.utils.tcp.events.args;

import java.util.List;

import ob.dataway.utils.tcp.TcpConnection;
import ob.dataway.utils.tcp.events.OnSocketDataReceived;

public class OnSocketDataReceivedArgs {
  /**
   * The data received from the socket.
   */
  public final byte[] data;

  /**
   * The socket connection.
   */
  public final TcpConnection socket;

  private final List<OnSocketDataReceived> listeners;
  private final OnSocketDataReceived listener;

  public OnSocketDataReceivedArgs(OnSocketDataReceived listener, List<OnSocketDataReceived> listeners, byte[] data, TcpConnection socket) {
    this.listeners = listeners;
    this.listener = listener;

    this.data = data;
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
