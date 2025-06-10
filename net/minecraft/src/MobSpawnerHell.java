package net.minecraft.src;

public class MobSpawnerHell extends MobSpawnerBase {
	public MobSpawnerHell() {
		this.biomeMonsters = new Class[]{EntityGhast.class, EntityPigZombie.class};
		this.biomeCreatures = new Class[0];
	}
}
