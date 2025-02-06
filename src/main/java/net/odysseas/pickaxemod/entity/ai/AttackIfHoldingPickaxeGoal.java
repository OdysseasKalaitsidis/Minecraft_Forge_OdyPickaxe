package net.odysseas.pickaxemod.entity.ai;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.odysseas.pickaxemod.item.CustomPickaxe;
import net.odysseas.pickaxemod.entity.CustomMobEntity;

public class AttackIfHoldingPickaxeGoal extends MeleeAttackGoal {
    private final CustomMobEntity mob;

    public AttackIfHoldingPickaxeGoal(CustomMobEntity mob, double speedModifier) {
        super(mob, speedModifier, true);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.getTarget() instanceof Player player) {
            ItemStack heldItem = player.getMainHandItem();
            return heldItem.getItem() instanceof CustomPickaxe;
        }
        return false;
    }
}
