package jugglestruggle.mineplexexphud.forge.gui.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

import java.math.BigDecimal;
import java.util.function.BiPredicate;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class NumericWidget extends AbstractMultiWidget
{
    protected double value;
    protected double startingValue;
    protected Double min;
    protected Double max;
    
    protected final TextWidget numbers;
    protected final ButtonWidget minusButton;
    protected final ButtonWidget plusButton;
    
    // Left: New, Right: Old
    protected BiPredicate<Double, Double> onValueChange;
    
    boolean ignoreTextChecks;
    boolean includeDecimals;
    
    public NumericWidget(FontRenderer fr, int w, int h, int value, int min, int max, String tooltipText) {
        this(fr, w, h, value, (double)min, (double)max, tooltipText, false);
    }
    public NumericWidget(FontRenderer fr, int w, int h, long value, long min, long max, String tooltipText) {
        this(fr, w, h, value, (double)min, (double)max, tooltipText, false);
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
    
        this.numbers = new TextWidget(fr, w - 42, h - 2);
        this.minusButton = new ButtonWidget(20, h, "-", this::onMinusClick);
        this.plusButton = new ButtonWidget(20, h, "+", this::onPlusClick);
        
        this.numbers.setText(this.includeDecimals ? Double.toString(value) : Long.toString((long)value));
        this.numbers.setValidator(this::isNumericTextValid);
        this.numbers.setTextChangedListener(this::applyNewTextAsValue);
        
        this.numbers.setTooltipText(tooltipText);
        this.minusButton.setTooltipText(I18n.format(LANG_FORMAT + "numerics.tooltipFormatForButtons",
                tooltipText, I18n.format(LANG_FORMAT + "numerics.tooltipSubtract")));
        this.plusButton.setTooltipText(I18n.format(LANG_FORMAT + "numerics.tooltipFormatForButtons",
                tooltipText, I18n.format(LANG_FORMAT + "numerics.tooltipAdd")));
        
        this.childWidgets.add(this.numbers);
        this.childWidgets.add(this.minusButton);
        this.childWidgets.add(this.plusButton);
    }
    
    private boolean onMinusClick(ButtonWidget b)
    {
        double newValue = BigDecimal.valueOf(this.value).subtract(BigDecimal.ONE).doubleValue();
    
        if (this.min != null && newValue < this.min)
            newValue = this.min;
    
        this.onValueChanged(newValue, this.value);
    
        return true;
    }
    private boolean onPlusClick(ButtonWidget b)
    {
        double newValue = BigDecimal.valueOf(this.value).add(BigDecimal.ONE).doubleValue();
    
        if (this.max != null && newValue > this.max)
            newValue = this.max;
    
        this.onValueChanged(newValue, this.value);
    
        return true;
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
        }
    }
    private boolean canChangeValue(double newValue, double oldValue)
    {
        if (this.onValueChange != null)
            return this.onValueChange.test(newValue, oldValue);
        
        return true;
    }
    
    public void setOnValueChange(BiPredicate<Double, Double> onValueChange) {
        this.onValueChange = onValueChange;
    }
    
    private void updateTextToValue()
    {
        this.ignoreTextChecks = true;
        
        this.numbers.setText(this.includeDecimals ?
                Double.toString(this.value) : Long.toString((long)this.value));
        
        this.ignoreTextChecks = false;
    }
    
    private boolean isNumericTextValid(String text)
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
    private void applyNewTextAsValue(String newText, String oldText)
    {
        if (this.ignoreTextChecks)
            return;
    
        try
        {
            double value = Double.parseDouble(newText);
            
            if (this.min != null && value < this.min)
                value = this.min;
            else if (this.max != null && value > this.max)
                value = this.max;
            
            this.onValueChanged(value, this.value, false);
        }
        catch (NumberFormatException e) {
            // swallow
        }
    }
    
    @Override
    protected void updateWidgetPositions()
    {
        this.numbers.setPos(this.x + 1, this.y + 1);
        this.numbers.setSize(this.width - 42, this.height - 2);
        this.plusButton.setPos(this.x + this.width - 20, this.y);
        this.minusButton.setPos(this.x + this.width - 40, this.y);
    }
    
    public double getValue() {
        return this.value;
    }
    public double getStartingValue() {
        return this.startingValue;
    }
    public int getIntValue() {
        return BigDecimal.valueOf(this.getValue()).intValue();
    }
    
    /**
     * Left: new, Right: old
     * @param onValueChange
     */
    public NumericWidget setValueChangeListener(BiPredicate<Double, Double> onValueChange) {
        this.onValueChange = onValueChange; return this;
    }
    
    /*
    @Override
    public boolean onMouseDown(Minecraft client, int mouseX, int mouseY, int button)
    {
        if (this.isMouseOver(mouseX, mouseY))
        {
            this.numbers.onMouseDown(client, mouseX, mouseY, button);
            this.minusButton.onMouseDown(client, mouseX, mouseY, button);
            this.plusButton.onMouseDown(client, mouseX, mouseY, button);

            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseUp(int mouseX, int mouseY, int button)
    {
        boolean s = this.numbers.onMouseUp(mouseX, mouseY, button);
        s |= this.minusButton.onMouseUp(mouseX, mouseY, button);
        s |= this.plusButton.onMouseUp(mouseX, mouseY, button);

        return s;
    }

    @Override
    public boolean onKeyTyped(char charCode, int keyCode)
    {
        this.numbers.onKeyTyped(charCode, keyCode);
        return false;
    }
     */
}
