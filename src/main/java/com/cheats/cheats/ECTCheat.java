package com.cheats.cheats;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.HashSet;
import java.util.Set;

/**
 * ECTCheat - Entity Culling Toggle
 * 
 * Controls entity rendering and visibility culling.
 * Allows selective toggling of entity rendering for performance or strategic purposes.
 * 
 * Features:
 * - Toggle all entities rendering on/off
 * - Selective entity type filtering (players, mobs, animals, items)
 * - Distance-based culling control
 * - Entity visibility management
 * - Performance monitoring
 */
public class ECTCheat {
    
    private static final Minecraft MC = Minecraft.getMinecraft();
    
    // Entity type culling states
    private boolean cullPlayers = false;
    private boolean cullMobs = false;
    private boolean cullAnimals = false;
    private boolean cullItems = false;
    private boolean cullArmorStands = false;
    
    // Global culling state
    private boolean globalCullingEnabled = false;
    private double cullingDistance = 64.0d;
    
    // Culled entity tracking
    private Set<Integer> culledEntities = new HashSet<>();
    private int totalEntitiesProcessed = 0;
    private int totalEntitiesCulled = 0;
    
    /**
     * Main update method - processes entity culling each tick
     */
    public void update() {
        if (MC.world == null || MC.player == null) {
            return;
        }
        
        culledEntities.clear();
        totalEntitiesProcessed = 0;
        totalEntitiesCulled = 0;
        
        // Process all loaded entities
        for (Entity entity : MC.world.loadedEntityList) {
            totalEntitiesProcessed++;
            
            // Skip self
            if (entity == MC.player) {
                continue;
            }
            
            // Check if entity should be culled
            if (shouldCullEntity(entity)) {
                culledEntities.add(entity.getEntityId());
                totalEntitiesCulled++;
                
                // Apply culling
                applyEntityCulling(entity);
            }
        }
    }
    
    /**
     * Determines if an entity should be culled based on type and settings
     */
    private boolean shouldCullEntity(Entity entity) {
        // Check global culling
        if (!globalCullingEnabled) {
            return false;
        }
        
        // Check distance
        double distance = MC.player.getDistance(entity);
        if (distance > cullingDistance) {
            return true; // Cull if too far
        }
        
        // Check entity type culling preferences
        if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
            return cullPlayers;
        } else if (entity instanceof net.minecraft.entity.monster.EntityMob) {
            return cullMobs;
        } else if (entity instanceof net.minecraft.entity.passive.EntityAnimal) {
            return cullAnimals;
        } else if (entity instanceof net.minecraft.entity.item.EntityItem) {
            return cullItems;
        } else if (entity instanceof net.minecraft.entity.item.EntityArmorStand) {
            return cullArmorStands;
        }
        
        return false;
    }
    
    /**
     * Applies culling to an entity (makes it invisible to renderer)
     */
    private void applyEntityCulling(Entity entity) {
        // Set entity as invisible
        entity.setInvisible(true);
        
        // Alternative: Disable entity rendering
        entity.ignoreItemEntityData = true;
        
        // Option to disable entity physics/AI
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase livingEntity = (EntityLivingBase) entity;
            // Disable AI updates if needed
            livingEntity.noClip = false; // Keep collision but hide rendering
        }
    }
    
    /**
     * Removes culling from an entity
     */
    private void removeEntityCulling(Entity entity) {
        entity.setInvisible(false);
        entity.ignoreItemEntityData = false;
        
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase livingEntity = (EntityLivingBase) entity;
            livingEntity.noClip = false;
        }
    }
    
    /**
     * Toggles all entity culling on/off
     */
    public void toggleGlobalCulling() {
        this.globalCullingEnabled = !this.globalCullingEnabled;
        
        if (!globalCullingEnabled) {
            // Restore all culled entities when disabling
            for (int entityId : culledEntities) {
                Entity entity = MC.world.getEntityByID(entityId);
                if (entity != null) {
                    removeEntityCulling(entity);
                }
            }
        }
    }
    
    /**
     * Toggles player culling
     */
    public void togglePlayerCulling() {
        this.cullPlayers = !this.cullPlayers;
    }
    
    /**
     * Toggles mob culling
     */
    public void toggleMobCulling() {
        this.cullMobs = !this.cullMobs;
    }
    
    /**
     * Toggles animal culling
     */
    public void toggleAnimalCulling() {
        this.cullAnimals = !this.cullAnimals;
    }
    
    /**
     * Toggles item culling
     */
    public void toggleItemCulling() {
        this.cullItems = !this.cullItems;
    }
    
    /**
     * Toggles armor stand culling
     */
    public void toggleArmorStandCulling() {
        this.cullArmorStands = !this.cullArmorStands;
    }
    
    /**
     * Culls all entities except the player
     */
    public void cullAllEntities() {
        this.cullPlayers = true;
        this.cullMobs = true;
        this.cullAnimals = true;
        this.cullItems = true;
        this.cullArmorStands = true;
        this.globalCullingEnabled = true;
    }
    
    /**
     * Restores visibility of all entities
     */
    public void restoreAllEntities() {
        this.cullPlayers = false;
        this.cullMobs = false;
        this.cullAnimals = false;
        this.cullItems = false;
        this.cullArmorStands = false;
        this.globalCullingEnabled = false;
        
        if (MC.world != null) {
            for (Entity entity : MC.world.loadedEntityList) {
                removeEntityCulling(entity);
            }
        }
    }
    
    /**
     * Gets culling statistics for debugging/info
     */
    public String getCullingStats() {
        return String.format("Culled: %d/%d entities | Distance: %.1f", 
            totalEntitiesCulled, totalEntitiesProcessed, cullingDistance);
    }
    
    // ========== Getters and Setters ==========
    
    public boolean isGlobalCullingEnabled() {
        return globalCullingEnabled;
    }
    
    public void setGlobalCullingEnabled(boolean enabled) {
        this.globalCullingEnabled = enabled;
    }
    
    public boolean isCullingPlayers() {
        return cullPlayers;
    }
    
    public void setCullPlayers(boolean cull) {
        this.cullPlayers = cull;
    }
    
    public boolean isCullingMobs() {
        return cullMobs;
    }
    
    public void setCullMobs(boolean cull) {
        this.cullMobs = cull;
    }
    
    public boolean isCullingAnimals() {
        return cullAnimals;
    }
    
    public void setCullAnimals(boolean cull) {
        this.cullAnimals = cull;
    }
    
    public boolean isCullingItems() {
        return cullItems;
    }
    
    public void setCullItems(boolean cull) {
        this.cullItems = cull;
    }
    
    public boolean isCullingArmorStands() {
        return cullArmorStands;
    }
    
    public void setCullArmorStands(boolean cull) {
        this.cullArmorStands = cull;
    }
    
    public double getCullingDistance() {
        return cullingDistance;
    }
    
    public void setCullingDistance(double distance) {
        this.cullingDistance = Math.max(distance, 10.0d);
    }
    
    public int getTotalCulledEntities() {
        return totalEntitiesCulled;
    }
    
    public int getTotalProcessedEntities() {
        return totalEntitiesProcessed;
    }
    
    public Set<Integer> getCulledEntityIds() {
        return new HashSet<>(culledEntities);
    }
}