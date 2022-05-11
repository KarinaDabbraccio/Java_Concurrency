import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class TrLightWorker extends SwingWorker<String, String>  {	
	
	private String clr;
	private boolean changed = false; // true when the light has changed
	private volatile boolean isPaused;
	private Color trColor;
	private final int name;
	private JLabel jlb2;
	// count is a class variable, to count all intersections
	
	public TrLightWorker (JLabel jlb, int count) {
		this.jlb2 = jlb;
		this.trColor = Color.RED;
		this.clr = "red";
		this.name = count;
		jlb2.setText("Intersection added");
	}

	
	protected String doInBackground() throws Exception {
		  
		 while (!isCancelled()) {
			   
			   if (!isPaused()) {
				   switch(clr) { 
			          case "green": 
			            Thread.sleep(5000); // green for 5 seconds 
			            break; 
			          case "yellow": 
			            Thread.sleep(2000);  // yellow for 2 seconds 
			            break; 
			          case "red": 
			            Thread.sleep(7000); // red for 7 seconds 
			            break; 
			        }
				   changeColor();
				   publish(clr);
				   //return "Karina";
			   }
	      	}
	       return "Karina";
	    }

	    @Override
	    protected void process(List<String> chunks) {
	        String value = chunks.get(chunks.size() - 1); 
	        jlb2.setText(this.name + ": "+ value);
	        jlb2.setBackground(trColor);
	        jlb2.setOpaque(true);
	            }


	    public final void pause() {
	        if (!isPaused())
	            isPaused = true;
	    }

	    public final void resume() {
	        if (isPaused())
	            isPaused = false;
	    }

	    public final boolean isPaused() {
	        return isPaused;
	    }
	
	// Change color. 
	  synchronized void changeColor() { 
	    switch(clr) { 
	      case "red": 
	    	  clr = "green";
	    	  this.trColor = Color.green;
	        break; 
	      case "yellow": 
	        clr = "red";
	        this.trColor = Color.red;
	        break; 
	      case "green": 
	       clr = "yellow"; 
	       this.trColor = Color.yellow;
	    } 
	 
	    changed = true;
	    notify(); // signal that the light has changed 
	  } 
	 
	  // Wait until a light change occurs. 
	  synchronized void waitForChange() { 
	    try { 
	      while(!changed) 
	        wait(); // wait for light to change 
	      changed = false;
	    } catch(InterruptedException exc) { 
	      System.out.println(exc); 
	    } 
	  } 
	 
	  // Return current color. 
	  synchronized String getColor() { 
	    return clr; 
	  }
	  
	  protected void done() {
          
          try {
        	  get();
        	  jlb2.setText("Add TL - Done");
        	  jlb2.setBackground(Color.gray);
          } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } catch (ExecutionException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } catch (CancellationException e) {
              // Do your task after cancellation
          	jlb2.setText("Add TL");
          	jlb2.setBackground(Color.gray);
          }
          
          
      }

}
