package jugglestruggle.mineplexexphud.forge.gui.widget;

import net.minecraft.client.gui.GuiScreen;

import java.util.function.BiPredicate;

public class ButtonRowListWidget<B extends ButtonWidget> extends WidgetRowListWidget<B>
{
    public ButtonRowListWidget(GuiScreen screen, int width, int height, int top, int bottom,
                               int rowHeight, B[] buttons)
    {
        super(screen, width, height, top, bottom, rowHeight, buttons);
    }
    public ButtonRowListWidget(GuiScreen screen, int width, int height, int top, int bottom,
                               int rowHeight, B[] buttons, BiPredicate<B, B> multiWidgetsRowPredicate)
    {
        super(screen, width, height, top, bottom, rowHeight, buttons, multiWidgetsRowPredicate);
    }
    /*
    public ButtonRowListWidget(Minecraft client, int width, int height, int top, int bottom,
                               int rowHeight, List<B> buttons)
    {
        super(client, width, height, top, bottom, rowHeight);
        this.field_148163_i = false;

        final int bSize = buttons.size();

        for (int i = 0; i < bSize; i += 2) {
            this.rows.add(new Row<>(this, buttons.get(i), (i < bSize - 1) ? buttons.get(i + 1) : null));
        }

        this.updateButtonLocations();
    }
     */
}
