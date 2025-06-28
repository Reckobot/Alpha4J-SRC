package net.minecraft.client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.src.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;

import static jdk.jfr.internal.SecuritySupport.getResourceAsStream;
import static org.lwjgl.opengl.GL14.glBlendColor;

public abstract class Minecraft implements Runnable {
	private final Frame frame;
	private int previousWidth;
	private int previousHeight;

	public PlayerController field_6327_b;
	private boolean a = false;
	public int displayWidth;
	public int displayHeight;
	private OpenGlCapsChecker glCapabilities;
	private Timer timer = new Timer(20.0F);
	public World theWorld;
	public RenderGlobal field_6323_f;
	public EntityPlayerSP thePlayer;
	public EffectRenderer field_6321_h;
	public Session field_6320_i = null;
	public String field_6319_j;
	public Canvas mcCanvas;
	public boolean field_6317_l = true;
	public volatile boolean field_6316_m = false;
	public RenderEngine renderEngine;
	public FontRenderer fontRenderer;
	public GuiScreen currentScreen = null;
	public LoadingScreenRenderer loadingScreen = new LoadingScreenRenderer(this);
	public EntityRenderer field_9243_r = new EntityRenderer(this);
	private ThreadDownloadResources downloadResourcesThread;
	private int ticksRan = 0;
	private int field_6282_S = 0;
	private int field_9236_T;
	private int field_9235_U;
	public String field_6310_s = null;
	public int field_6309_t = 0;
	public GuiIngame ingameGUI;
	public boolean field_6307_v = false;
	public ModelBiped field_9242_w = new ModelBiped(0.0F);
	public MovingObjectPosition objectMouseOver = null;
	public GameSettings gameSettings;
	protected MinecraftApplet mcApplet;
	public SoundManager sndManager = new SoundManager();
	public MouseHelper mouseHelper;
	public TexturePackList texturePackList;
	public File field_6297_D;
	public static long[] field_9240_E = new long[512];
	public static long[] field_9239_F = new long[512];
	public static int field_9238_G = 0;
	private String field_9234_V;
	private int field_9233_W;
	private TextureWaterFX field_9232_X = new TextureWaterFX();
	private TextureLavaFX field_9231_Y = new TextureLavaFX();
	private static File minecraftDir = null;
	public volatile boolean running = true;
	public String field_6292_I = "";
	boolean field_6291_J = false;
	long field_6290_K = -1L;
	public boolean field_6289_L = false;
	private int field_6302_aa = 0;
	public boolean field_6288_M = false;
	long field_6287_N = System.currentTimeMillis();
	private int field_6300_ab = 0;
	public int uiToggle = 0;
	public boolean f3Enabled = false;

	public boolean fullscreen = false;

	public Minecraft(Component var1, Canvas var2, MinecraftApplet var3, int var4, int var5, boolean var6) {
		this.field_9236_T = var4;
		this.field_9235_U = var5;
		this.a = var6;
		this.mcApplet = var3;
		new ThreadSleepForever(this, "Timer hack thread");
		this.mcCanvas = var2;
		this.displayWidth = var4;
		this.displayHeight = var5;
		this.a = var6;

		this.previousWidth = var4;
		this.previousHeight = var5;
		this.frame = new Frame("Minecraft");
	}

	public abstract void func_4007_a(UnexpectedThrowable var1);

	public void func_6258_a(String var1, int var2) {
		this.field_9234_V = var1;
		this.field_9233_W = var2;
	}

	public void startGame() throws LWJGLException {
		Display.setResizable(true);
		if(this.mcCanvas != null) {
			Graphics var1 = this.mcCanvas.getGraphics();
			if(var1 != null) {
				var1.setColor(Color.BLACK);
				var1.fillRect(0, 0, this.displayWidth, this.displayHeight);
				var1.dispose();
			}

			Display.setParent(this.mcCanvas);
		} else if(this.a) {
			Display.setFullscreen(true);
			this.displayWidth = Display.getDisplayMode().getWidth();
			this.displayHeight = Display.getDisplayMode().getHeight();
			if(this.displayWidth <= 0) {
				this.displayWidth = 1;
			}

			if(this.displayHeight <= 0) {
				this.displayHeight = 1;
			}
		} else {
			Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
		}

		Display.setTitle("Minecraft Minecraft Alpha4J");

		try {
			Display.create();
		} catch (LWJGLException var6) {
			var6.printStackTrace();

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException var5) {
			}

			Display.create();
		}

		RenderManager.instance.field_4236_f = new ItemRenderer(this);
		this.field_6297_D = getMinecraftDir();
		this.gameSettings = new GameSettings(this, this.field_6297_D);
		this.texturePackList = new TexturePackList(this, this.field_6297_D);
		this.renderEngine = new RenderEngine(this.texturePackList, this.gameSettings);
		this.fontRenderer = new FontRenderer(this.gameSettings, "/font/default.png", this.renderEngine);
		this.loadScreen();
		Keyboard.create();
		Mouse.create();
		this.mouseHelper = new MouseHelper(this.mcCanvas);

		try {
			Controllers.create();
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		this.checkGLError("Pre startup");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearDepth(1.0D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		this.checkGLError("Startup");
		this.glCapabilities = new OpenGlCapsChecker();
		this.sndManager.func_340_a(this.gameSettings);
		this.renderEngine.registerTextureFX(this.field_9231_Y);
		this.renderEngine.registerTextureFX(this.field_9232_X);
		this.renderEngine.registerTextureFX(new TexturePortalFX());
		this.renderEngine.registerTextureFX(new TextureCompassFX(this));
		this.renderEngine.registerTextureFX(new TextureWatchFX(this));
		this.renderEngine.registerTextureFX(new TexureWaterFlowFX());
		this.renderEngine.registerTextureFX(new TextureLavaFlowFX());
		this.renderEngine.registerTextureFX(new TextureFlamesFX(0));
		this.renderEngine.registerTextureFX(new TextureFlamesFX(1));
		this.field_6323_f = new RenderGlobal(this, this.renderEngine);
		GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
		this.field_6321_h = new EffectRenderer(this.theWorld, this.renderEngine);

		try {
			this.downloadResourcesThread = new ThreadDownloadResources(this.field_6297_D, this);
			this.downloadResourcesThread.start();
		} catch (Exception var3) {
		}

		this.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);
		if(this.field_9234_V != null) {
			this.displayGuiScreen(new GuiConnecting(this, this.field_9234_V, this.field_9233_W));
		} else {
			this.displayGuiScreen(new GuiMainMenu());
		}

		ByteBuffer[] icons = new ByteBuffer[2];
		try {
			icons[0] = loadIcon("/assets/gambac/icons/16.png");
			icons[1] = loadIcon("/assets/gambac/icons/32.png");
		} catch (Exception ignored) {
		}

		if(icons[0] != null && icons[1] != null){
			Display.setIcon(icons);
		}

		try {
			Display.makeCurrent();
			Display.update();
		} catch (LWJGLException e) {
			System.err.println("Error while making the Display current");
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}

	}

	private ByteBuffer loadIcon(String path) {
		try {
			InputStream stream = this.getClass().getResourceAsStream(path);
			if (stream == null) {
				throw new RuntimeException("Icon resource not found: " + path);
			}
			BufferedImage image = ImageIO.read(stream);

			int[] pixels = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = pixels[y * image.getWidth() + x];
					buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
					buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
					buffer.put((byte) (pixel & 0xFF));         // Blue
					buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
				}
			}

			buffer.flip();
			return buffer;
		} catch (Exception e) {
			return null;
		}
	}

	private void loadScreen() throws LWJGLException {
		ScaledResolution var1 = new ScaledResolution(this.displayWidth, this.displayHeight);
		int var2 = var1.getScaledWidth();
		int var3 = var1.getScaledHeight();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)var2, (double)var3, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
		GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		Tessellator var4 = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/title/mojang.png"));
		var4.startDrawingQuads();
		var4.setColorOpaque_I(16777215);
		var4.addVertexWithUV(0.0D, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
		var4.addVertexWithUV((double)this.displayWidth, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
		var4.addVertexWithUV((double)this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
		var4.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		var4.draw();
		short var5 = 256;
		short var6 = 256;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		var4.setColorOpaque_I(16777215);
		this.func_6274_a((this.displayWidth / 2 - var5) / 2, (this.displayHeight / 2 - var6) / 2, 0, 0, var5, var6);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		Display.swapBuffers();
	}

	public void func_6274_a(int var1, int var2, int var3, int var4, int var5, int var6) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double)(var1 + 0), (double)(var2 + var6), 0.0D, (double)((float)(var3 + 0) * var7), (double)((float)(var4 + var6) * var8));
		var9.addVertexWithUV((double)(var1 + var5), (double)(var2 + var6), 0.0D, (double)((float)(var3 + var5) * var7), (double)((float)(var4 + var6) * var8));
		var9.addVertexWithUV((double)(var1 + var5), (double)(var2 + 0), 0.0D, (double)((float)(var3 + var5) * var7), (double)((float)(var4 + 0) * var8));
		var9.addVertexWithUV((double)(var1 + 0), (double)(var2 + 0), 0.0D, (double)((float)(var3 + 0) * var7), (double)((float)(var4 + 0) * var8));
		var9.draw();
	}

	public static File getMinecraftDir() {
		if(minecraftDir == null) {
			minecraftDir = getAppDir("minecraft");
		}

		return minecraftDir;
	}

	public static File getAppDir(String var0) {
		String var1 = System.getProperty("user.home", ".");
		File var2;
		switch(EnumOSMappingHelper.field_1585_a[getOs().ordinal()]) {
		case 1:
		case 2:
			var2 = new File(var1, '.' + var0 + '/');
			break;
		case 3:
			String var3 = System.getenv("APPDATA");
			if(var3 != null) {
				var2 = new File(var3, "." + var0 + '/');
			} else {
				var2 = new File(var1, '.' + var0 + '/');
			}
			break;
		case 4:
			var2 = new File(var1, "Library/Application Support/" + var0);
			break;
		default:
			var2 = new File(var1, var0 + '/');
		}

		if(!var2.exists() && !var2.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + var2);
		} else {
			return var2;
		}
	}

	private static EnumOS2 getOs() {
		String var0 = System.getProperty("os.name").toLowerCase();
		return var0.contains("win") ? EnumOS2.c : (var0.contains("mac") ? EnumOS2.d : (var0.contains("solaris") ? EnumOS2.b : (var0.contains("sunos") ? EnumOS2.b : (var0.contains("linux") ? EnumOS2.a : (var0.contains("unix") ? EnumOS2.a : EnumOS2.unknown)))));
	}

	public void displayGuiScreen(GuiScreen var1) {
		if(!(this.currentScreen instanceof GuiUnused)) {
			if(this.currentScreen != null) {
				this.currentScreen.onGuiClosed();
			}

			if(var1 == null && this.theWorld == null) {
				var1 = new GuiMainMenu();
			} else if(var1 == null && this.thePlayer.health <= 0) {
				var1 = new GuiGameOver();
			}

			this.currentScreen = (GuiScreen)var1;
			if(var1 != null) {
				this.func_6273_f();
				ScaledResolution var2 = new ScaledResolution(this.displayWidth, this.displayHeight);
				int var3 = var2.getScaledWidth();
				int var4 = var2.getScaledHeight();
				((GuiScreen)var1).setWorldAndResolution(this, var3, var4);
				this.field_6307_v = false;
			} else {
				this.func_6259_e();
			}

		}
	}

	private void checkGLError(String var1) {
		int var2 = GL11.glGetError();
		if(var2 != 0) {
			String var3 = GLU.gluErrorString(var2);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + var1);
			System.out.println(var2 + ": " + var3);
			System.exit(0);
		}

	}

	public void func_6266_c() {
		if(this.mcApplet != null) {
			this.mcApplet.func_6231_c();
		}

		try {
			if(this.downloadResourcesThread != null) {
				this.downloadResourcesThread.closeMinecraft();
			}
		} catch (Exception var8) {
		}

		try {
			System.out.println("Stopping!");
			this.func_6261_a((World)null);

			try {
				GLAllocation.deleteTexturesAndDisplayLists();
			} catch (Exception var6) {
			}

			this.sndManager.closeMinecraft();
			Mouse.destroy();
			Keyboard.destroy();
		} finally {
			Display.destroy();
		}

		System.gc();
	}

	public void gammaOverlay1() {
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(0.2F, 0.2F, 0.2F, this.gameSettings.gamma*0.5F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0.0D, (double)this.displayHeight, -90.0D, 0.0D, 1.0D);
		tess.addVertexWithUV((double)this.displayWidth, (double)this.displayHeight, -90.0D, 1.0D, 1.0D);
		tess.addVertexWithUV((double)this.displayWidth, 0.0D, -90.0D, 1.0D, 0.0D);
		tess.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tess.draw();


		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void gammaOverlay2() {
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		float gamma = this.gameSettings.gamma*0.6F/4F;

		glBlendColor(1.0F, 1.0F, 1.0F, 0.95F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_CONSTANT_ALPHA);
		GL11.glColor4f(gamma, gamma, gamma, this.gameSettings.gamma);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0.0D, (double)this.displayHeight, -90.0D, 0.0D, 1.0D);
		tess.addVertexWithUV((double)this.displayWidth, (double)this.displayHeight, -90.0D, 1.0D, 1.0D);
		tess.addVertexWithUV((double)this.displayWidth, 0.0D, -90.0D, 1.0D, 0.0D);
		tess.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tess.draw();


		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	float prevTick = 0.0F;

	public void run() {
		this.running = true;

		try {
			this.startGame();
		} catch (Exception var15) {
			var15.printStackTrace();
			this.func_4007_a(new UnexpectedThrowable("Failed to start game", var15));
			return;
		}

		try {
			try {
				long var1 = System.currentTimeMillis();
				int var3 = 0;

				while(this.running && (this.mcApplet == null || this.mcApplet.isActive())) {
					if(this.currentScreen != null) {
						if (this.panoramaTimer < (double) ((this.currentScreen.height) * 4F)) {
							this.panoramaTimer += Math.abs(this.prevTick - this.timer.renderPartialTicks)/2.0F;
						} else {
							this.panoramaTimer = 0.0D;
						}
					}
					this.prevTick = this.timer.renderPartialTicks;

					AxisAlignedBB.clearBoundingBoxPool();
					Vec3D.initialize();
					if(this.mcCanvas == null && Display.isCloseRequested()) {
						this.shutdown();
					}

					if(this.field_6316_m && this.theWorld != null) {
						float var4 = this.timer.renderPartialTicks;
						this.timer.updateTimer();
						this.timer.renderPartialTicks = var4;
					} else {
						this.timer.updateTimer();
					}

					long var19 = System.nanoTime();

					for(int var6 = 0; var6 < this.timer.elapsedTicks; ++var6) {
						++this.ticksRan;

						try {
							this.runTick();
						} catch (MinecraftException var14) {
							this.theWorld = null;
							this.func_6261_a((World)null);
							this.displayGuiScreen(new GuiConflictWarning());
						}
					}

					long var20 = System.nanoTime() - var19;
					this.checkGLError("Pre render");
					this.sndManager.func_338_a(this.thePlayer, this.timer.renderPartialTicks);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					if(this.theWorld != null && !this.theWorld.multiplayerWorld) {
						while(this.theWorld.func_6465_g()) {
						}
					}

					if(this.theWorld != null && this.theWorld.multiplayerWorld) {
						this.theWorld.func_6465_g();
					}

					if(this.gameSettings.limitFramerate) {
						Thread.sleep(5L);
					}

					if(!Keyboard.isKeyDown(Keyboard.KEY_F7)) {
						Display.update();
					}

					if(!this.field_6307_v) {
						if(this.field_6327_b != null) {
							this.field_6327_b.func_6467_a(this.timer.renderPartialTicks);
						}

						this.field_9243_r.func_4136_b(this.timer.renderPartialTicks);
					}

					if(!Display.isActive()) {
						if(this.a) {
							this.toggleFullscreen();
						}

						Thread.sleep(10L);
					}

					if(this.f3Enabled) {
						this.func_6238_a(var20);
					} else {
						this.field_6290_K = System.nanoTime();
					}

					Thread.yield();
					this.gammaOverlay1();
					for(int i = 0; i < 6; i++) {
						this.gammaOverlay2();
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_F7)) {
						Display.update();
					}

					this.func_6248_s();
					if(this.mcCanvas != null && !this.a && (this.mcCanvas.getWidth() != this.displayWidth || this.mcCanvas.getHeight() != this.displayHeight)) {
						this.displayWidth = this.mcCanvas.getWidth();
						this.displayHeight = this.mcCanvas.getHeight();
						if(this.displayWidth <= 0) {
							this.displayWidth = 1;
						}

						if(this.displayHeight <= 0) {
							this.displayHeight = 1;
						}

						this.resize(this.displayWidth, this.displayHeight);
					}

					this.checkGLError("Post render");
					++var3;

					for(this.field_6316_m = !this.isMultiplayerWorld() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame(); System.currentTimeMillis() >= var1 + 1000L; var3 = 0) {
						this.field_6292_I = var3 + " fps, " + WorldRenderer.field_1762_b + " chunk updates";
						WorldRenderer.field_1762_b = 0;
						var1 += 1000L;
					}
				}
			} catch (MinecraftError var16) {
			} catch (Throwable var17) {
				this.theWorld = null;
				var17.printStackTrace();
				this.func_4007_a(new UnexpectedThrowable("Unexpected error", var17));
			}

		} finally {
		}
	}

	private void func_6248_s() {
		if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			if(!this.field_6291_J) {
				this.ingameGUI.addChatMessage(ScreenShotHelper.func_4148_a(minecraftDir, this.displayWidth, this.displayHeight));
				this.field_6291_J = true;
			}
		} else {
			this.field_6291_J = false;
		}

	}

	private void func_6238_a(long var1) {
		long var3 = 16666666L;
		if(this.field_6290_K == -1L) {
			this.field_6290_K = System.nanoTime();
		}

		long var5 = System.nanoTime();
		field_9239_F[field_9238_G & field_9240_E.length - 1] = var1;
		field_9240_E[field_9238_G++ & field_9240_E.length - 1] = var5 - this.field_6290_K;
		this.field_6290_K = var5;
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)this.displayWidth, (double)this.displayHeight, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
		GL11.glLineWidth(1.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator var7 = Tessellator.instance;
		var7.startDrawing(7);
		int var8 = (int)(var3 / 200000L);
		var7.setColorOpaque_I(536870912);
		var7.addVertex(0.0D, (double)(this.displayHeight - var8), 0.0D);
		var7.addVertex(0.0D, (double)this.displayHeight, 0.0D);
		var7.addVertex((double)field_9240_E.length, (double)this.displayHeight, 0.0D);
		var7.addVertex((double)field_9240_E.length, (double)(this.displayHeight - var8), 0.0D);
		var7.setColorOpaque_I(538968064);
		var7.addVertex(0.0D, (double)(this.displayHeight - var8 * 2), 0.0D);
		var7.addVertex(0.0D, (double)(this.displayHeight - var8), 0.0D);
		var7.addVertex((double)field_9240_E.length, (double)(this.displayHeight - var8), 0.0D);
		var7.addVertex((double)field_9240_E.length, (double)(this.displayHeight - var8 * 2), 0.0D);
		var7.draw();
		long var9 = 0L;

		int var11;
		for(var11 = 0; var11 < field_9240_E.length; ++var11) {
			var9 += field_9240_E[var11];
		}

		var11 = (int)(var9 / 200000L / (long)field_9240_E.length);
		var7.startDrawing(7);
		var7.setColorOpaque_I(541065216);
		var7.addVertex(0.0D, (double)(this.displayHeight - var11), 0.0D);
		var7.addVertex(0.0D, (double)this.displayHeight, 0.0D);
		var7.addVertex((double)field_9240_E.length, (double)this.displayHeight, 0.0D);
		var7.addVertex((double)field_9240_E.length, (double)(this.displayHeight - var11), 0.0D);
		var7.draw();
		var7.startDrawing(1);

		for(int var12 = 0; var12 < field_9240_E.length; ++var12) {
			int var13 = (var12 - field_9238_G & field_9240_E.length - 1) * 255 / field_9240_E.length;
			int var14 = var13 * var13 / 255;
			var14 = var14 * var14 / 255;
			int var15 = var14 * var14 / 255;
			var15 = var15 * var15 / 255;
			if(field_9240_E[var12] > var3) {
				var7.setColorOpaque_I(-16777216 + var14 * 65536);
			} else {
				var7.setColorOpaque_I(-16777216 + var14 * 256);
			}

			long var16 = field_9240_E[var12] / 200000L;
			long var18 = field_9239_F[var12] / 200000L;
			var7.addVertex((double)((float)var12 + 0.5F), (double)((float)((long)this.displayHeight - var16) + 0.5F), 0.0D);
			var7.addVertex((double)((float)var12 + 0.5F), (double)((float)this.displayHeight + 0.5F), 0.0D);
			var7.setColorOpaque_I(-16777216 + var14 * 65536 + var14 * 256 + var14 * 1);
			var7.addVertex((double)((float)var12 + 0.5F), (double)((float)((long)this.displayHeight - var16) + 0.5F), 0.0D);
			var7.addVertex((double)((float)var12 + 0.5F), (double)((float)((long)this.displayHeight - (var16 - var18)) + 0.5F), 0.0D);
		}

		var7.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void shutdown() {
		this.running = false;
	}

	public void func_6259_e() {
		if(Display.isActive()) {
			if(!this.field_6289_L) {
				this.field_6289_L = true;
				this.mouseHelper.func_774_a();
				this.displayGuiScreen((GuiScreen)null);
				this.field_6302_aa = this.ticksRan + 10000;
			}
		}
	}

	public void func_6273_f() {
		if(this.field_6289_L) {
			if(this.thePlayer != null) {
				this.thePlayer.func_458_k();
			}

			this.field_6289_L = false;
			this.mouseHelper.func_773_b();
		}
	}

	public void func_6252_g() {
		if(this.currentScreen == null) {
			this.displayGuiScreen(new GuiIngameMenu());
		}
	}

	private void func_6254_a(int var1, boolean var2) {
		if(!this.field_6327_b.field_1064_b) {
			if(var1 != 0 || this.field_6282_S <= 0) {
				if(var2 && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == 0 && var1 == 0) {
					int var3 = this.objectMouseOver.blockX;
					int var4 = this.objectMouseOver.blockY;
					int var5 = this.objectMouseOver.blockZ;
					this.field_6327_b.sendBlockRemoving(var3, var4, var5, this.objectMouseOver.sideHit);
					this.field_6321_h.func_1191_a(var3, var4, var5, this.objectMouseOver.sideHit);
				} else {
					this.field_6327_b.func_6468_a();
				}

			}
		}
	}

	private void clickMouse(int var1) {
		if(var1 != 0 || this.field_6282_S <= 0) {
			if(var1 == 0) {
				this.thePlayer.func_457_w();
			}

			boolean var2 = true;
			if(this.objectMouseOver == null) {
				if(var1 == 0 && !(this.field_6327_b instanceof PlayerControllerTest)) {
					this.field_6282_S = 10;
				}
			} else if(this.objectMouseOver.typeOfHit == 1) {
				if(var1 == 0) {
					this.field_6327_b.func_6472_b(this.thePlayer, this.objectMouseOver.entityHit);
				}

				if(var1 == 1) {
					this.field_6327_b.func_6475_a(this.thePlayer, this.objectMouseOver.entityHit);
				}
			} else if(this.objectMouseOver.typeOfHit == 0) {
				int var3 = this.objectMouseOver.blockX;
				int var4 = this.objectMouseOver.blockY;
				int var5 = this.objectMouseOver.blockZ;
				int var6 = this.objectMouseOver.sideHit;
				Block var7 = Block.blocksList[this.theWorld.getBlockId(var3, var4, var5)];
				if(var1 == 0) {
					this.theWorld.onBlockHit(var3, var4, var5, this.objectMouseOver.sideHit);
					if(var7 != Block.bedrock || this.thePlayer.field_9371_f >= 100) {
						this.field_6327_b.clickBlock(var3, var4, var5, this.objectMouseOver.sideHit);
					}
				} else {
					ItemStack var8 = this.thePlayer.inventory.getCurrentItem();
					int var9 = var8 != null ? var8.stackSize : 0;
					if(this.field_6327_b.sendPlaceBlock(this.thePlayer, this.theWorld, var8, var3, var4, var5, var6)) {
						var2 = false;
						this.thePlayer.func_457_w();
					}

					if(var8 == null) {
						return;
					}

					if(var8.stackSize == 0) {
						this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
					} else if(var8.stackSize != var9) {
						this.field_9243_r.field_1395_a.func_9449_b();
					}
				}
			}

			if(var2 && var1 == 1) {
				ItemStack var10 = this.thePlayer.inventory.getCurrentItem();
				if(var10 != null && this.field_6327_b.sendUseItem(this.thePlayer, this.theWorld, var10)) {
					this.field_9243_r.field_1395_a.func_9450_c();
				}
			}

		}
	}

	public void toggleFullscreen() {
		try {
			this.fullscreen = !this.fullscreen;

			if (this.fullscreen) {
				this.previousWidth = Display.getWidth();
				this.previousHeight = Display.getHeight();

				Display.setDisplayMode(Display.getDesktopDisplayMode());
				this.displayWidth = Display.getDisplayMode().getWidth();
				this.displayHeight = Display.getDisplayMode().getHeight();
			} else {
				this.displayWidth = this.previousWidth;
				this.displayHeight = this.previousHeight;
				Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
			}

			if (this.displayWidth <= 0) {
				this.displayWidth = 1;
			}

			if (this.displayHeight <= 0) {
				this.displayHeight = 1;
			}

			if (this.currentScreen != null) {
				this.resize(this.displayWidth, this.displayHeight);
			}

			Display.setFullscreen(this.fullscreen);
			Display.update();
		} catch (Exception e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private void resize(int var1, int var2) {
		if(var1 <= 0) {
			var1 = 1;
		}

		if(var2 <= 0) {
			var2 = 1;
		}

		this.displayWidth = var1;
		this.displayHeight = var2;
		if(this.currentScreen != null) {
			ScaledResolution var3 = new ScaledResolution(var1, var2);
			int var4 = var3.getScaledWidth();
			int var5 = var3.getScaledHeight();
			this.currentScreen.setWorldAndResolution(this, var4, var5);
		}

	}

	private void clickMiddleMouseButton() {
		if(this.objectMouseOver != null) {
			int var1 = this.theWorld.getBlockId(this.objectMouseOver.blockX, this.objectMouseOver.blockY, this.objectMouseOver.blockZ);
			if(var1 == Block.grass.blockID) {
				var1 = Block.dirt.blockID;
			}

			if(var1 == Block.stairDouble.blockID) {
				var1 = Block.stairSingle.blockID;
			}

			if(var1 == Block.bedrock.blockID) {
				var1 = Block.stone.blockID;
			}

			this.thePlayer.inventory.setCurrentItem(var1, this.field_6327_b instanceof PlayerControllerTest, this);
		}

	}

	public void unzip(String zipFilePath, String destDir) throws IOException {
		File destDirFile = new File(destDir);
		if (!destDirFile.exists()) {
			destDirFile.mkdirs();
		}

		try (InputStream fis = this.getClass().getResourceAsStream(zipFilePath);
			 ZipInputStream zis = new ZipInputStream(fis)) {

			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				File entryFile = new File(destDir, entry.getName());
				if (entry.isDirectory()) {
					entryFile.mkdirs();
				} else {
					File parentFile = entryFile.getParentFile();
					if (parentFile != null && !parentFile.exists()) {
						parentFile.mkdirs();
					}
					try (FileOutputStream fos = new FileOutputStream(entryFile)) {
						byte[] buffer = new byte[1024];
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
				zis.closeEntry();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public double panoramaTimer = 0.0;

	public void runTick() throws IOException {
		if (GL11.glGetString(GL11.GL_RENDERER).contains("Apple M")) {
			GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
		}

		if (Display.getWidth() != this.displayWidth || Display.getHeight() != this.displayHeight) {
			this.resize(Display.getWidth(), Display.getHeight());
		}

		this.ingameGUI.func_555_a();
		this.field_9243_r.func_910_a(1.0F);
		if(this.thePlayer != null) {
			this.thePlayer.func_6420_o();
		}

		if(!this.field_6316_m && this.theWorld != null) {
			this.field_6327_b.func_6474_c();
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain.png"));
		if(!this.field_6316_m) {
			this.renderEngine.func_1067_a();
		}

		if(this.currentScreen == null && this.thePlayer != null && this.thePlayer.health <= 0) {
			this.displayGuiScreen((GuiScreen)null);
		}

		if(this.currentScreen != null) {
			this.field_6302_aa = this.ticksRan + 10000;
		}

		if(this.currentScreen != null) {
			this.currentScreen.handleInput();
			if(this.currentScreen != null) {
				this.currentScreen.updateScreen();
			}
		}

		if(this.currentScreen == null || this.currentScreen.field_948_f) {
			label238:
			while(true) {
				while(true) {
					while(true) {
						long var1;
						do {
							if(!Mouse.next()) {
								if(this.field_6282_S > 0) {
									--this.field_6282_S;
								}

								while(true) {
									while(true) {
										do {
											if(!Keyboard.next()) {
												if(this.currentScreen == null) {
													if(Mouse.isButtonDown(0) && (float)(this.ticksRan - this.field_6302_aa) >= this.timer.ticksPerSecond / 4.0F && this.field_6289_L) {
														this.clickMouse(0);
														this.field_6302_aa = this.ticksRan;
													}

													if(Mouse.isButtonDown(1) && (float)(this.ticksRan - this.field_6302_aa) >= this.timer.ticksPerSecond / 4.0F && this.field_6289_L) {
														this.clickMouse(1);
														this.field_6302_aa = this.ticksRan;
													}
												}

												this.func_6254_a(0, this.currentScreen == null && Mouse.isButtonDown(0) && this.field_6289_L);
												break label238;
											}

											this.thePlayer.func_460_a(Keyboard.getEventKey(), Keyboard.getEventKeyState());
										} while(!Keyboard.getEventKeyState());

										if(Keyboard.getEventKey() == Keyboard.KEY_F11) {
											this.toggleFullscreen();
										} else {
											if(this.currentScreen != null) {
												this.currentScreen.handleKeyboardInput();
											} else {
												if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
													this.func_6252_g();
												}

												if(Keyboard.getEventKey() == Keyboard.KEY_S && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
													this.forceReload();
												}

												if(Keyboard.getEventKey() == Keyboard.KEY_F5) {
													this.gameSettings.thirdPersonView = !this.gameSettings.thirdPersonView;
												}

												if(Keyboard.getEventKey() == this.gameSettings.keyBindInventory.keyCode) {
													this.displayGuiScreen(new GuiInventory(this.thePlayer.inventory, this.thePlayer.inventory.craftingInventory));
												}

												if(Keyboard.getEventKey() == this.gameSettings.keyBindDrop.keyCode) {
													this.thePlayer.dropPlayerItemWithRandomChoice(this.thePlayer.inventory.decrStackSize(this.thePlayer.inventory.currentItem, 1), false);
												}

												if(this.isMultiplayerWorld() && Keyboard.getEventKey() == this.gameSettings.keyBindChat.keyCode) {
													this.displayGuiScreen(new GuiChat());
												}
											}

											for(int var4 = 0; var4 < 9; ++var4) {
												if(Keyboard.getEventKey() == Keyboard.KEY_1 + var4) {
													this.thePlayer.inventory.currentItem = var4;
													this.ingameGUI.opacity = 6.0F;
												}
											}

											if(Keyboard.getEventKey() == this.gameSettings.keyBindToggleFog.keyCode) {
												this.gameSettings.setOptionValue(4, !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? 1 : -1);
											}
										}

										if(Keyboard.isKeyDown(Keyboard.KEY_F1)) {
											if(this.uiToggle < 3) {
												this.uiToggle++;
											} else {
												this.uiToggle = 0;
											}
										}

										if(Keyboard.isKeyDown(Keyboard.KEY_F3)) {
											this.f3Enabled = !this.f3Enabled;
										}
									}
								}
							}

							var1 = System.currentTimeMillis() - this.field_6287_N;
						} while(var1 > 200L);

						int var3 = Mouse.getEventDWheel();
						if(var3 != 0) {
							this.thePlayer.inventory.changeCurrentItem(var3);
							this.ingameGUI.opacity = 6.0F;
						}

						if(this.currentScreen == null) {
							if(!this.field_6289_L && Mouse.getEventButtonState()) {
								this.func_6259_e();
							} else {
								if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
									this.clickMouse(0);
									this.field_6302_aa = this.ticksRan;
								}

								if(Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
									this.clickMouse(1);
									this.field_6302_aa = this.ticksRan;
								}

								if(Mouse.getEventButton() == 2 && Mouse.getEventButtonState()) {
									this.clickMiddleMouseButton();
								}
							}
						} else if(this.currentScreen != null) {
							this.currentScreen.handleMouseInput();
						}
					}
				}
			}
		}

		if(this.theWorld != null) {
			if(this.thePlayer != null) {
				++this.field_6300_ab;
				if(this.field_6300_ab == 30) {
					this.field_6300_ab = 0;
					this.theWorld.func_705_f(this.thePlayer);
				}
			}

			this.theWorld.difficultySetting = this.gameSettings.difficulty;
			if(this.theWorld.multiplayerWorld) {
				this.theWorld.difficultySetting = 3;
			}

			if(!this.field_6316_m) {
				this.field_9243_r.func_911_a();
			}

			if(!this.field_6316_m) {
				this.field_6323_f.func_945_d();
			}

			if(!this.field_6316_m) {
				this.theWorld.func_633_c();
			}

			if(!this.field_6316_m || this.isMultiplayerWorld()) {
				this.theWorld.tick();
			}

			if(!this.field_6316_m && this.theWorld != null) {
				this.theWorld.randomDisplayUpdates(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
			}

			if(!this.field_6316_m) {
				this.field_6321_h.func_1193_a();
			}
		}

		this.field_6287_N = System.currentTimeMillis();
	}

	private void forceReload() {
		System.out.println("FORCING RELOAD!");
		this.sndManager = new SoundManager();
		this.sndManager.func_340_a(this.gameSettings);
		this.downloadResourcesThread.reloadResources();
	}

	public boolean isMultiplayerWorld() {
		return this.theWorld != null && this.theWorld.multiplayerWorld;
	}

	public void func_6247_b(String var1) {
		this.func_6261_a((World)null);
		System.gc();
		World var2 = new World(new File(getMinecraftDir(), "saves"), var1);
		if(var2.field_1033_r) {
			this.func_6263_a(var2, "Generating level");
		} else {
			this.func_6263_a(var2, "Loading level");
		}

	}

	public void func_6237_k() {
		if(this.thePlayer.dimension == -1) {
			this.thePlayer.dimension = 0;
		} else {
			this.thePlayer.dimension = -1;
		}

		this.theWorld.setEntityDead(this.thePlayer);
		this.thePlayer.isDead = false;
		double var1 = this.thePlayer.posX;
		double var3 = this.thePlayer.posZ;
		double var5 = 8.0D;
		World var7;
		if(this.thePlayer.dimension == -1) {
			var1 /= var5;
			var3 /= var5;
			this.thePlayer.setLocationAndAngles(var1, this.thePlayer.posY, var3, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
			this.theWorld.func_4084_a(this.thePlayer, false);
			var7 = new World(this.theWorld, new WorldProviderHell());
			this.func_6256_a(var7, "Entering the Nether", this.thePlayer);
		} else {
			var1 *= var5;
			var3 *= var5;
			this.thePlayer.setLocationAndAngles(var1, this.thePlayer.posY, var3, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
			this.theWorld.func_4084_a(this.thePlayer, false);
			var7 = new World(this.theWorld, new WorldProvider());
			this.func_6256_a(var7, "Leaving the Nether", this.thePlayer);
		}

		this.thePlayer.worldObj = this.theWorld;
		this.thePlayer.setLocationAndAngles(var1, this.thePlayer.posY, var3, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
		this.theWorld.func_4084_a(this.thePlayer, false);
		(new Teleporter()).func_4107_a(this.theWorld, this.thePlayer);
	}

	public void func_6261_a(World var1) {
		this.func_6263_a(var1, "");
	}

	public void func_6263_a(World var1, String var2) {
		this.func_6256_a(var1, var2, (EntityPlayer)null);
	}

	public void func_6256_a(World var1, String var2, EntityPlayer var3) {
		this.loadingScreen.func_596_a(var2);
		this.loadingScreen.displayLoadingString("");
		this.sndManager.func_331_a((String)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		if(this.theWorld != null) {
			this.theWorld.func_651_a(this.loadingScreen);
		}

		this.theWorld = var1;
		System.out.println("Player is " + this.thePlayer);
		if(var1 != null) {
			this.field_6327_b.func_717_a(var1);
			if(!this.isMultiplayerWorld()) {
				if(var3 == null) {
					this.thePlayer = (EntityPlayerSP)var1.func_4085_a(EntityPlayerSP.class);
				}
			} else if(this.thePlayer != null) {
				this.thePlayer.preparePlayerToSpawn();
				if(var1 != null) {
					var1.entityJoinedWorld(this.thePlayer);
				}
			}

			if(!var1.multiplayerWorld) {
				this.func_6255_d(var2);
			}

			System.out.println("Player is now " + this.thePlayer);
			if(this.thePlayer == null) {
				this.thePlayer = (EntityPlayerSP)this.field_6327_b.func_4087_b(var1);
				this.thePlayer.preparePlayerToSpawn();
				this.field_6327_b.flipPlayer(this.thePlayer);
			}

			this.thePlayer.field_787_a = new MovementInputFromOptions(this.gameSettings);
			if(this.field_6323_f != null) {
				this.field_6323_f.func_946_a(var1);
			}

			if(this.field_6321_h != null) {
				this.field_6321_h.func_1188_a(var1);
			}

			this.field_6327_b.func_6473_b(this.thePlayer);
			if(var3 != null) {
				var1.func_6464_c();
			}

			var1.func_608_a(this.thePlayer);
			if(var1.field_1033_r) {
				var1.func_651_a(this.loadingScreen);
			}
		} else {
			this.thePlayer = null;
		}

		System.gc();
		this.field_6287_N = 0L;
	}

	private void func_6255_d(String var1) {
		this.loadingScreen.func_596_a(var1);
		this.loadingScreen.displayLoadingString("Building terrain");
		short var2 = 128;
		int var3 = 0;
		int var4 = var2 * 2 / 16 + 1;
		var4 *= var4;

		for(int var5 = -var2; var5 <= var2; var5 += 16) {
			int var6 = this.theWorld.spawnX;
			int var7 = this.theWorld.spawnZ;
			if(this.thePlayer != null) {
				var6 = (int)this.thePlayer.posX;
				var7 = (int)this.thePlayer.posZ;
			}

			for(int var8 = -var2; var8 <= var2; var8 += 16) {
				this.loadingScreen.setLoadingProgress(var3++ * 100 / var4);
				this.theWorld.getBlockId(var6 + var5, 64, var7 + var8);

				while(this.theWorld.func_6465_g()) {
				}
			}
		}

		this.loadingScreen.displayLoadingString("Simulating world for a bit");
		boolean var9 = true;
		this.theWorld.func_656_j();
	}

	public void installResource(String var1, File var2) {
		int var3 = var1.indexOf("/");
		String var4 = var1.substring(0, var3);
		var1 = var1.substring(var3 + 1);
		if(var4.equalsIgnoreCase("sound")) {
			this.sndManager.func_6372_a(var1, var2);
		} else if(var4.equalsIgnoreCase("newsound")) {
			this.sndManager.func_6372_a(var1, var2);
		} else if(var4.equalsIgnoreCase("streaming")) {
			this.sndManager.addStreaming(var1, var2);
		} else if(var4.equalsIgnoreCase("music")) {
			this.sndManager.addMusic(var1, var2);
		} else if(var4.equalsIgnoreCase("newmusic")) {
			this.sndManager.addMusic(var1, var2);
		}

	}

	public OpenGlCapsChecker func_6251_l() {
		return this.glCapabilities;
	}

	public String func_6241_m() {
		return this.field_6323_f.func_953_b();
	}

	public String func_6262_n() {
		return this.field_6323_f.func_957_c();
	}

	public String func_6245_o() {
		return "P: " + this.field_6321_h.func_1190_b() + ". T: " + this.theWorld.func_687_d();
	}

	public void respawn() {
		if(!this.theWorld.worldProvider.func_6477_d()) {
			this.func_6237_k();
		}

		this.theWorld.func_4076_b();
		this.theWorld.func_9424_o();
		int var1 = 0;
		if(this.thePlayer != null) {
			var1 = this.thePlayer.field_620_ab;
			this.theWorld.setEntityDead(this.thePlayer);
		}

		this.thePlayer = (EntityPlayerSP)this.field_6327_b.func_4087_b(this.theWorld);
		this.thePlayer.preparePlayerToSpawn();
		this.field_6327_b.flipPlayer(this.thePlayer);
		this.theWorld.func_608_a(this.thePlayer);
		this.thePlayer.field_787_a = new MovementInputFromOptions(this.gameSettings);
		this.thePlayer.field_620_ab = var1;
		this.field_6327_b.func_6473_b(this.thePlayer);
		this.func_6255_d("Respawning");
		if(this.currentScreen instanceof GuiGameOver) {
			this.displayGuiScreen((GuiScreen)null);
		}

	}

	public static void func_6269_a(String var0, String var1) {
		func_6253_a(var0, var1, (String)null);
	}

	public static void func_6253_a(String var0, String var1, String var2) {
		boolean var3 = false;
		Frame var5 = new Frame("Minecraft");
		Canvas var6 = new Canvas();
		var5.setLayout(new BorderLayout());
		var5.add(var6, "Center");
		var6.setPreferredSize(new Dimension(854, 480));
		var5.pack();
		var5.setLocationRelativeTo((Component)null);
		MinecraftImpl var7 = new MinecraftImpl(var5, var6, (MinecraftApplet)null, 854, 480, var3, var5);
		Thread var8 = new Thread(var7, "Minecraft main thread");
		var8.setPriority(10);
		var7.field_6317_l = false;
		var7.field_6319_j = "www.minecraft.net";
		if(var0 != null && var1 != null) {
			var7.field_6320_i = new Session(var0, var1);
		} else {
			var7.field_6320_i = new Session("Player" + System.currentTimeMillis() % 1000L, "");
		}

		if(var2 != null) {
			String[] var9 = var2.split(":");
			var7.func_6258_a(var9[0], Integer.parseInt(var9[1]));
		}

		var5.setVisible(true);
		var5.addWindowListener(new GameWindowListener(var7, var8));
		var8.start();
	}

	public static void main(String[] var0) {
		String var1 = "Player" + System.currentTimeMillis() % 1000L;
		if(var0.length > 0) {
			var1 = var0[0];
		}

		String var2 = "-";
		if(var0.length > 1) {
			var2 = var0[1];
		}

		var1 = "Player" + System.currentTimeMillis() % 1000L;
		var1 = "Player524";
		func_6269_a(var1, var2);
	}
}
