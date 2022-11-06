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
}
