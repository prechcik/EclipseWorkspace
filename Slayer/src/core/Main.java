package core;

import java.awt.Graphics2D;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.util.ItemContainer;
import org.osbot.rs07.api.Inventory;

@ScriptManifest(author = "Prechcik", info = "Simple Slayer tasker", name = "Prech Slayer", version = 0, logo = "")
public class Main extends Script {

	@Override
	public void onStart() {
		log("Let's get started!");
		
		
	}
	
	public void walktonpc() {
		Area TuraelArea = new Area(2930,3537,2932,3536);
		WalkingEvent myEvent = new WalkingEvent(TuraelArea);
		if (!myPlayer().isAnimating()) {
				execute(myEvent);
		}
	}

	@Override
	public int onLoop() throws InterruptedException {
		Position myPos = myPlayer().getPosition();
		Inventory myInv = getInventory();
		Item Crystal = myInv.getItem(4155);
		if (Crystal.getAmount()>0) {
			Crystal.interact("Check");
		} else {
			log("No crystal!");
		}
		Area TuraelArea = new Area(2930,3537,2932,3536);
		if (TuraelArea.contains(myPos)) {
		walktonpc();
		} else {
			NPC Turael = npcs.closest("Turael");
			if (Turael != null) {
				log("Clicking Assignment on Turael");
				Turael.interact("Assignment");
			} else {
				log("Walking to Turael");
				walktonpc();
			}
		}
		
		return random(500, 900);
	}

	@Override
	public void onExit() {
		log("Thanks for running my Tea Thiever!");
	}

	@Override
	public void onPaint(Graphics2D g) {

	}

}