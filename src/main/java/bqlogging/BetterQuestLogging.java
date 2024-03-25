package bqlogging;

import java.util.Set;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.events.QuestEvent;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.questing.QuestDatabase;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(
    modid = BetterQuestLogging.MODID,
    version = Tags.VERSION,
    name = "Better Quest Logging",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:betterquesting")
public class BetterQuestLogging {

    public static final String MODID = "bqlogging";
    public static final Logger LOG = LogManager.getLogger(MODID);

    // @SidedProxy(clientSide = MODID + ".ClientProxy", serverSide = MODID + ".CommonProxy")
    // public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // proxy.preInit(event);
        if (Loader.isModLoaded("betterquesting")) {
            LOG.info("BetterQuesting is already loaded");
            MinecraftForge.EVENT_BUS.register(this);
        } else {
            LOG.error("BetterQuesting is not loaded");
        }
    }

    @SubscribeEvent
    public void onQuestEvent(QuestEvent event) {
        Set<UUID> questIds = event.getQuestIDs();
        if (questIds.isEmpty()) {
            return;
        }
        String playerName = QuestingAPI.getPlayer(event.getPlayerID())
            .getDisplayName();
        questIds.forEach(uuid -> {
            LOG.info(
                String.format(
                    "%s completed the quest: %s",
                    playerName,
                    QuestTranslation.translateQuestName(uuid, QuestDatabase.INSTANCE.get(uuid))));
        });
    }

    // @Mod.EventHandler
    // // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not
    // needed)
    // public void init(FMLInitializationEvent event) {
    // proxy.init(event);
    // }
    //
    // @Mod.EventHandler
    // // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    // public void postInit(FMLPostInitializationEvent event) {
    // proxy.postInit(event);
    // }
    //
    // @Mod.EventHandler
    // // register server commands in this event handler (Remove if not needed)
    // public void serverStarting(FMLServerStartingEvent event) {
    // proxy.serverStarting(event);
    // }
}
