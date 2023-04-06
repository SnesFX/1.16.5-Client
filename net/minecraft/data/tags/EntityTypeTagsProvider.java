/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.data.tags;

import java.nio.file.Path;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;

public class EntityTypeTagsProvider
extends TagsProvider<EntityType<?>> {
    public EntityTypeTagsProvider(DataGenerator dataGenerator) {
        super(dataGenerator, Registry.ENTITY_TYPE);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        this.tag(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
        this.tag(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
        this.tag(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL);
    }

    @Override
    protected Path getPath(ResourceLocation resourceLocation) {
        return this.generator.getOutputFolder().resolve("data/" + resourceLocation.getNamespace() + "/tags/entity_types/" + resourceLocation.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Entity Type Tags";
    }
}

