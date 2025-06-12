package net.minecraft.src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.client.Minecraft;

public class GuiSelectWorld extends GuiScreen {
	protected GuiScreen parentScreen;
	protected String screenTitle = "Select world";
	private boolean selected = false;

	public GuiSelectWorld(GuiScreen var1) {
		this.parentScreen = var1;
	}

	public void initGui() {
		File var1 = Minecraft.getMinecraftDir();

		for(int var2 = -1; var2 < 5; ++var2) {
			NBTTagCompound var3 = World.func_629_a(var1, "World" + (var2 + 1));

			String emptyString = "- empty -";
			String occupiedString = "World " + (var2 + 1);
			int buttonOffset = 32;

			if(var2 == -1) {
				occupiedString = "Continue tutorial";
				emptyString = "Play tutorial";
				buttonOffset = 18;
			}

			if(var3 == null) {
				this.controlList.add(new GuiButton(var2, this.width / 2 - 100, this.height / 6 + 22 * var2 + buttonOffset, emptyString));
			} else {
				long var5 = var3.getLong("SizeOnDisk");
				if(var2 != -1) {
					occupiedString = occupiedString + " (" + (float) (var5 / 1024L * 100L / 1024L) / 100.0F + " MB)";
				}
				this.controlList.add(new GuiButton(var2, this.width / 2 - 100, this.height / 6 + 22 * var2 + buttonOffset, occupiedString));
			}
		}

		this.initGui2();
	}

	protected String getWorldName(int var1) {
		File var2 = Minecraft.getMinecraftDir();
		return World.func_629_a(var2, "World" + var1) != null ? "World" + var1 : null;
	}

	public void initGui2() {
		this.controlList.add(new GuiButton(5, this.width / 2 - 100, this.height - 58, "Delete world..."));
		this.controlList.add(new GuiButton(6, this.width / 2 - 100, this.height - 34, "Cancel"));
	}

	protected void actionPerformed(GuiButton var1) throws IOException {
		if(var1.enabled) {
			if(var1.id < 5) {
				this.selectWorld(var1.id + 1);
			} else if(var1.id == 5) {
				this.mc.displayGuiScreen(new GuiDeleteWorld(this));
			} else if(var1.id == 6) {
				this.mc.displayGuiScreen(this.parentScreen);
			}

		}
	}

	public void selectWorld(int var1) throws IOException {
		this.mc.displayGuiScreen((GuiScreen)null);
		if(!this.selected) {
			if(var1 == 0) {
				if(World.func_629_a(Minecraft.getMinecraftDir(), "World0") == null) {
					this.mc.unzip("/tutorial.zip", Minecraft.getMinecraftDir().getAbsolutePath() + "/saves/World0");
				}
			}

			this.selected = true;
			this.mc.field_6327_b = new PlayerControllerSP(this.mc);
			this.mc.func_6247_b("World" + var1);
			this.mc.displayGuiScreen((GuiScreen)null);
		}
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
		super.drawScreen(var1, var2, var3);
	}
}
