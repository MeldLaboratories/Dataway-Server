package ob.dataway.utils.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ob.dataway.utils.tcp.events.OnSocketConnect;
import ob.dataway.utils.tcp.events.OnSocketDataReceived;
import ob.dataway.utils.tcp.events.OnSocketDisconnect;

public class TcpConnection {
  private Thread socketThread;

  // events
  private List<OnSocketDisconnect> disconnectListeners = new ArrayList<>();
  private List<OnSocketDataReceived> dataReceivedListeners = new ArrayList<>();
  
  @Getter
  private InputStream inputStream;

  @Getter
  private OutputStream outputStream;
  
  /**
   * The {@link Socket} instance of this connection.
   */
  @Getter
  private Socket baseSocket;

  /**
   * Specifies the amount of in-memory stored bytes.
   */
  @Getter @Setter
  private int bufferSize = 256;

  /**
   * Represent a socket connection.
   * This class normally gets constructed by {@link TcpServer#onSocketConnect(OnSocketConnect)}.
   * @param socket The base socket.
   */
  public TcpConnection(Socket socket) {
    this.baseSocket = socket;

    // start the thread
    this.socketThread = new Thread(this::executeSocket);
    this.socketThread.start();
  }

  /**
   * The main logic for the socket.
   */
  private void executeSocket() {
    try {
      // start reading
      TcpConnection.this.inputStream = TcpConnection.this.baseSocket.getInputStream();
      InputStreamReader streamReader = new InputStreamReader(TcpConnection.this.inputStream);

      // prepare output stream
      TcpConnection.this.outputStream = TcpConnection.this.baseSocket.getOutputStream();

      while (!TcpConnection.this.baseSocket.isClosed()) {

        CharBuffer buffer = CharBuffer.allocate(bufferSize);
            
        if (streamReader.read(buffer) == -1) {
          TcpConnection.this.baseSocket.close();
          break;
        }

        byte[] data = new String(buffer.array()).getBytes();

        for (OnSocketDataReceived dataListener : TcpConnection.this.dataReceivedListeners) {
          dataListener.onDataReceived(data);
        }

        // close the socket
        if (!TcpConnection.this.baseSocket.isConnected())
          TcpConnection.this.baseSocket.close();
      }
    }
    catch (SocketException se) {
      // do nothing
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    // handle disconnect
    for (OnSocketDisconnect disconnectListener : TcpConnection.this.disconnectListeners) {
      disconnectListener.onDisconnect();
    }
  }

  /**
   * Subscribes to the data event.
   * This event is fired when data is received.
   * @param listener The listener to subscribe to the event.
   */
  public void onDataReceived(OnSocketDataReceived listener) {
    this.dataReceivedListeners.add(listener);
  }

  /**
   * Subscribes to the disconnect event.
   * This event is fired when the connection is closed.
   * @param listener The listener to subscribe to the event.
   */
  public void onDisconnect(OnSocketDisconnect listener) {
    this.disconnectListeners.add(listener);
  }

  /**
   * Sends data to the connected client.
   * @param data The data to send.
   * @throws IOException If the communication fails.
   */
  public void send(String data) throws IOException {
    this.outputStream.write(data.getBytes());
  }

}
