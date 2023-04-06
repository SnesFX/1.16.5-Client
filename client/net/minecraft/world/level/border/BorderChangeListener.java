/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.border;

import net.minecraft.world.level.border.WorldBorder;

public interface BorderChangeListener {
    public void onBorderSizeSet(WorldBorder var1, double var2);

    public void onBorderSizeLerping(WorldBorder var1, double var2, double var4, long var6);

    public void onBorderCenterSet(WorldBorder var1, double var2, double var4);

    public void onBorderSetWarningTime(WorldBorder var1, int var2);

    public void onBorderSetWarningBlocks(WorldBorder var1, int var2);

    public void onBorderSetDamagePerBlock(WorldBorder var1, double var2);

    public void onBorderSetDamageSafeZOne(WorldBorder var1, double var2);

    public static class DelegateBorderChangeListener
    implements BorderChangeListener {
        private final WorldBorder worldBorder;

        public DelegateBorderChangeListener(WorldBorder worldBorder) {
            this.worldBorder = worldBorder;
        }

        @Override
        public void onBorderSizeSet(WorldBorder worldBorder, double d) {
            this.worldBorder.setSize(d);
        }

        @Override
        public void onBorderSizeLerping(WorldBorder worldBorder, double d, double d2, long l) {
            this.worldBorder.lerpSizeBetween(d, d2, l);
        }

        @Override
        public void onBorderCenterSet(WorldBorder worldBorder, double d, double d2) {
            this.worldBorder.setCenter(d, d2);
        }

        @Override
        public void onBorderSetWarningTime(WorldBorder worldBorder, int n) {
            this.worldBorder.setWarningTime(n);
        }

        @Override
        public void onBorderSetWarningBlocks(WorldBorder worldBorder, int n) {
            this.worldBorder.setWarningBlocks(n);
        }

        @Override
        public void onBorderSetDamagePerBlock(WorldBorder worldBorder, double d) {
            this.worldBorder.setDamagePerBlock(d);
        }

        @Override
        public void onBorderSetDamageSafeZOne(WorldBorder worldBorder, double d) {
            this.worldBorder.setDamageSafeZone(d);
        }
    }

}

