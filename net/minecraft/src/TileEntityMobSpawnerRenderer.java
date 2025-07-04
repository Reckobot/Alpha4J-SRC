package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer {
	private Map field_1412_b = new HashMap();

	public void a(TileEntityMobSpawner var1, double var2, double var4, double var6, float var8) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var2 + 0.5F, (float)var4, (float)var6 + 0.5F);
		Entity var9 = (Entity)this.field_1412_b.get(var1.entityID);
		if(var9 == null) {
			var9 = EntityList.createEntityInWorld(var1.entityID, (World)null);
			this.field_1412_b.put(var1.entityID, var9);
		}

		if(var9 != null) {
			var9.setWorld(var1.worldObj);
			float var10 = 7.0F / 16.0F;
			GL11.glTranslatef(0.0F, 0.4F, 0.0F);
			GL11.glRotatef((float)(var1.field_830_d + (var1.field_831_c - var1.field_830_d) * (double)var8) * 10.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.4F, 0.0F);
			GL11.glScalef(var10, var10, var10);
			var9.setLocationAndAngles(var2, var4, var6, 0.0F, 0.0F);
			RenderManager.instance.func_853_a(var9, 0.0D, 0.0D, 0.0D, 0.0F, var8);
		}

		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
		this.a((TileEntityMobSpawner)var1, var2, var4, var6, var8);
	}
}
