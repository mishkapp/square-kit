package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

/**
 * Created by mishkapp on 02.02.17.
 */
public class Fly extends UseSuffix {

    private boolean isFlying = false;
    private double jumpManaCost = 10;


    private double jumpHeight = 2.5;
    private double flyHeight = 1;
    private double flyManaCost = 2;

    public Fly(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        jumpManaCost = manaCost;
        if(args.length > 2){
            jumpHeight = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            flyHeight = Double.parseDouble(args[3]);
        }
        if(args.length > 4){
            flyManaCost = Double.parseDouble(args[4]);
        }
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof SuffixTickEvent){
            if(kitPlayer.getMcPlayer().isOnGround()){
                isFlying = false;
            }
        }
    }

    @Override
    protected boolean isPrecondition() {
        if (isFlying) {
            manaCost = flyManaCost;
        } else {
            manaCost = jumpManaCost;
        }
        if (kitPlayer.getMcPlayer().isOnGround()) {
            return false;
        }

        return isFlying || isEnoughSpace();
    }

    @Override
    protected void onUse() {
        if(isFlying){
            fly();
        } else {
            jump();
        }
    }

    private void fly() {
        kitPlayer.getMcPlayer().setVelocity(kitPlayer.getMcPlayer().getVelocity().add(0, flyHeight, 0));
        kitPlayer.getMcPlayer().playSound(SoundTypes.ITEM_ELYTRA_FLYING, kitPlayer.getMcPlayer().getLocation().getPosition(), 1);
    }

    private void jump() {
        kitPlayer.getMcPlayer().setVelocity(kitPlayer.getMcPlayer().getVelocity().add(0, jumpHeight, 0));
        kitPlayer.getMcPlayer().playSound(SoundTypes.ITEM_ELYTRA_FLYING, kitPlayer.getMcPlayer().getLocation().getPosition(), 1);
    }

    private boolean isEnoughSpace() {
        World world = kitPlayer.getMcPlayer().getWorld();
        Vector3d position = kitPlayer.getMcPlayer().getLocation().getPosition();

        for(int i = 0; i < jumpHeight; i++){
            if(!world.getBlock(position.getFloorX(), position.getFloorY() + i, position.getFloorZ()).getType().equals(BlockTypes.AIR)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.fly")
                + super.getLoreEntry();
    }
}
