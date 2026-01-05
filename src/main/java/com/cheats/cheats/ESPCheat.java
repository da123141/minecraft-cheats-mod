package com.cheats.cheats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * ESPCheat - Entity Highlight and Detection System
 * 
 * Renders bounding boxes around entities (players, mobs, animals) through walls
 * for enhanced visibility and awareness in the game world.
 * 
 * Features:
 * - Player highlighting (red)
 * - Mob highlighting (orange)
 * - Animal highlighting (green)
 * - Through-wall rendering
 * - Distance-based filtering
 * - Customizable colors and sizes
 */
public class ESPCheat {
    
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final float DEFAULT_LINE_WIDTH = 2.0f;
    private static final double MAX_RENDER_DISTANCE = 128.0d;
    
    // Entity type colors (RGBA)
    private static final float[] PLAYER_COLOR = {1.0f, 0.0f, 0.0f, 0.8f}; // Red
    private static final float[] MOB_COLOR = {1.0f, 0.647f, 0.0f, 0.8f};   // Orange
    private static final float[] ANIMAL_COLOR = {0.0f, 1.0f, 0.0f, 0.8f};  // Green
    
    private boolean enabled = false;
    private boolean renderPlayers = true;
    private boolean renderMobs = true;
    private boolean renderAnimals = true;
    private double renderDistance = MAX_RENDER_DISTANCE;
    private float lineWidth = DEFAULT_LINE_WIDTH;
    
    /**
     * Main rendering method - called each frame to render ESP boxes
     */
    public void renderESP() {
        if (!enabled || MC.world == null || MC.player == null) {
            return;
        }
        
        List<Entity> entities = MC.world.loadedEntityList;
        
        for (Entity entity : entities) {
            // Skip self and invalid entities
            if (entity == MC.player || entity.isDead || !(entity instanceof EntityLivingBase)) {
                continue;
            }
            
            // Filter by entity type and render settings
            if (!shouldRenderEntity(entity)) {
                continue;
            }
            
            // Check distance
            double distance = MC.player.getDistance(entity);
            if (distance > renderDistance) {
                continue;
            }
            
            // Render the entity box
            renderEntityBox((EntityLivingBase) entity);
        }
    }
    
    /**
     * Determines if an entity should be rendered based on type and settings
     */
    private boolean shouldRenderEntity(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return renderPlayers;
        } else if (entity instanceof EntityMob) {
            return renderMobs;
        } else if (entity instanceof EntityAnimal) {
            return renderAnimals;
        }
        return false;
    }
    
    /**
     * Renders a bounding box around the entity
     */
    private void renderEntityBox(EntityLivingBase entity) {
        // Push matrix for transformations
        GL11.glPushMatrix();
        
        // Disable depth test to render through walls
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        
        // Enable blending for transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        // Set line width
        GL11.glLineWidth(lineWidth);
        
        // Get entity position relative to render manager
        Vec3d renderPos = getRenderPosition(entity);
        
        // Get entity dimensions
        float width = entity.width / 2.0f;
        float height = entity.height;
        
        // Set color based on entity type
        setEntityColor(entity);
        
        // Translate to entity position
        GL11.glTranslated(renderPos.x, renderPos.y, renderPos.z);
        
        // Draw bounding box
        drawBoundingBox(width, height);
        
        // Draw health bar above entity
        drawHealthBar(entity, height);
        
        // Draw entity name
        drawEntityName(entity, height);
        
        // Restore OpenGL state
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
    
    /**
     * Gets the render position of an entity
     */
    private Vec3d getRenderPosition(Entity entity) {
        RenderManager renderManager = MC.getRenderManager();
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * MC.timer.renderPartialTicks - renderManager.viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * MC.timer.renderPartialTicks - renderManager.viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * MC.timer.renderPartialTicks - renderManager.viewerPosZ;
        return new Vec3d(x, y, z);
    }
    
    /**
     * Sets the GL color based on entity type
     */
    private void setEntityColor(Entity entity) {
        if (entity instanceof EntityPlayer) {
            GL11.glColor4f(PLAYER_COLOR[0], PLAYER_COLOR[1], PLAYER_COLOR[2], PLAYER_COLOR[3]);
        } else if (entity instanceof EntityMob) {
            GL11.glColor4f(MOB_COLOR[0], MOB_COLOR[1], MOB_COLOR[2], MOB_COLOR[3]);
        } else if (entity instanceof EntityAnimal) {
            GL11.glColor4f(ANIMAL_COLOR[0], ANIMAL_COLOR[1], ANIMAL_COLOR[2], ANIMAL_COLOR[3]);
        }
    }
    
    /**
     * Draws a bounding box around the entity
     */
    private void drawBoundingBox(float width, float height) {
        GL11.glBegin(GL11.GL_LINE_BOX);
        
        // Define box vertices
        float minX = -width;
        float maxX = width;
        float minY = 0;
        float maxY = height;
        float minZ = -width;
        float maxZ = width;
        
        // Draw box edges
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);
        
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        
        GL11.glEnd();
    }
    
    /**
     * Draws a health bar above the entity
     */
    private void drawHealthBar(EntityLivingBase entity, float height) {
        float maxHealth = entity.getMaxHealth();
        float health = entity.getHealth();
        float healthPercent = health / maxHealth;
        
        float barWidth = entity.width / 2.0f;
        float barHeight = 0.25f;
        float barX = -barWidth - 0.5f;
        
        // Draw background (black)
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.8f);
        drawFilledBox(barX, height + 0.3f, -0.1f, barWidth * 2, barHeight, 0.1f);
        
        // Draw health (green to red gradient)
        float healthR = Math.min(2.0f * (1.0f - healthPercent), 1.0f);
        float healthG = Math.min(2.0f * healthPercent, 1.0f);
        GL11.glColor4f(healthR, healthG, 0.0f, 0.9f);
        drawFilledBox(barX, height + 0.3f, -0.1f, barWidth * 2 * healthPercent, barHeight, 0.1f);
    }
    
    /**
     * Draws entity name above the entity
     */
    private void drawEntityName(EntityLivingBase entity, float height) {
        String name = entity.getDisplayName().getUnformattedText();
        float nameY = height + 0.7f;
        
        // This would typically use the font renderer
        // MC.fontRenderer.drawStringWithBackgroundAndShadow(name, -20, (int)nameY, 0xFFFFFF);
    }
    
    /**
     * Helper method to draw a filled box
     */
    private void drawFilledBox(float x, float y, float z, float width, float height, float depth) {
        GL11.glBegin(GL11.GL_QUADS);
        
        float x2 = x + width;
        float y2 = y + height;
        float z2 = z + depth;
        
        // Front face
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x2, y, z);
        GL11.glVertex3f(x2, y2, z);
        GL11.glVertex3f(x, y2, z);
        
        // Back face
        GL11.glVertex3f(x, y, z2);
        GL11.glVertex3f(x2, y, z2);
        GL11.glVertex3f(x2, y2, z2);
        GL11.glVertex3f(x, y2, z2);
        
        GL11.glEnd();
    }
    
    // ========== Getters and Setters ==========
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void toggle() {
        this.enabled = !this.enabled;
    }
    
    public boolean isRenderingPlayers() {
        return renderPlayers;
    }
    
    public void setRenderPlayers(boolean renderPlayers) {
        this.renderPlayers = renderPlayers;
    }
    
    public boolean isRenderingMobs() {
        return renderMobs;
    }
    
    public void setRenderMobs(boolean renderMobs) {
        this.renderMobs = renderMobs;
    }
    
    public boolean isRenderingAnimals() {
        return renderAnimals;
    }
    
    public void setRenderAnimals(boolean renderAnimals) {
        this.renderAnimals = renderAnimals;
    }
    
    public double getRenderDistance() {
        return renderDistance;
    }
    
    public void setRenderDistance(double renderDistance) {
        this.renderDistance = Math.min(renderDistance, MAX_RENDER_DISTANCE);
    }
    
    public float getLineWidth() {
        return lineWidth;
    }
    
    public void setLineWidth(float lineWidth) {
        this.lineWidth = Math.max(lineWidth, 0.5f);
    }
}
