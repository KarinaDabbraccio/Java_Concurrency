import java.util.ArrayList;

import javax.swing.SwingUtilities;


public class App {
	
	public static ArrayList<Car> listCars = new ArrayList<Car>();
	public static ArrayList<TrLightWorker> listTrLi = new ArrayList<TrLightWorker>();
	public static ArrayList<CarShowWorker> listCarWor = new ArrayList<CarShowWorker>();
	public static ArrayList<Thread> listThread = new ArrayList<Thread>();

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                new MainFrame("Simulation DAbbraccio");
            }
        });

    }

}
