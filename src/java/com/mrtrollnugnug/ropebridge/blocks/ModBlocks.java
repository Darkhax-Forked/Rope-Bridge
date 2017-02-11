package com.mrtrollnugnug.ropebridge.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlocks {
	
	public static Block bridgeBlock1;
	public static Block bridgeBlock2;
	public static Block bridgeBlock3;
	public static Block bridgeBlock4;
	public static void createBlocks() {
		GameRegistry.register(bridgeBlock1 = new BridgeSlab1("bridge_block_1"));
		GameRegistry.register(bridgeBlock2 = new BridgeSlab2("bridge_block_2"));
		GameRegistry.register(bridgeBlock3 = new BridgeSlab3("bridge_block_3"));
		GameRegistry.register(bridgeBlock4 = new BridgeSlab4("bridge_block_4"));
	}
}
