package net.minecraft.src;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends Gui {
	protected Minecraft mc;
	public int width;
	public int height;
	protected List controlList = new ArrayList();
	public boolean field_948_f = false;
	protected FontRenderer fontRenderer;
	private GuiButton selectedButton = null;

	public void drawScreen(int var1, int var2, float var3) {
		for(int var4 = 0; var4 < this.controlList.size(); ++var4) {
			GuiButton var5 = (GuiButton)this.controlList.get(var4);
			var5.drawButton(this.mc, var1, var2);
		}

	}

	protected void keyTyped(char var1, int var2) {
		if(var2 == 1) {
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.func_6259_e();
		}

	}

	public static String getClipboardString() {
		try {
			Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);
			if(var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String var1 = (String)var0.getTransferData(DataFlavor.stringFlavor);
				return var1;
			}
		} catch (Exception var2) {
		}

		return null;
	}

	protected void mouseClicked(int var1, int var2, int var3) throws IOException, InterruptedException {
		if(var3 == 0) {
			for(int var4 = 0; var4 < this.controlList.size(); ++var4) {
				GuiButton var5 = (GuiButton)this.controlList.get(var4);
				if(var5.mousePressed(this.mc, var1, var2)) {
					this.selectedButton = var5;
					this.mc.sndManager.func_337_a("random.click", 1.0F, 1.0F);
					this.actionPerformed(var5);
				}
			}
		}

	}

	protected void mouseMovedOrUp(int var1, int var2, int var3) {
		if(this.selectedButton != null && var3 == 0) {
			this.selectedButton.mouseReleased(var1, var2);
			this.selectedButton = null;
		}

	}

	protected void actionPerformed(GuiButton var1) throws IOException, InterruptedException {
	}

	public void setWorldAndResolution(Minecraft var1, int var2, int var3) {
		this.mc = var1;
		this.fontRenderer = var1.fontRenderer;
		this.width = var2;
		this.height = var3;
		this.controlList.clear();
		this.initGui();
	}

	public void initGui() {
	}

	public void handleInput() throws IOException, InterruptedException {
		while(Mouse.next()) {
			this.handleMouseInput();
		}

		while(Keyboard.next()) {
			this.handleKeyboardInput();
		}

	}

	public void handleMouseInput() throws IOException, InterruptedException {
		int var1;
		int var2;
		if(Mouse.getEventButtonState()) {
			var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
			var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			this.mouseClicked(var1, var2, Mouse.getEventButton());
		} else {
			var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
			var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			this.mouseMovedOrUp(var1, var2, Mouse.getEventButton());
		}

	}

	public void handleKeyboardInput() {
		if(Keyboard.getEventKeyState()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_F11) {
				this.mc.toggleFullscreen();
				return;
			}

			this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
		}

	}

	public void updateScreen() {
	}

	public void onGuiClosed() {
	}

	public void drawDefaultBackground() {
		this.func_567_a(0);
	}

	public void func_567_a(int var1) {
		if(this.mc.theWorld != null) {
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.drawBackground(var1);
		}

	}

	public void drawBackground(int var1) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		Tessellator tess = Tessellator.instance;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/background.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float val = this.height;
		float widthMult = 4F;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0.0D - this.mc.panoramaTimer, (double)this.height, 0.0D, 0.0D, (double)((float)this.height / val + (float)var1));
		tess.addVertexWithUV((double)this.width * widthMult - this.mc.panoramaTimer, (double)this.height, 0.0D, (double)((float)this.width / val), (double)((float)this.height / val + (float)var1));
		tess.addVertexWithUV((double)this.width * widthMult - this.mc.panoramaTimer, 0.0D, 0.0D, (double)((float)this.width / val), (double)(0 + var1));
		tess.addVertexWithUV(0.0D - this.mc.panoramaTimer, 0.0D, 0.0D, 0.0D, (double)(0 + var1));
		tess.draw();
	}

	public boolean doesGuiPauseGame() {
		return true;
	}

	public void deleteWorld(boolean var1, int var2) {
	}
}
