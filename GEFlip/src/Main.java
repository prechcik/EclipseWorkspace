import org.dreambot.api.script.AbstractScript;  // All these imports are from the  API

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import scripts.mwindow;

import org.dreambot.api.methods.container.AbstractItemContainer;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.grandexchange.Status;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.path.impl.LocalPath;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



@ScriptManifest(author = "Prech", name = "GE Flip", version = 0.4, description = "", category = Category.MONEYMAKING)
public class Main
extends AbstractScript {	
	
	
	private final String serverIP = "prech.info/botnetwork";
	public int item_id;
	JFrame mainwindow = null;
	private status current_status = null;
    private boolean running = false;
    private mwindow window = null;
	private long lastsell_price = 0;
	private long lastbuy_price = 0;
	private Instant last_price_update;
	private int newprice_sell;
	private int newprice_buy;
	private int timeout = 60 * 1000; // 60 seconds
	private boolean firstbuy = true;
	private boolean firstsell = true;
	private int item_count = 1;
	private int minbuy;
	private int maxbuy;
	private int minsell;
	private int maxsell;
	private int itemdelay;
	private Instant buytime;
	private Instant selltime;
	private int current_price = 0;
	private Float maxpercent;
	private Float percentstep;
	private int newprice = 1;
	private Instant buystart;
	private Instant sellstart;
	private int bought = 0;
	private int sold = 0;
	private int lastbought;
	private int lastsold;
	private static HttpURLConnection con;
	private ZenAntiBan antiban;
	private boolean lastbought_bool = false;
	private boolean lastsold_bool = false;
	
	private enum status {
		READY, CHECKLOCATION, BUYING, SELLING, COLLECTINGBUY, COLLECTINGSELL, CANCELLINGBUY, CANCELLINGSELL, UPDATEPRICE
	}
	
	
private status getState(Player curplayer) throws IOException {
	if (running) {
		if ((int) Duration.between(last_price_update, Instant.now()).toMillis()/1000 >= 60) {
			return status.UPDATEPRICE;
		} 
	
		GrandExchangeItem[] items = getGrandExchange().getItems();
		if (getGrandExchange().isOpen()) {
			if(items[0].getStatus() == Status.EMPTY && getInventory().count("Coins")>maxbuy) {
			//log("Buy slot empty");
			window.status.setText("Buying..");
			return status.BUYING;
		} else {
			//log("Buy slot occupied");
			if (items[0].isReadyToCollect()) {
				window.status.setText("Bought item! Collecting..");
				return status.COLLECTINGBUY;
			}
			if (buystart != null) { 
				if (items[0].getStatus() != Status.EMPTY && !items[0].isReadyToCollect()) {
			if ((int) Duration.between(buystart, Instant.now()).toMillis()/1000 >= itemdelay) {
				window.status.setText("Timer passed, changing buy price..");
				return status.CANCELLINGBUY;
			}
				}
			}
		}
		if(items[1].getStatus() == Status.EMPTY) {
			//log("Sell slot empty");
			if ((getInventory().count(item_id) >= 2 * item_count) || ((getInventory().count("Coins") <= current_price)) && (getInventory().count(item_id) == item_count)) {
				window.status.setText("Selling..");
				return status.SELLING;
			}
		} else {
			//log("Sell slot occupied");
			if (items[1].isReadyToCollect()) {
				window.status.setText("Sold item! Collecting..");
				return status.COLLECTINGSELL;
			}
			if ((int) Duration.between(sellstart, Instant.now()).toMillis()/1000 >= itemdelay) {
				window.status.setText("Timer passed, changing sell price..");
				return status.CANCELLINGSELL;
			}
		}
		} else {
			window.status.setText("Am I at Grand Exchange?");
			return status.CHECKLOCATION;
		}
	
		
	}
	window.status.setText("Idle");
	return status.READY;
}
	
	
	
	private boolean toggle_bot(boolean status) {
		if (status == true) {
			return false;
		} else {
			return true;
		}
	}

	private long getItemPrice(int itemID) {
    	try {
    	String webPage = "http://" + serverIP + "/getprice.php?i=" + itemID;
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
    	//log("Webpage: '" +webPage+"'");
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
	
	
	private int adddbhistory(String botname, String type, Instant date, int price, String itemname) {
    	try {
    		botname = botname.replace(" ", "%20");
    		itemname = itemname.replace(" ", "%20");
    	String webPage = "http://" + serverIP + "/addbothistory.php?bot=" + botname + "&type=" + type + "&date=" + date.toEpochMilli()/1000 + "&price=" + price + "&itemname=" + itemname;
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
    	//log(result);

    	return 1;
    	} catch (Exception e) {
    	log("Exception occurred at adddbhistory." + e);
    	}

    	return 0;
    	}
	
	private int updatedbstatus(String botname, status status) {
    	try {
    		botname = botname.replace(" ", "%20");
    	String webPage = "http://" + serverIP + "/updatestatus.php?bot=" + botname + "&status=" + status;
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
    	//log(result);

    	return 1;
    	} catch (Exception e) {
    	log("Exception occurred at updatedbstatus." + e);
    	}

    	return 0;
    	}
	
	private int resethistory(String botname) {
    	try {
    		botname = botname.replace(" ", "%20");
    	String webPage = "http://" + serverIP + "/botclosing.php?bot=" + botname;
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
    	//log(result);

    	return 1;
    	} catch (Exception e) {
    	log("Exception occurred at botclosing." + e);
    	}

    	return 0;
    	}
	
	
	@Override
    public void onStart() {
		antiban = new ZenAntiBan(this);
		running = false;
		log("Starting!");
		mainwindow = new JFrame("GE Flipper");
		window = new mwindow();
		mainwindow.setContentPane(window.mainPanel);
		JButton startbtn = window.startbtn;
		startbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {			
				running = toggle_bot(running);

				if (running) {
					resethistory(getLocalPlayer().getName());
					window.startbtn.setText("Stop");
					log("started!");
					item_id = getItemID(window.jTextField1.getText());
					sleep(1000);
					current_price = (int) getItemPrice(item_id);
					sleep(500);
					item_count = (int) window.itemamount.getValue();
					maxpercent = new Float((float) window.maxpercent.getValue() /100);
					percentstep = new Float((float) window.percentstep.getValue() /100);
					itemdelay = (int) window.itemdelay.getValue();
					maxbuy = current_price;
					minsell = current_price;
					minbuy = (int) (current_price - (current_price * maxpercent));
					maxsell = (int) (current_price + (current_price * maxpercent));
					log("MaxPercent: " + maxpercent);
					log("Percentstep: " + percentstep);
					
				} else {
					window.startbtn.setText("Start");
					log("Stopped");
				}
			}
		});
		
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
		firstbuy = true;
		firstsell = true;
		last_price_update = Instant.now();
		log("Initialized!");
		current_status = status.READY;
		
		
		
		
    }
 
    @Override
    public int onLoop() {
    	if (running) {
    		
    		GrandExchangeItem[] items = getGrandExchange().getItems();
    		try {
				current_status = getState(getLocalPlayer());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log("Status error! " + e.toString());
			}
    		//Buy Slot
    		
    		switch (current_status) {
    		
    		case CHECKLOCATION:
    			//log("Checklocation");
    				getGrandExchange().open();
    			break;
    		case BUYING:
    			//log("Buying");
    			//log("Average price: " + current_price);
    			if (firstbuy) {
    				newprice = (int) ( current_price - ( current_price * maxpercent));
    				//log("Firstbuy. Price " + newprice);
    				firstbuy = false;
    			} else {
    				if (lastbought_bool) {
    					newprice = lastbought;
    				} else {
    				
    				if ((lastbuy_price + ( lastbuy_price * percentstep)) < current_price) {
    					//log("Last buy price " + lastbuy_price);
    					newprice = (int) (lastbuy_price + ( lastbuy_price * percentstep));
    					if (newprice == lastbuy_price) {
    						newprice++;
    					}
    					//log("Nextbuy. Price " + newprice);
    				} else {
    					//log("Buy price went above average, going back to lowest");
    					newprice = (int) ( current_price - ( current_price * maxpercent));
    				}
    				}
    				
    			}
    			
    			sleepUntil(() -> getGrandExchange().openBuyScreen(0), timeout);
    			sleepUntil(() -> getGrandExchange().addBuyItem(window.jTextField1.getText()), timeout);
    			sleep(1000);
    			sleepUntil(() -> getGrandExchange().setQuantity((int) item_count), timeout);
    			sleep(1000);
    			sleepUntil(() -> getGrandExchange().setPrice(newprice), timeout);
    			sleep(2000);
    			sleepUntil(() -> getGrandExchange().confirm(), timeout);
    			lastbuy_price = newprice;
    			buystart = Instant.now();
    			sleep(500);
    			if(antiban.doRandom()) {
                    log("Script-specific random flag triggered");
                    getGrandExchange().close();
                    sleep(Calculations.random(5000,15000));
        		}
    			break;
    		case COLLECTINGBUY:
    			//log("Collectingbuy");
    			lastbought = items[0].getTransferredValue();
    			//log("Lastbought price: " + lastbought);
    			bought++;
    			//log("Bought: " + bought);
    			adddbhistory(getLocalPlayer().getName(), "buy", Instant.now(), lastbought, window.jTextField1.getText());
    			sleepUntil(() -> getGrandExchange().collect(), timeout);
    			sleep(3000);
    			
    			sleep(1000);
    			//firstbuy=true;
    			lastbought_bool=true;
    			break;
    		case CANCELLINGBUY:
    			//log("CAncelling buy");
    			sleepUntil(() -> getGrandExchange().cancelOffer(0), timeout);
    			sleepUntil(() -> getGrandExchange().goBack(), timeout);
    			sleepUntil(() -> getGrandExchange().collect(), timeout);
    			sleep(1000);
    			lastbought_bool = false;
    			break;
    		case SELLING:
    			//log("Selling");
    			//log("Average price: " + current_price);
    			if (firstsell) {
    				newprice = (int) ( current_price + ( current_price * maxpercent));
    				//log("Firstsell. Price " + newprice);
    				firstsell = false;
    			} else {
    				
    				if (lastsold_bool) {
    					newprice = lastsold;
    				} else {
    				
    				if ((lastsell_price - ( lastsell_price * percentstep) > current_price)) {
    					//log("Last sell price " + lastsell_price);
    					newprice = (int) (lastsell_price - ( lastsell_price * percentstep));
    					//log("Nextsell. Price " + newprice);
    					if (newprice == lastsell_price) {
    						newprice--;
    					}
    				} else {
    					//log("Sell price went below average, going back to highest");
    					newprice = (int) ( current_price + ( current_price * maxpercent));
    				}
    				}
    			}
    			
    			sleepUntil(() -> getGrandExchange().openSellScreen(1), timeout);
    			sleepUntil(() -> getGrandExchange().addSellItem(window.jTextField1.getText()), timeout);
    			sleep(1000);
    			sleepUntil(() -> getGrandExchange().setQuantity((int) item_count), timeout);
    			sleep(1000);
    			sleepUntil(() -> getGrandExchange().setPrice(newprice), timeout);
    			sleep(2000);
    			sleepUntil(() -> getGrandExchange().confirm(), timeout);
    			lastsell_price = newprice;
    			sellstart = Instant.now();
    			sleep(500);
    			break;
    		case COLLECTINGSELL:
    			//log("Collecting sell");
    			lastsold = items[1].getTransferredValue();
    			//log("Last sold price: " + lastsold);
    			sold++;
    			//log("Sold: " + sold);
    			sleepUntil(() -> getGrandExchange().collect(), timeout);
    			sleep(3000);
    			adddbhistory(getLocalPlayer().getName(), "sell", Instant.now(), lastsold, window.jTextField1.getText());
    			sleep(1000);
    			//firstsell=true;
    			lastsold_bool = true;
    			break;
    		case CANCELLINGSELL:
    			//log("cancelling sell");
    			sleepUntil(() -> getGrandExchange().cancelOffer(1), timeout);
    			sleepUntil(() -> getGrandExchange().goBack(), timeout);
    			sleepUntil(() -> getGrandExchange().collect(), timeout);
    			sleep(1000);
    			lastsold_bool = false;
    			break;
    		case READY:
    			//log("ready");
    			break;
    		case UPDATEPRICE:
    			//log("Updating prices..");
    			window.status.setText("Updating item prices..");
    			current_price = (int) getItemPrice(item_id);
    			sleep(500);
    			last_price_update = Instant.now();
    			
    			break;
    		
    		
    		}
    	}
    	updatedbstatus(getLocalPlayer().getName(), current_status);
    	return antiban.antiBan();
    }
    
    
    @Override
    public void onExit() {
        	running = false;
            log("Stopping!");
            mainwindow.setVisible(false);
            mainwindow.dispose();
            resethistory(getLocalPlayer().getName());
        }
    
        
	


}
