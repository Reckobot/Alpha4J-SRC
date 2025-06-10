package net.minecraft.src;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Frame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;

public final class MinecraftImpl extends Minecraft {
	final Frame mainFrame;

	public MinecraftImpl(Component var1, Canvas var2, MinecraftApplet var3, int var4, int var5, boolean var6, Frame var7) {
		super(var1, var2, var3, var4, var5, var6);
		this.mainFrame = var7;
	}

	public void func_4007_a(UnexpectedThrowable var1) {
		this.mainFrame.removeAll();
		this.mainFrame.add(new PanelCrashReport(var1), "Center");
		this.mainFrame.validate();
	}
}
