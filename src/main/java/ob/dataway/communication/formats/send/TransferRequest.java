package ob.dataway.communication.formats.send;

public class TransferRequest {
  public final String type = "TransferRequest";
  public String senderFriendCode;
  public String transferID;
  public String filename;
  public String message;
  public long filesize;
}
