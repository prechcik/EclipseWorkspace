package scripts;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api.util.Screenshots;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.LOGIN_MESSAGE;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.util.Util;
import org.tribot.api.interfaces.*;
import scripts.BuyerGUI;
import scripts.NWorldHopper;

 
@ScriptManifest(authors = { "Prech" }, category = "Money Making", name = "BH - Buyer")
public class buyer extends Script implements Arguments {
	
	private JFrame mainwindow;
	private BuyerGUI window;
	private String current_tab = "";
	private String npcname = "";
	private String playername;
	private String serverIP = "localhost/botnetwork/";
	private DefaultListModel buy_list = new DefaultListModel();
	private DefaultListModel sell_list = new DefaultListModel();
	private Instant itemtimer;
	private int selectedindex = 0;
	private String selectednpc = "";
	private boolean running = false;
	private String[] buyl;
	private String[] selll;
	private boolean mule_enabled = false;
	private String mulenick = "";
	private int minmoney = 0;
	private String exchangeplace = "";
	private int currentworld = 0;
	ABCUtil util;
	private int buypass = 0;
	private int sellpass = 0;
	private int step = 1;
	private RSTile startingpoint;
	private String prevworldset = "";
	private Instant breaktime;
	
	private RSArea area_ge = new RSArea(new RSTile(3167,3487), new RSTile(3162,3492));
	private RSArea area_varrocksq = new RSArea(new RSTile(3209,3432), new RSTile(3217,3423));
	private RSTile tile_ge = new RSTile(3164,3484);
	private RSTile tile_varrocksq = new RSTile(3217,3423);
	private boolean mulesetting_goldcheck = false;
	private int mulesetting_goldlessthan = 0;
	private int mulesetting_goldmorethan = 0;
	private boolean soldall = false;
	private boolean requestitems = false;
	private int requestamount = 0;
	private boolean mulesetting_depobuy = false;
	private int muletemp_depobuy = 0;
	private int muletemp_goldcheck = 0;
	private int muletemp_sellitems = 0;
	private boolean muletask_active = false;
	private ArrayList<String> wl;
	private ArrayList<Integer> usedworlds = new ArrayList<Integer>();
	private int worldindex = 0;
	private boolean hopping = false;
	private ArrayList<String> shuffledwl;
	private RSArea varrockshop_deadzone = new RSArea(new RSTile(3235,3426), new RSTile(3239,3425));
	private RSTile varrockshop_deaddoor = new RSTile(3234,3426);
	private RSArea portsarimroom = new RSArea(new RSTile(3015,3244), new RSTile(3011,3248));
	private RSArea rimmingtonroom = new RSArea(new RSTile(2954,3204), new RSTile(2959,3202));
	private RSArea varrockshop = new RSArea(new RSTile(3230,3426), new RSTile(3235,3421));
	private String shoploc;
	private boolean finishedbuying = false;
	private boolean finishedselling = false;
	
	private enum status { IDLE, SETTINGWINDOW, MULETASK, OPENNPCTRADE, BUY, SELL, WORLDHOP, BREAK, SCREENSHOT, BREAKBOT, BANNED }
	private status curr_status;
	
	private int amounttobuy = 0;
	private int amounttosell = 0;
	private DynamicClicking clicker = new DynamicClicking();
	private String settings = "";
	private boolean autostart = false;
	private String worldset;
	private RSTile gepos1 = new RSTile(3164,3487);
	private RSTile gepos2 = new RSTile(3167,3488);
	private RSTile gepos3 = new RSTile(3165,3492);
	private RSTile gepos4 = new RSTile(3162,3489);
	private RSTile[] gepos = { gepos1, gepos2, gepos3, gepos4 };
	private Instant screenshottime;
	private int screenshotminutes = 10;
	private int online = 0;
	private Instant statustime;
	private long starttime = 0;
	private RSArea varrockarrowshop = new RSArea(new RSTile(3230,3426), new RSTile(3234,3420));
	
	private int[] f2pworlds = {
			301,308,316,326,335,379,380,382,383,384,393,394,397,398,399,418,426,430,431,433,434,435,436,437,438,439,440,451,452,453,454,455,456,457,458,459,469,470,471,472,473,474,475,476,477,497,498,499,500,501,502,503,504
	};
	
	@Override
	public void run() {
		if (Login.getLoginState() == Login.STATE.LOGINSCREEN) {
			online = 0;
		} else {
			online = 1;
		}
		if (Login.getLoginState() == Login.STATE.WELCOMESCREEN) {
			RSInterface loginint = Interfaces.get(378);
			RSInterface[] loginchilds = loginint.getChildren();
			for (RSInterface child : loginchilds) {
				String[] actions = child.getActions();
				if (actions != null && actions.length > 0) {
					for (String action : actions) {
						if (action.contains("Play")) {
							child.click();
						}
					}
				}
			}
		}
		statustime = Instant.now();
		if (Login.getLoginState() == STATE.INGAME) {
			playername = Player.getRSPlayer().getName();
			startingpoint = Player.getPosition();
		}
		util = new ABCUtil();
		ABCProperties props = new ABCProperties();
		props.setWaitingTime(0);
		props.setWaitingFixed(true);
		props.setHovering(true);
		props.setMenuOpen(false);
		this.setAIAntibanState(false);
		util.setProperties(props);
		currentworld = Game.getCurrentWorld();
		General.useAntiBanCompliance(false);
		ThreadSettings.get().setClickingAPIUseDynamic(false);
		ThreadSettings.get().setObjectCModelMethod(ThreadSettings.MODEL_CLICKING_METHOD.CENTRE);
		screenshottime = Instant.now();
		step = 1;
		 mainwindow = new JFrame("BotHeaven - Buyer v2.2 - Bot: ");
			window = new BuyerGUI();
			mainwindow.setContentPane(window.jTabbedPane1);
			window.scriptstop_btn.setEnabled(false);
			JButton savesettings = window.savesettings;
			savesettings.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(System.getenv("APPDATA") + "\\.tribot");
					fileChooser.setAcceptAllFileFilterUsed(false);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("Configuration files (.dat)", "dat");
					fileChooser.setFileFilter(filter);
					//println("Selectedfile: " + fileChooser.getSelectedFile().getAbsolutePath());
					if (fileChooser.showSaveDialog(mainwindow) == JFileChooser.APPROVE_OPTION) {
					  File file = fileChooser.getSelectedFile();
					  String filename = file.getAbsoluteFile().toString();
					  if (filename.contains(".dat")) {
						  filename = filename.replace(".dat", "");
					  }
					  //println("File: '" + file.getAbsoluteFile() + "'");

				        try (Writer writer = new BufferedWriter(
				                new OutputStreamWriter(new FileOutputStream(filename + ".dat"), "utf-8"))) { // sets the output where to write
				                    writer.write(savesettings());
				        }

				        catch (IOException e1) {
				        	println("Exception at writefile: " + e1);
				        }
					  
					}
				}
			});
			
			JButton loadsettings = window.loadsettings;
			loadsettings.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(System.getenv("APPDATA") + "\\.tribot");
					fileChooser.setAcceptAllFileFilterUsed(false);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("Configuration files (.dat)", "dat");
					fileChooser.setFileFilter(filter);
					
					if (fileChooser.showOpenDialog(mainwindow) == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						String content = "";
					    try
					    {
					        content = new String ( Files.readAllBytes( Paths.get(file.getPath()) ) );
					        //println("Content: '" + content + "'");
					        String[] array = content.split("\\|\\|");
					        //println(array.toString());
					        window.npcname.setText(array[0]);
					        //println("NPC: " + array[0].toString());
					        DefaultListModel tempmodel = new DefaultListModel();
					        array[1] = array[1].replaceAll("\\[", "");
					        array[1] = array[1].replaceAll("\\]", "");
					        array[1] = array[1].replaceAll("\\, ", ",");
					        //println("BuyArr: " + array[1].toString());
					        String[] blist = array[1].split(",");
					        if (blist.length > 0) {
					        	for (String element : blist) {
					        		tempmodel.addElement(element);
					        	}
					        }
					        window.buy_list.setModel(tempmodel);
					        buy_list = tempmodel;
					        array[2] = array[2].replaceAll("\\[", "");
					        array[2] = array[2].replaceAll("\\]", "");
					        array[2] = array[2].replaceAll("\\, ", ",");
					        //println("SellArr: " + array[2].toString());
					        DefaultListModel tempmodel2 = new DefaultListModel();
					        String[] slist = array[2].split(",");
					        if (slist.length > 0) {
					        for (String element : slist) {
					        	//println(element.toString());
					        		tempmodel2.addElement(element);
					        	}
					        }
					        window.sell_list.setModel(tempmodel2);
					        sell_list = tempmodel2;
					        window.mule_enabled.setSelected(Boolean.parseBoolean(array[3]));
					        window.exchangeplace.setSelectedItem(array[4]);
					        window.breakseconds.setValue(Integer.parseInt(array[5]));
					        String wsets = array[6];
					        println("Sets: '" + wsets +"'");
					        wsets = wsets.replaceAll("\\[", "");
					        wsets = wsets.replaceAll("\\]", "");
					        wsets = wsets.replaceAll("\\, ", ",");
					        println("Sets: '" + wsets + "'");
					        String[] tempsets = wsets.split("\\,");
					        println(tempsets.toString());
					        for (String w : tempsets) {
					        	if (w.equals("1")) {
					        		window.set1_box.setSelected(true);
					        	}
					        	if (w.equals("2")) {
					        		window.set2_box.setSelected(true);
					        	}
					        	if (w.equals("3")) {
					        		window.set3_box.setSelected(true);
					        	}
					        	if (w.equals("4")) {
					        		window.set4_box.setSelected(true);
					        	}
					        	if (w.equals("5")) {
					        		window.set5_box.setSelected(true);
					        	}
					        	if (w.equals("6")) {
					        		window.set6_box.setSelected(true);
					        	}
					        }
					        
					        
					        window.mulesetting_whengoldcheck.setSelected(Boolean.parseBoolean(array[7]));
					        window.mulesetting_goldlessthan.setValue(Integer.parseInt(array[8]));
					        window.mulesetting_goldmorethan.setValue(Integer.parseInt(array[9]));
					        window.mulesetting_ifsoldall.setSelected(Boolean.parseBoolean(array[10]));
					        window.mulesetting_requestsell.setSelected(Boolean.parseBoolean(array[11]));
					        window.mulesetting_requestsellamount.setValue(Integer.parseInt(array[12]));
					        window.mule_depoitems.setSelected(Boolean.parseBoolean(array[13]));
					        window.shoplocation.setSelectedItem(array[14]);
					        println("Successfully loaded all settings!");
					        
					    }
					    catch (IOException e1)
					    {
					        println(e1.toString());
					    }
					}
				}
			});
			
			JButton buyadd = window.buy_add;
			buyadd.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (window.npcinventory_buy.getSelectedValue() != null) {
						String itemamount_str = JOptionPane.showInputDialog("When to stop buying? (How many should be left in the store)");
						int itemamount = Integer.parseInt(itemamount_str);
					
						String selected = window.npcinventory_buy.getSelectedValue();
						String itemtoadd = selected + "|" + itemamount;
					
						buy_list.addElement(itemtoadd);
						window.buy_list.setModel(buy_list);
					}
				}
			});
			
			
			JButton buyremove = window.buy_remove;
			buyremove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedindex = window.buy_list.getSelectedIndex();
					buy_list.remove(selectedindex);
					window.buy_list.setModel(buy_list);
				}
			});
			JButton selladd = window.sell_add;
			selladd.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (window.npcinventory_sell.getSelectedValue() != null) {
						String itemamount_str = JOptionPane.showInputDialog("When to stop selling? (How many should be in the store to stop?)");
						int itemamount = Integer.parseInt(itemamount_str);
					
						String selected = window.npcinventory_sell.getSelectedValue();
						String itemtoadd = selected + "|" + itemamount;
					
						sell_list.addElement(itemtoadd);
						window.sell_list.setModel(sell_list);
					}
				}
			});
			JButton sellremove = window.sell_remove;
			sellremove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedindex = window.sell_list.getSelectedIndex();
					sell_list.remove(selectedindex);
					window.sell_list.setModel(sell_list);
				}
			});
			JButton scriptstop = window.scriptstop_btn;
			scriptstop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					running = false;
					window.scriptstart_btn.setEnabled(true);
					window.scriptstop_btn.setEnabled(false);
					
					window.npclist.setEnabled(true);
					window.buy_list.setEnabled(true);
					window.npcinventory_buy.setEnabled(true);
					window.sell_list.setEnabled(true);
					window.npcinventory_sell.setEnabled(true);
					window.mule_enabled.setEnabled(true);
					window.exchangeplace.setEnabled(true);
					window.mule_depoitems.setEnabled(true);
					window.set1_box.setEnabled(true);
					window.set2_box.setEnabled(true);
					window.set3_box.setEnabled(true);
					window.set4_box.setEnabled(true);
					window.set5_box.setEnabled(true);
					window.set6_box.setEnabled(true);
					window.mulesetting_whengoldcheck.setEnabled(true);
					window.mulesetting_goldlessthan.setEnabled(true);
					window.mulesetting_goldmorethan.setEnabled(true);
					window.mulesetting_ifsoldall.setEnabled(true);
					window.mulesetting_requestsell.setEnabled(true);
					window.mulesetting_requestsellamount.setEnabled(true);
					window.shoplocation.setEnabled(true);
				}
			});
			JButton scriptstart = window.scriptstart_btn;
			scriptstart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					String buylist = "" + window.buy_list.getModel();
					if (buylist.length() > 0) {
					buylist = buylist.replaceAll("\\[", "");
					buylist = buylist.replaceAll("\\]", "");
					buylist = buylist.replaceAll("\\, ", ",");
					buyl = buylist.split(",");
					}
					String selllist = "" + window.sell_list.getModel();
					if (selllist.length() > 0) {
					selllist = selllist.replaceAll("\\[", "");
					selllist = selllist.replaceAll("\\]", "");
					selllist = selllist.replaceAll("\\, ", ",");
					selll = selllist.split(",");
					println("Sell list:");
					playername = Player.getRSPlayer().getName();
					startingpoint = Player.getPosition();
					mule_enabled = window.mule_enabled.isSelected();
					exchangeplace = window.exchangeplace.getSelectedItem().toString();
					npcname = window.npcname.getText();
					mulesetting_goldcheck = window.mulesetting_whengoldcheck.isSelected();
					mulesetting_goldlessthan = (int) window.mulesetting_goldlessthan.getValue();
					mulesetting_goldmorethan = (int) window.mulesetting_goldmorethan.getValue();
					soldall = window.mulesetting_ifsoldall.isSelected();
					requestitems = window.mulesetting_requestsell.isSelected();
					requestamount = (int) window.mulesetting_requestsellamount.getValue();
					mulesetting_depobuy = window.mule_depoitems.isSelected();
					muletask_active = false;
					
					shoploc = window.shoplocation.getSelectedItem().toString();
					
					//println("Mule enabled: " + mule_enabled);
					//println("Mule nick: " + mulenick);
					//println("Minmoney: " + minmoney);
					//println("Exchance place: " + exchangeplace);
					
					running = true;
					window.scriptstart_btn.setEnabled(false);
					window.scriptstop_btn.setEnabled(true);
					
					window.npclist.setEnabled(false);
					window.buy_list.setEnabled(false);
					window.npcinventory_buy.setEnabled(false);
					window.sell_list.setEnabled(false);
					window.npcinventory_sell.setEnabled(false);
					window.mule_enabled.setEnabled(false);
					window.exchangeplace.setEnabled(false);
					window.mule_depoitems.setEnabled(false);
					window.set1_box.setEnabled(false);
					window.set2_box.setEnabled(false);
					window.set3_box.setEnabled(false);
					window.set4_box.setEnabled(false);
					window.set5_box.setEnabled(false);
					window.set6_box.setEnabled(false);
					window.mulesetting_whengoldcheck.setEnabled(false);
					window.mulesetting_goldlessthan.setEnabled(false);
					window.mulesetting_goldmorethan.setEnabled(false);
					window.mulesetting_ifsoldall.setEnabled(false);
					window.mulesetting_requestsell.setEnabled(false);
					window.mulesetting_requestsellamount.setEnabled(false);
					window.shoplocation.setEnabled(false);
					startingpoint = Player.getPosition();
					shuffledwl = wl;
					Collections.shuffle(shuffledwl);
					Collections.shuffle(shuffledwl);
					Collections.shuffle(shuffledwl);
					Collections.shuffle(shuffledwl);
					Collections.shuffle(shuffledwl);
					starttime = Instant.now().toEpochMilli();
				}
				}
			});
			mainwindow.pack();
			mainwindow.setResizable(false);
			
			
			mainwindow.setVisible(true);
			
			if (Login.getLoginState() == STATE.INGAME) {
				botstart();
				try {
					takeScreenshot();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					println("Error at screenshot: " + e2);
				}
			}
			
			if(settings != "" && settings != null) {
				File file;
				if (settings.contains(",")) {
					String[] settingdb = settings.split("\\,");
					file = new File(System.getenv("APPDATA") + "\\.tribot\\" + settingdb[0] + ".dat");
					autostart = Boolean.parseBoolean(settingdb[1]);
				} else {
					file = new File(System.getenv("APPDATA") + "\\.tribot\\" + settings + ".dat");
				}
				String content = "";
			    try
			    {
			    	if (file.isFile()) {
			        content = new String ( Files.readAllBytes( Paths.get(file.getPath()) ) );
			        //println("Content: '" + content + "'");
			        String[] array = content.split("\\|\\|");
			        //println(array.toString());
			        window.npcname.setText(array[0]);
			        //println("NPC: " + array[0].toString());
			        DefaultListModel tempmodel = new DefaultListModel();
			        array[1] = array[1].replaceAll("\\[", "");
			        array[1] = array[1].replaceAll("\\]", "");
			        array[1] = array[1].replaceAll("\\, ", ",");
			        //println("BuyArr: " + array[1].toString());
			        String[] blist = array[1].split(",");
			        if (blist.length > 0) {
			        	for (String element : blist) {
			        		tempmodel.addElement(element);
			        	}
			        }
			        window.buy_list.setModel(tempmodel);
			        buy_list = tempmodel;
			        array[2] = array[2].replaceAll("\\[", "");
			        array[2] = array[2].replaceAll("\\]", "");
			        array[2] = array[2].replaceAll("\\, ", ",");
			        //println("SellArr: " + array[2].toString());
			        DefaultListModel tempmodel2 = new DefaultListModel();
			        String[] slist = array[2].split(",");
			        if (slist.length > 0) {
			        for (String element : slist) {
			        	//println(element.toString());
			        		tempmodel2.addElement(element);
			        	}
			        }
			        window.sell_list.setModel(tempmodel2);
			        sell_list = tempmodel2;
			        window.mule_enabled.setSelected(Boolean.parseBoolean(array[3]));
			        window.exchangeplace.setSelectedItem(array[4]);
			        window.breakseconds.setValue(Integer.parseInt(array[5]));
			        
			    	}
			    }
			    catch (IOException e1)
			    {
			        println(e1.toString());
			    }
			    
			    if (autostart == true) {
			    	window.scriptstart_btn.doClick();
			    }
			}
			
			
			while (mainwindow.isShowing()) {
				mainwindow.setTitle("BotHeaven - Buyer v2.2 - Bot: " + playername);
				Mouse.setSpeed(600);
				currentworld = Game.getCurrentWorld();
				curr_status = getStatus();
				this.setLoginBotState(false);
				if (WorldHopper.atSelectWorldScreen())
				{
					this.setLoginBotState(false);
					General.sleep(1000);
					Point wpoint = new Point(700,100);
					Mouse.hop(wpoint);
					Mouse.click(0);
					General.sleep(1000);
					this.setLoginBotState(true);
				} else {
					this.setLoginBotState(true);
				}
				
				wl = new ArrayList<String>();
				int worldmin = 0;
				int worldmax = 52;
				if (window.set1_box.isSelected()) {
					worldmin = 0;
					worldmax = 8;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.add(Integer.toString(f2pworlds[world]));
						}
					}
				} else {
					worldmin = 0;
					worldmax = 8;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.remove(Integer.toString(f2pworlds[world]));
						}
					}
				}
				if (window.set2_box.isSelected()) {
					worldmin = 9;
					worldmax = 17;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.add(Integer.toString(f2pworlds[world]));
						}
					}
				} else {
					worldmin = 9;
					worldmax = 17;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.remove(Integer.toString(f2pworlds[world]));
						}
					}
				}
				if (window.set3_box.isSelected()) {
					worldmin = 18;
					worldmax = 26;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.add(Integer.toString(f2pworlds[world]));
						}
					}
				} else {
					worldmin = 18;
					worldmax = 26;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.remove(Integer.toString(f2pworlds[world]));
						}
					}
				}
				if (window.set4_box.isSelected()) {
					worldmin = 27;
					worldmax = 35;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.add(Integer.toString(f2pworlds[world]));
						}
					}
				} else {
					worldmin = 27;
					worldmax = 35;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.remove(Integer.toString(f2pworlds[world]));
						}
					}
				}
				if (window.set5_box.isSelected()) {
					worldmin = 36;
					worldmax = 45;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.add(Integer.toString(f2pworlds[world]));
						}
					}
				} else {
					worldmin = 36;
					worldmax = 45;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.remove(Integer.toString(f2pworlds[world]));
						}
					}
				}
				if (window.set6_box.isSelected()) {
					worldmin = 46;
					worldmax = 52;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.add(Integer.toString(f2pworlds[world]));
						}
					}
				} else {
					worldmin = 46;
					worldmax = 52;
					for (int world = worldmin; world<=worldmax; world++) {
						if (!wl.contains(Integer.toString(f2pworlds[world]))) {
							wl.remove(Integer.toString(f2pworlds[world]));
						}
					}
				}
				
				
				window.worldset_lbl.setText("Selected list: " + wl.toString());
				
				if (isWorldScreenOpen()) {
					Mouse.click(735, 10, 0);
					General.sleep(100);
				}
				
				
				switch (curr_status) {
				case SETTINGWINDOW:
					window.scriptstatus_lbl.setText("Not running..");
					if (selectednpc != "") {
						window.npcname.setText(selectednpc);
					}
					DefaultListModel npclm = new DefaultListModel();
					RSNPC[] npcs = NPCs.getAll();
					for (RSNPC npc : npcs) {
						String npcname = npc.getName();
						if (npcname != "null") {
							npclm.addElement(npcname);
						}
					}
					if (window.npclist.getModel().getSize() != npclm.getSize()) {
						window.npclist.setModel(npclm);
					}
					
					if ((window.npclist.getSelectedValue() != "") && (window.npclist.getSelectedIndex() != -1)) {
						selectednpc = window.npclist.getSelectedValue();
						window.npcname.setText(selectednpc);
					}
					
					
					
					RSInterface buywindow = Interfaces.get(300,16);
					if (buywindow != null) {
						
						
							RSInterface[] itemoffers = buywindow.getChildren();
							String npcitems = "";
							DefaultListModel npclistmodel = new DefaultListModel();
							for (RSInterface child : itemoffers) {
								String name = child.getComponentName();
								name = name.replace("<col=ff9040>", "");
								name = name.replace("</col>", "");
								if (name != "") {
									npcitems = npcitems + name + "|";
									npclistmodel.addElement(name);
								}
							}
							if (window.npcinventory_buy.getModel().getSize() != npclistmodel.getSize()) {
									window.npcinventory_buy.setModel(npclistmodel);
							}
							if (window.npcinventory_sell.getModel().getSize() != npclistmodel.getSize()) {
									window.npcinventory_sell.setModel(npclistmodel);
							}
						}
					break;
					
				case OPENNPCTRADE:
					Mouse.setSpeed(600);
					RSArea playerstart = null;
					if (shoploc == "Varrock") {
						playerstart = varrockshop;
					} else if (shoploc == "Rimmington") {
						playerstart = rimmingtonroom;
					} else if (shoploc == "Port Sarim") {
						playerstart = portsarimroom;
					}
					
					
					
					if (playerstart.contains(Player.getRSPlayer())) {
						window.scriptstatus_lbl.setText("Looking for NPC..");
						RSNPC[] findnpc = NPCs.findNearest(npcname);
						if (findnpc.length > 0) {
							RSNPC targetnpc = findnpc[0];
							RSArea npcarea = new RSArea(targetnpc, 1);
							Point npcpoint = targetnpc.getModel().getHumanHoverPoint();
							
							if (varrockshop_deadzone.contains(Player.getRSPlayer())) {
								WebWalking.walkTo(startingpoint);
							}
							if (portsarimroom.contains(startingpoint) && !portsarimroom.contains(Player.getRSPlayer())) {
								WebWalking.walkTo(startingpoint);
							}
							
							Camera.turnToTile(targetnpc);
							if (targetnpc.isOnScreen() && targetnpc.isClickable() && Projection.isInViewport(npcpoint)) {
								if (!Player.isMoving() && !targetnpc.isMoving()) {
									Mouse.setSpeed(6000);
									DynamicClicking.clickRSNPC(targetnpc, "Trade");
									General.sleep(1000);
								}
							} else {
								Camera.turnToTile(targetnpc);
							}
						
						}
					} else {
						window.scriptstatus_lbl.setText("Walking to NPC..");
						WebWalking.setUseAStar(true);
						WebWalking.walkTo(playerstart.getRandomTile());
						General.sleep(General.random(800, 1500));
					}
					break;
				case WORLDHOP:
					Mouse.setSpeed(300);
					window.scriptstatus_lbl.setText("Hopping worlds..");
					
					
					
					RSInterface wwindow = Interfaces.get(300,1);
					RSInterface logoutbutton = Interfaces.get(164, 29);
					Interfaces.closeAll();
					RSInterface closebtn = Interfaces.get(300, 1, 11);
					worldindex++;
					if (wwindow != null) {
						closebtn.click();
						General.sleep(1500);
					}
						println("Worldindex: " + worldindex);
						
						if (worldindex >= shuffledwl.size()) {
							usedworlds.clear();
							worldindex = 0;
							if (Integer.parseInt(shuffledwl.get(worldindex)) != Game.getCurrentWorld()) {
								println("Start over, first world is not the same as current");
									NWorldHopper.changeWorld((Integer.parseInt(shuffledwl.get(worldindex))));
									finishedbuying = false;
									finishedselling = false;
										println("Hopped");
										buypass = 0;
										sellpass = 0;
										General.sleep(2500,3500);
										hopping = false;
										Collections.shuffle(shuffledwl);
										Collections.shuffle(shuffledwl);
										Collections.shuffle(shuffledwl);
										Collections.shuffle(shuffledwl);
										Collections.shuffle(shuffledwl);
										
										break;
									
							}
						} else {
							println("Hopping..");
							if (Integer.parseInt(shuffledwl.get(worldindex)) != Game.getCurrentWorld()) {
								println("Next world not same as current");
								if (!usedworlds.contains(shuffledwl.get(worldindex))) {
									println("Didnt use this world in this roll");
									NWorldHopper.changeWorld((Integer.parseInt(shuffledwl.get(worldindex))));
										finishedbuying = false;
										finishedselling = false;
										buypass = 0;
										usedworlds.add(Game.getCurrentWorld());
										sellpass = 0;
										General.sleep(2500,3500);
										println("Hopped");
										hopping = false;
										if (Login.getLoginResponse().contains("Too many login attempts")) {
											curr_status = status.BREAK;
											break;
										}
										break;
								}
							}
						}
				case BUY:
					muletask_active = false;
						window.scriptstatus_lbl.setText("Buying");
						RSInterface buywindow1 = Interfaces.get(300,16);
						
						if (buywindow1 != null) {
							for (String item : buyl) {
								String[] itemdb = item.split("\\|");
								if (itemdb.length > 0) {
									String buylistname = itemdb[0];
									int buylistcount = Integer.parseInt(itemdb[1]);
									RSInterface[] itemoffers = buywindow1.getChildren();
									if (!itemoffers.equals(null)) {
											for (RSInterface child : itemoffers) {
												String name = child.getComponentName();
												name = name.replace("<col=ff9040>", "");
												name = name.replace("</col>", "");
												int count = child.getComponentStack();
												if (name != "") {
													if (name.equals(buylistname)) {
														while (count > buylistcount && !Inventory.isFull()) {
															Mouse.setSpeed(3000);
															count = buysellitem(child, count-buylistcount, buylistcount, "buy");
															Mouse.setSpeed(100);
														}
													}
												}
											}
									}
								}
							}
							finishedbuying = true;
							
						}
					break;
				case SELL:
					window.scriptstatus_lbl.setText("Selling");
					RSInterface buywindow2 = Interfaces.get(300,16);
					if (buywindow2 != null) {
						for (String itemmm : selll) {
							String[] itemdbb = itemmm.split("\\|");
							if (itemdbb.length > 0) {
								String selllistname1 = itemdbb[0];
								int selllistcount1 = Integer.parseInt(itemdbb[1]);
								println("Item: " + selllistname1);
								RSItem[] itemm = Inventory.find(selllistname1);
								if (itemm.length > 0) {
									RSInterface[] itemofferss = buywindow2.getChildren();
									if (!itemofferss.equals(null)) {
											for (RSInterface childd : itemofferss) {
												String name = childd.getComponentName();
												name = name.replace("<col=ff9040>", "");
												name = name.replace("</col>", "");
												int countt = childd.getComponentStack();
												int amleft = selllistcount1-countt;
												if (name != "") {
													if (name.equals(selllistname1)) {
														while (amleft > 0) {
															Mouse.setSpeed(3000);
															amleft = buysellitem(itemm[0], amleft, selllistcount1, "sell");
															Mouse.setSpeed(100);
														}
													}
												}
											}
									}
								}
							}
						}
						finishedselling = true;
						
					}
					break;
				case MULETASK:
					
					window.scriptstatus_lbl.setText("Mule Task");
					RSInterface closebtnnn = Interfaces.get(300, 1, 11);
					if (closebtnnn != null) closebtnnn.click();
					String assignedbot = "";
					if (muletask_active == true) {
						assignedbot = dbrequest();
					}
					if (!assignedbot.isEmpty()) {
						if (!assignedbot.equals("empty")) {
							println("bot: '" + assignedbot + "'");
							this.setLoginBotState(true);
						if (window.exchangeplace.getSelectedItem().toString() == "Varrock square" && !area_varrocksq.contains(Player.getRSPlayer())) {
							window.scriptstatus_lbl.setText("Walking to exchange place..");
							WebWalking.setUseAStar(true);
							WebWalking.walkTo(tile_varrocksq);
								General.sleep(General.random(1000,1600));
							
						} else if (window.exchangeplace.getSelectedItem().toString() == "Grand Exchange" && !area_ge.contains(Player.getRSPlayer())) {
							WebWalking.setUseAStar(true);
							WebWalking.walkTo(gepos2);
								General.sleep(1000,1600);
						} else {
							if (muletask_active) {
							RSPlayer[] targetplayer = Players.find(assignedbot);
							if (targetplayer.length > 0) {
								if (Trading.getWindowState() == null) {
									window.scriptstatus_lbl.setText("Target player in place, attempting to trade");
									if (!targetplayer[0].isMoving() && area_ge.contains(targetplayer[0])) {
										Camera.turnToTile(targetplayer[0]);
										targetplayer[0].click("Trade with " + targetplayer[0].getName());
									}
									General.sleep(General.random(1000,2000));
								} else {
									if (Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW) {
										//println("Trade window opened");
										
										
										
										RSItem[] invitems = Inventory.getAll();
										
										for (RSItem item : invitems) {
											Trading.offer(item, item.getStack());
											General.sleep(100,200);
											
										}
										General.sleep(500);
											Trading.accept();
											General.sleep(1500);
											
									}
									if (Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
										//println("Second trade window");
										General.sleep(1000);
										if (Trading.accept()) {
												//println("Successfully deposited items and gold!");
												muletemp_depobuy = 0;
												muletemp_goldcheck = 0;
												muletemp_sellitems = 0;
												muletask_active = false;
												General.sleep(General.random(1200, 2200));
												curr_status = status.OPENNPCTRADE;
												break;
										}
										muletask_active = false;
											}
									}
									}
						}
									
							}
						} else {
							RSInterface buywindow3 = Interfaces.get(300,16);
							if (buywindow3 == null) {
							window.scriptstatus_lbl.setText("Mule empty.. Waiting...");
							RSArea tarea = null;
							if (shoploc == "Varrock") {
								tarea = varrockshop;
							} else if (shoploc == "Rimmington") {
								tarea = rimmingtonroom;
							} else if (shoploc == "Port Sarim") {
								tarea = portsarimroom;
							}
							if (!tarea.contains(Player.getRSPlayer())) {
								//println("Walking to start point..");
								WebWalking.walkTo(tarea.getRandomTile());
							} else {
								//println("Logging out..");
								Login.logout();
								this.setLoginBotState(false);
							}
							} else {
								//println("Closing window");
								RSInterface closebtnn = Interfaces.get(300, 1, 11);
								closebtnn.click();
							}
						}
						} else {
							window.scriptstatus_lbl.setText(("Waiting for answer..."));
							General.sleep(General.random(800, 1600));
							break;
						}
					break;
				case BREAK:
					Instant breakend = breaktime.plus((int) window.breakseconds.getValue(),ChronoUnit.SECONDS);
					int secondsleft = (int) Duration.between(Instant.now(), breakend).toMillis()/1000;
					window.scriptstatus_lbl.setText("Too many login attempts... Trying again in " + secondsleft + " seconds..");
					this.setLoginBotState(false);
					//println("LoginState: false");
					General.sleep( (int) window.breakseconds.getValue() * 1000);
						if (Login.login()) {
							General.sleep(2000);
							this.setLoginBotState(true);
						}
					//println("LoginState: true");
					break;
				case SCREENSHOT:
					window.scriptstatus_lbl.setText("Taking screenshot...");
					try {
						takeScreenshot();
					} catch (IOException e1) {
						println("Error at take screenshot: " + e1);
					}
					General.sleep(General.random(500, 1200));
					break;
				case BREAKBOT:
					window.scriptstatus_lbl.setText("Taking a break..");
					break;
				case BANNED:
					window.scriptstatus_lbl.setText("Account banned!");
					break;
				case IDLE:
					window.scriptstatus_lbl.setText("Idle..");
					break;
				}
					
				General.sleep(General.random(400, 800));
			}
		}
	
	private status getStatus() {
		if (Login.getLoginState() == STATE.UNKNOWN || Login.getLoginState() == STATE.LOGINSCREEN) {
			if (Screen.getColorAt(0, 0) == new Color(189,152,57)) {
				Mouse.click(730, 10, 0);
				General.sleep(1000);
			}
		} 
		
		if (Login.getLoginState() != STATE.INGAME) {
			online = 0;
		} else {
			online = 1;
		}
		
		if (online == 0) {
			if (Login.getLoginMessage() == LOGIN_MESSAGE.BANNED || Login.getLoginMessage() == LOGIN_MESSAGE.LOCKED) {
				
				return status.BANNED;
			}
		}
		
		if (running) {
		if (Duration.between(statustime, Instant.now()).toMillis()/1000 >= 15) {
			checkdb();
			General.sleep(500);
			statustime = Instant.now();
		}
		}
		
		
		
		int worlddmin = 0;
		int worlddmax = 0;
		
		if (Camera.getCameraAngle() < 60) {
			Camera.setCameraAngle(General.random(61, 85));
		}
		
		if (!running) {
			return status.SETTINGWINDOW;
		} else {
			
			
			if (Login.getLoginResponse().contains("Too many login attempts")) {
				breaktime = Instant.now();
				return status.BREAK;
			}
			if (hopping) return status.WORLDHOP;
			
			
			if (Duration.between(screenshottime, Instant.now()).toMinutes() >= screenshotminutes) {
				return status.SCREENSHOT;
			}
			
			
			if (muletask_active == true) { return status.MULETASK; }
				RSInterface buywindow = Interfaces.get(300,16);
				
					int slength = 0;
					if (selll.length == 1) {
						if (!selll[0].equals("")) {
							slength = 1;
						}
					} else {
						slength = selll.length;
					}
					int totalsell = 0;
					for (String sl : selll) {
						if (!sl.equals("")) {
							totalsell++;
							//println("sl: " + sl);
						}
						
					}
					
					
					if (window.mule_enabled.isSelected() && hopping == false) {
						if (window.mulesetting_whengoldcheck.isSelected()) {
							if (!WorldHopper.atSelectWorldScreen()) {
								if (Inventory.open()) {
								if (Inventory.find("Coins").length > 0) {
								if (Inventory.getCount("Coins") >= (int) window.mulesetting_goldmorethan.getValue()) {
									muletask_active = true;
									//println("Muletask active - coins boundary");
									Interfaces.closeAll();
									return status.MULETASK;
								}
							}
							}
							}
						}
						if (window.mulesetting_ifsoldall.isSelected()) {
							int emptysells = 0;
							if (selll.length > 0) {
								if (Inventory.open()) {
								for (String item : selll) {
									String[] itemm = item.split("\\|");
									RSItem[] invitem = Inventory.find(itemm[0]);
									if (invitem.length < 1) {
										emptysells++;
									}
								}
								if (!WorldHopper.atSelectWorldScreen()) {
								if (emptysells >= selll.length) {
										muletask_active = true;
										//println("Muletask active - no sell items, empty: " + emptysells);
										return status.MULETASK;
								}
								}
								}
							}
						}
						if (muletask_active == false && !hopping) {
							if (buywindow != null) {
							//println("Sellpass: " + sellpass + ", totalsell: " + totalsell);
							if (buyl.length > 0 && !buyl[0].equals("")) {
								//println("buypass: " + buypass + ", length: " + buyl.length);
								if (Inventory.getCount("Coins") > 10) {
									if (!finishedbuying) {
										return status.BUY;
									}
								}
							} else {
								finishedbuying = true;
							}
							if (selll.length > 0 && !selll[0].equals("")) {
								//println("sellpass: " + sellpass + ", length: " + slength);
								if (!finishedselling) {
									return status.SELL;
								}
							} else {
								finishedselling = true;
							}
							}
						} else {
							return status.OPENNPCTRADE;
						}
					}
					int emptyitems = 0;
					if (hopping) { 
						emptyitems = buyl.length + selll.length; 
					} else {
						emptyitems = buypass + sellpass;
					}
					
					if (!Interfaces.isInterfaceSubstantiated(buywindow) && curr_status != status.WORLDHOP && muletask_active == false && !hopping) {	
						return status.OPENNPCTRADE;
					} 
					
				//println("Buy:" + buyl.length +", Sell:" + totalsell);
				//println("Buypass: " + buypass + ", Sellpass: " + sellpass);
				//println("Empty: " + emptyitems + ", total: " + (buyl.length + totalsell));
				
				if (finishedbuying && finishedselling) {
					return status.WORLDHOP;
				}
				
				
			return status.IDLE;
		}
		
	}
	
	
	
	public void onBreakStart(long breaktime) {
		curr_status = status.BREAKBOT;
		checkdb();
		
	}
	
	public void onBreakEnd() {
		curr_status = status.IDLE;
		checkdb();
		if (Login.getLoginState() == STATE.UNKNOWN || Login.getLoginState() == STATE.LOGINSCREEN) {
			if (Screen.getColorAt(0, 0) == new Color(189,152,57)) {
				Mouse.click(730, 10, 0);
				General.sleep(1000);
			}
		}
	}
	
	
	
	public void onStop() {
		
	}
	
	
	
	private String checkdb() {
		String loc = shoploc;
		loc = loc.replace(" ", "%20");
		try {
			String pname = playername.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/bhstatus.php?bot=" + pname + "&type=buyer&online=" + online + "&status=" + curr_status.toString() + "&stime=" + starttime/1000 + "&botloc=" + loc;
    	
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
    		
    	println("Exception occurred at connectdb." + e);
    	return "";
    	}

	}
	
	
	private String savesettings() {
		String finalstring = "";
		finalstring += window.npcname.getText() + "||";
		if (buy_list.isEmpty()) {
			finalstring += "||";
		} else {
			finalstring += window.buy_list.getModel() + "||";
		}
		if (sell_list.isEmpty()) {
			finalstring += "||";
		} else {
			finalstring += window.sell_list.getModel() + "||";
		}
		
		ArrayList<String> setlist = new ArrayList<String>();
		if (window.set1_box.isSelected()) {
			setlist.add("1");
		}
		if (window.set2_box.isSelected()) {
			setlist.add("2");
		}
		if (window.set3_box.isSelected()) {
			setlist.add("3");
		}
		if (window.set4_box.isSelected()) {
			setlist.add("4");
		}
		if (window.set5_box.isSelected()) {
			setlist.add("5");
		}
		if (window.set6_box.isSelected()) {
			setlist.add("6");
		}
		
		finalstring += window.mule_enabled.isSelected() + "||";
		finalstring += window.exchangeplace.getSelectedItem().toString() + "||";
		finalstring += window.breakseconds.getValue() + "||";
		finalstring += setlist.toString() + "||";
		finalstring += window.mulesetting_whengoldcheck.isSelected() + "||";
		finalstring += window.mulesetting_goldlessthan.getValue() + "||";
		finalstring += window.mulesetting_goldmorethan.getValue() + "||";
		finalstring += window.mulesetting_ifsoldall.isSelected() + "||";
		finalstring += window.mulesetting_requestsell.isSelected() + "||";
		finalstring += window.mulesetting_requestsellamount.getValue() + "||";
		finalstring += window.mule_depoitems.isSelected() + "||";
		finalstring += window.shoplocation.getSelectedItem().toString();
		
		
		return finalstring;
	}
	
	
	private int buysellitem(Clickable item, int amountleft, int stopamount, String method) {
		Point itempos = new Point();
		int itemam = 0;
		if (method == "buy") {
			RSInterface itemm = (RSInterface) item;
			Rectangle itemmbox = itemm.getAbsoluteBounds();
			int x = (int) itemmbox.getCenterX();
			int y = (int) itemmbox.getCenterY();
			int rx = General.random(x-10, x+10);
			int ry = General.random(y-10, y+10);
			itempos = new Point(rx, ry);
			itemam = itemm.getComponentStack();
		} else if (method == "sell") {
			RSItem invitem = (RSItem) item;
			Rectangle invitembox = invitem.getArea();
			int x = (int) invitembox.getCenterX();
			int y = (int) invitembox.getCenterY();
			int rx = General.random(x-10, x+10);
			int ry = General.random(y-10, y+10);
			itempos = new Point(rx, ry);
			itemam = invitem.getStack();
		}
		if (amountleft > 0 && itemam > 0) {
			Point buypos = new Point();
			String option = "";
			if ( amountleft >= 50) {
				buypos = new Point((int) itempos.getX(),(int) itempos.getY()+85);
				option = "50";
			} else if (amountleft >= 10 && amountleft < 50) {
				buypos = new Point((int) itempos.getX(),(int) itempos.getY()+70);
				option = "10";
			} else if (amountleft >= 5 && amountleft < 10) {
				buypos = new Point((int) itempos.getX(),(int) itempos.getY()+55);
				option = "5";
			} else if (amountleft < 5 && amountleft > 0) {
				buypos = new Point((int) itempos.getX(),(int) itempos.getY()+40);
				option = "1";	
			}
			Mouse.click(itempos, 3);
			if (ChooseOption.isOpen()) {
				if (ChooseOption.isOptionValid("Buy " + option)) {
					if (itemam >= Integer.parseInt(option)) {
						Mouse.click(buypos, 1);
						amountleft -= Integer.parseInt(option);
					}
				} else if (ChooseOption.isOptionValid("Sell " + option)) {
					if (itemam >= Integer.parseInt(option)) {
						Mouse.click(buypos, 1);
						amountleft -= Integer.parseInt(option);
					}
				} else {
					ChooseOption.close();
				}
			} else {
				Mouse.click(itempos, 3);
				General.sleep(35);
			}
			
		}
			return amountleft;
		} 
	
	
	public static Point subtract(Point p1, Point p2) {
	    return new Point((int) p1.getX() - (int) p2.getX(), (int) p1.getY() - (int) p2.getY());
	}
	
private void botstart() {
	
		
		try {
			String pname = playername;
    		pname = pname.replace(" ", "%20");
    		pname = pname.replaceAll("\\W", "%20");
    	String webPage = "http://" + serverIP + "/bhstart.php?bot=" + pname + "&type=buyer&world=" + currentworld + "&status=" + curr_status;
    	
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

private String dbrequest() {
	
	try {
		String pname = playername;
		pname = pname.replace(" ", "%20");
		pname = pname.replaceAll("\\W", "%20");
		String locationn = window.exchangeplace.getSelectedItem().toString();
		locationn = locationn.replace(" ", "%20");
		ArrayList<String> requestitems = new ArrayList<String>();
		if (selll.length > 0) {
			for (String slistitem : selll) {
				String[] itemdb = slistitem.split("\\|");
				requestitems.add(itemdb[0]+"_"+requestamount);
			}
		}
		String ritems = (String) requestitems.toString();
		ritems = ritems.replaceAll("\\ ", "%20");
        
	String webPage = "http://" + serverIP + "bhrequest.php?bot=" + pname + "&request=bank&world=" + currentworld + "&loc=" + locationn + "&requestitems=" + ritems;
	
	println("Updatepage: " + webPage);
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
	return "";
	}
}

	@Override
	public void passArguments(HashMap<String, String> arg0) {
	String scriptSelect = arg0.get("custom_input");
	String clientStarter = arg0.get("autostart");
	String input = clientStarter != null ? clientStarter : scriptSelect;
	settings = input;
	println("Arguments: " + settings);
	
}
	
	private static void shuffle(List list) {
		// get size of the list
		int totalElements = list.size();
		// initialize random number generator
		Random random = new Random();
		for (int loopCounter = 0; loopCounter < totalElements; loopCounter++) {
			// get the list element at current index
			int currentElement = (int) list.get(loopCounter);
			// generate a random index within the range of list size
			int randomIndex = loopCounter + random.nextInt(totalElements - loopCounter);
			// set the element at current index with the element at random
			// generated index
			list.set(loopCounter, list.get(randomIndex));
			// set the element at random index with the element at current loop
			// index
			list.set(randomIndex, currentElement);
		}
	   }
	
	public void takeScreenshot() throws IOException {
		BufferedImage screenshot = Screenshots.getScreenshotImage();
		if (screenshot != null) {
			String pname = playername;
			pname = pname.replaceAll("\\W", " ");
			String dir = Util.getWorkingDirectory() + "\\botscreenshots\\" + pname + "\\";
			long noww = Instant.now().toEpochMilli()/1000;
			String filename = noww + ".png";
			
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
				File file = new File(dir + filename);
				if (file != null) {
					if (ImageIO.write(screenshot, "PNG", file)) {
						screenshottime = Instant.now();
					}
				}
			} else {
				File filee = new File(dir + filename);
				if (filee != null) {
					if (ImageIO.write(screenshot, "PNG", filee)) {
						screenshottime = Instant.now();
					}
				}
			}
		}
	}
	
	
	private boolean isWorldScreenOpen() {
        Color wScreen = Screen.getColorAt(100, 100);
        return (wScreen.getRed() == 0 && wScreen.getGreen() == 0 && wScreen.getBlue() == 0);
    }
	
}