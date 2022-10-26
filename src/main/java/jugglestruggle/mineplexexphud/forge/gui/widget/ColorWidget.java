package jugglestruggle.mineplexexphud.forge.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class ColorWidget extends AbstractMultiWidget
{
    static String hexPattern = "[\\da-fA-F]";
//    static String hexPattern = "[\\da-fA-F]";
    
    static Pattern[] hexColorPatterns = {
        Pattern.compile("#("+ hexPattern +"{1,8})")
//        Pattern.compile("("+ hexPattern +"{1,8})")
    };
    
    static String hexColor(int color) {
        return "#" + Integer.toHexString(color);
    }
    
    int color; int startingColor;
    ButtonWidget resetColorButton;
    TextWidget colorAreaTextBox;
    
    BiConsumer<ColorWidget, Integer> colorChangeListener;
    
    String[] tooltipText;
    boolean ignoreTextChecks;
    
    public ColorWidget(FontRenderer fr, int w, int color, String tooltipText)
    {
        super(w, 20, 2);
        
        this.resetColorButton = new ButtonWidget(20, 20, "\u21BA");
        this.colorAreaTextBox = new TextWidget(fr, w - 42, 18);
    
        this.resetColorButton.setTooltipText(I18n.format(LANG_FORMAT + "color.reset", tooltipText));
        this.resetColorButton.setOnClick(this::onResetClick);
    
        this.colorAreaTextBox.setText(ColorWidget.hexColor(color));
        this.colorAreaTextBox.setValidator(this::isTextValid);
        this.colorAreaTextBox.setTextChangedListener(this::onColorTextChange);
        this.colorAreaTextBox.setMaxStringLength(9);
    
        this.childWidgets.add(this.resetColorButton);
        this.childWidgets.add(this.colorAreaTextBox);
    
        this.startingColor = color;
        this.changeColor(color);
    
        this.tooltipText = tooltipText.split("\\\\n");
        this.ignoreTextChecks = false;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public ColorWidget setOnColorApply(BiConsumer<ColorWidget, Integer> colorApply) {
        this.colorChangeListener = colorApply; return this;
    }
    
    public boolean isTextValid(String s)
    {
        if (this.ignoreTextChecks || s.equals("#"))
            return true;
//        if (this.ignoreTextChecks || s.isEmpty() || s.equals("#"))
//            return true;
        else if (s.startsWith("#"))
            return hexColorPatterns[0].matcher(s).find();
        else
            return false;
            // return hexColorPatterns[1].matcher(s).find();
    }
    private void onColorTextChange(String newText, String oldText)
    {
        if (this.ignoreTextChecks)
            return;
            
        try
        {
            int color = Integer.parseInt(newText);
            this.changeColor(color);
        }
        catch (NumberFormatException e)
        {
            for (Pattern p : hexColorPatterns)
            {
                Matcher m = p.matcher(newText);
    
                if (m.find())
                {
                    // try converting to int by converting from a hexadecimal
                    try
                    {
                        int color = Integer.parseUnsignedInt(m.group(1), 16);
                        
                        this.changeColor(color);
                        
                        break;
                    }
                    catch (NumberFormatException e2) {
                        e2.printStackTrace();
                        // swallow
                    }
                }
            }
        }
    }
    
    private boolean onResetClick(ButtonWidget b)
    {
        this.changeColor(this.startingColor);
        
        this.ignoreTextChecks = true;
    
        boolean startedWithHash = this.colorAreaTextBox.getText().startsWith("#");
        this.colorAreaTextBox.setText((startedWithHash ? "#" : "") + Integer.toHexString(this.startingColor));
    
        this.ignoreTextChecks = false;
        
        return true;
    }
    
    void changeColor(int newColor)
    {
        if (this.colorChangeListener != null)
            this.colorChangeListener.accept(this, newColor);
    
        this.color = newColor;
        this.resetColorButton.setInteractable(this.color != this.startingColor);
    }
    
    
    @Override
    protected void updateWidgetPositions()
    {
        this.resetColorButton.setPos(this.x + this.width - 20, this.y);
        this.colorAreaTextBox.setPos(this.x + 21, this.y + 1);
    }
    
    @Override
    public void render(Minecraft client, int mouseX, int mouseY, float delta)
    {
        if (this.isVisibleToUser())
        {
            super.render(client, mouseX, mouseY, delta);
    
            final int sX = this.getX();
            final int sY = this.getY();
    
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 300);
            
            Gui.drawRect(sX, sY,
                    sX + 20,
                    sY + 20, 0xFF000000);
            Gui.drawRect(sX + 2, sY + 2,
                    sX + 18, sY + 18, this.color);
            
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public String[] getTooltipLines(int mouseX, int mouseY)
    {
        return this.resetColorButton.isMouseOver(mouseX, mouseY) ?
                this.resetColorButton.getTooltipLines(mouseX, mouseY) : this.tooltipText;
    }
}
