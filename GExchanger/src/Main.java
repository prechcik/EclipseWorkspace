import org.dreambot.api.script.AbstractScript;  // All these imports are from the dreambot API

import org.dreambot.api.script.ScriptManifest;  // API can be found here: https://dreambot.org/javadocs/
import org.dreambot.api.utilities.impl.Condition;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.dreambot.api.methods.container.AbstractItemContainer;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.*;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.path.impl.LocalPath;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


@ScriptManifest(author = "Prech", name = "GExchanger", version = 0.2, description = "Harrasing people's wallets", category = Category.MONEYMAKING)
public class Main
extends AbstractScript {	
	
	private final String serverIP = "167.114.98.62";
	private final Area GE_Area = new Area(3161,3493,3168,3485);
	private int player_cash = 0;
	private final int min_cash = 20;
	private String item_to_trade = "Uncut sapphire";
	private int item_id = 1623;
	private int item_quantity = 1;
	private int delay = 1; // in minutes
	private final int antiban_percent = 15;
	private long startTime = 0;
	private long runTime = 0;
	private status state = null;
	private String status_text = "";
	private final Color color1 = new Color(51, 51, 0);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(255, 255, 255);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 11);
    private final Font font2 = new Font("Arial", 1, 11);
    private final Font font3 = new Font("Arial", 1, 15);
    private final double first_percent = 4.0;
    private final double next_percent = 0.3;
    private int profit = 0;
    private int lookup_price = 0;
    private int lookup_counter = 0;
    private int last_buy_price = 0;
    private int last_sell_price = 0;

	

	
	private enum status {
		READY, WALKTOGE, ANTIBAN, DELAY, SELL, BUY, IDLE
	}
	
	private status getState(Player curplayer) throws IOException {
		
		
		
		
		//if your inventory is full, you should either be walking to the bank or banking
			int i = Calculations.random(1,100);
			if (i <= antiban_percent) {
				return status.ANTIBAN;
			}
			
			if (!GE_Area.contains(getLocalPlayer())) {
				return status.WALKTOGE;
			} else {
				if (getGrandExchange().isOpen()) {
					if ((getInventory().count(item_to_trade) > 0)) {
						log("SELL!");
						
						return status.SELL;
					} else {
						log("BUY!");
						
						return status.BUY;
					}
				} else {
					getGrandExchange().open();
					return status.READY;
				}
			}
			
			
			

	}
	
	
	private long getItemPrice(int itemID) {
    	try {
    	String webPage = "http://" + serverIP + "/getprice.php?i=" + itemID;
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

    	return Long.parseLong(result);
    	} catch (Exception e) {
    	log("Exception occurred at getPrice." + e);
    	}

    	return 0;
    	}
	
	
	@Override
    public void onStart() {
		log("Starting!");	 // This will display in the CMD or logger on dreambot
		startTime = System.nanoTime();
		lookup_price = (int) getItemPrice(item_id);
    }
 
    @Override
    public int onLoop() {
    	
    	
    	lookup_counter++;
    	if (lookup_counter == 60) {
    		lookup_price = (int) getItemPrice(item_id);
    		lookup_counter = 0;
    	}
    	player_cash = getInventory().count("Coins");
    	
    	
    	if (getGrandExchange().isReadyToCollect()) {
    		getGrandExchange().collect();
    		sleep(Calculations.random(100, 400));
    	}
    	
    	long curtime = System.nanoTime();
    	runTime = curtime - startTime;
    	runTime = runTime / 1000000000;
    	
    	//log(state.toString());
		if(getLocalPlayer().isMoving()){
			Tile dest = getClient().getDestination();
			if(dest != null && getLocalPlayer().getTile().distance(dest) > 5){
				return Calculations.random(200,400);
			}
		}
		//we should add a run energy check too! see all the fun things we think of
		if(!getWalking().isRunEnabled() && getWalking().getRunEnergy() > Calculations.random(20, 50)){
			getWalking().toggleRun();
		}
		//so you want to start by setting your current state
		
		try {
			state = getState(getLocalPlayer());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			log(e1.toString());
		}
		
		
		
		switch(state){
		
		case WALKTOGE:
			status_text = "Walking to Grand Exchange..";
			//so this is our bank state, we know the gist of banking so we can do this no problem
			if(GE_Area.contains(getLocalPlayer())){
				
				getGrandExchange().open();
				
				
				//if the bank is open, just deposit everything.
				
				//woo another sleepUntil time
				
				//this sleepUntil will sleep until your inventory is empty.
			}
			else{
				//if it isn't open, open it!
				getWalking().walk(GE_Area.getRandomTile());
				//now we get into our first sleepUntil, this takes a Condition and a timeout as arguments
				//a Condition is an object that has a verify method, when it returns true the sleepUntil cuts out
				//or it'll wait until you reach the timeout
				sleepUntil(new Condition(){
					public boolean verify(){
						return GE_Area.contains(getLocalPlayer());
					}
				},Calculations.random(900,1200));
				//so in that condition we set it to go until the bank is open, or 900-1200 milliseconds has passed
			}
			//the reak statement is required after each case otherwise it'll spill over into the next
			//this says to break out of your switch/case and to not continue on to the others.
			break;
		case BUY:
			if (getGrandExchange().isOpen()) {
				if (getInventory().count("Coins") >= lookup_price) {
				status_text = "Buying..";
				log("Current RSBuddy price: " + lookup_price);
					int adjustedprice = (int) (lookup_price - (lookup_price * (first_percent / 100)));
					last_buy_price = adjustedprice;
					log("Adjusted the price: " + last_buy_price);
					status_text = "Buying " + item_to_trade + " for " + adjustedprice + " gold. First pass!";
					log("Buying " + item_to_trade + " for " + adjustedprice + " gold. First pass!");
					getGrandExchange().buyItem(item_to_trade, item_quantity, adjustedprice);
					sleep(Calculations.random(600,1400));
					break;
				} else {
					log("No more coins to flip with! Exiting script!");
					stop();
				}

			} else {
				getGrandExchange().open();
				break;
			}
		case SELL:
			log("Sell case?");
			if (getGrandExchange().isOpen()) {
				if (getInventory().count(item_to_trade) > 0) {
				status_text = "Selling..";
					log("GE Open!");
					log("Current RSBuddy price: " + lookup_price);
					int adjustedprice = (int) (lookup_price + (lookup_price * (first_percent / 100)));
					last_sell_price = adjustedprice;
					log("Adjusted the price: " + last_sell_price);
					status_text = "Selling " + item_to_trade + " for " + adjustedprice + " gold. First pass!";
					log("Selling " + item_to_trade + " for " + adjustedprice + " gold. First pass!");
					getGrandExchange().sellItem(item_to_trade, item_quantity, adjustedprice);
					sleep(Calculations.random(600,1400));
					break;
				} else {
					log("No more items to sell! Back to buying!");
					break;
				}
			} else {
				log("Opening GE!");
				getGrandExchange().open();
				break;
			}
		case ANTIBAN:
			status_text = "ANTIBAN";
			break;
		case READY:
			status_text = "Ready!";
			if (state == status.READY && (getInventory().count(995) > min_cash)) {
				log("Ready and has more than 20 gold!");
				if (!getGrandExchange().isOpen()) {
					getGrandExchange().open();
				}
			
			}
			break;
		case DELAY:
			log("Sleeping!");
			status_text = "Delay..";
			sleep(Calculations.random(550, 1450));
			break;
		default:
			break;
		
		
		}
		
		
		
		
    	return Calculations.random(425,1241); // basic return value. 
    }
    
 
       
    @Override
    public void onExit() {
    	log("Stopping!"); // this will display in the CMD or logger on dreambot
    }
    
    public void onPaint(Graphics g1) {
    	Graphics2D g = (Graphics2D)g1;
        g.setColor(color1);
        g.fillRect(7, 345, 506, 129);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRect(7, 345, 506, 129);
        g.setFont(font1);
        g.setColor(color3);
        g.drawString("Trading item:", 12, 361);
        g.drawString("Average RSBuddy price:", 12, 377);
        g.drawString("RSBuddy price updated:", 12, 393);
        g.drawString("Profit so far:", 12, 409);
        g.drawString("Time running:", 12, 424);
        g.setFont(font2);
        g.drawString(item_to_trade, 76, 361);
        g.drawString(String.valueOf(lookup_price), 135, 377);
        g.drawString(String.valueOf(lookup_counter) + " seconds ago", 131, 393);
        g.drawString(String.valueOf(profit), 75, 409);
        g.drawString(String.valueOf(runTime), 78, 424);
        g.setFont(font1);
        g.drawString("Status:", 13, 439);
        g.setFont(font2);
        g.drawString(status_text, 50, 439);
        g.setFont(font3);
        g.drawString("Cash:", 318, 368);
        g.drawString(String.valueOf(player_cash), 361, 368);


    }
    
    
  
    

    
    
}