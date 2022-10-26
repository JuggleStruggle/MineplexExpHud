package jugglestruggle.mineplexexphud.forge;

import jugglestruggle.mineplexexphud.hud.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ForgeRenderContext implements RenderContext
{
    private final Minecraft client;
    private ScaledResolution sr;
    
    public ForgeRenderContext()
    {
        this.client = Minecraft.getMinecraft();
        this.updateScaledResolution();
    }
    
    public void updateScaledResolution() {
        this.updateScaledResolution(new ScaledResolution(this.client));
    }
    public void updateScaledResolution(ScaledResolution sr) {
        this.sr = sr;
    }
    
    public Minecraft getClient() {
        return this.client;
    }
    
    @Override
    public void createScissor(int x, int y, int w, int h)
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    
        final int s = this.sr.getScaleFactor();
        final int heightS = (int)((float)h * (float)s);
    
        GL11.glScissor(x * s, (this.client.displayHeight - (y * s) - heightS), w * s, heightS);
    }
    
    @Override
    public void removeScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
    
    @Override
    public void fill(float left, float top, float right, float bottom, int color)
    {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
        GlStateManager.color
        (
            (color >> 16 & 255) / 255.0F,
            (color >> 8 & 255) / 255.0F,
            (color & 255) / 255.0F,
            (color >> 24 & 255) / 255.0F
        );
        
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos( left, bottom, 0.0D).endVertex();
        wr.pos(right, bottom, 0.0D).endVertex();
        wr.pos(right,    top, 0.0D).endVertex();
        wr.pos( left,    top, 0.0D).endVertex();
        tess.draw();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        
    }
    
    @Override
    public void outerWireframe(float left, float top, float right, float bottom, int color)
    {
        double left2; double top2;
        double right2; double bottom2;
    
            top2 = top;
            right2 = right;
            bottom2 = bottom;
            left2 = left;
        
            left = left - 1;
            top = top - 1;
            right = right + 1;
            bottom = bottom + 1;
    
    
        Tessellator ts = Tessellator.getInstance();
        WorldRenderer wr = ts.getWorldRenderer();
    
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
        GlStateManager.color
        (
            (color >> 16 & 255) / 255.0F,
            (color >> 8 & 255) / 255.0F,
            (color & 255) / 255.0F,
            (color >> 24 & 255) / 255.0F
        );
    
    
        // Top Rect
        wr.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        // Top Rect: Top Right
        wr.pos(right2, top2, 0.0d).endVertex();
        wr.pos(right , top , 0.0d).endVertex();
        wr.pos(left,   top , 0.0d).endVertex();
        // Top Rect: Bottom Left
        wr.pos(left,   top , 0.0d).endVertex();
        wr.pos(left2 , top2, 0.0d).endVertex();
        wr.pos(right2, top2, 0.0d).endVertex();
        ts.draw();
    
        // Left Rect
        wr.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        // Left Rect: Top Right
        wr.pos(left2, bottom2, 0.0d).endVertex();
        wr.pos(left2,    top2, 0.0d).endVertex();
        wr.pos(left,      top, 0.0d).endVertex();
        // Left Rect: Bottom Left
        wr.pos(left,      top, 0.0d).endVertex();
        wr.pos(left,   bottom, 0.0d).endVertex();
        wr.pos(left2, bottom2, 0.0d).endVertex();
        ts.draw();
    
        // Bottom Rect
        wr.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        // Bottom Rect: Top Right
        wr.pos(right , bottom , 0.0d).endVertex();
        wr.pos(right2, bottom2, 0.0d).endVertex();
        wr.pos(left2 , bottom2, 0.0d).endVertex();
        // Bottom Rect: Bottom Left
        wr.pos(right,  bottom , 0.0d).endVertex();
        wr.pos(left2,  bottom2, 0.0d).endVertex();
        wr.pos(left ,  bottom , 0.0d).endVertex();
        ts.draw();
    
        // Right Rect
        wr.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        // Right  Rect: Top Right
        wr.pos(right ,  bottom, 0.0d).endVertex();
        wr.pos(right ,     top, 0.0d).endVertex();
        wr.pos(right2,    top2, 0.0d).endVertex();
        // Right Rect: Bottom Left
        wr.pos(right,   bottom, 0.0d).endVertex();
        wr.pos(right2,    top2, 0.0d).endVertex();
        wr.pos(right2, bottom2, 0.0d).endVertex();
        ts.draw();
    
    
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    @Override
    public void drawString(String text, float x, float y, int color, boolean withShadow) {
        this.client.fontRendererObj.drawString(text, x, y, color, withShadow);
    }
    
    @Override
    public void enableBlend() {
        GlStateManager.enableBlend();
    }
    
    @Override
    public void disableBlend() {
        GlStateManager.disableBlend();
    }
    
    @Override
    public void enableTexture() {
        GlStateManager.enableTexture2D();
    }
    
    @Override
    public void disableTexture() {
        GlStateManager.disableTexture2D();
    }
    
    @Override
    public void defaultBlendFunc() {
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }
    
    @Override
    public int[] getSCR()
    {
        return new int[]
        {
            this.sr.getScaledWidth(),
            this.sr.getScaledHeight(),
            this.sr.getScaleFactor(),
            this.client.displayWidth,
            this.client.displayHeight
        };
    }
}
