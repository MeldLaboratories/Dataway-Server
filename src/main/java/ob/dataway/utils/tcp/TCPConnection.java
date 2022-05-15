package ob.dataway.utils.tcp;

import java.io.BufferedReader;
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

public class TCPConnection {
  private Thread socketThread;

  // events
  private List<OnSocketDisconnect> disconnectListeners = new ArrayList<OnSocketDisconnect>();
  private List<OnSocketDataReceived> dataReceivedListeners = new ArrayList<OnSocketDataReceived>();
  
  @Getter
  private InputStream inputStream;

  @Getter
  private OutputStream outputStream;
  
  @Getter
  private Socket baseSocket;

  @Getter @Setter
  private int bufferSize = 256;

  public TCPConnection(Socket socket) {
    this.baseSocket = socket;

    // start the thread
    this.socketThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // start reading
          TCPConnection.this.inputStream = TCPConnection.this.baseSocket.getInputStream();
          InputStreamReader streamReader = new InputStreamReader(TCPConnection.this.inputStream);

          // prepare output stream
          TCPConnection.this.outputStream = TCPConnection.this.baseSocket.getOutputStream();

          while (!TCPConnection.this.baseSocket.isClosed()) {

            CharBuffer buffer = CharBuffer.allocate(bufferSize);
                
            if (streamReader.read(buffer) == -1) {
              TCPConnection.this.baseSocket.close();
              break;
            }

            byte[] data = new String(buffer.array()).getBytes();

            for (OnSocketDataReceived dataListener : TCPConnection.this.dataReceivedListeners) {
              dataListener.onDataReceived(data);
            }

            // close the socket
            if (!TCPConnection.this.baseSocket.isConnected())
              TCPConnection.this.baseSocket.close();
          }
        }
        catch (SocketException se) {
          // do nothing
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        
        // handle disconnect
        for (OnSocketDisconnect disconnectListener : TCPConnection.this.disconnectListeners) {
          disconnectListener.onDisconnect();
        }
      }
    });

    this.socketThread.start();
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
