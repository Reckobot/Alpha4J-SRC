package net.minecraft.src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static org.lwjgl.opengl.GL14.glBlendColor;

public class GuiIngame extends Gui {
	private static RenderItem itemRenderer = new RenderItem();
	private List chatMessageList = new ArrayList();
	private Random rand = new Random();
	private Minecraft mc;
	public String field_933_a = null;
	private int updateCounter = 0;
	private String field_9420_i = "";
	private int field_9419_j = 0;
	public float field_6446_b;
	float field_931_c = 1.0F;

	int tooltipX = 0;

	public float opacity = 0.0F;

	public GuiIngame(Minecraft var1) {
		this.mc = var1;
	}

	public void renderToolTip(String key, String text, int length) {
		float scale = 0.75F;
		GL11.glScalef(scale, scale, scale);
		ScaledResolution var5 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		int var6 = var5.getScaledWidth();
		int var7 = var5.getScaledHeight();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, this.opacity);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/key.png"));
		this.drawTexturedModalRect((int)(24*(1F/scale))+tooltipX, (int)((var7 - 24)*(1F/scale)), 0, 0, 16, 16);

		this.drawString(this.mc.fontRenderer, key, ((int)(24*(1F/scale)) + tooltipX) + 5, (int)((var7 - 24)*(1F/scale)) + 4, 16777215, this.opacity);
		this.drawString(this.mc.fontRenderer, text, ((int)(24*(1F/scale)) + tooltipX) + 20, (int)((var7 - 24)*(1F/scale)) + 4, 16777215, this.opacity);

		tooltipX += length;

		GL11.glScalef(1F/scale, 1F/scale, 1F/scale);
	}

	public void renderMouseToolTip(String tex, String text, int length) {
		float scale = 0.75F;
		GL11.glScalef(scale, scale, scale);
		ScaledResolution var5 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		int var6 = var5.getScaledWidth();
		int var7 = var5.getScaledHeight();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, this.opacity);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture(tex));
		this.drawTexturedModalRect((int)(24*(1F/scale))+tooltipX, (int)((var7 - 24)*(1F/scale)), 0, 0, 16, 16);

		this.drawString(this.mc.fontRenderer, text, ((int)(24*(1F/scale)) + tooltipX) + 18, (int)((var7 - 24)*(1F/scale)) + 4, 16777215, this.opacity);

		tooltipX += length;

		GL11.glScalef(1F/scale, 1F/scale, 1F/scale);
	}

	public void renderGameOverlay(float var1, boolean var2, int var3, int var4, boolean valid) {
		ScaledResolution var5 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		int var6 = var5.getScaledWidth();
		int var7 = var5.getScaledHeight();
		FontRenderer var8 = this.mc.fontRenderer;
		this.mc.field_9243_r.func_905_b();
		GL11.glEnable(GL11.GL_BLEND);
		if(valid) {
			if (this.mc.gameSettings.fancyGraphics) {
				this.func_4064_a(this.mc.thePlayer.getEntityBrightness(var1), var6, var7);
			}
		}

		ItemStack var9 = this.mc.thePlayer.inventory.armorItemInSlot(3);
		if(valid) {
			if (!this.mc.gameSettings.thirdPersonView && var9 != null && var9.itemID == Block.pumpkin.blockID) {
				this.func_4063_a(var6, var7);
			}
		}

		float var10 = this.mc.thePlayer.field_4133_d + (this.mc.thePlayer.field_4134_c - this.mc.thePlayer.field_4133_d) * var1;
		if(valid) {
			if (var10 > 0.0F) {
				this.func_4065_b(var10, var6, var7);
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, this.opacity);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/gui.png"));
		InventoryPlayer var11 = this.mc.thePlayer.inventory;
		this.zLevel = -90.0F;
		if(valid) {
			this.drawTexturedModalRect(var6 / 2 - 91, var7 - 22 - (int) (this.mc.gameSettings.offset * 100), 0, 0, 182, 22);
			this.drawTexturedModalRect(var6 / 2 - 91 - 1 + var11.currentItem * 20, var7 - 22 - 1 - (int) (this.mc.gameSettings.offset * 100), 0, 22, 24, 24);
		}
		this.tooltipX = 0;
		if(valid && this.mc.gameSettings.tooltipsEnabled) {
			renderToolTip(this.mc.gameSettings.getKeyBinding(7).toUpperCase().substring(11), "Inventory", 76);
			ItemStack itm = this.mc.thePlayer.inventory.getCurrentItem();

			boolean doneMouseL = false;
			boolean doneMouseR = false;

			if(this.mc.objectMouseOver != null) {
				int blockX = this.mc.objectMouseOver.blockX;
				int blockY = this.mc.objectMouseOver.blockY;
				int blockZ = this.mc.objectMouseOver.blockZ;
				int sideHit = this.mc.objectMouseOver.sideHit;
				if (Block.blocksList[this.mc.theWorld.getBlockId(blockX, blockY, blockZ)].isInteractive()) {
					renderMouseToolTip("/gui/mouseR.png", "Interact", 64);
					doneMouseR = true;
				} else if (itm != null && itm.getItem().isBlock()) {
					renderMouseToolTip("/gui/mouseR.png", "Place", 48);
				}
			}

			if(itm != null && !doneMouseR) {
				if (itm.getItem().isFood()) {
					renderMouseToolTip("/gui/mouseR.png", "Consume", 64);
					doneMouseR = true;
				}
			}

			if (this.mc.objectMouseOver != null) {
				int blockX = this.mc.objectMouseOver.blockX;
				int blockY = this.mc.objectMouseOver.blockY;
				int blockZ = this.mc.objectMouseOver.blockZ;
				int sideHit = this.mc.objectMouseOver.sideHit;
				if (this.mc.objectMouseOver.entityHit != null) {
					renderMouseToolTip("/gui/mouseL.png", "Attack", 36);
				} else {
					renderMouseToolTip("/gui/mouseL.png", "Mine", 24);
				}
			}
		}
		if(valid) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
			glBlendColor(1.0F, 1.0F, 1.0F, this.opacity);
			GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_CONSTANT_ALPHA);
			this.drawTexturedModalRect(var6 / 2 - 7, var7 / 2 - 7, 0, 0, 16, 16);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			boolean var12 = this.mc.thePlayer.field_9306_bj / 3 % 2 == 1;
			if (this.mc.thePlayer.field_9306_bj < 10) {
				var12 = false;
			}

			int var13 = this.mc.thePlayer.health;
			int var14 = this.mc.thePlayer.field_9335_K;
			this.rand.setSeed((long) (this.updateCounter * 312871));
			int var15;
			int var16;
			int var17;
			if (this.mc.field_6327_b.func_6469_d()) {
				var15 = this.mc.thePlayer.getPlayerArmorValue();

				int var18;
				for (var16 = 0; var16 < 10; ++var16) {
					var17 = var7 - 32;
					if (var15 > 0) {
						var18 = var6 / 2 + 91 - var16 * 8 - 9;
						if (var16 * 2 + 1 < var15) {
							this.drawTexturedModalRect(var18, var17 - (int) (this.mc.gameSettings.offset * 100), 34, 9, 9, 9);
						}

						if (var16 * 2 + 1 == var15) {
							this.drawTexturedModalRect(var18, var17 - (int) (this.mc.gameSettings.offset * 100), 25, 9, 9, 9);
						}

						if (var16 * 2 + 1 > var15) {
							this.drawTexturedModalRect(var18, var17 - (int) (this.mc.gameSettings.offset * 100), 16, 9, 9, 9);
						}
					}

					byte var27 = 0;
					if (var12) {
						var27 = 1;
					}

					int var19 = var6 / 2 - 91 + var16 * 8;
					if (var13 <= 4) {
						var17 += this.rand.nextInt(2);
					}

					this.drawTexturedModalRect(var19, var17 - (int) (this.mc.gameSettings.offset * 100), 16 + var27 * 9, 0, 9, 9);
					if (var12) {
						if (var16 * 2 + 1 < var14) {
							this.drawTexturedModalRect(var19, var17 - (int) (this.mc.gameSettings.offset * 100), 70, 0, 9, 9);
						}

						if (var16 * 2 + 1 == var14) {
							this.drawTexturedModalRect(var19, var17 - (int) (this.mc.gameSettings.offset * 100), 79, 0, 9, 9);
						}
					}

					if (var16 * 2 + 1 < var13) {
						this.drawTexturedModalRect(var19, var17 - (int) (this.mc.gameSettings.offset * 100), 52, 0, 9, 9);
					}

					if (var16 * 2 + 1 == var13) {
						this.drawTexturedModalRect(var19, var17 - (int) (this.mc.gameSettings.offset * 100), 61, 0, 9, 9);
					}
				}

				if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
					var16 = (int) Math.ceil((double) (this.mc.thePlayer.air - 2) * 10.0D / 300.0D);
					var17 = (int) Math.ceil((double) this.mc.thePlayer.air * 10.0D / 300.0D) - var16;

					for (var18 = 0; var18 < var16 + var17; ++var18) {
						if (var18 < var16) {
							this.drawTexturedModalRect(var6 / 2 - 91 + var18 * 8, var7 - 32 - 9 - (int) (this.mc.gameSettings.offset * 100), 16, 18, 9, 9);
						} else {
							this.drawTexturedModalRect(var6 / 2 - 91 + var18 * 8, var7 - 32 - 9 - (int) (this.mc.gameSettings.offset * 100), 25, 18, 9, 9);
						}
					}
				}
			}

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();

			for (var15 = 0; var15 < 9; ++var15) {
				var16 = var6 / 2 - 90 + var15 * 20 + 2;
				var17 = var7 - 16 - 3;
				this.func_554_a(var15, var16, var17, var1);
			}

			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			String var23;
			if (this.mc.f3Enabled) {
				var8.drawStringWithShadow("Minecraft Alpha4J (" + this.mc.field_6292_I + ")", 2, 2, 16777215);
				var8.drawStringWithShadow(this.mc.func_6241_m(), 2, 12, 16777215);
				var8.drawStringWithShadow(this.mc.func_6262_n(), 2, 22, 16777215);
				var8.drawStringWithShadow(this.mc.func_6245_o(), 2, 32, 16777215);
				long var24 = Runtime.getRuntime().maxMemory();
				long var29 = Runtime.getRuntime().totalMemory();
				long var30 = Runtime.getRuntime().freeMemory();
				long var21 = var29 - var30;
				var23 = "Used memory: " + var21 * 100L / var24 + "% (" + var21 / 1024L / 1024L + "MB) of " + var24 / 1024L / 1024L + "MB";
				this.drawString(var8, var23, var6 - var8.getStringWidth(var23) - 2, 2, 14737632, 1.0F);
				var23 = "Allocated memory: " + var29 * 100L / var24 + "% (" + var29 / 1024L / 1024L + "MB)";
				this.drawString(var8, var23, var6 - var8.getStringWidth(var23) - 2, 12, 14737632, 1.0F);
				this.drawString(var8, "x: " + this.mc.thePlayer.posX, 2, 64, 14737632, 1.0F);
				this.drawString(var8, "y: " + this.mc.thePlayer.posY, 2, 72, 14737632, 1.0F);
				this.drawString(var8, "z: " + this.mc.thePlayer.posZ, 2, 80, 14737632, 1.0F);
			} else if (this.mc.gameSettings.watermark) {
				var8.drawStringWithShadow("Minecraft Alpha v1.2.6", 2, 2, 16777215);
			}

			if (this.field_9419_j > 0) {
				float var25 = (float) this.field_9419_j - var1;
				var16 = (int) (var25 * 256.0F / 20.0F);
				if (var16 > 255) {
					var16 = 255;
				}

				if (var16 > 0) {
					GL11.glPushMatrix();
					GL11.glTranslatef((float) (var6 / 2), (float) (var7 - 48), 0.0F);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					var17 = Color.HSBtoRGB(var25 / 50.0F, 0.7F, 0.6F) & 16777215;
					var8.drawString(this.field_9420_i, -var8.getStringWidth(this.field_9420_i) / 2, -4, var17 + (var16 << 24));
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}
			}

			byte var26 = 10;
			boolean var28 = false;
			if (this.mc.currentScreen instanceof GuiChat) {
				var26 = 20;
				var28 = true;
			}

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, (float) (var7 - 48 - (int) (this.mc.gameSettings.offset * 100)), 0.0F);

			for (var17 = 0; var17 < this.chatMessageList.size() && var17 < var26; ++var17) {
				if (((ChatLine) this.chatMessageList.get(var17)).updateCounter < 200 || var28) {
					double var31 = (double) ((ChatLine) this.chatMessageList.get(var17)).updateCounter / 200.0D;
					var31 = 1.0D - var31;
					var31 *= 10.0D;
					if (var31 < 0.0D) {
						var31 = 0.0D;
					}

					if (var31 > 1.0D) {
						var31 = 1.0D;
					}

					var31 *= var31;
					int var20 = (int) (255.0D * var31);
					if (var28) {
						var20 = 255;
					}

					if (var20 > 0) {
						byte var32 = 2;
						int var22 = -var17 * 9;
						var23 = ((ChatLine) this.chatMessageList.get(var17)).message;
						this.drawRect(var32, var22 - 1, var32 + 320, var22 + 8, var20 / 2 << 24);
						GL11.glEnable(GL11.GL_BLEND);
						var8.drawStringWithShadow(var23, var32, var22, 16777215 + (var20 << 24));
					}
				}
			}

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	private void func_4063_a(int var1, int var2) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/misc/pumpkinblur.png"));
		Tessellator var3 = Tessellator.instance;
		var3.startDrawingQuads();
		var3.addVertexWithUV(0.0D, (double)var2, -90.0D, 0.0D, 1.0D);
		var3.addVertexWithUV((double)var1, (double)var2, -90.0D, 1.0D, 1.0D);
		var3.addVertexWithUV((double)var1, 0.0D, -90.0D, 1.0D, 0.0D);
		var3.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		var3.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void func_4064_a(float var1, int var2, int var3) {
		var1 = 1.0F - var1;
		if(var1 < 0.0F) {
			var1 = 0.0F;
		}

		if(var1 > 1.0F) {
			var1 = 1.0F;
		}

		this.field_931_c = (float)((double)this.field_931_c + (double)(var1 - this.field_931_c) * 0.01D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
		GL11.glColor4f(this.field_931_c, this.field_931_c, this.field_931_c, 1.0F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
		Tessellator var4 = Tessellator.instance;
		var4.startDrawingQuads();
		var4.addVertexWithUV(0.0D, (double)var3, -90.0D, 0.0D, 1.0D);
		var4.addVertexWithUV((double)var2, (double)var3, -90.0D, 1.0D, 1.0D);
		var4.addVertexWithUV((double)var2, 0.0D, -90.0D, 1.0D, 0.0D);
		var4.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		var4.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void func_4065_b(float var1, int var2, int var3) {
		var1 *= var1;
		var1 *= var1;
		var1 = var1 * 0.8F + 0.2F;
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, var1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
		float var4 = (float)(Block.portal.blockIndexInTexture % 16) / 16.0F;
		float var5 = (float)(Block.portal.blockIndexInTexture / 16) / 16.0F;
		float var6 = (float)(Block.portal.blockIndexInTexture % 16 + 1) / 16.0F;
		float var7 = (float)(Block.portal.blockIndexInTexture / 16 + 1) / 16.0F;
		Tessellator var8 = Tessellator.instance;
		var8.startDrawingQuads();
		var8.addVertexWithUV(0.0D, (double)var3, -90.0D, (double)var4, (double)var7);
		var8.addVertexWithUV((double)var2, (double)var3, -90.0D, (double)var6, (double)var7);
		var8.addVertexWithUV((double)var2, 0.0D, -90.0D, (double)var6, (double)var5);
		var8.addVertexWithUV(0.0D, 0.0D, -90.0D, (double)var4, (double)var5);
		var8.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void func_554_a(int var1, int var2, int var3, float var4) {
		ItemStack var5 = this.mc.thePlayer.inventory.mainInventory[var1];
		if(var5 != null) {
			float var6 = (float)var5.animationsToGo - var4;
			if(var6 > 0.0F) {
				GL11.glPushMatrix();
				float var7 = 1.0F + var6 / 5.0F;
				GL11.glTranslatef((float)(var2 + 8), (float)(var3 + 12), 0.0F);
				GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef((float)(-(var2 + 8)), (float)(-(var3 + 12)), 0.0F);
			}

			itemRenderer.renderItemIntoGUIAlpha(this.mc.fontRenderer, this.mc.renderEngine, var5, var2, var3 - (int)(this.mc.gameSettings.offset*100), this.opacity);
			if(var6 > 0.0F) {
				GL11.glPopMatrix();
			}

			itemRenderer.renderItemOverlayIntoGUIAlpha(this.mc.fontRenderer, this.mc.renderEngine, var5, var2, var3 - (int)(this.mc.gameSettings.offset*100), this.opacity);
		}
	}

	public void func_555_a() {
		if(this.field_9419_j > 0) {
			--this.field_9419_j;
		}

		++this.updateCounter;

		this.opacity -= 1/20F;
		if(this.opacity < this.mc.gameSettings.opacity) {
			this.opacity = this.mc.gameSettings.opacity;
		}

		for(int var1 = 0; var1 < this.chatMessageList.size(); ++var1) {
			++((ChatLine)this.chatMessageList.get(var1)).updateCounter;
		}

	}

	public void addChatMessage(String var1) {
		while(this.mc.fontRenderer.getStringWidth(var1) > 320) {
			int var2;
			for(var2 = 1; var2 < var1.length() && this.mc.fontRenderer.getStringWidth(var1.substring(0, var2 + 1)) <= 320; ++var2) {
			}

			this.addChatMessage(var1.substring(0, var2));
			var1 = var1.substring(var2);
		}

		this.chatMessageList.add(0, new ChatLine(var1));

		while(this.chatMessageList.size() > 50) {
			this.chatMessageList.remove(this.chatMessageList.size() - 1);
		}

	}

	public void func_553_b(String var1) {
		this.field_9420_i = "Now playing: " + var1;
		this.field_9419_j = 60;
	}
}
