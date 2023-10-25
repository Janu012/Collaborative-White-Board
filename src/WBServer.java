import java.io.*;
import java.net.*;
import java.util.*;

public class WBServer
{
  private static Hashtable clienthash = new Hashtable();
  private static int port = 2023;
  public static boolean debug = false;
  public WBServer(Hashtable clienthash, int port) {
    WBServer.clienthash = clienthash;
    WBServer.port = port;
  } 
  public static void main(String[] args) throws IOException
  {
    String str;
    // if this property is defined as true, we run in debug mode
    str = System.getProperty("WBServer-debug");
    if ( str != null ) {
      debug = str.equalsIgnoreCase("true");
    }
    // the user may specify a different port to use
    if ( args.length > 0 ) {
      port = Integer.parseInt(args[0]);
    }
    // let the user know the server has started
    System.out.println("### Starting WBServer on " +
		       InetAddress.getLocalHost().getHostName() + "/" +
		       port + "\n###");
    // spawn a thread to listen for new connections on the port
    new WBSListenThread(clienthash, port).start();      
  }
}