package jugglestruggle.mineplexexphud.forge.gui.widget;

import jugglestruggle.mineplexexphud.forge.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class TextWidget extends GuiTextField implements Widget
{
    static Field isEnabledField;
    
    static
    {
        isEnabledField = ReflectionUtils.getAndAccessField(GuiTextField.class, true,
                "isEnabled", "field_146226_p");
    }
    
    
    
    protected int xOffset;
    protected int yOffset;
    
    /**
     * Left: New Text, Right: Old Text
     */
    BiConsumer<String, String> textChangedListener;
    
    String[] tooltipText;
    
    public TextWidget(FontRenderer textRenderer, int width, int height) {
        super(0, textRenderer, 0, 0, width, height); this.isInteractable();
    }
    /**
     * Left: New Text, Right: Old Text
     */
    public void setTextChangedListener(BiConsumer<String, String> listener) {
        this.textChangedListener = listener;
    }
    
    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText.split("\\\\n");
    }
    
    @Override
    public void render(Minecraft client, int mouseX, int mouseY, float delta) {
        super.drawTextBox();
    }
    
    @Override
    public boolean onMouseDown(Minecraft client, int mouseX, int mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);
        return this.isMouseOver(mouseX, mouseY);
    }
    @Override
    public boolean onMouseUp(int mouseX, int mouseY, int button) {
        return false;
    }
    
    @Override
    public void setText(String newText) {
        this.applyTextThenListen(() -> super.setText(newText));
    }
    
    @Override
    public void writeText(String text) {
        this.applyTextThenListen(() -> super.writeText(text));
    }
    
    @Override
    public void deleteFromCursor(int cursor) {
        this.applyTextThenListen(() -> super.deleteFromCursor(cursor));
    }
    
    @Override
    public void setMaxStringLength(int size) {
        this.applyTextThenListen(() -> super.setMaxStringLength(size));
    }
    
    
    private void applyTextThenListen(Runnable r)
    {
        String oldText = this.getText();
        r.run();
    
        String newText = this.getText();
    
        if (!oldText.equals(newText))
            this.onTextChanged(newText, oldText);
    }
    
    protected void onTextChanged(String newText, String oldText)
    {
        if (this.textChangedListener != null)
            this.textChangedListener.accept(newText, oldText);
    }
    
    @Override
    public int getX() {
        return super.xPosition;
    }
    @Override
    public int getY() {
        return super.yPosition;
    }
    @Override
    public int getXOffset() {
        return this.xOffset;
    }
    @Override
    public int getYOffset() {
        return this.yOffset;
    }
    @Override
    public int getW() {
        return super.width;
    }
    @Override
    public int getH() {
        return super.height;
    }
    
    @Override
    public void setX(int x) {
        super.xPosition = x;
    }
    @Override
    public void setY(int y) {
        super.yPosition = y;
    }
    
    @Override
    public void setXOffset(int x) {
        this.xOffset = x;
    }
    @Override
    public void setYOffset(int y) {
        this.yOffset = y;
    }
    
    @Override
    public void setW(int w) {
        super.width = w;
    }
    @Override
    public void setH(int h) {
        super.height = h;
    }
    
    @Override
    public void tick() {
        this.updateCursorCounter();
    }
    
    
    @Override
    public boolean isVisibleToUser() {
        return super.getVisible();
    }
    @Override
    public boolean isInteractable()
    {
        try {
            return isEnabledField.getBoolean(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
    @Override
    public boolean isAttentionGiven() {
        return super.isFocused();
    }
    
    @Override
    public void setVisibleToUser(boolean visible) {
        super.setVisible(visible);
    }
    
    @Override
    public void setInteractable(boolean enabled) {
        super.setEnabled(enabled);
    }
    
    @Override
    public void setAttentionGiven(boolean focused) {
        super.setFocused(focused);
    }
    
    @Override
    public boolean onKeyTyped(char charCode, int keyCode) {
        return this.textboxKeyTyped(charCode, keyCode);
    }
    
    @Override
    public String[] getTooltipLines(int mouseX, int mouseY) {
        return this.tooltipText;
    }
}
