import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;


public class MainFrame extends JFrame {

    private JLabel lbClock = new JLabel("Clock display");
    public JLabel lbc1 = new JLabel("Car-1");
    public JLabel lbc2 = new JLabel("Car-2");
    public JLabel lbc3 = new JLabel("Car-3");
    public JLabel lbc4 = new JLabel("Car-4");
    public JLabel lbc5 = new JLabel("Car-5");
    
    public JLabel lbt1 = new JLabel("TL-1");
    public JLabel lbt2 = new JLabel("TL-2");
    public JLabel lbt3 = new JLabel("TL-3");
    public JLabel lbt4 = new JLabel("TL-4");
    
    public JLabel lbDetailsT = new JLabel("");
    public JLabel lbStartTime = new JLabel("StartTime");
    public JLabel lbStopTime = new JLabel("StopTime");
    private JTextField tfCarSpeed = new JTextField("50", 5);
    public JLabel lbCarSpeed = new JLabel("Car speed, km/h:");
    
    // buttons
    private JButton btStart = new JButton("Start");
    private JButton btPause = new JButton("Pause");
    private JButton btResume = new JButton("Continue");
    private JButton btStop = new JButton("Stop");
    private JButton btAddCar = new JButton("Add Car");
    private JButton btAddTr = new JButton("Add Intersection");
    
    private String dateStr;
    private boolean simulationStop = false;
    private boolean simulationStart = false;
    private double speed;
    
    // lists of car and tr light labels, counters
    private ArrayList <JLabel> listLbC = new ArrayList <JLabel>();
    private ArrayList <JLabel> listLbT = new ArrayList <JLabel>();
    private int countCar = 0;
    private int countTL = 0;
    private Date currdate;
    private SimpleDateFormat dft = new SimpleDateFormat (" hh:mm:ss a");
    // used by this thread only
    private StringBuffer strBuff = new StringBuffer();
    
    // constructor
    public MainFrame(String title) {
    	
    	super(title);
    	addToLists();
    	// 3 sections of GUI: controls + time, cars display, intersections display
    	// System.out.println(javax.swing.SwingUtilities.isEventDispatchThread());
    	MenuBar menuBr = new MenuBar();
		setMenuBar(menuBr);
		Menu menu2 = new Menu("Menu");
		MenuItem limit, imgmenu, printStack;
		menu2.add(limit = new MenuItem("Limitations"));
		menu2.add(imgmenu = new MenuItem("User Manual"));
		menuBr.add(menu2);
		limit.addActionListener((ee) -> menuLimit(1));
		imgmenu.addActionListener((ee) -> menuLimit(2));
    	
        setLayout(new GridLayout(3, 0));
        
        // controls and time: all buttons here
        JPanel clockPnl = new JPanel(new GridLayout(3, 0));
        JPanel clockPnl1 = new JPanel(new FlowLayout());
        clockPnl1.add(btStart);
        clockPnl1.add(btPause);
        clockPnl1.add(btResume);
        clockPnl1.add(btStop);
        JPanel clockPnl2 = new JPanel(new FlowLayout());
        clockPnl2.add(lbCarSpeed);
        clockPnl2.add(tfCarSpeed);
        clockPnl2.add(btAddCar);
        clockPnl2.add(btAddTr);
        JPanel clockPnl3 = new JPanel(new FlowLayout());
        clockPnl3.add(lbClock);      
        
        clockPnl.add(clockPnl1);
        clockPnl.add(clockPnl2);
        clockPnl.add(clockPnl3);
        add(clockPnl);
        
        // allow max 5 cars: 
        JPanel carPnl = new JPanel(new GridLayout(3,2));
        carPnl.setBorder(new EmptyBorder(0, 30, 10, 10));
        carPnl.add(lbc1);
        carPnl.add(lbc2);
        carPnl.add(lbc3);
        carPnl.add(lbc4);
        carPnl.add(lbc5);
        carPnl.add(lbDetailsT);
        add(carPnl);
        
        JPanel intersPnl = new JPanel(new GridLayout(3,4));
        intersPnl.setBorder(new EmptyBorder(0, 30, 10, 10));
        intersPnl.add(lbt1);
        intersPnl.add(new JLabel(""));
        intersPnl.add(lbt2);
        intersPnl.add(new JLabel(""));
        intersPnl.add(lbt3);
        intersPnl.add(new JLabel(""));
        intersPnl.add(lbt4);
        intersPnl.add(new JLabel(""));
        intersPnl.add(lbStartTime);
        intersPnl.add(new JLabel(""));
        intersPnl.add(lbStopTime);

        add(intersPnl);

        btStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (!simulationStop) {
            		startTime();
            	currdate = new Date();
  		      	dateStr = "" + dft.format(currdate);
  		        lbStartTime.setText("Start:" + dateStr);
  		        lbStopTime.setText("StopTime");
  		        // start everything that already added
  		        for(TrLightWorker tw: App.listTrLi)
  		        	tw.execute();
  		        for (Thread t: App.listThread)
  		        	t.start();
  		        for (CarShowWorker csw : App.listCarWor)
  		        	csw.execute();
  		        simulationStart = true;
  		        strBuff.append(dateStr + " START\n");
            	} else {
            		menuLimit(3);
            	}
            }
        });
        
        btStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	//stop cars, lights, swing Workers that show cars
            	for (Car c : App.listCars)
					c.stop();
            	for (TrLightWorker tl : App.listTrLi)
					tl.pause(); 
            	for (CarShowWorker csw : App.listCarWor) 
					csw.cancel(true);
            	currdate = new Date();
		      	dateStr = "" + dft.format(currdate);
            	lbStopTime.setText("Stop:" + dateStr);
  		        // clear info-label
  		        lbDetailsT.setText("");
		      	simulationStop = true;
		      	strBuff.append(dateStr + " STOP\n");
		      	try {
					createReport();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        btPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	// pause all existing cars
            	for (Car c : App.listCars)
					c.pause();
            	// pause all existing traffic lights
            	for (TrLightWorker tl : App.listTrLi) 
					tl.pause();
            }
        });
        
        btResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                
            	if (!simulationStop){
            		// restart all cars, traffic lights
            		for (Car c : App.listCars) 
            			c.resume();
            		for (TrLightWorker tl : App.listTrLi) 
            			tl.resume();
            	}
            }
        });
        
        btAddCar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (!simulationStop && countCar <5) {
            		// add car and check errors on speed value
            		try {
            			speed = Double.parseDouble(tfCarSpeed.getText());
            		} catch (Exception e) {
            			tfCarSpeed.setText("Err");
            			tfCarSpeed.setBackground(Color.red);
            			lbDetailsT.setText("Car speed: 30 to 170 km/h");
            		}
            		if (speed >= 30 && speed <= 170) {
            			tfCarSpeed.setBackground(Color.white);
                	// get the reference to the label at index as many cars already, start with 0
                	JLabel nextlb = listLbC.get(countCar);
                	Car c = new Car(speed, nextlb);
                	countCar++;
                	// car runs in its own new thread with sleep time 100 ms, 
                	//swingWorker only updates GUI each second
                	Thread th = new Thread(c);
                	App.listThread.add(th); 
                	CarShowWorker csw = new CarShowWorker(c, speed, nextlb);
                	// add to list so that possible to stop them
                	App.listCars.add(c); 
                	App.listCarWor.add(csw);
                	// if already started, need to start this new cars
                	if (simulationStart) {
                		th.start();
                    	csw.execute();
                		}
                	currdate = new Date();
    		      	dateStr = "" + dft.format(currdate);
                	strBuff.append(dateStr + " Car-" + countCar + ", speed: " + speed + " added\n");
            		} else {
            			tfCarSpeed.setText("Err");
            			tfCarSpeed.setBackground(Color.red);
            			lbDetailsT.setText("Car speed: 30 to 170 km/h");
            		}
                } else if (simulationStop) {
            		menuLimit(3);
                } else { 
                	lbDetailsT.setText("Cannot add car");
                }
            }
        });
        
        btAddTr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (!simulationStop && countTL < 4) {
            	JLabel nextlbt = listLbT.get(countTL);
            	TrLightWorker tlw = new TrLightWorker(nextlbt, (countTL+1));
            	countTL++;
                App.listTrLi.add(tlw); 
                if (simulationStart) {
                	tlw.execute();
            		}
                currdate = new Date();
		      	dateStr = "" + dft.format(currdate);
            	strBuff.append(dateStr + " Intersection-" + countTL + " added\n");
            	} else if (simulationStop) {
            		menuLimit(3);
                } else {
            		lbDetailsT.setText("Cannot add Intersection");
            	}
            }
        });

        setSize(480, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void menuLimit(int ch) {
    	ShowDialog imgDialog;
    	if (ch ==1) {
    		imgDialog = new ShowDialog(this, "Limitations");
    		imgDialog.setVisible(true);}
    	else if (ch == 2) {
    		imgDialog = new ShowDialog(this, "User Manual");
    		imgDialog.setVisible(true);
    		}
    	else if (ch == 3) {
    		imgDialog = new ShowDialog(this, "Stopped");
    		imgDialog.setVisible(true);}
	}
    
    class ShowDialog extends Dialog{

    	ShowDialog(Frame parent, String title){
			super(parent, title, false);
			setLayout(new FlowLayout());
			//setSize(250, 200);
			
			if(title.equals("Limitations")) {
	        // in constructor can add rows, cols
	        JTextArea ta = new JTextArea(" Maximum number of cars: 5\n"
	        	      +" Maximum number of intersections: 4\n\n"
	        	      +" Maximum car speed: 170km/h\n"
	        	      +" Minimum car speed: 30km/h\n\n"
	        	      +" Car is aware of the traffic light color\n"
	        	      +" 5m or less before it and stops there.\n"
	        	      +" Car stops on Red light.");
	        add(ta);
	        setSize(250, 200);
			} else if (title.equals("User Manual")) {
				JTextArea ta = new JTextArea("  1. Button \"Start\"\n Start all added objects\n "
						+"  The Start time will appear at the bottom of the screen.\n"
						+"  2. Button\"Pause\"\n will pause cars and traffic lights only.\n"
						+"  3. Button \"Continue\" \n will resume cars and traffic lights.\n"
						+"  4. Button \"Stop\"\n will stop the simulation,\n"
						+"  labels will be reset and stop time appear at the bottom.\n"
						+"  5. Button \"Add car\" \n will get the speed and create the new car.\n"
						+"  The car route is 5000 m maximum, then it arrives.\n"
						+"  If the speed has invalid value, the error will appear\n"
						+"  6. Button \"Add Intersection\"\n will add intersection. Car stops on Red\n"
						+"  The red light is 7s, yellow is 4s, green is 5s.");
		        add(ta);
		        setSize(360, 365);
			} else if (title.equals("Stopped")) {
				JTextArea ta = new JTextArea("  The simulation has\n "
						+"  completed its tasks.\n"
						+"  To repeat must reload.");
		        add(ta);
		        setSize(180, 100);
			}
			
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					dispose();
				}
			});
		}
	}

	private void startTime() {
        
        // Use SwingWorker<Void, Void> and return null from doInBackground if
        // you don't want any final result and you don't want to update the GUI
        // as the thread goes along.
        // First argument is the thread result, returned when processing finished.
        // Second argument is the value to update the GUI with via publish() and process()
        SwingWorker<String, String> worker = new SwingWorker<String, String>() {

            @Override
            /* Note: do not update the GUI from within doInBackground. */
            protected String doInBackground() throws Exception {
                    
                    // optional: use publish to send values to process(), which
                    // you can then use to update the GUI.
                    // The type we pass to publish() is determined
                    // by the second template parameter.                
                for ( ; ; ) {
      		      Thread.sleep(1000);
      		      currdate = new Date();
    		      dateStr = "" + dft.format(currdate);
      		      publish(dateStr);
      		 }  // Here we can return some object of whatever type
                // we specified for the first template parameter.
                //return "Karina";
            }

            @Override
            // This will be called if you call publish() from doInBackground()
            // Can safely update the GUI here.
            protected void process(List<String> chunks) {
                String value = chunks.get(chunks.size() - 1); 
                lbClock.setText("Current TIME: " + value);
            }

          /*@Override
            This is called when the thread finishes.
            Can safely update GUI here.
            protected void done() {  
            } */
            
        };
        
        worker.execute();
    }
    

	private void createReport () throws IOException {
	       
	       PrintWriter out = null;
	       strBuff.append("Distance:\n");
	       for (int i = 0; i < countCar; i++) {
	    	   JLabel  lb = listLbC.get(i);
	    	   strBuff.append(lb.getText() + "\n");
	    	   }
	       LocalDate currentdate = LocalDate.now();
	       String filename = currentdate + "report" + ".txt";
	       System.out.println("Here is filename :  " + filename);
	       out = new PrintWriter(new FileWriter(filename));  // create/overwrite file
	       out.println(strBuff);  // write the data
	       out.flush();      // flush all the data to the file
	       out.close();    // close the stream
	     
	   }
	
    private void addToLists() {
    	// add labels for cars, specify indexes
    	listLbC.add(0, lbc1);
    	listLbC.add(1, lbc2);
    	listLbC.add(2, lbc3);
    	listLbC.add(3, lbc4);
    	listLbC.add(4, lbc5);
    	
    	listLbT.add(0, lbt1);
    	listLbT.add(1, lbt2);
    	listLbT.add(2, lbt3);
    	listLbT.add(3, lbt4);

    };
}