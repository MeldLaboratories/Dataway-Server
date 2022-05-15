package ob.dataway.utils.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import ob.dataway.utils.tcp.events.OnSocketConnect;

@Slf4j
public class TCPServer {
  private ServerSocket serverSocket;
  private Thread serverThread;
  private List<TCPConnection> connections = new ArrayList<>();

  private int port;
  private int backlog;
  private InetAddress bindAddress;

  // events 
  private List<OnSocketConnect> onSocketConnectEvents = new ArrayList<>();

  /**
   * Creates a new multi-threaded TCP server that allows multiple connections.
   * @param port The port to listen on.
   */
  public TCPServer(int port) throws IOException {
    this(port, 50, null);
  }

  /**
   * Creates a new multi-threaded TCP server that allows multiple connections.
   * @param port The port to listen on.
   * @param backlog The maximum number of connections to allow in the queue.
   */
  public TCPServer(int port, int backlog) throws IOException {
    this(port, backlog, null);
  }

  /**
   * Creates a new multi-threaded TCP server that allows multiple connections.
   * @param port The port to listen on.
   * @param backlog The maximum number of connections to allow in the queue.
   * @param bindAddress The address to bind to.
   * @throws IOException
   */
  public TCPServer(int port, int backlog, InetAddress bindAddress) throws IOException {
    this.port = port;
    this.backlog = backlog;
    this.bindAddress = bindAddress;

    // Create the server socket.
    if (this.bindAddress == null)
      this.serverSocket = new ServerSocket(this.port, this.backlog);
    else
      this.serverSocket = new ServerSocket(this.port, this.backlog, this.bindAddress);

    // Start the server thread.
    this.serverThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          while (!serverSocket.isClosed()) {
            // Accept a new connection.
            TCPConnection connection = new TCPConnection(TCPServer.this.serverSocket.accept());
            
            // Add the connection to the list.
            TCPServer.this.connections.add(connection);

            // Fire the connection event.
            for (OnSocketConnect onSocketConnect : TCPServer.this.onSocketConnectEvents) {
              onSocketConnect.onConnect(connection);
            }
          }
        } catch (IOException e) {
          log.error("Error accepting connection.", e);
        }
      }
    });
  }

  /**
   * Starts the server.
   */
  public void listen() {
    this.serverThread.start();
  }

  /**
   * Gets fired when a new connection is accepted.
   * @param onSocketConnect The events callback.
   */
  public void onSocketConnect(OnSocketConnect onSocketConnect) {
    this.onSocketConnectEvents.add(onSocketConnect);
  }

}
