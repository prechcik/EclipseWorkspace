package scripts;
 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.input.Mouse;

import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSGEOffer.TYPE;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.api.util.abc.*;

import scripts.mwindow;
 
@ScriptManifest(authors = { "Prech" }, category = "Money Making", name = "GE Flipper")
public class Flipper extends Script{
	
	private final String serverIP = "localhost/botnetwork";
	
	private enum status { IDLE, OPENWINDOW, BUYING, COLLECTINGBUY, CANCELBUY, SELLING, COLLECTINGSELL, CANCELSELL, ANTIBAN, UPDATEPRICES, UPDATEMONEY };
	
	private boolean running;
	private int item_id;
	private int current_price;
	private int item_count;
	private Float maxpercent;
	private Float percentstep;
	private int itemdelay;
	private int minbuy, maxbuy, minsell, maxsell;
	private status current_status;
	private RSGEOffer buyoffer;
	private RSGEOffer selloffer;
	private mwindow window;
	private Instant buyoffer_time;
	private Instant selloffer_time;
	private int lastbuy_price;
	private int lastsell_price;
	private int lastbought_price;
	private int lastsold_price;
	private boolean firstbuy;
	private boolean firstsell;
	private int curr_buy;
	private int curr_sell;
	private String playername = Player.getRSPlayer().getName();
	private ABCUtil util = new ABCUtil();
	Instant pricetimer;
	private RSTile GETile1 = new RSTile(3168,3493);
	private RSTile GETile2 = new RSTile(3161,3486);
	private RSArea GEArea = new RSArea(GETile1, GETile2);
	private boolean bought = false;
	private boolean sold = false;
	private Instant moneytimer;
	private Instant starttime;
	private boolean buying = false;
	private boolean selling = false;
	private JFrame mainwindow;
	private int online = 0;
	private int lastonline = 0;
	
	private boolean toggle_bot(boolean status) {
		if (status == true) {
			return false;
		} else {
			return true;
		}
	}
	
	
	 @Override
	    public void run() {
		 General.useAntiBanCompliance(true);
		 
		 //println(Player.getRSPlayer().getName());

		 	running = false;
		 	
	        println("Script started");
	        mainwindow = new JFrame("GE Flipper" + playername);
			window = new mwindow();
			mainwindow.setContentPane(window.mainPanel);
			JButton startbtn = window.startbtn;
			startbtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!running) {
						firstbuy = true;
						firstsell = true;
						running = toggle_bot(running);
						pricetimer = Instant.now();
						println("Starting!");
						window.startbtn.setText("Stop");
						item_id = getItemID(window.jTextField1.getText());
						current_price = (int) getItemPrice(item_id);
						item_count = (int) window.itemamount.getValue();
						maxpercent = new Float((float) window.maxpercent.getValue() /100);
						percentstep = new Float((float) window.percentstep.getValue() /100);
						itemdelay = (int) window.itemdelay.getValue();
						maxbuy = current_price;
						minsell = current_price;
						minbuy = (int) (current_price - (current_price * maxpercent));
						maxsell = (int) (current_price + (current_price * maxpercent));
						println("Item: " + window.jTextField1.getText() + ", Max percent: " + maxpercent + ", Step: " + percentstep + ", Minimum buy: " + minbuy + ", Max sell: " + maxsell);
						println("running: " + running);
						lastbought_price = 0;
						lastsold_price = 0;
						lastbuy_price = 0;
						lastsell_price = 0;
						
						window.jTextField1.setEnabled(false);
						window.itemamount.setEnabled(false);
						window.maxpercent.setEnabled(false);
						window.percentstep.setEnabled(false);
						window.itemdelay.setEnabled(false);
						
						
						starttime = Instant.now();
						start_time(playername, starttime);

					} else {
						println("Stopping!");
						window.startbtn.setText("Start");
						window.jTextField1.setEnabled(true);
						window.itemamount.setEnabled(true);
						window.maxpercent.setEnabled(true);
						window.percentstep.setEnabled(true);
						window.itemdelay.setEnabled(true);
						running = toggle_bot(running);
						println("running: " + running);

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
			        }
			    }
			});
			
			firstbuy = true;
			firstsell = true;
			updatemoney(playername, Inventory.getCount("Coins"));
			moneytimer = Instant.now();
			
			
			
			
			
			
			
			mainwindow.setVisible(true);
			while (mainwindow.isShowing()) {
				
					if (running) {
						//println("Running");
						current_status = getStatus(); 
						//println("Status: " + current_status);
						//println("Duration: " + Duration.between(pricetimer, Instant.now()).toMillis()/1000);
						if ((Login.getLoginState() == Login.STATE.INGAME) || (Login.getLoginState() == Login.STATE.WELCOMESCREEN)) {
							online = 1;
						} else {
							online = 0;
						}
						
						if (online != lastonline) {
							updateonline(playername, 1);
						}
						
						switch (current_status) {
						case OPENWINDOW:
							RSNPC[] GENPCs = NPCs.findNearest("Grand Exchange Clerk");
							GENPCs[0].click("Exchange Grand Exchange Clerk");
							window.status.setText("Opening GE Window");
							break;
						case BUYING:
							buying = true;
							window.status.setText("Buying..");
							if (firstbuy) {
								
								
								if (lastbought_price > 0) {
									curr_buy = lastbought_price;
									println("Concurrent buy, price: " + curr_buy);
								} else {
									curr_buy = minbuy;
									println("First buy, price: " + curr_buy);
									if (lastsold_price > 0) {
										if (curr_buy >= lastsold_price) {
											curr_buy = (int) (lastbought_price - (lastbought_price * maxpercent));
											println("Somehow first buy price is bigger than last sold price - setting sold price as average for now");
										}
									}
									
								}
								
								if (curr_buy == 0) {
									curr_buy = minbuy;
								}
								
								boolean buy_success = GrandExchange.offer(window.jTextField1.getText(), curr_buy, item_count, false);
								if (buy_success) {
									println("Added item");
									buyoffer_time = Instant.now();
									lastbuy_price = curr_buy;
									firstbuy = false;
								}
							} else {
								if (bought) {
									curr_buy = (int) (lastbought_price + (lastbought_price * percentstep));
									println("Concurrent buy, price: " + curr_buy);
									bought = false;
									
								} else {
									curr_buy = (int) (lastbuy_price + (lastbuy_price * percentstep));
									println("Next buy, price: " + curr_buy);
								}
								
								if (curr_buy == 0) {
									curr_buy = minbuy;
								}
								if (curr_buy > lastsold_price) {
									curr_buy = lastbought_price;
								}
								if (curr_buy%10 == 0) {
									curr_buy = curr_buy + 3;
								}
								
								boolean buy_success = GrandExchange.offer(window.jTextField1.getText(), curr_buy, item_count, false);
								if (buy_success) {
									println("Added item");
									buyoffer_time = Instant.now();
									lastbuy_price = curr_buy;
								}
							}
							
							break;
						case SELLING:
							selling = true;
							window.status.setText("Selling..");
							if (firstsell) {
								if (lastsold_price != 0) {
									curr_sell = lastsold_price;
									println("First, Concurrent sell, price: " + curr_sell);
								} else {
									curr_sell = maxsell;
									println("First sell, price: " + curr_sell);
								}
								if (lastbought_price > 0) {
									if (curr_sell < lastbought_price) {
										curr_sell = lastsell_price;
										println("Somehow the first sell price was lower than buy price, setting buy price as average for now");
									}
								}
								if (curr_sell == 0) {
									curr_sell = maxsell;
								}
								boolean sell_success = GrandExchange.offer(window.jTextField1.getText(), curr_sell, item_count, true);
								if (sell_success) {
									println("Added item");
									selloffer_time = Instant.now();
									lastsell_price = curr_sell;
									firstsell = false;
								}
								
							} else {
									curr_sell = (int) (lastsell_price - (lastsell_price * percentstep));
									println("Next sell, price: " + curr_sell);
								if (curr_sell <= lastbought_price) {
									curr_sell = lastsell_price;
								}
								if (curr_sell%10 == 0) {
									curr_sell = curr_sell - 3;
									println("Price adjustment: " + curr_sell);
								}
								//println("Next sell, price: " + curr_sell);
								boolean sell_success = GrandExchange.offer(window.jTextField1.getText(), curr_sell, item_count, true);
								if (sell_success) {
									println("Added item");
									selloffer_time = Instant.now();
									lastsell_price = curr_sell;
									
								}
							}
							
							break;
						case CANCELBUY:
							window.status.setText("Cancelling buy slot");
							println("Cancelling buy..");
							buyoffer.click();
							if (GrandExchange.cancelOffer(true)) {
								println("Cancelled");
								RSItem[] items = GrandExchange.getCollectItems();
								for (RSItem item : items) {
									item.click();
								}
								println("Collected");
								buying = false;
							}
							if (buyoffer.getStatus() == STATUS.CANCELLED) {
								println("Cancelled");
								RSItem[] items = GrandExchange.getCollectItems();
								for (RSItem item : items) {
									item.click();
								}
								println("Collected");
								buying = false;
							}
							firstbuy = false;
							
							break;
						case CANCELSELL:
							window.status.setText("Cancelling sell slot");
							println("Cancelling sell..");
							selloffer.click();
							if (GrandExchange.cancelOffer(true)) {
								println("Cancelled");
								RSItem[] items = GrandExchange.getCollectItems();
								for (RSItem item : items) {
									item.click();
								}
								println("Collected");
								selling = false;
							}
							if (selloffer.getStatus() == STATUS.CANCELLED) {
								println("Cancelled");
								RSItem[] items = GrandExchange.getCollectItems();
								for (RSItem item : items) {
									item.click();
								}
								println("Collected");
								selling = false;
							}
							firstsell = false;
							sold = false;
							break;
						case COLLECTINGBUY:
							
								
							window.status.setText("Collecting buy slot");
							println("Bought item! Collecting..");
							buyoffer.click();
							if (GrandExchange.getTransferredGP() > 0) {
							
							if (buying) {
								RSItem[] items = GrandExchange.getCollectItems();
								for (RSItem item : items) {
									item.click();
								}
								lastbought_price = GrandExchange.getTransferredGP();
								adddbhistory(playername, "buy", Instant.now(), lastbought_price, window.jTextField1.getText());
								println("Collected and sent to database!");
								
								if (Duration.between(buyoffer_time, Instant.now()).toMillis()/1000 < 15) {
									println("Item bought in less than 15 seconds, lowering price by PercentStep");
									lastbought_price = (int) (GrandExchange.getTransferredGP() - (GrandExchange.getTransferredGP() * percentstep));
								} else {
									lastbought_price = GrandExchange.getTransferredGP();
								}
								
								
								firstbuy = true;
								bought = true;
								buying = false;
							}
							}
								
							
							break;
						case COLLECTINGSELL:
							
							window.status.setText("Collecting sell slot");
							println("Sold item! Collecting..");
							selloffer.click();
							if (GrandExchange.getTransferredGP() > 0) {
							
							if (selling) {
							
								RSItem[] itemss = GrandExchange.getCollectItems();
								for (RSItem item : itemss) {
									item.click();
								}
								lastsold_price = GrandExchange.getTransferredGP();
									adddbhistory(playername, "sell", Instant.now(), lastsold_price, window.jTextField1.getText());
									println("Collected and sent to database!");
									if (firstsell == false) {
										sold = true;
									} else {
										sold = false;
									}
									
									if (Duration.between(selloffer_time, Instant.now()).toMillis()/1000 < 15) {
										println("Item sold in less than 15 seconds, raising price by PercentStep");
										lastsold_price = (int) (GrandExchange.getTransferredGP() + (GrandExchange.getTransferredGP() * percentstep));
									} else {
										lastsold_price = GrandExchange.getTransferredGP();
									}
									
									
									firstsell = true;
									
									selling = false;
								}
							}
							break;
						case IDLE:
							window.status.setText("Idle");
							
							break;
						case ANTIBAN:
							window.status.setText("Antiban");
							int rand = General.random(0,100);
							int event = General.random(0, 5);
							switch (event) {
							case 0: // Move mouse off screen
								if (rand < 5) {
										window.status.setText("Leaving mouse outside the window..");
										util.leaveGame();
								}
								break;
							case 1: // Rotate camera
								if (rand < 20) {	
										window.status.setText("Rotating camera..");
										util.rotateCamera();
								}
								break;
							case 2: // Exit the GE window for a moment
								if (rand < 15) {
									window.status.setText("Closing GE window for a moment..");
									GrandExchange.close();
									util.rotateCamera();
									General.sleep(General.random(800, 1800));
									Walking.walkTo(GEArea.getRandomTile());
									General.sleep(General.random(1000, 2000));
									util.rotateCamera();
									General.sleep(General.random(1500, 5000));
								}
								break;
							case 3: // pickup mouse
								if (rand < 30) {
										window.status.setText("Picking up the mouse..");
										util.pickupMouse();
								}
								break;
							case 4: // Move mouse
								if (rand < 50) {
									util.moveMouse();
								}
								
								break;
							case 5: // Idle
								if (rand > 50) {
									current_status = status.IDLE;
								}
								break;
							}
							break;
						case UPDATEPRICES:
								window.status.setText("Updating item prices..");
									current_price = (int) getItemPrice(item_id);
									println("Using RSBuddy average prices");
								maxbuy = current_price;
								minsell = current_price;
								minbuy = (int) (current_price - (current_price * maxpercent));
								maxsell = (int) (current_price + (current_price * maxpercent));
								pricetimer = Instant.now();
								General.sleep(General.random(800, 2000));
							break;
						case UPDATEMONEY:
								int totalgold = 0;
								if (selling) {
									totalgold = Inventory.getCount("Coins")+lastsell_price;
								} else if (buying) {
									totalgold = Inventory.getCount("Coins")+lastbuy_price;
								} else {
									totalgold = Inventory.getCount("Coins");
								}
								updatemoney(playername, totalgold);
								moneytimer = Instant.now();
							break;
						
						
						
						}
						
						//updatedbstatus(playername, current_status);
						General.sleep(General.random(500, 1500));
					
					} else {
						General.sleep(General.random(500, 1500));
					}
				
				
				
				
			}

	       
	    }
	 
	 void onEnd() {
		 mainwindow.setVisible(false);
		 mainwindow.dispose();
	 }
	
	
	private status getStatus() {
		
		if (running) {
			
			if (Duration.between(pricetimer, Instant.now()).toMillis()/1000 > 5*60) {
				return status.UPDATEPRICES;
			}
			if ((Duration.between(moneytimer, Instant.now()).toMillis()/1000) > 10*60) {
							window.status.setText("Updating gold status..");
							return status.UPDATEMONEY;

			}
			
			//println(GrandExchange.getWindowState());
			
			if (GrandExchange.getWindowState() == null) {
				return status.OPENWINDOW;
			} else {
				
				if (GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.OFFER_WINDOW) {
					if (GrandExchange.getType() == TYPE.BUY) {
						if (GrandExchange.getStatus() == STATUS.COMPLETED) {
							return status.COLLECTINGBUY;
						}
						if (GrandExchange.getStatus() == STATUS.CANCELLED) {
							return status.CANCELBUY;
						}
						if (Duration.between(buyoffer_time, Instant.now()).toMillis()/1000 >= itemdelay) {
							return status.CANCELBUY;
						}
					}
					if (GrandExchange.getType() == TYPE.SELL) {
						if (GrandExchange.getStatus() == STATUS.COMPLETED) {
							return status.COLLECTINGSELL;
						}
						if (Duration.between(selloffer_time, Instant.now()).toMillis()/1000 >= itemdelay) {
							return status.CANCELSELL;
						}	
					}
				}
				
				
				int buyoffers = 0;
				int selloffers = 0;
				RSGEOffer[] offers = GrandExchange.getOffers();
				for(RSGEOffer offer : offers) {
					if (offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
					if (offer.getType() == RSGEOffer.TYPE.BUY) {
						buyoffer = offer; 
						buyoffers++;
					} else if (offer.getType() == RSGEOffer.TYPE.SELL) {
						selloffer = offer;
						selloffers++;
					}
					}
				}
				
				
				
				
				
				if (buyoffers == 0) {
					if (Inventory.getCount(window.jTextField1.getText()) < 1) {
						if (selloffers == 0) {
						if (Inventory.getCount("Coins") > current_price) {
							return status.BUYING;
						}
						}
					}
				} else {
					switch (buyoffer.getStatus()) {
					case IN_PROGRESS:
						if (Duration.between(buyoffer_time, Instant.now()).toMillis()/1000 >= itemdelay) {
							return status.CANCELBUY;
						}		
						break;
					case COMPLETED:
						if (GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
							return status.COLLECTINGBUY;
						}
						break;
					case CANCELLED:
						return status.CANCELBUY;
					}
				}
				
				if (selloffers == 0) {
					if (Inventory.getCount(window.jTextField1.getText()) > 0) {
						return status.SELLING;
					}
				} else {
					
					switch (selloffer.getStatus()) {
					case IN_PROGRESS:
						if (Duration.between(selloffer_time, Instant.now()).toMillis()/1000 >= itemdelay) {
							return status.CANCELSELL;
						}	
						break;
					case COMPLETED:
						if (GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
							return status.COLLECTINGSELL;
						}
						break;
					case CANCELLED:
						return status.CANCELSELL;
					
					
					}
				}
				
				if (current_status == status.IDLE) {
					int rand = General.random(0, 100);
					if (rand < 25) {
						return status.ANTIBAN;
						
					}
				}
				
				
				
				}
			
				
			
			
		}
		
		return status.IDLE;
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
    	println("Exception occurred at getPrice." + e);
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
    	println("Exception occurred at getID." + e);
    	}

    	return 0;
    	}
	
	private int checkaverage(String bot, String item) {
		//window.status.setText("Updating current item price..");
		item = item.replace(" ", "%20");
		bot = bot.replace(" ", "%20");
		bot = bot.replaceAll("\\W", "%20");
		//log("Item: " + item);
    	try {
    	String webPage = "http://" + serverIP + "/checkaverage.php?bot=" + bot + "&item=" + item;
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
    	println("Exception occurred at getID." + e);
    	}

    	return 0;
    	}
	
	
	private int adddbhistory(String botname, String type, Instant date, int price, String itemname) {
    	try {
    		botname = botname.replace(" ", "%20");
    		botname = botname.replaceAll("\\W", "%20");
    		itemname = itemname.replace(" ", "%20");
    	String webPage = "http://" + serverIP + "/addbothistory.php?bot=" + botname + "&type=" + type + "&date=" + date.toEpochMilli()/1000 + "&price=" + price + "&itemname=" + itemname;
    	//println("Historypage: " + webPage);
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
    	println("Exception occurred at adddbhistory." + e);
    	}

    	return 0;
    	}
	
	private int updatedbstatus(String botname, status status) {
    	try {
    		botname = botname.replace(" ", "%20");
    		botname = botname.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/updatestatus.php?bot=" + botname + "&status=" + status;
    	
    	//println("Updatepage: " + webPage);
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
    	println("Exception occurred at updatedbstatus." + e);
    	}

    	return 0;
    	}
	
	private int resethistory(String botname) {
    	try {
    		botname = botname.replace(" ", "%20");
    		botname = botname.replaceAll("\\W", "%20");
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
    	println("Exception occurred at botclosing." + e);
    	}

    	return 0;
    	}
	
	
	private int updateonline(String botname, int online) {
    	try {
    		botname = botname.replace(" ", "%20");
    		botname = botname.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/setonline.php?bot=" + botname + "&online=" + online;
    	
    	//println("Updatepage: " + webPage);
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
    	println("Exception occurred at updatedbstatus." + e);
    	}

    	return 0;
    	}
	
	private int updatemoney(String botname, int money) {
    	try {
    		botname = botname.replace(" ", "%20");
    		botname = botname.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/updatemoney.php?bot=" + botname + "&money=" + money;
    	
    	//println("Updatepage: " + webPage);
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
    	println("Exception occurred at updatedbstatus." + e);
    	}

    	return 0;
    	}
	
	private int resetmoney(String botname) {
    	try {
    		botname = botname.replace(" ", "%20");
    		botname = botname.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/resetmoney.php?bot=" + botname;
    	
    	//println("Updatepage: " + webPage);
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
    	println("Exception occurred at updatedbstatus." + e);
    	}

    	return 0;
    	}
	
	private int start_time(String botname, Instant time) {
    	try {
    		botname = botname.replace(" ", "%20");
    		botname = botname.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/startbot.php?bot=" + botname + "&time=" + time.toEpochMilli()/1000;
    	
    	//println("Updatepage: " + webPage);
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
    	println("Exception occurred at updatedbstatus." + e);
    	}

    	return 0;
    	}
	
	
	
	
	
	
	
	
	
	
	
	
       
}
