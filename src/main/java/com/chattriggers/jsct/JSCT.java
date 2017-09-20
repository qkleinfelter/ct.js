package com.chattriggers.jsct;

import com.chattriggers.jsct.commands.CTCommand;
import com.chattriggers.jsct.listeners.WorldListener;
import com.chattriggers.jsct.loader.ScriptLoader;
import com.chattriggers.jsct.objects.DisplayHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Mod(modid = Reference.MODID, name = Reference.MODNAME, version = Reference.MODVERSION, clientSideOnly = true)
public class JSCT {
    @Getter
    private static JSCT instance;

    @Getter @Setter
    private ScriptEngine scriptEngine;
    @Getter @Setter
    private Invocable invocableEngine;
    @Getter @Setter
    private ScriptLoader scriptLoader;
    @Getter
    private DisplayHandler displayHandler;

    @EventHandler
    public void init(FMLInitializationEvent e) {
        instance = this;
        this.scriptEngine = new ScriptEngineManager(null).getEngineByName("nashorn");
        this.invocableEngine = ((Invocable) scriptEngine);

        this.displayHandler = new DisplayHandler();
        this.scriptLoader = new ScriptLoader();

        MinecraftForge.EVENT_BUS.register(scriptLoader);
        MinecraftForge.EVENT_BUS.register(new WorldListener());
        MinecraftForge.EVENT_BUS.register(displayHandler);

        ClientCommandHandler.instance.registerCommand(new CTCommand());
    }
}

class Reference {
    public static final String MODID = "jsct";
    public static final String MODNAME = "jsChatTriggers";
    public static final String MODVERSION = "0.1-SNAPSHOT";
}