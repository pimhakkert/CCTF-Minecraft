package com.purehavoc.ctf_beacon.beacon;

import com.purehavoc.ctf_beacon.CTF_Beacon;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static net.md_5.bungee.api.ChatColor.*;

public class BeaconManager implements Listener {

    private final CTF_Beacon plugin;

    private BeaconEvents beaconEvents;

    private Location beaconLoc;

    private PhysicalState currentPhysicalState;

    private int beaconUnlockTimestamp;

    public enum PhysicalState {
        BLOCK, //Block placed into the world
        INVENTORY, //Slot in an entity's inventory
        DROPPED //Dropped item in the world
    }

    //TODO check if events are cancelled before acting upon them

    public BeaconManager(CTF_Beacon plugin) {
        this.plugin = plugin;
        this.beaconEvents = new BeaconEvents(this);
        plugin.getServer().getPluginManager().registerEvents(beaconEvents, plugin);

        //TODO grab current location of becaon from DATABASE
        World overworld = plugin.getServer().getWorld("world");
        beaconLoc = new Location(overworld, 0, -60, 0);

        setBeaconUnlockTimestamp((int) (System.currentTimeMillis() / 1000L));


        writeBeaconToFile(beaconLoc);


        Block temp = beaconLoc.getBlock();
        temp.setType(Material.END_PORTAL_FRAME);

        setCurrentPhysicalState(PhysicalState.BLOCK);

        lockBeacon();
    }

    public ItemMeta setupBeaconData(ItemMeta itemMeta) {
        NamespacedKey key = new NamespacedKey(plugin, "beacon.itemstack");
        boolean check = Boolean.TRUE.equals(itemMeta.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN));

        if (!check) {
            System.out.println("Creating persistent data for beacon! Should only happen once, ever.");
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        }

        itemMeta.setDisplayName("Super cool beacon!!");

        List<BaseComponent[]> components = new ArrayList<>();

        components.add(new ComponentBuilder("Hello world").color(RED).bold(true).italic(false).create());

        itemMeta.setLoreComponents(components);

        return itemMeta;
    }

    public void writeBeaconToFile(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        JSONObject coords = new JSONObject();
        JSONObject main = new JSONObject();

        coords.put("x", x);
        coords.put("y", y);
        coords.put("z", z);

        main.put("beaconLoc", coords);
        main.put("beaconUnlockTimestamp", getBeaconUnlockTimestamp());

        try {
            Files.createDirectories(Paths.get(plugin.getDataFolder().toURI()));
            File file = new File(plugin.getDataFolder() + "/beacon.json");
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(plugin.getDataFolder() + "/beacon.json");
            fileWriter.write(main.toJSONString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBeaconUnlocked() {
        int currentTimetamp = (int) (System.currentTimeMillis() / 1000L);
        System.out.println(currentTimetamp);
        System.out.println(getBeaconUnlockTimestamp());
        return currentTimetamp >= getBeaconUnlockTimestamp();
    }

    public void lockBeacon() {
        int currentTimetamp = (int) (System.currentTimeMillis() / 1000L);
//        beaconUnlockTimestamp = currentTimetamp + (60 * 60 * 2); //2 hours
        setBeaconUnlockTimestamp(currentTimetamp + 5);
    }
























    /** GETTERS & SETTERS **/

    public CTF_Beacon getPlugin() {
        return plugin;
    }

    public Location getBeaconLoc() {
        return beaconLoc;
    }

    public void setBeaconLoc(Location beaconLoc) {
        this.beaconLoc = beaconLoc;
    }

    public PhysicalState getCurrentPhysicalState() {
        return currentPhysicalState;
    }

    public void setCurrentPhysicalState(PhysicalState currentPhysicalState) {
        this.currentPhysicalState = currentPhysicalState;
    }

    public int getBeaconUnlockTimestamp() {
        return beaconUnlockTimestamp;
    }

    public void setBeaconUnlockTimestamp(int beaconUnlockTimestamp) {
        this.beaconUnlockTimestamp = beaconUnlockTimestamp;
    }
}

