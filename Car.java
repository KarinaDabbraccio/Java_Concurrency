import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.SwingWorker;


public class Car implements Runnable {	
	
	// speed km/h, label to display info
	private double speed;
	private String getCol;
	private int lightCount;
	private volatile double distance = 0;
	private boolean isStopLight = false;
	private volatile boolean isPaused;
	private volatile boolean isExit = false;
	private JLabel jlbname;
	
	public Car (double kmh, JLabel lb) {
		this.speed = kmh;
		jlbname = lb;
		this.jlbname.setText("Car: " + kmh + " km/h added");
	}

	@Override
	public void run() {	
		// max route is 10 km
		while (!isExit) {
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println ("InterruptedException in Car.run() ");
			}
			
			if (!isPaused()) {
				// car moving, distance per 0.1 second = speed / 36
				if (!isStopLight)
					distance += speed / 36;

				// get the number of intersections, check on each
			  	lightCount = App.listTrLi.size();
				//lightCount = App.listTL.size();
			  	if (lightCount != 0) {
			  		for (int i = 1; i <= lightCount; i++) {
			  			//at light
			  			if (distance > i*1000 - 5 && distance < i*1000) {
			  				TrLightWorker tli = App.listTrLi.get(i-1);
			  				//TrLight tli = App.listTL.get(i-1);
			  				getCol = tli.getColor();
			  				if (getCol.equals("red")){
			  					isStopLight = true;
			  				} else {
			  					isStopLight = false;
			  				}
			  				tli = null;
			  			} // end if at trlight
			  } //end for	
			  }
		   } // end if (!isPaused())
		}
	} // end run()
	
	public void stop(){
        isExit = true;
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
    
    public final double getDistance() {
        return distance;
    }

}



