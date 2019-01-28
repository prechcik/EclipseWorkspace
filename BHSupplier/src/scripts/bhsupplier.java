package scripts;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.GrandExchange.COLLECT_METHOD;
import org.tribot.api2007.GrandExchange.WINDOW_STATE;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Trading;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;

import scripts.SupplierGUI;

public class bhsupplier extends Script {
	
	private String serverIP = "prech.info/botnetwork/";
	private int curr_world = 0;
	
	private JFrame mainwindow;
	private SupplierGUI window;
	private String playername;
	private boolean running = false;
	private boolean muletask = false;
	private boolean buying = false;
	private Instant relogtimer;
	private int mintime;
	private RSArea GEArea = new RSArea(new RSTile(3161,3493), new RSTile(3168,3486));
	private RSTile GETile = new RSTile(3165,3486);
	private DefaultListModel buylist = new DefaultListModel();
	private Instant slot1time;
	private Instant slot2time;
	private Instant slot3time;
	private List<String> blist;
	private int bought = 0;
	private Instant lastbreak;
	private Instant breakend;
	private Instant logtime;
	private boolean onbreak = false;
	
	private enum status {
		IDLE, OPENGE, BUYTASK, MULETASK, AFK
	}
	private status curr_status;
	@Override
	public void run() {
		mainwindow = new JFrame("BotHeaven - Supplier - Bot: ");
		window = new SupplierGUI();
		mainwindow.setContentPane(window.mainpanel);
		lastbreak = Instant.now().minusSeconds(4 * 60 * 60);
		logtime = Instant.now();
		JButton savesettings = window.jButton1;
		savesettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = System.getenv("APPDATA") + "\\.tribot\\BHSupplier.dat";
			        try (Writer writer = new BufferedWriter(
			                new OutputStreamWriter(new FileOutputStream(filename), "utf-8"))) { // sets the output where to write
			                    writer.write(savesettings());
			        }

			        catch (IOException e1) {
			        	println("Exception at savesettings: " + e1);
			        }
			}});
		
		JButton buyadd = window.additem_addbtn;
		buyadd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (window.additem_field.getText() != "" && (int) window.additem_price.getValue() > 0 && (int) window.additem_stack.getValue() > 0) {
					String item = window.additem_field.getText();
					int maxbuyprice = (int) window.additem_price.getValue();
					int amount = (int) window.additem_stack.getValue();
					
					String itemtoadd = item + "|" + maxbuyprice + "|" + amount;
				
					buylist.addElement(itemtoadd);
					window.buylist.setModel(buylist);
				} else {
					JOptionPane.showMessageDialog(window, "Please fill out all the fields!");
				}
			}
		});
		
		
		JButton buyremove = window.additem_rembtn;
		buyremove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedindex = window.buylist.getSelectedIndex();
				buylist.remove(selectedindex);
				window.buylist.setModel(buylist);
			}
		});
		
		JButton scriptstart = window.startbtn;
		scriptstart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buylist = new DefaultListModel();
				
				JList<String> tempmodel = window.buylist;
				blist = new ArrayList<>();
				for(int i = 0; i< tempmodel.getModel().getSize();i++){
		            buylist.addElement(tempmodel.getModel().getElementAt(i));
		            blist.add(tempmodel.getModel().getElementAt(i));
		            
		        }
				savesettings();
				
				
				
				
				running = true;
				window.startbtn.setEnabled(false);
				window.stopbtn.setEnabled(true);
				window.jButton1.setEnabled(false);
				window.additem_addbtn.setEnabled(false);
				window.additem_price.setEnabled(false);
				window.additem_stack.setEnabled(false);
				window.additem_rembtn.setEnabled(false);
				window.additem_field.setEnabled(false);
				window.moneyrequest_amount.setEnabled(false);
				
				
			}
		});
		
		JButton scriptstop = window.stopbtn;
		scriptstop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				running = false;
				window.startbtn.setEnabled(true);
				window.stopbtn.setEnabled(false);
				window.jButton1.setEnabled(true);
				window.additem_addbtn.setEnabled(true);
				window.additem_price.setEnabled(true);
				window.additem_stack.setEnabled(true);
				window.additem_rembtn.setEnabled(true);
				window.additem_field.setEnabled(true);
				window.moneyrequest_amount.setEnabled(true);
			}
		});
		
		
		mainwindow.pack();
		mainwindow.setResizable(false);
		
		
		mainwindow.setVisible(true);
		
		
		botstart();
		
		if (Login.getLoginState().equals(Login.STATE.INGAME)) {
			playername = Player.getRSPlayer().getName().toString();
		} else {
			playername = "";
		}
		
		File file = new File(System.getenv("APPDATA") + "\\.tribot\\BHSupplier.dat");
		if (file.exists()) {
			String content = "";
			try
			{
				content = new String ( Files.readAllBytes( Paths.get(file.getPath()) ) );
				//println("Content: '" + content + "'");
				String[] array = content.split("\\|\\|");
				//println(array.toString());
				DefaultListModel tempmodel = new DefaultListModel();
				array[0] = array[0].replaceAll("\\[", "");
				array[0] = array[0].replaceAll("\\]", "");
				array[0] = array[0].replaceAll("\\, ", ",");
				//println("BuyArr: " + array[1].toString());
				String[] blist = array[0].split(",");
				if (blist.length > 0) {
					for (String element : blist) {
						tempmodel.addElement(element);
						buylist.addElement(element);
					}
				}
				window.buylist.setModel(tempmodel);
				window.moneyrequest_amount.setValue(Integer.parseInt(array[1]));
				
			} catch (IOException e1) {
				println(e1.toString());
			}
		}
		
		while (mainwindow.isShowing()) {
			
			curr_status = getstatus();
			window.scriptstatus_lbl.setText(curr_status.toString());
			if (bought >= 3) {
				RSGEOffer[] geoffers = GrandExchange.getOffers();
				if (geoffers[0].getStatus() == STATUS.EMPTY && geoffers[1].getStatus() == STATUS.EMPTY && geoffers[2].getStatus() == STATUS.EMPTY) {
					General.sleep(1000);
					GrandExchange.close();
					buying = false;
					muletask = true;
				}
			}
			
			
			switch (curr_status) {
			
			case MULETASK:
				blist.clear();
				
				Interfaces.closeAll();
				String assignedbot = dbrequest();
				if (assignedbot != "") {
					RSPlayer[] targetplayer = Players.find(assignedbot);
					if (targetplayer.length > 0) {
						if (Trading.getWindowState() == null) {
							window.scriptstatus_lbl.setText("Target player in place, attempting to trade");
							targetplayer[0].click("Trade with " + targetplayer[0].getName());
							General.sleep(General.random(1000,2000));
						} else {
							if (Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW) {
								//println("Trade window opened");
								
								RSItem[] pinv = Inventory.getAll();
								for (RSItem pitem : pinv) {
									if (!pitem.getDefinition().getName().equals("Coins")) {
										Trading.offer(pitem.getStack(), pitem.getDefinition().getName());
										General.sleep(100,200);
									}
								}
								
								
								//println("Depo: " + muletemp_depobuy + ", Gold: " + muletemp_goldcheck);
								if (Trading.getOfferedItems(false).length == buylist.getSize()) {
									Trading.accept();
								}
									
							}
							if (Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
								//println("Second trade window");
								if (Trading.getOfferedItems(false).length == buylist.getSize()) {
									if (Trading.accept()) {
										//println("Successfully deposited items and gold!");
										onbreak = true;
										blist.clear();
										buying = false;
										muletask = false;
										lastbreak = Instant.now();
										breakend = lastbreak.plusSeconds(4 * 60 * 60);
										break;
									}
								}
							}
							
					}
				} else {
					window.scriptstatus_lbl.setText(("Waiting for mule..."));
					General.sleep(General.random(800, 1600));
					break;
				}
			}
				
				
				
				break;
			case BUYTASK:
				buying = true;
				int emptyoffers = 0;
				int usedslots = 0;
				if (GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
					RSGEOffer[] slots = GrandExchange.getOffers();
					for (RSGEOffer slot : slots) {
						if (slot.getIndex() < 3) {
						if (slot.getStatus() == STATUS.EMPTY && Duration.between(logtime, Instant.now()).toMinutes() < 2) {
							for (int i=0; i < buylist.getSize(); i++) {
								String itemdetails = (String) buylist.getElementAt(i);
								String[] itemdet = itemdetails.split("\\|");
									if (!blist.contains(itemdet[0])) {
										if (GrandExchange.offer(itemdet[0], Integer.parseInt(itemdet[1]), Integer.parseInt(itemdet[2]), false)) {
											blist.add(itemdet[0]);
											General.sleep(200,400);
											break;
										}
									}
							}
						}
						if (slot.getStatus() == STATUS.IN_PROGRESS && slot.getTransferredAmount() > 1) {
							if (slot.click()) {
								General.sleep(800,1300);
								if (GrandExchange.cancelOffer(true)) {
									General.sleep(1000);
									RSItem[] items = GrandExchange.getCollectItems();
									if (GrandExchange.collectItems(COLLECT_METHOD.NOTES, items)) {
										General.sleep(200,400);
										
										bought++;
										usedslots++;
										break;
									}
								}
							}
						}
						if (slot.getStatus() == STATUS.COMPLETED) {
							if (slot.click()) {
								General.sleep(1000,1500);
								RSItem[] items = GrandExchange.getCollectItems();
								for (RSItem item : items) {
									GrandExchange.collectItems(COLLECT_METHOD.NOTES, items);
									
									General.sleep(400);
								}
								bought++;
									break;
							}
						}
						
						if (slot.getStatus() == STATUS.IN_PROGRESS && Duration.between(logtime, Instant.now()).toMinutes() >= 2) {
							RSGEOffer[] offers = GrandExchange.getOffers();
							for (RSGEOffer offer : offers) {
								if (offer.getIndex() < 3) {
								if (offer.getStatus() != STATUS.EMPTY) {
									if (slot.click()) {
										General.sleep(800,1300);
										if (GrandExchange.cancelOffer(true)) {
											General.sleep(1000);
											RSItem[] items = GrandExchange.getCollectItems();
											GrandExchange.collectItems(COLLECT_METHOD.NOTES, items);
												
												General.sleep(200,400);
												break;
										}
									}
								}
								}
							}
						}
						
						
					}
					}
					
					int emptyoff = 0;
					RSGEOffer[] geoffers = GrandExchange.getOffers();
					
					
					if (Duration.between(logtime, Instant.now()).toMinutes() >= 2) {
						if (geoffers[0].getStatus() == STATUS.EMPTY && geoffers[1].getStatus() == STATUS.EMPTY && geoffers[2].getStatus() == STATUS.EMPTY) {
							General.sleep(1000);
							GrandExchange.close();
							buying = false;
							muletask = true;
							break;
						}
					}
					
				}
				
				if (GrandExchange.getWindowState() == WINDOW_STATE.OFFER_WINDOW) {
					General.sleep(1000,2000);
					RSItem[] items = GrandExchange.getCollectItems();
					if (GrandExchange.collectItems(COLLECT_METHOD.NOTES, items)) {
						General.sleep(200,400);
						
						bought++;
						usedslots++;
						break;
					}
				}
				
				
				if (GrandExchange.getWindowState() == null && buying && !muletask) {
					RSNPC[] GENpc = NPCs.findNearest("Grand Exchange Clerk");
					GENpc[0].click("Exchange " + GENpc[0].getName());
				}
				
				break;
			case OPENGE:
					WebWalking.walkTo(GETile);
				break;
			case AFK:
				int timeleft = (int) Duration.between(breakend, lastbreak).toMillis()/1000;
				window.scriptstatus_lbl.setText("On a 4 hour break... Time left: " + String.format("%d:%02d:%02d", timeleft / 3600, (timeleft % 3600) / 60, (timeleft % 60)) );
				this.setLoginBotState(false);
				if (timeleft < 1) {
					onbreak = false;
					blist.clear();
					buying = false;
					muletask = false;
					this.setLoginBotState(true);
					
				}
				
				break;
			case IDLE:
				
				break;		
			}
			
			
			
			General.sleep(800,1600);
		}
	}
	
	
private status getstatus() {
	
	if (Login.getLoginState().equals(Login.STATE.INGAME)) {
		
		if (running) {
			
			if (onbreak) {
				return status.AFK;
			}
			
			if (GEArea.contains(Player.getRSPlayer())) {
				if (!muletask && Inventory.getCount("Coins") > 1) {
					buying = true;
					return status.BUYTASK;
				}
				if (!buying && muletask) {
					return status.MULETASK;
				}
			} else {
				return status.OPENGE;
			}
			
			
		}
		
	} else {
		if (!Login.login()) {
			if (Login.getLoginMessage() != null) {
				println("Error logging in... Error: " + Login.getLoginMessage());
			}
		}
	}
	
	
	
	return status.IDLE;
}
	
	
	
	
	
	
	
private void botstart() {
		
		try {
			String pname = Player.getRSPlayer().getName().toString();
    		pname = pname.replace(" ", "%20");
    		pname = pname.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/bhstart.php?bot=" + pname + "&type=supplier&world=" + curr_world + "&status=" + curr_status;
    	
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
    	} catch (Exception e) {
    	println("Exception occurred at botstart." + e);
    	}
	}

private String savesettings() {
	

	
	String finalstring = "";
	
	finalstring += (buylist + "||");
	finalstring += (window.moneyrequest_amount.getValue().toString());
	
	return finalstring;
}

private String dbrequest() {
	
	try {
		String pname = playername;
		pname = pname.replace(" ", "%20");
		pname = pname.replaceAll("\\W", "%20");
		String locationn = "Grand Exchange";
		locationn = locationn.replace(" ", "%20");
		
		int requestamount = (int) window.moneyrequest_amount.getValue() - Inventory.getCount("Coins");
		int currentworld = Game.getCurrentWorld();
        
	String webPage = "http://" + serverIP + "bhrequest.php?bot=" + pname + "&request=bank&world=" + currentworld + "&loc=" + locationn + "&requestitems=Coins&requestamount=" + requestamount;
	
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
	return result;
	} catch (Exception e) {
	println("Exception occurred at dbrequest." + e);
	}
	return "";
}


}