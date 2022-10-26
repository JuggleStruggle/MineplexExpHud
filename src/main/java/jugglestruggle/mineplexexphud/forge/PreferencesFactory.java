package jugglestruggle.mineplexexphud.forge;

import jugglestruggle.mineplexexphud.forge.gui.screen.PreferencesScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class PreferencesFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft client) {
    
    }
    
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return PreferencesScreen.class;
    }
    
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
}
