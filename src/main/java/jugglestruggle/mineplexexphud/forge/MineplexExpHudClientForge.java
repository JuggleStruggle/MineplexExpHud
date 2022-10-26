package jugglestruggle.mineplexexphud.forge;

import jugglestruggle.mineplexexphud.MineplexExpHudClient;
import jugglestruggle.mineplexexphud.forge.event.MIPLEventListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod
(
    modid = MineplexExpHudClientForge.MODID, version = MineplexExpHudClientForge.VERSION,
    clientSideOnly = true, guiFactory = "jugglestruggle.mineplexexphud.forge.PreferencesFactory"
)
public class MineplexExpHudClientForge extends MineplexExpHudClient
{
    public static final String MODID = "mineplexexphud";
    public static final String VERSION = "1.0.0";
    
    public static KeyBinding showHudBinding;
    public static KeyBinding showPrefsBinding;
    public static KeyBinding showHudEditorBinding;
    
    public static MineplexExpHudClientForge getForgeInstance() {
        return (MineplexExpHudClientForge)MineplexExpHudClient.instance;
    }
    public static ForgeRenderContext getCtx() {
        return (ForgeRenderContext)MineplexExpHudClient.renderContext;
    }
    public static ForgeExpHud getForgeExpHud() {
        return (ForgeExpHud)MineplexExpHudClient.getInstance().getExpHud();
    }
    
    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e)
    {
        MineplexExpHudClient.langForTranslation = new ForgeLang();
        MineplexExpHudClient.instance = this;
    
        super.init(e.getModConfigurationDirectory());
    }
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent e)
    {
    }
    
    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent e)
    {
        MineplexExpHudClient.renderContext = new ForgeRenderContext();
       
        MinecraftForge.EVENT_BUS.register(new MIPLEventListener());
        
        final String category = LANG_FORMAT+"key.category";
        
        showHudBinding = new KeyBinding(LANG_FORMAT+"key.showHud", 0, category);
        showPrefsBinding = new KeyBinding(LANG_FORMAT+"key.showPrefs", 0, category);
        showHudEditorBinding = new KeyBinding(LANG_FORMAT+"key.showHudEditor", 0, category);
    
        ClientRegistry.registerKeyBinding(showHudBinding);
        ClientRegistry.registerKeyBinding(showPrefsBinding);
        ClientRegistry.registerKeyBinding(showHudEditorBinding);
        
        super.expHud = new ForgeExpHud();
    }
}
