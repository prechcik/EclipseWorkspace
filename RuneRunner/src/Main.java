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
import java.util.List;
import java.util.Random;
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
import org.dreambot.api.methods.container.AbstractItemContainer;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.*;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.astar.AStarPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;


@ScriptManifest(author = "Prech", name = "Rune Runner!", version = 0.3, description = "Harrasing runes", category = Category.MONEYMAKING)
public class Main
extends AbstractScript {	
	
	private final String serverIP = "167.114.98.62";
	private final Area BANK_AREA = new Area(3255,3420,3251,3421);
	private final Tile Bank_Tile = new Tile(3253,3421);
	private final BankLocation banklocation = BankLocation.VARROCK_EAST;
	private final Tile Wild_entrance = new Tile(3243,3519);
	private final Area RUNE_PLAYERAREA = new Area(3302,3861,3301,3855);
	private final Tile RUNE_PLAYERTILE = new Tile(340,224);
	private final Tile RUNE_AREA1 = new Tile(3307,3859);
	private final Tile RUNE_AREA2 = new Tile(3311,3853);
	private int min_lawrunes = 1;
	private int max_lawrunes = 50;
	private int min_food = 6;
	private int max_food = 22;
	private String food_name = "Jug of wine";
	private int percent_to_heal = 98;
	private int runes_to_pick = max_lawrunes;
	private final int lawruneid = 563; // Law Rune
	private final int natureruneid = 561;
	private String username = null;
	private status state = null;
	private final Color color1 = new Color(193, 193, 193, 170);
    private final Color color2 = new Color(0, 0, 0);
    private int picked = 0;
    private final BasicStroke stroke1 = new BasicStroke(1);
    private long startTime = 0;
    private long runTime = 0;
    private String status_text = "";
    private int totalpicked = 0;
    private final int wandid = 1381;
    private int panic = 0;
    private final int energy_pots = 4;

    private final Font font1 = new Font("Tahoma", 0, 17);

    
	
	private enum status {
		READY, TOAREA, CAST, SWITCHWORLD, TOBANK, BANK, HEAL, RUN
	}
	
	private int[] f2pworlds = {
			301,326,371,393,386,397,378,393,394,397,398,399,418,424,425,426,430,431,433,434,435,436,437,438,439,440,441,451,452,453,454,455,456,457,458,459,460,469,470,471,472,473,474,475,476,477,478,479,497,498,499,500,501,502,503,504,505,506,
	};
	
	private status getState(Player curplayer) throws IOException {
		
		List<Player> playersInArea = getPlayers().all(p -> p != null && getLocalPlayer().getTile().getArea(20).contains(p));
		int amountInArea = playersInArea.size();
		
		if ((amountInArea >= 2) && (getCombat().getWildernessLevel() > 1)) {
			if (getLocalPlayer().isInCombat()) {
				panic = 1;
				return status.RUN;
			} else {
				return status.SWITCHWORLD;
			}
		}
		if(healthPerc() <= percent_to_heal){
			return status.HEAL;
		}
		Area lumbridge = new Area(3217,3221,3226,3214);
		if(lumbridge.contains(getLocalPlayer())) {
			return status.TOBANK;
		}
		
		
		
		//if your inventory is full, you should either be walking to the bank or banking
		if((getInventory().count(lawruneid)<=min_lawrunes) || (getInventory().count(natureruneid)>=runes_to_pick) || (getInventory().count(food_name)<=min_food) || (!getEquipment().contains(wandid))){
			if(BANK_AREA.contains(getLocalPlayer())){
				return status.BANK;
			}
			else {
				return status.TOBANK;
			}
		}
		else {
			//if it isn't full, you'll want to either be picking up hides or walking to the cows
			if(RUNE_PLAYERAREA.contains(getLocalPlayer())){
				GroundItem rune = getGroundItems().closest("Nature rune");
				Area runearea = new Area(RUNE_AREA1, RUNE_AREA2);
				
				if (!runearea.contains(rune)) {
					sleep(Calculations.random(450, 1500));
					return status.SWITCHWORLD;
				} else {
				return status.CAST;
				}
			}
			else {
				return status.TOAREA;
			}
		}
		
		
		
		
		
		
	}
	
	@Override
    public void onStart() {
		log("Starting!");	 // This will display in the CMD or logger on dreambot
		username = getLocalPlayer().getName();
		state = status.READY;
		setStatus("ready",username);
		picked = 0;
		startTime = System.nanoTime();
		
		
    }
 
    @Override
    public int onLoop() {
    	panic = 0;
    	long curtime = System.nanoTime();
    	runTime = curtime - startTime;
    	runTime = runTime / 1000000000;
    	
    	//log(state.toString());
		int lawrunes = getInventory().count(563);
		//log("Lawrunes: "+ lawrunes);
		if(getLocalPlayer().isMoving()){
			Tile dest = getClient().getDestination();
			if(dest != null && getLocalPlayer().getTile().distance(dest) > 5){
				return Calculations.random(200,400);
			}
		}
		//we should add a run energy check too! see all the fun things we think of
		
		if (getWalking().getRunEnergy() < Calculations.random(15, 30)) {
			if (getInventory().contains("Energy potion(1)")) {
				getInventory().interact("Energy potion(1)", "Drink");
			} else if (getInventory().contains("Energy potion(2)")) {
				getInventory().interact("Energy potion(2)", "Drink");
			} else if (getInventory().contains("Energy potion(3)")) {
				getInventory().interact("Energy potion(3)", "Drink");
			} else if (getInventory().contains("Energy potion(4)")) {
				getInventory().interact("Energy potion(4)", "Drink");
			}
		}
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
		
		case BANK:
			setStatus("bank",username);
			status_text = "Banking..";
			//so this is our bank state, we know the gist of banking so we can do this no problem
			if(BANK_AREA.contains(getLocalPlayer())){
				panic = 0;
				totalpicked += getInventory().count("Nature rune");
				//if the bank is open, just deposit everything.
				getBank().open(banklocation);
				sleep(Calculations.random(900,1200));
				getBank().depositAllItems();
				sleep(Calculations.random(900,1200));
				log("Checking for food..");
				if (getBank().contains(food_name)) {
					getBank().withdraw(food_name, max_food);
				} else {
					log("No food left! Exiting the script!");
					stop();
				}
				sleep(Calculations.random(900,1200));
				log("Checking for Law Runes...");
				if (getBank().contains("Law Rune")) {
					getBank().withdraw(lawruneid, max_lawrunes);
				} else {
					log("No Law runes left! Exiting!");
					stop();
				}
				log("Getting energy pots..");
				if (getBank().contains("Energy potion(4)")) {
					getBank().withdraw("Energy potion(4)", energy_pots);
				}
				
				log("Checking for air wand");
				if(!getEquipment().contains("Staff of air")) {
					if (!getBank().contains("Staff of air")) {
						log("No air wands left! Exiting!");
						stop();
					} else {
						getBank().withdraw("Staff of air", 1);
						sleep(1500);
						if (getInventory().contains("Staff of air")) {
						getBank().close();
						sleep(Calculations.random(300, 600));
						getEquipment().equip(EquipmentSlot.WEAPON, wandid);
						sleep(600);
						} else {
							
						}
					}
				} else {
					getBank().close();
					sleep(Calculations.random(150, 400));
					getEquipment().equip(EquipmentSlot.WEAPON, wandid);
				}
				
				sleep(Calculations.random(250,800));
				log("Done banking!");
				getBank().close();
				//woo another sleepUntil time
				
				//this sleepUntil will sleep until your inventory is empty.
			}
			else{
				//if it isn't open, open it!
				log("Walk");
				getBank().open(banklocation);
				//now we get into our first sleepUntil, this takes a Condition and a timeout as arguments
				//a Condition is an object that has a verify method, when it returns true the sleepUntil cuts out
				//or it'll wait until you reach the timeout
				sleepUntil(new Condition(){
					public boolean verify(){
						return getBank().isOpen();
					}
				},Calculations.random(300,700));
				//so in that condition we set it to go until the bank is open, or 900-1200 milliseconds has passed
			}
			//the reak statement is required after each case otherwise it'll spill over into the next
			//this says to break out of your switch/case and to not continue on to the others.
			break;
		case CAST:
			setStatus("cast",username);
			status_text = "Grabbing runes..";
			//this is where you'll be picking stuff up.
			//first you'll want to see if there's even a ground item available
			//good thing I checked :')
			final GroundItem rune = getGroundItems().closest("Nature rune");//I don't know if this is what it is
			//so I"ll have to check that when I actually sign in for data collection
			//"But what if it isn't in the area?" you may ask.
			//that's a good point. So we may want to throw a filter onto the ground items.
			//log("Rune: '" + rune + "'");
			if(rune != null){
				//log("Found rune");
				//if it isn' tnull, then it exists and we should pick it up.
				//so coming back to this, we should probably only sleep if the interaction
				//was successful. So we change this to an if statement
				Spell grab = Normal.TELEKINETIC_GRAB;
				
					//log("Can cast the spell");
					GroundItem floor_rune = getGroundItems().closest("Nature rune");
					if (floor_rune != null) {
						getMagic().castSpellOn(grab, floor_rune);
						sleepUntil(new Condition(){
							public boolean verify(){
								return !floor_rune.exists();//so we had to change hide to final because
								//we're using it in an inner class, and you can only access variables
								//from outside the inner class if it's final.
								//final essentially just means that once it's set, you can't change it.
							}
						},Calculations.random(180,240));
					} else {
						log("No runes detected!");
					}
			} else {
				sleep(Calculations.random(1500, 3000));
				state = status.SWITCHWORLD;
				break;
			}
			break;
		case TOBANK:
			setStatus("tobank",username);
			status_text = "Returning to bank..";
			if(!BANK_AREA.contains(getLocalPlayer())){
				getBank().open(banklocation);
				
			}
			break;
		case TOAREA:
			setStatus("toarea",username);
			status_text = "Going to pickup area..";
				if (RUNE_PLAYERAREA.contains(getLocalPlayer())) {
					state = status.CAST;
					break;
				} else {
					//log("Not in area!");
					getWalking().walk(RUNE_PLAYERAREA.getRandomTile());
					getWalking().getAStarPathFinder().addObstacle(new PassableObstacle("Wilderness Ditch", "Cross", null, null, null));
					WidgetChild c = getWidgets().getWidgetChild(475,11);
					if (c != null && c.isVisible()) c.interact("Enter Wilderness");
					//log("Walked?");
					break;
				}
		case READY:
			setStatus("ready",username);
			status_text = "Ready!";
			try {
				getState(getLocalPlayer());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log(e.toString());
			}
			break;
			
		case HEAL:
			log("Healing!");
			status_text = "Healing..";
			for(int i = 0; i < 28; i++){
				Item item = getInventory().getItemInSlot(i);
				if(item != null && item.hasAction("Drink")){
					getInventory().slotInteract(i, "Drink");
					break;
				}
			}
			break;
			
		case SWITCHWORLD:
			status_text = "Switching world!";
			log("Worldswitch!");
			int curr_world = getClient().getCurrentWorld();
			int newworld = getRandom(f2pworlds);
			
			if (newworld != curr_world) {
				getWorldHopper().quickHop(newworld);
			} else {
				break;
			}
			
		case RUN:
			panic = 1;
			setStatus("tobank",username);
			status_text = "Player attacked us! Running back!";
			if(!BANK_AREA.contains(getLocalPlayer())){
				getWalking().getAStarPathFinder().addObstacle(new PassableObstacle("Wilderness Ditch", "Cross", null, null, null));
				getBank().open(banklocation);
		break;
			}
		default:
			break;
		
		
		}
		
		
		
		
    	return Calculations.random(100,350); // basic return value. 
    }
    
    private int healthPerc(){
		int currHealth = getSkills().getBoostedLevels(Skill.HITPOINTS);
		int maxHealth = getSkills().getRealLevel(Skill.HITPOINTS);
		return ((currHealth*100)/maxHealth);
	}
    
    private String getData(String filename, String botaccount) {
    	try {
    	String encodedaccount = URLEncoder.encode(botaccount,"UTF-8");
    	String webPage = "http://" + serverIP + "/getdata.php?b=" + encodedaccount + "&f=" + filename;
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

    	return result.replaceAll("\\s+","");
    	} catch (Exception e) {
    	log("Exception occurred at getData." + e);
    	}

    	return "";
    	}
    
    private String setStatus(String newstatus, String botaccount) {
    	try {
    	//log("Setting Web status to " + newstatus);
    	String encodedaccount = URLEncoder.encode(botaccount,"UTF-8");
    	String webPage = "http://" + serverIP + "/setstatus.php?b=" + encodedaccount + "&s=" + newstatus;
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
    	//log("Web status set to " + newstatus);
    	return result.replaceAll("\\s+","");
    	} catch (Exception e) {
    	log("Exception occurred at setData." + e);
    	}

    	return "";
    	}
       
    @Override
    public void onExit() {
    	log("Stopping!"); // this will display in the CMD or logger on dreambot
    }
    
    public void onPaint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        g.setColor(color1);
        g.fillRect(0, 338, 517, 140);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRect(0, 338, 517, 140);
        g.setFont(font1);
        g.drawString("Character name:", 17, 375);
        g.drawString("Status:", 17, 396);
        g.drawString("Runes picked:", 18, 418);
        g.drawString("Time run:", 16, 439);
        g.drawString(username, 147, 375);
        g.drawString(status_text, 74, 396);
        g.drawString(String.valueOf(totalpicked), 125, 418);
        g.drawString(String.valueOf(runTime), 91, 440);
    }

    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
    
    
}