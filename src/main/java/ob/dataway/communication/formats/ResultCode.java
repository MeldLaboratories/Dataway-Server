package ob.dataway.communication.formats;

public class ResultCode {

  private ResultCode() {}

  public static final int SUCCESS = 0;
  public static final int USER_NOT_FOUND = 1;
  public static final int RECEIVER_IN_DO_NOT_DISTURB = 2;
  public static final int RECIEVER_OFFLINE = 3;
  public static final int RECEIVER_NOT_FOUND = 4;
  public static final int NOT_LOGGED_IN = 5;
  public static final int HANDSHAKE_EXPECTED = 6;
  public static final int UNKNOWN_ERROR = 99;
}
