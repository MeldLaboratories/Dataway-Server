package xyz.meldlabs.dataway.communication.enums;

public class ResultCode {

  private ResultCode() {}

  public static final int SUCCESS = 0;
  public static final int USER_NOT_FOUND = 1;
  public static final int RECIEVER_OFFLINE = 2;
  public static final int RECEIVER_NOT_FOUND = 3;
  public static final int NOT_LOGGED_IN = 4;
  public static final int HANDSHAKE_EXPECTED = 5;
  public static final int SELF_TRANSFER = 6;
  public static final int UNKNOWN_ERROR = 99;
}
