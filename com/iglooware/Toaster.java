package com.iglooware;
/**
 * Java Toaster is a java utility class for your swing applications
 * that show an animate box coming from the bottom of your screen
 * with a notification message and/or an associated image 
 * (like msn online/offline notifications).
 * 
 * @author Daniele Piras
 * @author Warren Downs
 * 
 * Toaster panel in windows system follow the taskbar; So if
 * the taskbar is into the bottom the panel coming from the bottom
 * and if the taskbar is on the top then the panel coming from the top.
 * 
 * public class ToasterTest 
 * { 
 *
 *  public static void main(String[] args) 
 *  { 
 *   // Initialize toaster manager... 
 *   Toaster toasterManager = new Toaster(); 
 *
 *   // Show a simple toaster 
 *   toasterManager.showToaster( new ImageIcon( "mylogo.gif" ), "A simple toaster with an image" ); 
 *  } 
 * } 
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.*;

public class Toaster  {
	private int toasterWidth = 300;
	private int toasterHeight = 80;
	private int step = 20; // Step for the toaster
	private int stepTime = 20;
	private int displayTime = 3000;
	private int currentNumberOfToaster = 0;
	private int maxToaster = 0; // Last opened toaster
	private int maxToasterInSceen;
  private Image backgroundImage;
	private Font font; // Font used to display message
	private Color borderColor;
	private Color toasterColor;
	private Color messageColor;

	int margin;
	
	// Flag that indicate if use alwaysOnTop or not.
	// method always on top start only SINCE JDK 5 !
	boolean useAlwaysOnTop = true;
	
	private static final long serialVersionUID = 2L;

	/**
	 * Constructor to initialized toaster component...
	 */
	public Toaster() {
		// Set default font...
		font = new Font("Arial", Font.BOLD, 12);
		// Border color
		borderColor = new Color(245, 153, 15);
		toasterColor = Color.WHITE;
		messageColor = Color.BLACK;
		useAlwaysOnTop = true;
		// Verify AlwaysOnTop Flag...
		try {
		  JWindow.class.getMethod( "setAlwaysOnTop", new Class[] { Boolean.class } );
		}
		catch( Exception e ) { useAlwaysOnTop = false; }		
	}
	
  
  public Font getToasterMessageFont() { return font; }
  public void setToasterMessageFont( Font f) { font = f; }
  public Color getBorderColor() { return borderColor; }
  public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
  public int getDisplayTime() { return displayTime; }
  public void setDisplayTime(int displayTime) { this.displayTime = displayTime; }
  public int getMargin() { return margin; }
  public void setMargin(int margin) { this.margin = margin; }
  public Color getMessageColor() { return messageColor; }
  public void setMessageColor(Color messageColor) { this.messageColor = messageColor; }
  public int getStep() { return step; }
  public void setStep(int step) { this.step = step; }
  public int getStepTime() { return stepTime; }
  public void setStepTime(int stepTime) { this.stepTime = stepTime; }
  public Color getToasterColor() { return toasterColor; }
  public void setToasterColor(Color toasterColor) { this.toasterColor = toasterColor; }
  public int getToasterHeight() { return toasterHeight; }
  public void setToasterHeight(int toasterHeight) { this.toasterHeight = toasterHeight; }
  public int getToasterWidth() { return toasterWidth; }
  public void setToasterWidth(int toasterWidth) { this.toasterWidth = toasterWidth; }
  public Image getBackgroundImage() { return backgroundImage; }
  public void setBackgroundImage(Image backgroundImage) { this.backgroundImage = backgroundImage; }
  /**
   * Show a toaster with the specified message and the associated icon.
   */
  public void showToaster( String msg ) throws Exception { showToaster( null, msg ); }
  public void showToaster( String msg, String link) throws Exception { showToaster( null, null, link, msg); }
  public void showToaster(Icon icon, String msg, String link, String linkText) throws Exception {
    Toaster.SingleToaster singleToaster = new Toaster.SingleToaster(icon, msg, link, linkText);
    singleToaster.animate();
  }

  /**
	 * Class that represents a single toaster
	 */
	class SingleToaster extends javax.swing.JWindow {
		private static final long serialVersionUID = 2L;
		private JLabel iconLabel = new JLabel();
    private LinkLabel message = null;
		
		/***
		 * Simple constructor that initialized components...
		 */
		public SingleToaster(Icon icon, String msg, String link, String linkText) {
      initComponents(icon, msg, link, linkText);
    }

		/***
		 * Function to initialized components
		 */
		private void initComponents(Icon icon, String msg, String link, String linkText) {			
      //setVisible(false);
			setSize(toasterWidth, toasterHeight);
			
      if ( icon != null ) { iconLabel.setIcon( icon ); }
      java.net.URI uri=null;
      try { uri = new java.net.URI(link); }
      catch(Exception e) {}
      message = new LinkLabel(uri, linkText);
			message.setFont( getToasterMessageFont() );
			JPanel externalPanel = new JPanel(new BorderLayout(1, 1));
			externalPanel.setBackground( getBorderColor() );
			JPanel innerPanel = new JPanel(new BorderLayout( getMargin(), getMargin() )) {
        @Override
        public void paint(Graphics g) {
          if ( getBackgroundImage() != null ) { g.drawImage(getBackgroundImage(),0,0,null); }
          super.paint(g);
        }
      };
      if ( getBackgroundImage() != null ) {
        innerPanel.setOpaque(false);
			  message.setOpaque(false);
			  iconLabel.setOpaque(false);
      }
      innerPanel.setBackground( getToasterColor() );
      message.setMargin( new Insets( 2,2,2,2 ) );
     	message.setBackground( getToasterColor() );

     	EtchedBorder etchedBorder = (EtchedBorder) BorderFactory.createEtchedBorder();
			externalPanel.setBorder(etchedBorder);
			externalPanel.add(innerPanel);
      message.setForeground( getMessageColor() );
			innerPanel.add(iconLabel, BorderLayout.WEST);
			
      message.setStandardColor(new Color(0,128,0));
      message.setHoverColor(new Color(222,128,0));
      message.init();

			JLabel msgLabel=new JLabel(msg);
      innerPanel.add(msgLabel, BorderLayout.NORTH);
			innerPanel.add(message/*linkLabelWeb*/, BorderLayout.CENTER);
			getContentPane().add(externalPanel);
 		}
    

		/***
		 * Start toaster animation...
		 */
		public void animate() {
			( new Toaster.Animation( this ) ).start();
		}
		
	}

	/***
	 * Class that manage the animation
	 */
	class Animation extends Thread {
		Toaster.SingleToaster toaster;
		
		public Animation( Toaster.SingleToaster toaster ) {
			this.toaster = toaster;
		}
		
		
		/**
		 * Animate vertically the toaster. The toaster could be moved from bottom
		 * to upper or to upper to bottom
		 * @param posx
		 * @param fromy
		 * @param toy
		 * @throws InterruptedException 
		 */
		protected void animateVertically( int posx, int fromY, int toY ) throws InterruptedException {
      toaster.setVisible(true);
			toaster.setLocation( posx, fromY );
			if ( toY < fromY ) {
				for (int i = fromY; i > toY; i -= step) {
					toaster.setLocation(posx, i);
					Thread.sleep(stepTime);
				}
			}
			else {
				for (int i = fromY; i < toY; i += step) {
					toaster.setLocation(posx, i);
					Thread.sleep(stepTime);
				}
			}
			toaster.setLocation( posx, toY );
		}
		
		public void run() {
			try {
				boolean animateFromBottom = true;
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				Rectangle screenRect = ge.getMaximumWindowBounds();

				int screenHeight = (int) screenRect.height;
			
				int startYPosition;
				int stopYPosition;
				
				if ( screenRect.y > 0 ) {
				  animateFromBottom = false; // Animate from top!
				}

				maxToasterInSceen = screenHeight / toasterHeight;
				
				
				int posx = (int) screenRect.width - toasterWidth - 1;

				toaster.setLocation(posx, screenHeight);
				if ( useAlwaysOnTop ) {
				  toaster.setAlwaysOnTop(true);
				}
				
				if ( animateFromBottom ) {
					startYPosition = screenHeight;
					stopYPosition = startYPosition - toasterHeight - 1;
					if ( currentNumberOfToaster > 0 ) {
						stopYPosition = stopYPosition - ( maxToaster % maxToasterInSceen * toasterHeight );
					}
					else {
						maxToaster = 0;
					}
				}
				else {
					startYPosition = screenRect.y - toasterHeight;
					stopYPosition = screenRect.y;
					
					if ( currentNumberOfToaster > 0 ) {
						stopYPosition = stopYPosition + ( maxToaster % maxToasterInSceen * toasterHeight );
					}
					else {
						maxToaster = 0;
					}
				}
				
				currentNumberOfToaster++;
				maxToaster++;
				
				animateVertically( posx, startYPosition, stopYPosition );
				Thread.sleep(displayTime);
				animateVertically( posx, stopYPosition, startYPosition );
				
				currentNumberOfToaster--;
				toaster.setVisible(false);
				toaster.dispose();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
