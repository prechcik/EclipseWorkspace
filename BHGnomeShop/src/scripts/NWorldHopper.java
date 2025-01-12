package scripts;

import java.awt.Rectangle;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;

import scripts.Entities;
import scripts.entityselector.finders.prefabs.InterfaceEntity;

public class NWorldHopper {

	public static final int WORLD_HOPPER_MASTER_ID = 69;
	public static final int LOGOUT_MASTER_ID = 182;
	
	private static final String[] DIALOGUE = { "Yes. In future, only warn about dangerous worlds.", "Yes." };
	
	/**
	 * This will attempt to hop to the target world four times. Will default to TRiBot's world hopper if not ingame.
	 * @return true if the target world was successfully reached, false otherwise
	 */
	public static boolean changeWorld(int target) {
		
		if (target <= 300)
			target += 300;
		
		for (int i = 0; i < 4; i++)
			if (attemptChangeWorld(target))
				return true;
		
		return false;
	}
	
	private static boolean attemptChangeWorld(final int target) {
		
		if (Login.getLoginState() != STATE.INGAME)
			return WorldHopper.changeWorld(target);
		
		if (!worldSwitcherOpen()) {
		
			if (!GameTab.TABS.LOGOUT.open())
				return false;
			
			if (!worldSwitcherOpen()) { // can still be open if another tab is open
			
				final RSInterface iface = Entities.find(InterfaceEntity::new)
											.inMaster(LOGOUT_MASTER_ID)
											.actionEquals("World Switcher")
											.custom(Interfaces::isInterfaceSubstantiated)
											.getFirstResult();
				if (iface == null)
					return false;
				
				if (!iface.click("World Switcher"))
					return false;
				
				if (!Timing.waitCondition(NWorldHopper::worldSwitcherOpen, General.random(1800, 2400)))
					return false;
			}
			
		}
		
		// could potentially cache this result for later calls
		final int child = getWorldListChild();
		if (child == -1)
			return false;
		
		final RSInterface clickTarget = Entities.find(InterfaceEntity::new)
										.inMasterAndChild(WORLD_HOPPER_MASTER_ID, child)
										.componentNameContains(String.valueOf(target))
										.actionEquals("Switch")
										.getFirstResult();
		
		// wait for the interface to appear incase we just opened the tab
		if (!Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(clickTarget), General.random(800, 1100)))
			return false;
		
		// find scroll area by 1 before child
		final RSInterface scrollArea = Interfaces.get(WORLD_HOPPER_MASTER_ID, child - 1);
		if (scrollArea == null)
			return false;
		
		final Rectangle scrollBounds = scrollArea.getAbsoluteBounds();
		if (scrollBounds == null)
			return false;
		
		Rectangle targetBounds = clickTarget.getAbsoluteBounds();
		if (targetBounds == null)
			return false;
		
		final int timeout = General.random(10000, 15000);
		final long start = Timing.currentTimeMillis();
		
		while (!scrollBounds.contains(targetBounds)) {
			
			if (Timing.timeFromMark(start) >= timeout)
				return false;
		
			if (!scrollBounds.contains(Mouse.getPos()))
				Mouse.moveBox(scrollBounds);
			
			Mouse.scroll(scrollBounds.getY() > targetBounds.getY());
			
			General.sleep(General.randomSD(65, 15));
			
			targetBounds = clickTarget.getAbsoluteBounds();
			
			if (targetBounds == null)
				return false;
		}
		
		if (!clickTarget.click("Switch " + target))
			return false;
		
		if (!Timing.waitCondition(() -> WorldHopper.getWorld() == target
				|| chatOptionsContains(DIALOGUE), General.random(1800, 2400)))
			return false;
		
		if (WorldHopper.getWorld() == target)
			return true;
		
		for (String s : DIALOGUE) {
			if (chatOptionsContains(s)) {
				General.sleep(General.randomSD(450, 60));
				if (!NPCChat.selectOption(s, true))
					return false;
				Timing.waitCondition(() -> WorldHopper.getWorld() == target, General.random(1800, 2400));
				break;
			}
		}
		
		return WorldHopper.getWorld() == target;
	}
	
	public static boolean worldSwitcherOpen() {
		return Interfaces.isInterfaceSubstantiated(WORLD_HOPPER_MASTER_ID);
	}
	
	private static boolean chatOptionsContains(String... searches) {
		
		final String[] chatOptions = NPCChat.getOptions();
		
		if (chatOptions == null) // for some reason this can return null
			return false;
		
		for (String s : chatOptions)
			for (String search : searches)
				if (s.toLowerCase().contains(search.toLowerCase()))
					return true;
		
		return false;
	}
	
	private static int getWorldListChild() {
		
		// search the master interface for children who have children with the action Switch
		final RSInterface worldList = Entities.find(InterfaceEntity::new)
				.inMaster(WORLD_HOPPER_MASTER_ID)
				.custom(iface -> {
			
			if (!(iface instanceof RSInterfaceChild))
				return false;
			
			final RSInterface[] comp = iface.getChildren();
			if (comp == null || comp.length == 0)
				return false;
			
			final String[] actions = comp[0].getActions();
			if (actions == null)
				return false;
			
			for (String s : actions)
				if (s.equals("Switch"))
					return true;
			
			return false;
		}).getFirstResult();
		
		return worldList != null ? worldList.getIndex() : -1;
	}
	
}
