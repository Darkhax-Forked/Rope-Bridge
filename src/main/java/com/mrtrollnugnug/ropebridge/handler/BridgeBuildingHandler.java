package com.mrtrollnugnug.ropebridge.handler;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.mrtrollnugnug.ropebridge.BridgeMessage;
import com.mrtrollnugnug.ropebridge.RopeBridge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class BridgeBuildingHandler {

    // TODO Wrong Package
    public static void newBridge (EntityPlayer player, float playerFov, ItemStack stack, int inputType, BlockPos pos1, BlockPos pos2) {

        final LinkedList<SlabPosHandler> bridge = new LinkedList<>();
        boolean allClear = true;

        int x1, y1, x2, y2, z, z2;
        final int Xdiff = Math.abs(pos1.getX() - pos2.getX());
        final int Zdiff = Math.abs(pos1.getZ() - pos2.getZ());
        boolean rotate;
        if (Xdiff > Zdiff) {
            rotate = false;
            x1 = pos1.getX();
            y1 = pos1.getY();
            x2 = pos2.getX();
            y2 = pos2.getY();
            z = pos1.getZ();
            z2 = pos2.getZ();
        }
        else {
            rotate = true;
            x1 = pos1.getZ();
            y1 = pos1.getY();
            x2 = pos2.getZ();
            y2 = pos2.getY();
            z = pos1.getX();
            z2 = pos2.getX();
        }
        if (Math.abs(z2 - z) > 3) {
            tell(player, "Sorry, bridge must be built in a cardinal dirrection. Please try again.");
            return;
        }
        double m;
        double b;
        double distance;
        int distInt;

        m = (double) (y2 - y1) / (double) (x2 - x1);
        if (!RopeBridge.ignoreSlopeWarnings && Math.abs(m) > 0.2) {
            tell(player, "Sorry, your slope is too great. Please try again.");
            return;
        }
        b = y1 - m * x1;
        distance = Math.abs(x2 - x1);
        distInt = Math.abs(x2 - x1);

        // Check if bridge longer than 0
        if (distInt < 2)
            // bridge too short
            return;

        // Check for materials in inventory
        if (!player.capabilities.isCreativeMode && !hasMaterials(player, distInt - 1))
            return;

        for (int x = Math.min(x1, x2) + 1; x <= Math.max(x1, x2) - 1; x++) {
            for (int y = Math.max(y1, y2); y >= Math.min(y1, y2) - distInt / 8 - 1; y--) {
                final double funcVal = m * x + b - distance / 1000 * Math.sin((x - Math.min(x1, x2)) * (Math.PI / distance)) * RopeBridge.bridgeDroopFactor + RopeBridge.bridgeYOffset;
                if (y + 0.5 > funcVal && y - 0.5 <= funcVal) {
                    int level;
                    if (funcVal >= y) {
                        if (funcVal >= y + 0.25) {
                            level = 4;
                        }
                        else {
                            level = 3;
                        }
                    }
                    else {
                        if (funcVal >= y - 0.25) {
                            level = 2;
                        }
                        else {
                            level = 1;
                        }
                    }
                    allClear = !addSlab(player.world, bridge, x, y + 1, z, level, rotate) ? false : allClear;
                }
            }
        }

        if (allClear) {
            final int type = inputType == -1 ? getWoodType(player) : inputType;
            if (inputType == -1 && !player.capabilities.isCreativeMode) {
                takeMaterials(player, distInt - 1);
                if (stack.getItemDamage() == stack.getMaxDamage()) {
                    resetFov(playerFov);
                }
                RopeBridge.snw.sendToServer(new BridgeMessage(3, 0, 0, 0, 0, 0)); // damage
                // item
            }
            // Main.snw.sendToServer(new BridgeMessage(4, 0, 0, 0, 0, 0)); // trigger
            // building
            // achievement

            buildBridge(player.world, bridge, type);
        }
        else {
            tell(player, "Oops! Looks like there's something in the way. Look for the Smoke to see where that is and try again.");
            return;
        }
    }

    private static boolean hasMaterials (EntityPlayer player, int dist) {

        if (player.capabilities.isCreativeMode)
            return true;
        final int slabsNeeded = dist;
        final int stringNeeded = 1 + Math.round(dist / 2);
        int slabsHad = 0;
        int stringHad = 0;

        for (int i = 0; i < 36; i++) {
            final ItemStack stack = player.inventory.mainInventory[i];
            if (stack == null) {
                continue;
            }
            final String name = stack.getItem().getUnlocalizedName();
            if (name.equals("item.string")) {
                stringHad += stack.stackSize;
            }
            if (name.equals("tile.woodSlab")) {
                slabsHad += stack.stackSize;
            }
        }
        if (slabsHad >= slabsNeeded && stringHad >= stringNeeded)
            return true;
        else {
            tell(player, "You need at least " + slabsNeeded + " slabs and " + stringNeeded + " strings to build this bridge.");
            return false;
        }
    }

    private static void takeMaterials (EntityPlayer player, int dist) {

        if (player.capabilities.isCreativeMode)
            return;
        int slabsNeeded = dist;
        int stringNeeded = 1 + Math.round(dist / 2);

        for (int i = 0; i < 36; i++) {
            final ItemStack stack = player.inventory.mainInventory[i];
            if (stack == null) {
                continue;
            }
            final String name = stack.getItem().getUnlocalizedName();
            if (name.equals("item.string")) {
                if (stack.stackSize > stringNeeded) {
                    stack.stackSize = stack.stackSize - stringNeeded; // TODO Potential Issue
                    RopeBridge.snw.sendToServer(new BridgeMessage(2, 0, 0, 0, i, stack.stackSize - stringNeeded));
                    stringNeeded = 0;
                }
                else {
                    stringNeeded -= stack.stackSize;
                    player.inventory.mainInventory[i] = null; // TODO Potential Issue
                    // Update on server
                    RopeBridge.snw.sendToServer(new BridgeMessage(2, 0, 0, 0, i, 0));
                    continue;
                }
            }
            if (name.equals("tile.woodSlab")) {
                if (stack.stackSize > slabsNeeded) {
                    stack.stackSize = stack.stackSize - slabsNeeded; // TODO Potential Issue
                    // Update on server
                    RopeBridge.snw.sendToServer(new BridgeMessage(2, 0, 0, 0, i, stack.stackSize - slabsNeeded));
                    slabsNeeded = 0;
                }
                else {
                    slabsNeeded -= stack.stackSize;
                    player.inventory.mainInventory[i] = null; // TODO Potential Issue
                    // update on server
                    RopeBridge.snw.sendToServer(new BridgeMessage(2, 0, 0, 0, i, 0));
                    continue;
                }
            }
        }
    }

    // Controls Removing Slabs + Building Physical Bridge
    private static boolean addSlab (World world, LinkedList<SlabPosHandler> list, int x, int y, int z, int level, boolean rotate) {

        boolean isClear;
        BlockPos pos;
        if (rotate) {
            pos = new BlockPos(z, y, x);
        }
        else {
            pos = new BlockPos(x, y, z);
        }
        isClear = RopeBridge.breakThroughBlocks || world.isAirBlock(pos) || world.getBlockState(pos).getBlock().isReplaceable(world, pos);
        list.add(new SlabPosHandler(pos, level, rotate));
        if (!isClear) {
            spawnSmoke(world, pos, 15);
        }
        return isClear;
    }

    // Controls if blocks are in the way
    private static void spawnSmoke (World world, BlockPos pos, int times) {

        if (times > 0) {
            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D, new int[0]);
            final World finworld = world;
            final BlockPos finPos = pos;
            final int finTimes = times - 1;
            new Timer().schedule(new TimerTask() {

                @Override
                public void run () {

                    spawnSmoke(finworld, finPos, finTimes);
                }
            }, 1000);
        }
    }

    private static void buildBridge (World world, LinkedList<SlabPosHandler> bridge, int type) {

        SlabPosHandler slab;
        if (!bridge.isEmpty()) {
            slab = bridge.pop();
            // Server call build x y z
            RopeBridge.snw.sendToServer(new BridgeMessage(1, slab.x, slab.y, slab.z, slab.level, (slab.rotate ? 1 : 0) + 2 * type));

            spawnSmoke(world, new BlockPos(slab.x, slab.y, slab.z), 1);
            // play sound at x y z wood
            // Main.snw.sendToServer(new BridgeMessage(0, slab.x, slab.y, slab.z, 1, 0));

            final World finworld = world;
            final LinkedList<SlabPosHandler> finBridge = bridge;
            final int finType = type;
            new Timer().schedule(new TimerTask() {

                @Override
                public void run () {

                    buildBridge(finworld, finBridge, finType);
                }
            }, 100);
        }
    }

    private static int getWoodType (EntityPlayer player) {

        for (final ItemStack stack : player.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }
            final String name = stack.getItem().getUnlocalizedName();
            if (name.equals("tile.woodSlab"))
                return stack.getItemDamage();
        }
        return 0;
    }

    private static void resetFov (float toFov) {

        if (RopeBridge.zoomOnAim && toFov != 0) {
            Minecraft.getMinecraft().gameSettings.fovSetting = toFov;
        }
    }

    private static void tell (EntityPlayer playerIn, String message) {

        playerIn.sendMessage(new TextComponentString("[Rope Bridge]: " + message).setStyle(new Style().setColor(TextFormatting.DARK_AQUA)));
    }
}
