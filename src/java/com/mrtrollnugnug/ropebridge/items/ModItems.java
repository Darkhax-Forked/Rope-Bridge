package com.mrtrollnugnug.ropebridge.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static Item bridgeBuilder;
	public static Item bridgeBuilderHook;
	public static Item bridgeBuilderBarrel;
	public static Item bridgeBuilderHandle;
	
	public static void createItems() {
		GameRegistry.register(bridgeBuilder = new BBItem("bridge_builder"));
		GameRegistry.register(bridgeBuilderHook = new BasicItem("bridge_builder_hook"));
		GameRegistry.register(bridgeBuilderBarrel = new BasicItem("bridge_builder_barrel"));
		GameRegistry.register(bridgeBuilderHandle = new BasicItem("bridge_builder_handle"));
	}
	
	
}
