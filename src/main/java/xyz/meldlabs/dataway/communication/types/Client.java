package xyz.meldlabs.dataway.communication.types;

import xyz.meldlabs.dataway.utils.tcp.TcpConnection;


public class Client {

  /**
   * The Tcp connection the client communicates with.
   */
  public TcpConnection comSocket;

  /**
   * The Tcp connection the client transfers data with.
   */
  public TcpConnection dataSocket;

  public String userID;

  public boolean isLoggedIn = false;

  /**
   * Represents a client and provides access to all client sockets.
   * @param comSocket The Tcp connection the client uses to communicate with.
   */
  public Client(TcpConnection comSocket, String userID) {
    this.comSocket = comSocket;
    this.userID = userID;
  }
}
