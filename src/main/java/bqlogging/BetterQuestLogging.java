package bqlogging;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import betterquesting.api.events.QuestEvent;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.questing.QuestDatabase;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod(
    modid = BetterQuestLogging.MODID,
    version = Tags.VERSION,
    name = "BetterQuestLogging",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:betterquesting",
    acceptableRemoteVersions = "*")
public class BetterQuestLogging {

    public static final String MODID = "bqlogging";
    public static final Logger LOG = LogManager.getLogger(MODID);

    private static final Set<UUID> COMPLETED_QUESTS = new HashSet<>();
    private static int tickCount = 60;

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
            FMLCommonHandler.instance()
                .bus()
                .register(this);
        } else {
            LOG.error("BetterQuesting is not loaded");
        }
    }

    @SubscribeEvent
    public void onQuestEvent(QuestEvent event) {
        if (event.getType() != QuestEvent.Type.COMPLETED) {
            return;
        }
        COMPLETED_QUESTS.addAll(event.getQuestIDs());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (--tickCount != 0) {
            return;
        }
        tickCount = 60;

        COMPLETED_QUESTS.forEach(uuid -> {
            IQuest quest = QuestDatabase.INSTANCE.get(uuid);
            if (quest == null) {
                LOG.error(String.format("Quest with ID %s does not exist", uuid));
                return;
            } else if (quest.getProperty(NativeProps.SILENT)) {
                return;
            }
            LOG.info(
                String.format(
                    "Quest Complete: %s",
                    QuestTranslation.translateQuestName(uuid, quest)
                        .replaceAll("§.", "")));
        });
        COMPLETED_QUESTS.clear();
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
