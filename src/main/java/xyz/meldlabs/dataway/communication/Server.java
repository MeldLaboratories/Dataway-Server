package xyz.meldlabs.dataway.communication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import xyz.meldlabs.dataway.communication.formats.ResultCode;
import xyz.meldlabs.dataway.communication.formats.receive.*;
import xyz.meldlabs.dataway.communication.formats.receive.TransferRequest;
import xyz.meldlabs.dataway.communication.formats.receive.TransferRequestAnswer;
import xyz.meldlabs.dataway.communication.formats.send.*;
import xyz.meldlabs.dataway.communication.types.Client;
import xyz.meldlabs.dataway.database.DatabaseManager;
import xyz.meldlabs.dataway.database.types.User;
import xyz.meldlabs.dataway.utils.tcp.*;
import xyz.meldlabs.dataway.utils.tcp.events.args.OnSocketConnectArgs;
import xyz.meldlabs.dataway.utils.tcp.events.args.OnSocketDataReceivedArgs;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

  private int port;

  private TcpServer server;

  private DatabaseManager db = DatabaseManager.getInstance();
  /**
   * Gson instance for message serialization and deserialization.
   */
  private Gson gson = new Gson();

  /**
   * Stores all the clients with their respective connections.
   */
  private List<Client> clients = new ArrayList<Client>();


  /**
   * The Dataway communication and file transfer server.
   * @param port
   */
  public Server(int port) {
    try {
      this.port = port;

      server = new TcpServer(port);
  
      // Set the event handler for the server socket connection
      server.onSocketConnect(this::onSocketConnect);
    }
    catch (IOException e) {
      log.error("Failed to create the server socket.");
      System.exit(1);
    }
  }

  public void start() {
    log.info("Server listening on port {}", port);
    server.listen();
  }

  /**
   * Called when a new socket connection is established.
   * @param connection
   */
  private void onSocketConnect(Object sender, OnSocketConnectArgs args) {
    log.trace("New connection from {}", args.socket.getBaseSocket().getInetAddress());

    //TODO on disconnect
    args.socket.onDataReceived(this::newSocketDataHandler);
  }


  /**
   * Handles all the connected clients that have not yet sent a handshake.
   * @param data
   * @param socket
   */
  private void newSocketDataHandler(TcpConnection socket, OnSocketDataReceivedArgs args) {

    String message = new String(args.data).trim();

    BaseType baseType = gson.fromJson(message, BaseType.class);

    if(!baseType.type.equals("Handshake")) {
      HandshakeResult handshakeResult = new HandshakeResult();
      handshakeResult.resultCode = ResultCode.HANDSHAKE_EXPECTED;

      try {
        socket.send(gson.toJson(handshakeResult));
      }
      catch(IOException e) {
        log.error("Error sending handshake result", e);
      };
      
      return;
    }

    Handshake handshake = gson.fromJson(message, Handshake.class);

    if(handshake.socketType == SocketType.COM.ordinal()){
      args.unsubscribe();
      socket.onDataReceived(this::comSocketDataHandler);
    }
    else if(handshake.socketType == SocketType.DATA.ordinal()){
      args.unsubscribe();
      socket.onDataReceived(this::dataSocketDataHandler);
    }

    HandshakeResult handshakeResult = new HandshakeResult();
    handshakeResult.resultCode = ResultCode.SUCCESS;

    try {
      socket.send(gson.toJson(handshakeResult));
    }
    catch(IOException e) {
      log.error("Error sending handshake result", e);
    };
  }


  /**
   * Handles all the connected clients that have identified as com sockets.
   * @param data
   * @param socket
   */
  private void comSocketDataHandler(TcpConnection socket, OnSocketDataReceivedArgs args){

    String message = new String(args.data).trim();

    BaseType baseType = gson.fromJson(message, BaseType.class);

    switch (baseType.type) {
      case "Login":
        Login login = gson.fromJson(message, Login.class);
        this.handleLogin(login, socket);
        break;

      case "Logout":
        this.handleLogout(socket);
        break;

      case "Register":
        this.handleRegister(socket);
        break;

      case "TransferRequest":
        TransferRequest transferRequest = gson.fromJson(message, TransferRequest.class);
        this.handleTransferRequest(transferRequest, socket);
        break;

      case "TransferRequestAnswer":
        TransferRequestAnswer transferRequestAnswer = gson.fromJson(message, TransferRequestAnswer.class);
        this.handleTransferRequestAnswer(transferRequestAnswer, socket);
        break;

      default:
        log.warn("Unknown message type: {}", baseType.type);
        break;
    }
  }


  /**
   * Handles all the connected clients that have identified as data sockets.
   * @param data
   * @param socket
   */
  private void dataSocketDataHandler(TcpConnection sender, OnSocketDataReceivedArgs args){
    throw new UnsupportedOperationException("Not implemented yet.");
  }


  /**
   * Handles a login request.
   * @param login
   * @param socket
   */
  private void handleLogin(Login login, TcpConnection socket) {

    boolean success = db.loginUser(login.userID);

    if(success){

      // Check if the user is already logged in
      Client establishedClient = getClientFromComSocket(socket);

      // If the user is already logged in do nothing and send a success message
      if(establishedClient != null){
        log.warn("User {} is already logged in.", establishedClient.userID);
      }
      // If the user is not logged in, add the user to the list of clients
      else {
        Client client = new Client(socket, login.userID);
        clients.add(client);
        log.trace("Client {} logged in.", login.userID);
      }

      log.trace("Clients logged in: {}", clients.size());
    }

    LoginResult loginResult = new LoginResult();
    loginResult.resultCode = success ? ResultCode.SUCCESS : ResultCode.USER_NOT_FOUND;

    try {
      socket.send(gson.toJson(loginResult));
    }
    catch(IOException e) {
      log.error("Error sending login result", e);
    };
  }

  
  /**
   * Handles a logout request.
   * @param login
   * @param socket
   */
  private void handleLogout(TcpConnection socket) {

    Client client = getClientFromComSocket(socket);

    if(client == null) {
      log.warn("Client not found in HandleLogout. This can happen if the client tries to logout and has not yet logged in.");
      return;
    }

    log.trace("Client {} logged out.", client.userID);

    db.logoutUser(client.userID);

    clients.remove(client);

    LogoutResult logoutResult = new LogoutResult();
    logoutResult.resultCode = ResultCode.SUCCESS;

    try {
      socket.send(gson.toJson(logoutResult));
    }
    catch(IOException e) {
      log.error("Error sending logout result", e);
    };
  }


  /**
   * 
   * @param login
   * @param socket
   */
  private void handleRegister(TcpConnection socket) {
    
    User newUser = db.addUser();

    RegisterResult registerResult = new RegisterResult();
    registerResult.resultCode = ResultCode.SUCCESS;
    registerResult.userID = newUser.getId();
    registerResult.friendCode = newUser.getFriendCode();

    try {
      socket.send(gson.toJson(registerResult));
    }
    catch(IOException e) {
      log.error("Error sending register result", e);
    };
  }


  /**
   * 
   * @param login
   * @param socket
   */
  private void handleTransferRequest(TransferRequest transferRequest, TcpConnection socket) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }


  /**
   * 
   * @param login
   * @param socket
   */
  private void handleTransferRequestAnswer(TransferRequestAnswer transferRequestAnswer, TcpConnection socket) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }


  /**
   * Retrieves the client instance for the given com socket.
   * @return the client instance or null if not found.
   * @param socket
   */
  private Client getClientFromComSocket(TcpConnection socket) {
    UUID socketUuid = socket.getUuid();
    
    for(Client client : clients) {
      if(client.comSocket.getUuid().equals(socketUuid)) {
        return client;
      }
    }

    return null;
  }
}
