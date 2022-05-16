package ob.dataway;

import ob.dataway.communication.Server;

public class App 
{
  public static void main( String[] args )
  {
    Server server = new Server(2000);
    server.start();
  }
}
