/*
 * MineplexExpHud: A mod which tracks the current
 * EXP the user has on the Mineplex server.
 * Copyright (C) 2022  JuggleStruggle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 *  <https://www.gnu.org/licenses/>.
 */

package jugglestruggle.mineplexexphud.forge.gui.widget;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class CyclingButtonWidget<V> extends ButtonWidget
{
    public static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(true, false);
    
    
    protected V value;
    protected V defaultValue;
    protected List<V> values;
    protected Function<V, String> valueToText;
    protected Function<V, String> valueToTooltipText;
    
    private int valueIndex;
    
    private String baseText;
    private String displayTextFormatting;
    
    private BiPredicate<CyclingButtonWidget<V>, V> onValueChangeListener;
    private Consumer<CyclingButtonWidget<V>> onPostValueChangeListener;
    
    public CyclingButtonWidget(int w, int h, String title, V value, List<V> values, Function<V, String> valueToText) {
        this(w, h, title, value, values, valueToText, null);
    }
    public CyclingButtonWidget(int w, int h, String title, V value, List<V> values, Function<V, String> valueToText,
                               Function<V, String> valueToTooltipText)
    {
        super(w, h, title);
        
        this.baseText = title;
        this.value = value;
        this.defaultValue = value;
        this.values = values;
        
        this.valueToText = valueToText;
        this.valueToTooltipText = valueToTooltipText;
        
        this.updateValueIndex();
        this.defaultDisplayFormat();
    }
    
    public V getValue() {
        return this.value;
    }
    public V getDefaultValue() {
        return this.defaultValue;
    }
    
    
    public void setValue(V newValue) {
        this.setValue(newValue, true);
    }
    private void setValue(V newValue, boolean setIndex)
    {
        if (this.onValueChangeListener != null && !this.onValueChangeListener.test(this, newValue))
            return;
    
        this.value = newValue;
        
        if (setIndex)
            this.updateValueIndex();
    
        this.updateDisplayText();
        
        if (this.onPostValueChangeListener != null)
            this.onPostValueChangeListener.accept(this);
    }
    
    
    private void updateValueIndex() {
        this.valueIndex = this.values.indexOf(this.value);
    }
    public void updateDisplayText()
    {
        super.displayString = String.format(this.displayTextFormatting,
                this.baseText, this.valueToText.apply(this.value));
    
        if (this.valueToTooltipText != null)
            super.setTooltipText(this.valueToTooltipText.apply(this.value));
    }
    
    public void cycleByOne(boolean forwards)
    {
        final int size = this.values.size();
        
        if (forwards)
        {
            ++this.valueIndex;
            
            if (this.valueIndex >= size) {
                this.valueIndex = 0;
            }
        }
        else
        {
            --this.valueIndex;
            
            if (this.valueIndex < 0) {
                this.valueIndex = size - 1;
            }
        }
        
        this.setValue(this.values.get(this.valueIndex), false);
    }
    
    public CyclingButtonWidget<V> setValueChangeListener(BiPredicate<CyclingButtonWidget<V>, V> changeListener) {
        this.onValueChangeListener = changeListener; return this;
    }
    public CyclingButtonWidget<V> setPostValueChangeListener(Consumer<CyclingButtonWidget<V>> changeListener) {
        this.onPostValueChangeListener = changeListener; return this;
    }
//    public CyclingButtonWidget<V> setValueToTooltipText(Function<V, String> valueToTooltipText) {
//        this.valueToTooltipText = valueToTooltipText; return this;
//    }
    
    public void defaultDisplayFormat() {
        this.setDisplayFormat("%1$s: %2$s");
    }
    public void setDisplayFormat(String displayFormat)
    {
        this.displayTextFormatting = displayFormat;
        this.updateDisplayText();
    }
    
    public CyclingButtonWidget<V> setPos(int x, int y) {
        super.xPosition = x; super.yPosition = y; return this;
    }
    public CyclingButtonWidget<V> setSize(int w, int h) {
        super.width = w; super.height = h; return this;
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.superMousePressed(mouseX, mouseY))
        {
            this.cycleByOne(!GuiScreen.isShiftKeyDown());
            return true;
        }
        
        return false;
    }
    
    
    
    
    
    
    /**
     * Creates a boolean cycling button widget with the width and height set to 200, 20.
     *
     * @param value the default value to start with
     * @param type 0 = Minecraft's default: ON & OFF | 1 = YES & NO
     *
     * @return a new cycling button widget with boolean values to tinker with; don't forget
     * to set the value change listener and the base text to use!
     */
    public static CyclingButtonWidget<Boolean> bool(boolean value, byte type) {
        return bool(200, 20, null, value, type, null, null);
    }
    
    public static CyclingButtonWidget<Boolean> bool(int w, int h, String title, boolean value, byte type) {
        return bool(w, h, title, value, type, null, null);
    }
    public static CyclingButtonWidget<Boolean> bool(int w, int h, String title, boolean value, String trueText, String falseText) {
        return bool(w, h, title, value, (byte)2, trueText, falseText);
    }
    /**
     * Creates a boolean cycling button widget with the set width and height, along with the title.
     *
     * @param value the default value to start with
     * @param type 0 & Default = Minecraft's default: ON & OFF | 1 = YES & NO | 2 = use custom True and False text
     * @param trueText  custom text to use for Value -> Text when true;  must have {@code type} be set to {@code 2}
     * @param falseText custom text to use for Value -> Text when false; must have {@code type} be set to {@code 2}
     *
     * @return a new cycling button widget with boolean values to tinker with; don't forget
     * to set the value change listener!
     */
    public static CyclingButtonWidget<Boolean> bool(int w, int h, String title, boolean value, byte type, String trueText, String falseText)
    {
        Function<Boolean, String> valueToText;
    
        if (type == 2 && (trueText == null || falseText == null)) {
            type = 0;
        }
        
        // Depending on the type, we'll have to do our thing here...
        switch (type)
        {
            default:
            
            case 0: { // Minecraft's default (ON & OFF)
                valueToText = b -> I18n.format("options." + (b ? "on" : "off")); break;
            }
            case 1: { // YES & NO
                valueToText = b -> I18n.format("jugglestruggle." + (b ? "yes" : "no")); break;
            }
            case 2: { //
                valueToText = b -> I18n.format(b ? trueText : falseText,  new Object[0]); break;
            }
//            case 3: { //
//                valueToText = b -> I18n.format(b ? "" : "",  new Object[0]); break;
//            }
        }
        
        return new CyclingButtonWidget<>(w, h, title == null ? "" : title, value, BOOLEAN_VALUES, valueToText);
    }
}
