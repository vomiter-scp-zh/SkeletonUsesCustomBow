package com.vomiter.skeletonusescustombow.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SkeletonFakePlayer extends FakePlayer {
    AbstractSkeleton skeleton;
    SkeletonFakePlayer(ServerLevel level, GameProfile name) {
        super(level, name);
    }

    void setSkeleton(AbstractSkeleton skeleton){
        this.skeleton = skeleton;
    }

    public AbstractSkeleton getSkeleton(){
        return skeleton;
    }

    public static SkeletonFakePlayer createFakePlayer(AbstractSkeleton skeleton){
        var player = new SkeletonFakePlayer((ServerLevel) skeleton.level(), new GameProfile(
                UUID.nameUUIDFromBytes(("sucb:" + skeleton.getUUID()).getBytes(StandardCharsets.UTF_8)),
                "[SKELETON]"
        ));
        player.setSkeleton(skeleton);
        player.setGameMode(GameType.CREATIVE);
        return player;
    }

    private static void faceTarget(FakePlayer player, LivingEntity target) {
        double px = player.getX();
        double py = player.getEyeY();
        double pz = player.getZ();

        double tx = target.getX();
        double ty = target.getEyeY();
        double tz = target.getZ();

        double dx = tx - px;
        double dy = ty - py;
        double dz = tz - pz;

        double dHoriz = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, dHoriz)));

        // 同步上一 tick 的 rot，避免插值/取向用舊值
        player.yRotO = yaw;
        player.xRotO = pitch;

        // head/body 同步，避免有些邏輯用 headRot
        player.setYHeadRot(yaw);
        player.yHeadRotO = yaw;
        player.yBodyRot = yaw;
        player.yBodyRotO = yaw;
    }


    public void readyToShoot(){
        this.moveTo(skeleton.getX(), skeleton.getEyeY() - this.getEyeHeight(), skeleton.getZ(), skeleton.getYRot(), skeleton.getXRot());
        this.setItemInHand(InteractionHand.MAIN_HAND, skeleton.getUseItem().copy());
        this.setItemInHand(InteractionHand.OFF_HAND, skeleton.getProjectile(skeleton.getUseItem()).copy());
        if(skeleton.getTarget() == null) return;
        faceTarget(this, skeleton.getTarget());
    }
}
