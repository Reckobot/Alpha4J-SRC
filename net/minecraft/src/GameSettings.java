package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class GameSettings {
	private static final String[] RENDER_DISTANCES = new String[]{"FAR", "NORMAL", "SHORT", "TINY"};
	private static final String[] DIFFICULTY_LEVELS = new String[]{"Peaceful", "Easy", "Normal", "Hard"};
	public float musicVolume = 1.0F;
	public float soundVolume = 1.0F;
	public float mouseSensitivity = 0.5F;
	public boolean invertMouse = false;
	public int renderDistance = 0;
	public boolean viewBobbing = true;
	public boolean anaglyph = false;
	public boolean limitFramerate = false;
	public boolean fancyGraphics = true;
	public String skin = "Default";
	public KeyBinding keyBindForward = new KeyBinding("Forward", 17);
	public KeyBinding keyBindLeft = new KeyBinding("Left", 30);
	public KeyBinding keyBindBack = new KeyBinding("Back", 31);
	public KeyBinding keyBindRight = new KeyBinding("Right", 32);
	public KeyBinding keyBindJump = new KeyBinding("Jump", 57);
	public KeyBinding keyBindInventory = new KeyBinding("Inventory", 18);
	public KeyBinding keyBindDrop = new KeyBinding("Drop", 16);
	public KeyBinding keyBindChat = new KeyBinding("Chat", 20);
	public KeyBinding keyBindToggleFog = new KeyBinding("Toggle fog", 33);
	public KeyBinding keyBindSneak = new KeyBinding("Sneak", 42);
	public KeyBinding[] keyBindings = new KeyBinding[]{this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindToggleFog};
	protected Minecraft mc;
	private File optionsFile;
	public int numberOfOptions = 16;
	public int difficulty = 2;
	public boolean thirdPersonView = false;
	public String field_12259_z = "";

	public float gamma = 1.0F;
	public float opacity = 0.5F;
	public float offset = 0.34F;
	public boolean watermark = false;
	public boolean tooltipsEnabled = true;
	public boolean mipmapping = true;

	public GameSettings(Minecraft var1, File var2) {
		this.mc = var1;
		this.optionsFile = new File(var2, "options.txt");
		this.loadOptions();
	}

	public GameSettings() {
	}

	public String getKeyBinding(int var1) {
		return this.keyBindings[var1].keyDescription + ": " + Keyboard.getKeyName(this.keyBindings[var1].keyCode);
	}

	public void setKeyBinding(int var1, int var2) {
		this.keyBindings[var1].keyCode = var2;
		this.saveOptions();
	}

	public void setOptionFloatValue(int var1, float var2) {
		if(var1 == 0) {
			this.musicVolume = var2;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(var1 == 1) {
			this.soundVolume = var2;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(var1 == 3) {
			this.mouseSensitivity = var2;
		}

		if(var1 == 10) {
			this.gamma = Math.round(var2 * 100)/100F;
		}

		if(var1 == 11) {
			this.opacity = Math.round(var2 * 100)/100F;
		}

		if(var1 == 12) {
			this.offset = Math.round(var2 * 100)/100F;
		}

	}

	public void setOptionValue(int var1, int var2) {
		if(var1 == 2) {
			this.invertMouse = !this.invertMouse;
		}

		if(var1 == 4) {
			this.renderDistance = this.renderDistance + var2 & 3;
		}

		if(var1 == 5) {
			this.viewBobbing = !this.viewBobbing;
		}

		if(var1 == 6) {
			this.anaglyph = !this.anaglyph;
			this.mc.renderEngine.refreshTextures();
		}

		if(var1 == 7) {
			this.limitFramerate = !this.limitFramerate;
		}

		if(var1 == 8) {
			this.difficulty = this.difficulty + var2 & 3;
		}

		if(var1 == 9) {
			this.fancyGraphics = !this.fancyGraphics;
			this.mc.field_6323_f.func_958_a();
		}

		if(var1 == 13) {
			this.watermark = !this.watermark;
		}

		if(var1 == 14) {
			this.tooltipsEnabled = !this.tooltipsEnabled;
		}

		if(var1 == 15) {
			this.mipmapping = !this.mipmapping;
		}

		this.saveOptions();
	}

	public int getOptionControlType(int var1) {
		return var1 == 12 ? 1 : (var1 == 11 ? 1 : (var1 == 10 ? 1 : (var1 == 0 ? 1 : (var1 == 1 ? 1 : (var1 == 3 ? 1 : 0)))));
	}

	public float getOptionFloatValue(int var1) {
		return var1 == 12 ? this.offset : (var1 == 11 ? this.opacity : (var1 == 10 ? this.gamma : (var1 == 0 ? this.musicVolume : (var1 == 1 ? this.soundVolume : (var1 == 3 ? this.mouseSensitivity : 0.0F)))));
	}

	public String getOptionDisplayString(int var1) {
		return var1 == 15 ? "Mipmapping: " + (this.mipmapping ? "ON" : "OFF") : (var1 == 14 ? "Tooltips: " + (this.tooltipsEnabled ? "ON" : "OFF") : (var1 == 13 ? "Alpha watermark: " + (this.watermark ? "ON" : "OFF") : (var1 == 12 ? "Interface offset: " + ((int)(this.offset * 100.0F)) : (var1 == 11 ? "Interface opacity: " + ((int)(this.opacity * 100.0F) + "%") : (var1 == 10 ? "Gamma: " + ((int)(this.gamma * 100.0F) + "%") : (var1 == 0 ? "Music: " + (this.musicVolume > 0.0F ? (int)(this.musicVolume * 100.0F) + "%" : "OFF") : (var1 == 1 ? "Sound: " + (this.soundVolume > 0.0F ? (int)(this.soundVolume * 100.0F) + "%" : "OFF") : (var1 == 2 ? "Invert mouse: " + (this.invertMouse ? "ON" : "OFF") : (var1 == 3 ? (this.mouseSensitivity == 0.0F ? "Sensitivity: *yawn*" : (this.mouseSensitivity == 1.0F ? "Sensitivity: HYPERSPEED!!!" : "Sensitivity: " + (int)(this.mouseSensitivity * 200.0F) + "%")) : (var1 == 4 ? "Render distance: " + RENDER_DISTANCES[this.renderDistance] : (var1 == 5 ? "View bobbing: " + (this.viewBobbing ? "ON" : "OFF") : (var1 == 6 ? "3d anaglyph: " + (this.anaglyph ? "ON" : "OFF") : (var1 == 7 ? "Limit framerate: " + (this.limitFramerate ? "ON" : "OFF") : (var1 == 8 ? "Difficulty: " + DIFFICULTY_LEVELS[this.difficulty] : (var1 == 9 ? "Graphics: " + (this.fancyGraphics ? "FANCY" : "FAST") : "")))))))))))))));
	}

	public void loadOptions() {
		try {
			if(!this.optionsFile.exists()) {
				return;
			}

			BufferedReader var1 = new BufferedReader(new FileReader(this.optionsFile));
			String var2 = "";

			while(true) {
				var2 = var1.readLine();
				if(var2 == null) {
					var1.close();
					break;
				}

				String[] var3 = var2.split(":");
				if(var3[0].equals("music")) {
					this.musicVolume = this.parseFloat(var3[1]);
				}

				if(var3[0].equals("sound")) {
					this.soundVolume = this.parseFloat(var3[1]);
				}

				if(var3[0].equals("mouseSensitivity")) {
					this.mouseSensitivity = this.parseFloat(var3[1]);
				}

				if(var3[0].equals("invertYMouse")) {
					this.invertMouse = var3[1].equals("true");
				}

				if(var3[0].equals("viewDistance")) {
					this.renderDistance = Integer.parseInt(var3[1]);
				}

				if(var3[0].equals("bobView")) {
					this.viewBobbing = var3[1].equals("true");
				}

				if(var3[0].equals("anaglyph3d")) {
					this.anaglyph = var3[1].equals("true");
				}

				if(var3[0].equals("limitFramerate")) {
					this.limitFramerate = var3[1].equals("true");
				}

				if(var3[0].equals("difficulty")) {
					this.difficulty = Integer.parseInt(var3[1]);
				}

				if(var3[0].equals("fancyGraphics")) {
					this.fancyGraphics = var3[1].equals("true");
				}

				if(var3[0].equals("skin")) {
					this.skin = var3[1];
				}

				if(var3[0].equals("lastServer")) {
					this.field_12259_z = var3[1];
				}

				if(var3[0].equals("gamma")) {
					this.gamma = Math.round(this.parseFloat(var3[1]) * 100)/100F;
				}

				if(var3[0].equals("opacity")) {
					this.opacity = Math.round(this.parseFloat(var3[1]) * 100)/100F;
				}

				if(var3[0].equals("offset")) {
					this.offset = Math.round(this.parseFloat(var3[1]) * 100)/100F;
				}

				if(var3[0].equals("watermark")) {
					this.watermark = var3[1].equals("true");
				}

				if(var3[0].equals("tooltipsEnabled")) {
					this.tooltipsEnabled = var3[1].equals("true");
				}

				if(var3[0].equals("mipmapping")) {
					this.mipmapping = var3[1].equals("true");
				}

				for(int var4 = 0; var4 < this.keyBindings.length; ++var4) {
					if(var3[0].equals("key_" + this.keyBindings[var4].keyDescription)) {
						this.keyBindings[var4].keyCode = Integer.parseInt(var3[1]);
					}
				}
			}
		} catch (Exception var5) {
			System.out.println("Failed to load options");
			var5.printStackTrace();
		}

	}

	private float parseFloat(String var1) {
		return var1.equals("true") ? 1.0F : (var1.equals("false") ? 0.0F : Float.parseFloat(var1));
	}

	public void saveOptions() {
		try {
			PrintWriter var1 = new PrintWriter(new FileWriter(this.optionsFile));
			var1.println("music:" + this.musicVolume);
			var1.println("sound:" + this.soundVolume);
			var1.println("invertYMouse:" + this.invertMouse);
			var1.println("mouseSensitivity:" + this.mouseSensitivity);
			var1.println("viewDistance:" + this.renderDistance);
			var1.println("bobView:" + this.viewBobbing);
			var1.println("anaglyph3d:" + this.anaglyph);
			var1.println("limitFramerate:" + this.limitFramerate);
			var1.println("difficulty:" + this.difficulty);
			var1.println("fancyGraphics:" + this.fancyGraphics);
			var1.println("skin:" + this.skin);
			var1.println("lastServer:" + this.field_12259_z);
			var1.println("gamma:" + this.gamma);
			var1.println("opacity:" + this.opacity);
			var1.println("offset:" + this.offset);
			var1.println("watermark:" + this.watermark);
			var1.println("tooltipsEnabled" + this.tooltipsEnabled);
			var1.println("mipmapping:" + this.mipmapping);

			for(int var2 = 0; var2 < this.keyBindings.length; ++var2) {
				var1.println("key_" + this.keyBindings[var2].keyDescription + ":" + this.keyBindings[var2].keyCode);
			}

			var1.close();
		} catch (Exception var3) {
			System.out.println("Failed to save options");
			var3.printStackTrace();
		}

	}
}
