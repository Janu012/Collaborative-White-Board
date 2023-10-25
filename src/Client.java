import java.io.*;

class Client {
  public static void main(String[] argv) {
    int port = 0;   
    // Parse the port specification
    if(argv.length != 3)
      usage();
    try {
      port = Integer.parseInt(argv[1]);
    }
    catch (NumberFormatException e) {
      usage();
    }    
    try {
      new ClientSocket(null, argv[2], argv[0], port);
    }
    catch(IOException | DupUserException e) {
      System.out.println("Client: " + e);
    }
  }

  public static void usage() {
    System.out.println("Usage: java Client <hostname> <port> <user>");
    System.exit(0);
  }    
}