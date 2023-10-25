import java.io.*;

public class WBCPrintThread extends Thread
{
  private PrintWriter pwout = null;
  private String str = null;

  public WBCPrintThread(ClientInfo clientinfo, String str)
  {
    this.pwout = clientinfo.getPwout();
    this.str = str;
  }
  @Override
  public void run()
  {
    // synchronize on the PrintWriter to not interfere with other threads
    synchronized ( pwout ) {
      pwout.print(str);
      pwout.flush();

    }
  }
}