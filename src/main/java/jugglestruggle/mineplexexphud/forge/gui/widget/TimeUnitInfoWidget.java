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

import jugglestruggle.util.JuggleTimeUnit;
import jugglestruggle.util.TimeUnitInfo;
import net.minecraft.client.gui.FontRenderer;

import java.util.function.Consumer;

public class TimeUnitInfoWidget extends NumericWidget
{
    protected CyclingButtonWidget<JuggleTimeUnit> cyclingTimeUnitButton;
    protected Consumer<TimeUnitInfoWidget> onPostValueChangedListener;
    
    public TimeUnitInfoWidget(FontRenderer fr, int w, int h, TimeUnitInfo tui, String tooltipText)
    {
        super(fr, w, h, tui.getValue(), tui.getMin(), tui.getMax(), tooltipText);
        
        this.cyclingTimeUnitButton = new CyclingButtonWidget<>((int)((float)w / 2.5f), h, "", tui.getUnit(),
                JuggleTimeUnit.TIME_UNIT_LIST, this::onSetTimeUnitFormat);
        this.cyclingTimeUnitButton.setPostValueChangeListener(this::onSetTimeUnitValueChanged);
        this.cyclingTimeUnitButton.setDisplayFormat("%2$s");
        this.cyclingTimeUnitButton.setTooltipText(tooltipText);
        
        super.childWidgets.add(this.cyclingTimeUnitButton);
        
        super.numbersWidget.setSize(w - 102, h);
        
    }
    
    private String onSetTimeUnitFormat(JuggleTimeUnit v) {
        return v.getDisplayName(super.value == 1);
    }
    
    public JuggleTimeUnit getUnit() {
        return this.cyclingTimeUnitButton.getValue();
    }
    
    private void onSetTimeUnitValueChanged(CyclingButtonWidget<JuggleTimeUnit> b)
    {
        super.setMin(b.getValue().getMinMillis());
        super.setMax(b.getValue().getMaxMillis());
        
        super.adjustCurrentValueToBounds();
        
        this.reportToChangedValueListener();
    }
    
    @Override
    protected void onPostValueUpdate(double newValue, double oldValue)
    {
        this.cyclingTimeUnitButton.updateDisplayText();
        super.onPostValueUpdate(newValue, oldValue);
    
        this.reportToChangedValueListener();
    }
    
    private void reportToChangedValueListener()
    {
        if (this.onPostValueChangedListener != null)
            this.onPostValueChangedListener.accept(this);
    }
    
    public void setPostOnValueChanged(Consumer<TimeUnitInfoWidget> valueChanged) {
        this.onPostValueChangedListener = valueChanged;
    }
    
    @Override
    protected void updateWidgetPositions()
    {
        final int cycleUnitButtonW = this.cyclingTimeUnitButton.getW();
        final int xr = this.getXR();
        
        super.numbersWidget.setPos(this.x + 1, this.y + 1);
        super.numbersWidget.setSize(this.width - (cycleUnitButtonW + 42), this.height - 2);
        super.plusButton.setPos(xr - (cycleUnitButtonW + 20), this.y);
        super.minusButton.setPos(xr - (cycleUnitButtonW + 40), this.y);
        
        
        this.cyclingTimeUnitButton.setPos(this.x + this.width - cycleUnitButtonW, this.y);
    }
    
    public TimeUnitInfo getUnitInfo() {
        return new TimeUnitInfo(this.getUnit(), this.getValueLong());
    }
}
