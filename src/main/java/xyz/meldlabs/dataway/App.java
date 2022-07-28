package xyz.meldlabs.dataway;

import xyz.meldlabs.dataway.communication.Server;

public class App 
{
  public static void main( String[] args )
  {
    Server server = new Server(2000);
    server.start();
  }
}
