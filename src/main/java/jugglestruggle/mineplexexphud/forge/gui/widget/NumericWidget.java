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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class NumericWidget extends AbstractMultiWidget
{
    protected double value;
    protected double startingValue;
    protected Double min;
    protected Double max;
    
    protected final TextWidget numbersWidget;
    protected final ButtonWidget minusButton;
    protected final ButtonWidget plusButton;
    
    /** Left: New, Right: Old */
    protected BiPredicate<Double, Double> onValueChanged;
    /** Left: New, Right: Old */
    protected BiConsumer<Double, Double> onPostValueChanged;
    
    protected boolean ignoreTextChecks;
    protected boolean includeDecimals;
    
    public NumericWidget(FontRenderer fr, int w, int h, int value, int min, int max, String tooltipText) {
        this(fr, w, h, value, (double)min, (double)max, tooltipText, false);
    }
    public NumericWidget(FontRenderer fr, int w, int h, long value, long min, long max, String tooltipText) {
        this(fr, w, h, value, (double)min, (double)max, tooltipText, false);
    }
    public NumericWidget(FontRenderer fr, int w, int h, float value, float min, float max, String tooltipText) {
        this(fr, w, h, value, (double)min, (double)max, tooltipText, true);
    }
    public NumericWidget(FontRenderer fr, int w, int h, double value, Double min, Double max, String tooltipText) {
        this(fr, w, h, value, min, max, tooltipText, true);
    }
    public NumericWidget(FontRenderer fr, int w, int h, double value, Double min, Double max, String tooltipText, boolean includeDecimals)
    {
        super(w, h, 3);
        
        this.value = value;
        this.min = min;
        this.max = max;
    
        this.startingValue = value;
        this.includeDecimals = includeDecimals;
    
        this.numbersWidget = new TextWidget(fr, w - 42, h - 2);
        this.minusButton = new ButtonWidget(20, h, "-", this::onMinusClick);
        this.plusButton = new ButtonWidget(20, h, "+", this::onPlusClick);
        
        this.numbersWidget.setText(this.includeDecimals ? Double.toString(value) : Long.toString((long)value));
        this.numbersWidget.setValidator(this::isNumericTextValid);
        this.numbersWidget.setTextChangedListener(this::applyNewTextAsValue);
        
        this.numbersWidget.setTooltipText(tooltipText);
        this.minusButton.setTooltipText(I18n.format(LANG_FORMAT + "numerics.tooltipFormatForButtons",
                tooltipText, I18n.format(LANG_FORMAT + "numerics.tooltipSubtract")));
        this.plusButton.setTooltipText(I18n.format(LANG_FORMAT + "numerics.tooltipFormatForButtons",
                tooltipText, I18n.format(LANG_FORMAT + "numerics.tooltipAdd")));
        
        super.childWidgets.add(this.numbersWidget);
        super.childWidgets.add(this.minusButton);
        super.childWidgets.add(this.plusButton);
        
        this.checkForMinusPlusAvailability();
    }
    
    private boolean onMinusClick(ButtonWidget b)
    {
        double newValue = BigDecimal.valueOf(this.value).subtract(BigDecimal.ONE).doubleValue();
    
        if (this.min != null && newValue < this.min)
            newValue = this.min;
    
        this.onValueChanged(newValue, this.value);
        this.checkForMinusPlusAvailability();
    
        return true;
    }
    private boolean onPlusClick(ButtonWidget b)
    {
        double newValue = BigDecimal.valueOf(this.value).add(BigDecimal.ONE).doubleValue();
    
        if (this.max != null && newValue > this.max)
            newValue = this.max;
    
        this.onValueChanged(newValue, this.value);
        this.checkForMinusPlusAvailability();
    
        return true;
    }
    
    void checkForMinusPlusAvailability()
    {
        this.minusButton.setInteractable(this.min == null || this.value > this.min);
        this.plusButton .setInteractable(this.max == null || this.value < this.max);
    }
    
    private void onValueChanged(double newValue, double oldValue) {
        this.onValueChanged(newValue, oldValue, true);
    }
    private void onValueChanged(double newValue, double oldValue, boolean updateText)
    {
        if (this.canChangeValue(newValue, oldValue))
        {
            this.value = newValue;
    
            if (updateText)
                this.updateTextToValue();
            
            this.onPostValueUpdate(newValue, oldValue);
        }
    }
    protected boolean canChangeValue(double newValue, double oldValue)
    {
        if (newValue == oldValue)
            return false;
        if (this.min != null && newValue < this.min)
            return false;
        if (this.max != null && newValue > this.max)
            return false;
        
        if (this.onValueChanged != null)
            return this.onValueChanged.test(newValue, oldValue);
        
        return true;
    }
    
    /**
     * Left: new, Right: old
     * @param onValueChanged
     */
    public NumericWidget setValueChangeListener(BiPredicate<Double, Double> onValueChanged) {
        this.onValueChanged = onValueChanged; return this;
    }
    /**
     * Left: new, Right: old
     * @param onPostValueChanged
     */
    public NumericWidget setPostValueChangeListener(BiConsumer<Double, Double> onPostValueChanged) {
        this.onPostValueChanged = onPostValueChanged; return this;
    }
    
    protected void updateTextToValue() {
        this.updateTextToValue(this.value);
    }
    private void updateTextToValue(double value)
    {
        this.ignoreTextChecks = true;
    
        this.numbersWidget.setText(this.includeDecimals ?
                Double.toString(value) : Long.toString((long)value));
    
        this.ignoreTextChecks = false;
    }
    
    /**
     * If there are any minimum or maximum limits (or bounds), adjust the
     * new value to that particular bound if it went beyond minimum or maximum.
     */
    public void adjustCurrentValueToBounds()
    {
        double newValue = this.value;
        
        if (this.min != null && newValue < this.min)
            newValue = this.min;
        if (this.max != null && newValue > this.max)
            newValue = this.max;
    
        if (newValue != this.value)
            this.onValueChanged(newValue, this.value);
    }
    
    protected void onPostValueUpdate(double newValue, double oldValue)
    {
        if (this.onPostValueChanged != null)
            this.onPostValueChanged.accept(newValue, oldValue);
    }
    
    protected boolean isNumericTextValid(String text)
    {
        if (this.ignoreTextChecks)
            return true;
        
        if (text.isEmpty() || text.equals("-") || (this.includeDecimals && text.equals(".")))
            return true;
        
        try
        {
            if (this.includeDecimals)
                Double.parseDouble(text);
            else
                Long.parseLong(text);
            
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    protected void applyNewTextAsValue(String newText, String oldText)
    {
        if (this.ignoreTextChecks)
            return;
    
        try
        {
            double value = Double.parseDouble(newText);
            
            if (this.min != null && value < this.min)
            {
                value = this.min;
                this.updateTextToValue(this.min);
            }
            if (this.max != null && value > this.max)
            {
                value = this.max;
                this.updateTextToValue(this.max);
            }
            
            this.onValueChanged(value, this.value, false);
            this.checkForMinusPlusAvailability();
        }
        catch (NumberFormatException e) {
            // swallow
        }
    }
    
    @Override
    protected void updateWidgetPositions()
    {
        this.numbersWidget.setPos(this.x + 1, this.y + 1);
        this.numbersWidget.setSize(this.width - 42, this.height - 2);
        this.plusButton.setPos(this.x + this.width - 20, this.y);
        this.minusButton.setPos(this.x + this.width - 40, this.y);
    }
    
    public double getValue() {
        return this.value;
    }
    public double getStartingValue() {
        return this.startingValue;
    }
    
    public int getValueInt() {
        return BigDecimal.valueOf(this.getValue()).intValue();
    }
    public long getValueLong() {
        return BigDecimal.valueOf(this.getValue()).longValue();
    }
    public float getValueFloat() {
        return BigDecimal.valueOf(this.getValue()).floatValue();
    }
    
    public Double getMin() {
        return this.min;
    }
    public Double getMax() {
        return this.max;
    }
    
    public long getMaxLong() {
        return BigDecimal.valueOf(this.getMax()).longValue();
    }
    
    public void setValue(double value)
    {
        this.onValueChanged(value, this.value);
        this.checkForMinusPlusAvailability();
    }
    public void setValue(int value) {
        this.setValue((double)value);
    }
    public void setValue(long value) {
        this.setValue((double)value);
    }
    
    public void setMin(Double min) {
        this.min = min; this.checkForMinusPlusAvailability();
    }
    public void setMax(Double max) {
        this.max = max; this.checkForMinusPlusAvailability();
    }
    public void setMin(int min) {
        this.setMin((double)min);
    }
    public void setMax(int max) {
        this.setMax((double)max);
    }
    public void setMin(long min) {
        this.setMin((double)min);
    }
    public void setMax(long max) {
        this.setMax((double)max);
    }
}
