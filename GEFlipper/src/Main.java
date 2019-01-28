import org.dreambot.api.script.AbstractScript;  // All these imports are from the dreambot API

import org.dreambot.api.script.ScriptManifest;  // API can be found here: https://dreambot.org/javadocs/
import org.dreambot.api.script.event.impl.PaintEvent;
import org.dreambot.api.utilities.impl.Condition;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import org.dreambot.api.methods.container.AbstractItemContainer;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.path.impl.LocalPath;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



@ScriptManifest(author = "Prech", name = "GE Item Flipper", version = 1.0, description = "", category = Category.MONEYMAKING)
public class Main
extends AbstractScript {	
	
	
	private final String serverIP = "167.114.98.62";
	private final Area GE_Area = new Area(3162,3492,3167,3487);
	private int player_gold = 0;
	public int item_id;
	JFrame mainwindow = null;
	private float base_percent;
    private float percentstep;
    private status current_status = null;
    private boolean running = false;
    private long current_buy_price = 0;
    private long current_sell_price = 0;
	private mainWindow window = null;
	private long lastsell_price = 0;
	private long lastbuy_price = 0;
	private Instant last_price_update;
	private int newprice_sell;
	private int newprice_buy;
	private int timeout = 60 * 1000; // 60 seconds
	private boolean firstbuy = true;
	private boolean firstsell = true;
	private Instant checkBuy_time;
	private Instant checkSell_time;
	private boolean cancelbuy = false;
	private boolean cancelsell = false;
	private Timer itemprice_check;
	private Timer itemprice_check2;
	private int item_count = 1;
	private int minbuy;
	private int maxbuy;
	private int minsell;
	private int maxsell;
	private int itemdelay;
	
	private int last_ok_buy = 0;
	private int last_ok_sell = 0;
	
	
	
	private enum status {
		READY, CHECKLOCATION, BUYING, SELLING, COLLECTING, CANCELLING
	}
	
	
	
	private status getState(Player curplayer) throws IOException {
		
		if (!GE_Area.contains(getLocalPlayer()) || getGrandExchange().open()) {
			return status.CHECKLOCATION;
		}
		if (!getGrandExchange().slotContainsItem(0) && player_gold > current_buy_price && getInventory().getEmptySlots() > 0) {
			return status.BUYING;
		} else {
			if (getGrandExchange().isReadyToCollect(0)) {
				return status.COLLECTING;
			}
			int cbtime = (int) Duration.between(checkBuy_time, Instant.now()).toMillis()/1000;
			if (cbtime >= (int) window.itemdelay.getValue()) {
				cancelbuy = true;
				return status.CANCELLING;
			}
		}
		
		if(!getGrandExchange().slotContainsItem(1)) {
			return status.SELLING;
		} else {
			if (getGrandExchange().isReadyToCollect(1)) {
				return status.COLLECTING;
			}
			int sbtime = (int) Duration.between(checkSell_time, Instant.now()).toMillis()/1000;
			if (sbtime >= (int) window.itemdelay.getValue()) {
				cancelsell = true;
				return status.CANCELLING;
			}
		} 
		return status.READY;

	}

	
	private boolean check_slot(int id) {
		if (getGrandExchange().slotContainsItem(id)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean toggle_bot(boolean status) {
		if (status == true) {
			return false;
		} else {
			return true;
		}
	}

	private long getItemPrice(int itemID, String slot) {
		window.status.setText("Updating RSBuddy average prices..");
    	try {
    	String webPage = "http://" + serverIP + "/getprice.php?i=" + itemID + "&s=" + slot;
    	//log("PricePage: " + webPage);
    	URL url = new URL(webPage);
    	URLConnection urlConnection = url.openConnection();
    	InputStream is = urlConnection.getInputStream();
    	InputStreamReader isr = new InputStreamReader(is);

    	int numCharsRead;
    	char[] charArray = new char[1024];
    	StringBuffer sb = new StringBuffer();
    	while ((numCharsRead = isr.read(charArray)) > 0) {
    	sb.append(charArray, 0, numCharsRead);
    	}
    	String result = sb.toString();
    	//log("Got new item price: " + result);


    	return Long.parseLong(result);
    	} catch (Exception e) {
    	log("Exception occurred at getPrice." + e);
    	}

    	return 0;
    	}
	
	private int getItemID(String item) {
		//window.status.setText("Updating current item price..");
		item = item.replace(" ", "%20");
		//log("Item: " + item);
    	try {
    	String webPage = "http://" + serverIP + "/getid.php?i=" + item;
    	log("Webpage: '" +webPage+"'");
    	URL url = new URL(webPage);
    	URLConnection urlConnection = url.openConnection();
    	InputStream is = urlConnection.getInputStream();
    	InputStreamReader isr = new InputStreamReader(is);

    	int numCharsRead;
    	char[] charArray = new char[1024];
    	StringBuffer sb = new StringBuffer();
    	while ((numCharsRead = isr.read(charArray)) > 0) {
    	sb.append(charArray, 0, numCharsRead);
    	}
    	String result = sb.toString();
    	//log("ItemID: " + result);
    	//window.status.setText("Ready");

    	return Integer.parseInt(result);
    	} catch (Exception e) {
    	log("Exception occurred at getID." + e);
    	}

    	return 0;
    	}
	
	
	@Override
    public void onStart() {
		running = false;
		log("Starting!");	 // This will display in the CMD or logger on dreambot
		last_price_update = Instant.now();
		mainwindow = new JFrame("GE Flipper");
		window = new mainWindow();
		mainwindow.setContentPane(window.mainPanel);
		firstbuy = true;
		firstsell = true;
		itemprice_check = new Timer();
		bought = -1;
		sold = -1;
		
		JButton savesettings = window.savesettings_btn1;
		savesettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setAcceptAllFileFilterUsed(false);
				
				if (fileChooser.showSaveDialog(mainwindow) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  // save to file
				  if (!file.exists()) {
			            FileWriter fWriter = null;;
						try {
							fWriter = new FileWriter(file);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			            PrintWriter pWriter = new PrintWriter(fWriter);
			        }


			        try (Writer writer = new BufferedWriter(
			                new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()), "utf-8"))) { // sets the output where to write
			                    writer.write(window.itemname.getText() + "," 
			                + window.percent_first.getText() + "," 
			                    		+ window.percent_next.getText() + "," 
			                + window.mingold.getValue() + "," 
			                    		+ window.itemdelay.getValue() + "," 
			                    		+ window.minbuy.getValue() + "," 
			                    		+ window.maxbuy.getValue() + "," 
			                    		+ window.minsell.getValue() + "," 
			                    		+ window.maxsell.getValue() + "," 
			                    		+ window.itemamount.getValue()); // writes
			        }

			        catch (IOException e1) {

			        }
				  
				}
			}
		});
		
		
		JButton loadsettings = window.loadsettings_btn;
		loadsettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int returnval = fileChooser.showOpenDialog(mainwindow);
				
				if (returnval == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					String content = "";
				    try
				    {
				        content = new String ( Files.readAllBytes( Paths.get(file.getPath()) ) );
				        String[] array = content.split(",");
				        window.itemname.setText(array[0]);
				        window.percent_first.setText(array[1]);
				        window.percent_next.setText(array[2]);
				        window.mingold.setValue(Integer.valueOf(array[3]));
				        window.itemdelay.setValue(Integer.valueOf(array[4]));
				        window.minbuy.setValue(Integer.valueOf(array[5]));
				        window.maxbuy.setValue(Integer.valueOf(array[6]));
				        window.minsell.setValue(Integer.valueOf(array[7]));
				        window.maxsell.setValue(Integer.valueOf(array[8]));
				        window.itemamount.setValue(Integer.valueOf(array[9]));
				    }
				    catch (IOException e1)
				    {
				        log(e1.toString());
				    }
				}
				
			}});
		
		JButton manualupdate = window.manualupdate;
		manualupdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				last_price_update = Instant.now();
	            current_buy_price = getItemPrice(getItemID(window.itemname.getText()),"buy_average");
	            current_sell_price = getItemPrice(getItemID(window.itemname.getText()),"sell_average");
	            window.rs_buy.setText(current_buy_price + " gold");
		        window.rs_sell.setText(current_sell_price + " gold");
			}});
		
		JButton startbtn = window.start_btn;
		startbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {			
				running = toggle_bot(running);

				if (running) {
					
					item_id = getItemID(window.itemname.getText());
					
					startitemcount = getInventory().count(item_id);
					bought = 0;
					sold = 0;
					profit = 0;
					minbuy = (int) window.minbuy.getValue();
					maxbuy = (int) window.maxbuy.getValue();
					minsell = (int) window.minsell.getValue();
					maxsell = (int) window.maxsell.getValue();
					item_count = (int) window.itemamount.getValue();
					//log("Itemid: " + item_id);
					base_percent = new Float(Float.valueOf( window.percent_first.getText() )/100);
					//log("Base percent: " + base_percent);
					next_percent = new Float( Float.valueOf( window.percent_next.getText() )/100);
					//log("Next percent: " + next_percent);
					itemdelay = (int) window.itemdelay.getValue();
					last_price_update = Instant.now();
		            //current_buy_price = getItemPrice(item_id,"buy_average");
		            //current_sell_price = getItemPrice(item_id,"sell_average");
		            //window.rs_buy.setText(current_buy_price + " gold");
		           // window.rs_sell.setText(current_sell_price + " gold");
					itemprice_check.schedule(new FirstPriceTask(), 1);
					itemprice_check2.schedule(new PriceTask(), 10, 30000);
					
				window.start_btn.setText("Pause");
				} else {
					itemprice_check.cancel();
					window.start_btn.setText("Start");
				}
				
			}});
		
		mainwindow.pack();
		mainwindow.setResizable(false);
		mainwindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainwindow.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(mainwindow, 
		            "Are you sure you want to stop the script?", "Exit script?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		            mainwindow.setVisible(false);
		            mainwindow.dispose();
		            stop();
		        }
		    }
		});
		
		mainwindow.setVisible(true);
		log("Initialized!");
		current_status = status.READY;

		window.lastupdated.setText("Run script to update item price!");
		
		
    }
 
    @Override
    public int onLoop() {
    	
    	if (running) {
    		
    		
    		player_gold = getInventory().count("Coins");
    		Instant noww = Instant.now();
    		int last_price_update_seconds = 0;
        	try {
        		last_price_update_seconds = (int) Duration.between(last_price_update, noww).toMillis()/1000;
        	} catch (NullPointerException e) {
        		log(e.toString());
        	}
        	
        	if (last_price_update_seconds >= 30) {
        		window.status.setText("Updating item prices..");
            	last_price_update = Instant.now();
                current_buy_price = getItemPrice(getItemID(window.itemname.getText()),"buy_average");
                current_sell_price = getItemPrice(getItemID(window.itemname.getText()),"sell_average");
                window.rs_buy.setText(current_buy_price + " gold");
    	        window.rs_sell.setText(current_sell_price + " gold");
        	}
        	window.lastupdated.setText(last_price_update_seconds + " seconds ago");
		window.start_btn.setText("Pause");
    	try {
			current_status = getState(getLocalPlayer());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log(e.toString());
		}
    	switch (current_status) {
    	
    	case READY:
    		window.status.setText("Idle..");
    		break;
    	case CHECKLOCATION:
    		window.status.setText("Check if player is in GE area");
    		if (getGrandExchange().open()) {
    		getGrandExchange().open();
    		}
    		break;
    	case BUYING:
    		window.status.setText("Buying");
    		if (firstbuy) {
    			if (bought==0) {
    				last_ok_buy = (int) lastbuy_price;
    			}
    			log("First buy");
    			if (window.percent_first.getText().equals("0")) {
    				log("PercentFirst = 0");
    				int obj = (int) window.minbuy.getValue();
    				newprice_buy = obj;
    				log("Newbuy price: " + newprice_buy);
    				firstbuy = false;
    			} else {
    				log("PercentFirst != 0");
    			newprice_buy = (int) (current_buy_price - (current_buy_price * base_percent));
    			log("Newbuy price: " + newprice_buy);
    			firstbuy = false;
    			}
    		} else {
    			log("Next buy");
    			log("lastbuy: " + lastbuy_price);
    			if (base_percent == 0) {
    				log("FirstPercent = 0");
    				if (((int) lastbuy_price + (lastbuy_price * next_percent) >= maxbuy)) {
    					log("Price higher than maxbuy");
						int obj = (int) window.minbuy.getValue();
        				newprice_buy = obj;
        				log("New price: " + newprice_buy);
					} else {
						newprice_buy = (int) (lastbuy_price + (lastbuy_price * next_percent));
						log("New price: " + newprice_buy);
					}
    			} else {
						log("FirstPercent != 0");
						newprice_buy = (int) (lastbuy_price + (lastbuy_price * next_percent));
						log("New price: " + newprice_buy);
					} 
				}

    		
    		
    		
    		sleepUntil(() -> getGrandExchange().openBuyScreen(0),timeout);
    		sleep(1000);
			//log("add item");
			sleepUntil(() -> getGrandExchange().addBuyItem(window.itemname.getText()),timeout);
			sleep(500);
			sleepUntil(() -> getGrandExchange().setQuantity(item_count),timeout);
			//log("Curr price: " + current_price + ", new buy price: " + newprice_buy);
			sleep(500);
			//log("setprice");
			sleepUntil(() -> getGrandExchange().setPrice(newprice_buy),timeout);
			// set item back up on same slot
			//log("Send confirm");
			sleepUntil(() -> getGrandExchange().confirm(),timeout);
			checkBuy_time = Instant.now();
			lastbuy_price = newprice_buy;
    		break;
    	case SELLING:
    		if (getInventory().count(item_id) > 0) {
    			log("Has item for sale");
    		window.status.setText("Selling");
    		if (firstsell) {
    			log("First sell");
    			if (sold==0) {
    				last_ok_sell = (int) lastsell_price;
    			}
    			if (window.percent_first.getText() == "0") {
    				log("FirstPercent = 0");
    				int obj = (int) window.maxsell.getValue();
    				newprice_sell = obj;
    				log("New sell price: " + newprice_sell);
    				firstsell = false;
    			} else {
    				log("Firstpercent != 0");
    			newprice_sell = (int) (current_sell_price + (current_sell_price * base_percent));
    			firstsell = false;
    			}
    		} else {
    			log("Next sell");
    			log("Last sell: " + lastsell_price);
    			if (base_percent == 0) {
					log("FirstPercent = 0");
    				if (((int) lastsell_price + (lastsell_price * next_percent) >= maxsell)) {
    					log("Price higher than maxsell");
    					int obj = (int) window.minsell.getValue();
            			newprice_sell = obj;
            			log("New price: " + newprice_sell);
    				} else {
    					newprice_sell = (int) (lastsell_price + (lastsell_price * next_percent));
        				log("New price: " + newprice_sell);
    				}
    			} else {
    				log("FirstPercent != 0");
    				if (((int) lastsell_price + (lastsell_price * next_percent) <= (current_sell_price - (current_sell_price * base_percent)))) {
    					log("Price higher than maxsell");
            			newprice_sell = (int) lastsell_price + (int) (lastsell_price * base_percent);
            			log("New price: " + newprice_sell);
    				} else {
    				newprice_sell = (int) (lastsell_price + (lastsell_price * next_percent));
    				log("New price: " + newprice_sell);
    			}
    			}
    		}
  
    		sleepUntil(() -> getGrandExchange().openSellScreen(1),timeout);
    		sleep(1000);
			//log("add item");
			sleepUntil(() -> getGrandExchange().addSellItem(window.itemname.getText()),timeout);
			
			sleep(500);
			sleepUntil(() -> getGrandExchange().setQuantity(item_count),timeout);
			//log("Curr price: " + current_price + ", new buy price: " + newprice_buy);
			sleep(500);
			//log("setprice");
			sleepUntil(() -> getGrandExchange().setPrice(newprice_sell),timeout);
			// set item back up on same slot
			//log("Send confirm");
			sleepUntil(() -> getGrandExchange().confirm(),timeout);
			checkSell_time = Instant.now();
			lastsell_price = newprice_sell;
    		}
    		break;
    	case COLLECTING:
    		window.status.setText("Collecting..");
    		if (getGrandExchange().isReadyToCollect(0)) {
    			sleepUntil(() -> getGrandExchange().collect(),timeout);
    			bought++;
    			window.bought.setText("" + bought);
    		} else if (getGrandExchange().isReadyToCollect(1)) {
    			sleepUntil(() -> getGrandExchange().collect(),timeout);
    			window.sold.setText("" + sold);
    			sold++;
    		} 
    		
    		break;
    	case CANCELLING:
    		
    		if (cancelbuy) {
    			bought--;
    			sleepUntil(() -> getGrandExchange().cancelOffer(0),timeout);
    			sleepUntil(() -> getGrandExchange().goBack(), timeout);
    			
    			cancelbuy = false;
    		}
    		if (cancelsell) {
    			sold--;
    			sleepUntil(() -> getGrandExchange().cancelOffer(1),timeout);
    			sleepUntil(() -> getGrandExchange().goBack(), timeout);
    			cancelsell = false;
    		}
    		
    		break;
    	}
    	
    	
    	} else {
			window.start_btn.setText("Start");
		}
    	return Calculations.random(400, 1400);
    
    }
    
    @Override
    public void onExit() {
    	running = false;
        log("Stopping!"); // this will display in the CMD or logger on dreambot
        itemprice_check.cancel();
        mainwindow.setVisible(false);
        mainwindow.dispose();
    }
    
    
    private class PriceTask extends TimerTask {
        // run is a abstract method that defines task performed at scheduled time.
        public void run() {
        	window.status.setText("Updating item prices..");
        	last_price_update = Instant.now();
            current_buy_price = getItemPrice(getItemID(window.itemname.getText()),"buy_average");
            current_sell_price = getItemPrice(getItemID(window.itemname.getText()),"sell_average");
            window.rs_buy.setText(current_buy_price + " gold");
	        window.rs_sell.setText(current_sell_price + " gold");
        }
    }
    
    private class FirstPriceTask extends TimerTask {
        // run is a abstract method that defines task performed at scheduled time.
        public void run() {
        	window.status.setText("Updating item prices..");
        	last_price_update = Instant.now();
            current_buy_price = getItemPrice(getItemID(window.itemname.getText()),"buy_average");
            current_sell_price = getItemPrice(getItemID(window.itemname.getText()),"sell_average");
            window.rs_buy.setText(current_buy_price + " gold");
	        window.rs_sell.setText(current_sell_price + " gold");
            itemprice_check.cancel();
        }
    }
    
    
    
    
    
    
    

        
        
        
    
    
    
    

    
 
        
}
