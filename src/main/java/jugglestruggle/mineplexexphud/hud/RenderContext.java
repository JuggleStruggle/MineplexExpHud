package jugglestruggle.mineplexexphud.hud;

public interface RenderContext
{
    void createScissor(int x, int y, int w, int h);
    void removeScissor();
    
    void fill(float left, float top, float right, float bottom, int color);
    void outerWireframe(float left, float top, float right, float bottom, int color);
    void drawString(String text, float x, float y, int color, boolean drawTextWithShadow);
    
    void enableBlend();
    void disableBlend();
    void enableTexture();
    void disableTexture();
    
    void defaultBlendFunc();
    
    
    default void fillWithWireframe(float left, float top, float right, float bottom, int fillColor, int wireframeColor)
    {
        this.fill(left, top, right, bottom, fillColor);
        this.outerWireframe(left, top, right, bottom, wireframeColor);
    }
    
    /**
     * Gets the scaled resolution but in integers.
     *
     * @return
     * <ul>
     *     <li> 0 = Scaled X Size </li>
     *     <li> 1 = Scaled Y Size </li>
     *     <li> 2 = Scale Factor </li>
     *     <li> 3 = Original X Size </li>
     *     <li> 4 = Original Y Size </li>
     * </ul>
     */
    int[] getSCR();
}
