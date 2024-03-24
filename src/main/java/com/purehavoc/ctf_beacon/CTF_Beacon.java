package com.purehavoc.ctf_beacon;

import com.dansplugins.factionsystem.MedievalFactions;
import com.purehavoc.ctf_beacon.beacon.BeaconManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CTF_Beacon extends JavaPlugin {

    private BeaconManager beaconManager;
    private MedievalFactions medievalFactions;
    public boolean unrecoverableExceptionOccurred = false;

    @Override
    public void onEnable() {
        medievalFactions = new MedievalFactions();
        beaconManager = new BeaconManager(this);
        //TODO if a player goes offline with beacon in inventory, TP beacon back to original spot.
        //TODO onEnable check if beacon is in world. If not, TP it back to original spot.

        //TODO factions: beacon can be placed anywhere, but if in a faction: start counter for that faction.
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
