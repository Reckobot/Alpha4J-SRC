package net.minecraft.src;

public class ItemFood extends Item {
	public int healAmount;

	public ItemFood(int var1, int var2) {
		super(var1);
		this.healAmount = var2;
		this.maxStackSize = 1;
	}

	public boolean isFood() {
		return true;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		--var1.stackSize;
		var3.heal(this.healAmount);
		return var1;
	}
}
