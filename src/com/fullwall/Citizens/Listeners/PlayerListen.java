package com.fullwall.Citizens.Listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.fullwall.Citizens.Citizens;
import com.fullwall.Citizens.NPCManager;
import com.fullwall.Citizens.Events.CitizensBasicNPCEvent;
import com.fullwall.Citizens.Events.CitizensBasicNPCEvent.Reason;
import com.fullwall.Citizens.Utils.MessageUtils;
import com.fullwall.resources.redecouverte.NPClib.HumanNPC;
import com.fullwall.resources.redecouverte.NPClib.NPCEntityTargetEvent;
import com.fullwall.resources.redecouverte.NPClib.NPCEntityTargetEvent.NpcTargetReason;

/**
 *  Just for fun, I've hacked this into returning megahal.  
 *  Eventually, I'll use this class to handle the scripting actions
 * 
 * @author eltmon
 */
public class PlayerListen extends PlayerListener {
	private final Citizens plugin;
	private Process process = null;
	private static final String lineSeparator = System
			.getProperty("line.separator");
	public static OutputStream stdin = null;
	InputStream stderr = null;
	InputStream stdout = null;
	InputStreamReader isr = null;
	// InputStreamReader is = null;
	Random randomGenerator = new Random();

	public static String previousText = null;

	ProcessBuilder pb = null;
	public static BufferedReader brCleanUp = null;
	public static ConsoleInput con = null;

	public PlayerListen(final Citizens plugin) {

		this.plugin = plugin;

		try {
			pb = new ProcessBuilder("megahal");
			process = pb.start();
			pb.redirectErrorStream(true);
			stdin = process.getOutputStream();
			stderr = process.getErrorStream();
			stdout = process.getInputStream();
			isr = new InputStreamReader(stdout);
			brCleanUp = new BufferedReader(isr);
			con = new ConsoleInput(2, 2, TimeUnit.SECONDS, brCleanUp);

			// skip header
			String currentLine = "start";

			// for (int i = 0; i < 10 ; i ++){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			boolean hadFirstLine = false;
			
			while (currentLine != null && brCleanUp.ready()) {
				try {
					currentLine = con.readLine();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//if (currentLine.indexOf(">") != -1) {
				if (currentLine.indexOf("|".charAt(0)) != -1 && hadFirstLine){
					break;
				}
				hadFirstLine = true;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		Player p = event.getPlayer();
		String msg = event.getMessage();
		for (Entry<Integer, HumanNPC> entry : NPCManager.getNPCList()
				.entrySet()) {
			if (entry.getValue().getName().equalsIgnoreCase("Notch")) {
				// MessageUtils.sendText(entry.getValue(), p, plugin);
				// getServer().broadcastMessage("Message here.");

				// ((Player) p).sendMessage(text);

				// invoke megahal

				System.out.println("On player chat");

				String line;

				// launch EXE and grab stdin/stdout and stderr

				String currentLine = null;
				try {

					// process = Runtime.getRuntime ().exec ("megahal.exe");

					System.out.println("writing to console: " + msg);

					stdin.write((msg + lineSeparator + lineSeparator)
							.getBytes());
					stdin.flush();
					// stdin.write((msg + lineSeparator +
					// lineSeparator).getBytes() );
					// stdin.flush();

					// System.out.println("testing if ready");
					// if (!brCleanUp.ready()){
					// System.out.println("not ready");
					// return;
					// }

					System.out.println("grabbing line");
					// get input

					super.onPlayerChat(event);
					String lastLine = null; // "start blank";
					currentLine = "notnull";
					while (isr.ready() && currentLine != null) {
						try {
							currentLine = con.readLine();
							/*
							 * StringBuffer sb = new StringBuffer(); int c;
							 * while((c=isr.read()) != -1) {
							 * 
							 * sb.append((char)c); }
							 */

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						System.out.println("grabbed line:" + currentLine);

						String text = "<" + Citizens.NPCColour
								+ entry.getValue().getName() + ChatColor.WHITE
								+ ">: " + currentLine.substring(3);

						// Remove lined with |
						if (text.indexOf("|".charAt(0)) != -1)
							text = null;
						//
						if (previousText != null && text != null) {
							List<Player> a = Arrays.asList(plugin.getServer()
									.getOnlinePlayers());
							// plugin.getServer().getOnlinePlayers();

							if (randomGenerator.nextInt(100) > 20) {

								for (int i = 0; i < a.size(); i++) {

									((Player) a.get(i))
											.sendMessage(previousText);
								}

							}
						} 
						previousText = text;
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// ((Player) p).sendMessage(test);
				// MessageUtils.sendText(msg, p, plugin);
			}
		}
		/*
		 * // Variables we can use in a message String prefix =
		 * ichat.getPrefix(p); String suffix = ichat.getSuffix(p); String group
		 * = ichat.getGroup(p); if (prefix == null) prefix = ""; if (suffix ==
		 * null) suffix = ""; if (group == null) group = ""; String healthbar =
		 * ichat.healthBar(p); String health = String.valueOf(p.getHealth());
		 * String world = p.getWorld().getName(); // Timestamp support Date now
		 * = new Date(); SimpleDateFormat dateFormat = new
		 * SimpleDateFormat(ichat.dateFormat); String time =
		 * dateFormat.format(now);
		 * 
		 * // Screwit, adding a space to make color-code glitch not kill us msg
		 * = msg + " "; // We're sending this to String.format, so we need to
		 * escape those pesky % symbols msg = msg.replaceAll("%", "%%"); //
		 * Censor message msg = ichat.censor(p, msg);
		 * 
		 * 
		 * String format = ichat.chatFormat; // Order is important, this allows
		 * us to use all variables in the suffix and prefix! But no variables in
		 * the message String[] search = new String[] {"+suffix,+s",
		 * "+prefix,+p", "+group,+g", "+healthbar,+hb", "+health,+h",
		 * "+world,+w", "+time,+t", "+name,+n", "+message,+m"}; String[] replace
		 * = new String[] { suffix, prefix, group, healthbar, health, world,
		 * time, "%1$s", msg }; event.setFormat( ichat.parse(format, search,
		 * replace) );
		 */
		// System.out.println(p.getName() + " says " + msg + " BIOTCH!");
	}

	// @Override

	// @SuppressWarnings("unused")
	/*
	 * private float pointToPoint(double xa, double za, double xb, double zb) {
	 * double deg = 0; double x = xa - xb; double y = za - zb; if (x > 0) { if
	 * (y > 0) { deg = Math.tan(x / y); } else { deg = 90 + Math.tan(y / x); } }
	 * else { if (y > 0) { deg = 270 + Math.tan(y / x); } else { deg = 180 +
	 * Math.tan(x / y); } } return (float) deg; }
	 */
}
