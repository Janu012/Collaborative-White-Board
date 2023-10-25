import java.io.IOException;

class whiteBoard
{
  public controlPanel cp;
  public drawPanel dp;
  protected ClientSocket socket;
  public whiteBoard()
  {
    cp = new controlPanel(this);
    cp.setTitle("WhiteBoard");
    dp = new drawPanel(this,400,400);
    dp.setTitle("WhiteBoard");
  }
  public void connect(String user, String host, int port)
       throws IOException, DupUserException
  {
    socket = new ClientSocket(this,user,host,port);
  }
  public void setType(String type)
  {
    if (type.equalsIgnoreCase("student")) {
      cp.setLevel(false);
      dp.setDrawable(false);
    } else if (type.equalsIgnoreCase("teacher")) {
      cp.setLevel(true);
      dp.setDrawable(true);
    }
  }
  public void doQuit()
  {
    cp.setVisible(false);
    cp.dispose();

    dp.setVisible(false);
    dp.dispose();
    
    System.exit(0);
  }
  public static void main(String args[]) 
  {
    whiteBoard wb = new whiteBoard();
    
    wb.cp.setLocation(100,100);
    wb.dp.setLocation(500,100);
    wb.cp.setVisible(true);
    wb.dp.setVisible(true);
  }
}