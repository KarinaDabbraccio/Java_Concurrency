import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.SwingWorker;


public class CarShowWorker extends SwingWorker<String, String> {	
	
	private double distanceShow;
	private JLabel jlb;
	private Car myCar;
	private double speed;
	private DecimalFormat formatter = new DecimalFormat("#0.00");
	private String output = "";
	
	public CarShowWorker (Car c, double sp, JLabel lb) {
		myCar = c;
		jlb = lb;
		speed = sp;
	}

   protected String doInBackground() throws Exception {
  
	   while (distanceShow <=5000 && !isCancelled()) {
		   Thread.sleep(1000);
		   distanceShow = myCar.getDistance();
		   output = formatter.format(distanceShow);
		   publish(output);
		   }
	   if (distanceShow >=5000)
		   return "Car Arrived";
	   return output;
     }


    @Override
    protected void process(List<String> chunks) {
        String value = chunks.get(chunks.size() - 1); 
        
        jlb.setText("Car-" + speed + " km/h:   " + value);
        }

    protected void done() {
                
                try {
                    String status = get();
                    jlb.setText(status);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (CancellationException e) {
                }                  
            } // end done()                
}

