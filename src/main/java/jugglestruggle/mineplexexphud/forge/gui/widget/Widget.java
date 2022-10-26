package jugglestruggle.mineplexexphud.forge.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;

public interface Widget
{
    int getX();
    int getY();
    int getW();
    int getH();
    int getXOffset();
    int getYOffset();
    
    void setX(int x);
    void setY(int y);
    void setW(int w);
    void setH(int h);
    void setXOffset(int x);
    void setYOffset(int y);
    
    void tick();
    
    boolean isVisibleToUser();  // alternative for: isVisible
    boolean isInteractable();   // alternative for: isEnabled
    boolean isAttentionGiven(); // alternative for: isFocused
    
    void setVisibleToUser(boolean visible);
    void setInteractable(boolean enabled);
    void setAttentionGiven(boolean focused);
    
    // Forge just likes to crash in non-dev environments should I choose to include
    // what they actually are supposed to mean... due to, possibly, mapping renaming...
    /*
    boolean isVisible();
    boolean isEnabled();
    boolean isFocused();
    
    void setVisible(boolean visible);
    void setEnabled(boolean enabled);
    void setFocused(boolean focused);
    */
    
    void render(Minecraft client, int mouseX, int mouseY, float delta);
    
    boolean onMouseDown(Minecraft client, int mouseX, int mouseY, int button);
    boolean onMouseUp(int mouseX, int mouseY, int button);
    
    /*
    default boolean onKeyDown(char charCode, int keyCode)  { return true; }
    default boolean onKeyUp(char charCode, int keyCode)    { return true; }
    */
    default boolean onKeyTyped(char charCode, int keyCode) { return true; }
    
    default Widget setPos(int x, int y) {
        this.setX(x); this.setY(y); return this;
    }
    default Widget setSize(int w, int h) {
        this.setW(w); this.setH(h); return this;
    }
    default Widget setPosOffset(int x, int y) {
        this.setXOffset(x); this.setYOffset(y); return this;
    }
    
    default int getXR() {
        return this.getX() + this.getW();
    }
    default int getYB() {
        return this.getY() + this.getH();
    }
    
    default boolean isActuallyFocused() {
        return this.isInteractable() && this.isVisibleToUser() && this.isAttentionGiven();
    }
    
    default boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getW() &&
               mouseY >= this.getY() && mouseY < this.getY() + getH();
    }
    
    default boolean isVisibleAndMouseOver(int mouseX, int mouseY) {
        return this.isVisibleToUser() && this.isMouseOver(mouseX, mouseY);
    }
    default boolean isInteractableAndMouseOver(int mouseX, int mouseY) {
        return this.isInteractable() && this.isVisibleAndMouseOver(mouseX, mouseY);
    }
    
    default void emitSoundOnClick(SoundHandler handler) {}
    
    default String[] getTooltipLines(int mouseX, int mouseY) {
        return null;
    }
}
