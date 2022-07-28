package xyz.meldlabs.dataway.communication.formats.send;

public class TransferRequestResult {
  public final String type = "TransferRequestResult";
  public int resultCode;
  public String transferID;
  public String privateTransferID;
}
