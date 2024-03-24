package com.purehavoc.ctf_beacon.beacon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

public class BeaconEvents implements Listener {

    private final BeaconManager beaconManager;

    public BeaconEvents(BeaconManager beaconManager) {
        this.beaconManager = beaconManager;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        if(itemInHand.getType() != Material.END_PORTAL_FRAME) {
            return;
        }

        //Grab the item from hand. If it has the data we need, it's a beacon block
        NamespacedKey key = new NamespacedKey(beaconManager.getPlugin(), "beacon.itemstack");
        boolean check = Boolean.TRUE.equals(itemInHand.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN));

        if(!check) {
            //Not THE beacon, but just a END_PORTAL_FRAME
            return;
        }

        //Item IS beacon!
        beaconManager.setBeaconLoc(event.getBlockPlaced().getLocation());
        beaconManager.writeBeaconToFile(beaconManager.getBeaconLoc());

        beaconManager.setCurrentPhysicalState(BeaconManager.PhysicalState.BLOCK);
        event.getPlayer().getInventory().remove(itemInHand);

        beaconManager.lockBeacon();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Location blockLocation = event.getClickedBlock().getLocation();
        int x = blockLocation.getBlockX();
        int y = blockLocation.getBlockY();
        int z = blockLocation.getBlockZ();

        Player player = event.getPlayer();
        Location beaconLoc = beaconManager.getBeaconLoc();


        //If the beacon is placed, beaconLoc will always be the correct location of the beacon
        if(beaconLoc.getBlockX() == x && beaconLoc.getBlockY() == y && beaconLoc.getBlockZ() == z) {
            if(beaconLoc.getBlock().getType() == Material.END_PORTAL_FRAME) {

                if(!beaconManager.isBeaconUnlocked()) {
                    player.sendMessage("The beacon is not unlocked yet!");
                    return;
                }

                if(player.getInventory().firstEmpty() == -1) {
                    player.sendMessage("Your inventory is full!");
                    return;
                }

                //TODO when locking/unlocking is made, ensure that beacon is not currently locked



                //TODO write logic for when player clicks on beacon
                Bukkit.broadcastMessage(player.getName() + " picked up the beacon!");

                //Remove beacon
                beaconLoc.getBlock().setType(Material.AIR);
                beaconManager.setCurrentPhysicalState(BeaconManager.PhysicalState.INVENTORY);

                //Create itemstack in inventory with custom data
                ItemStack beaconItem = new ItemStack(Material.END_PORTAL_FRAME, 1);

                //Persist that THIS itemstack is the beacon
                ItemMeta itemMeta = beaconItem.getItemMeta();
                beaconItem.setItemMeta(beaconManager.setupBeaconData(itemMeta));

                player.getInventory().addItem(beaconItem);

                //When a player picks up a beacon, the beacon is non-lockable for X hours.
                //The player receives a ItemStack in their inventory (if there is space) with custom data.
                //TODO write above as well

            } else {
                //TODO write error
//                this.plugin.getLogger().severe("");
//                this.plugin.unrecoverableExceptionOccurred = true;
            }
        }
    }


    //Prevent the beacon from ever accidentally being broken by someone in creative or otherwise
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        int x = blockLocation.getBlockX();
        int y = blockLocation.getBlockY();
        int z = blockLocation.getBlockZ();

        Location beaconLoc = beaconManager.getBeaconLoc();

        //If the beacon is placed, beaconLoc will always be the correct location of the beacon
        if(beaconLoc.getBlockX() == x && beaconLoc.getBlockY() == y && beaconLoc.getBlockZ() == z) {

            if(beaconLoc.getBlock().getType() == Material.END_PORTAL_FRAME) {
                //Prevent breaking, even by those in creative
                event.setCancelled(true);
            } else {
                //TODO write error
//                this.plugin.getLogger().severe("");
//                this.plugin.unrecoverableExceptionOccurred = true;
            }
        }
    }

}
