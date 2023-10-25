import java.awt.*;
import java.awt.event.*;

public class ErrorDialog extends Dialog implements ActionListener,
  WindowListener , KeyListener
{
  private Button okButton = new Button("Ok");
  public ErrorDialog(Frame parent, String body)
  {
    super(parent,"Error",true);
    add("Center",new Label(body,Label.CENTER));
    add("South",okButton);
    okButton.addActionListener(this);
    addWindowListener(this);
    pack();
  }
  public ErrorDialog(Frame parent, String title, String body)
  {
    super(parent,title,true);

    add("Center",new Label(body,Label.CENTER));
    add("South",okButton);
    okButton.addActionListener(this);
    addWindowListener(this);
    pack();
  }
  //Below here is Action Handling stuff
  @Override
  public void actionPerformed(ActionEvent event)
  {
    Object src = event.getSource();
        
    if (src.equals(okButton)) {
      dispose();
    }
  }

  @Override
  public void keyTyped(KeyEvent e){
    if (e.getKeyChar() == '\n') dispose();
  }
  @Override
  public void keyPressed(KeyEvent e){
  }
  @Override
  public void keyReleased(KeyEvent e){
  }
  @Override
  public void windowClosed(WindowEvent event){
  }
  @Override
  public void windowDeiconified(WindowEvent event) {
  }
  @Override
  public void windowIconified(WindowEvent event){
  }
  @Override
  public void windowActivated(WindowEvent event){
  }
  @Override
  public void windowDeactivated(WindowEvent event) {
  }
  @Override
  public void windowOpened(WindowEvent event){
  }
  @Override
  public void windowClosing(WindowEvent event)
  {
    dispose();
  }
}
