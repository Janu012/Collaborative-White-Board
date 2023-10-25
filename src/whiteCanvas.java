import java.awt.*;

public class whiteCanvas extends Canvas
{
  /** The current color */
  private Color currentColor = Color.black;

  /** The current thickness to draw objects in */
  private int thickness = 1;

  /** The image we draw to, which is then dumped to the screen */
  private Image buffer = null;

  /** The graphics object assosciated with out buffer */
  private Graphics buffer_gfx;

  /**
   * This function clears the drawing area
   */
  public void clear()
  {
    if(buffer_gfx != null) {
      buffer_gfx.setColor(Color.white);
      buffer_gfx.fillRect(0, 0, getSize().width, getSize().height);
      getGraphics().drawImage(buffer,0,0,this);
    }
  }
  public void drawLine(int x1, int y1, int x2, int y2)
  {
    if (buffer == null) {
      buffer = createImage(getSize().width, getSize().height);
      buffer_gfx = buffer.getGraphics();
      buffer_gfx.setColor(Color.white);
      buffer_gfx.fillRect(0, 0, getSize().width, getSize().height);
    }
    
    buffer_gfx.setColor(currentColor);
    if (thickness == -1) {
      buffer_gfx.drawLine(x1,y1,x2,y2);
    } else {
      int hs = thickness/2;
      int w = (int) (((double) hs) / Math.sqrt(2.0));
      if((x1 < x2 && y1 < y2) || (x1 > x2 && y1 > y2)) {
        int xvec[] = {x1-w-1, x1+w, x2+w, x2-w-1};
        int yvec[] = {y1+w+1, y1-w, y2-w, y2+w+1};
        buffer_gfx.fillPolygon(xvec, yvec, 4);
      } else {
        int xvec[] = {x1-w-1, x1+w, x2+w, x2-w-1};
        int yvec[] = {y1-w-1, y1+w, y2+w, y2-w-1};
        buffer_gfx.fillPolygon(xvec, yvec, 4);
      }

    }
    getGraphics().drawImage(buffer,0,0,this);
  }
  public void drawRect(int x, int y, int w, int h)
  {
    int i = 0;
    if (buffer == null) {
      buffer = createImage(getSize().width, getSize().height);
      buffer_gfx = buffer.getGraphics();
      buffer_gfx.setColor(Color.white);
      buffer_gfx.fillRect(0, 0, getSize().width, getSize().height);
    }
    
    buffer_gfx.setColor(currentColor);
    if (thickness == -1) {
      buffer_gfx.fillRect(x,y,w,h);
    } else {
      for(i=0;i<thickness;i++) {
	buffer_gfx.drawRect(x+i,y+i,w-(2*i),h-(2*i));
      }
    }
    getGraphics().drawImage(buffer,0,0,this);
  }
  public void drawOval(int x, int y, int w, int h)
  {
    int i = 0;
    if (buffer == null) {
      buffer = createImage(getSize().width, getSize().height);
      buffer_gfx = buffer.getGraphics();
      buffer_gfx.setColor(Color.white);
      buffer_gfx.fillRect(0, 0, getSize().width, getSize().height);
    } 
    buffer_gfx.setColor(currentColor);
    if (thickness == -1) {
      buffer_gfx.fillOval(x,y,w,h);
    } else {
      for(i=0;i<thickness;i++) {
	buffer_gfx.drawOval(x+i,y+i,w-(2*i),h-(2*i));
      }
    }

    getGraphics().drawImage(buffer,0,0,this);
  }
  public void setColor(Color c)
  {
    currentColor = c;
  }
  public Color getColor()
  {
    return(currentColor);
  }
  public void setThickness(int s)
  {
    thickness = s;
  }
  public int getThickness()
  {
    return(thickness);
  }
    @Override
  public void paint(Graphics g)
  {
    if(buffer != null) {
      g.drawImage(buffer,0,0,this);
    } else {
      g.setColor(Color.white);
      g.fillRect(0,0,getSize().width, getSize().height);
    }
  }
}