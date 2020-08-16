package firis.lmlib;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firis.lmlib.api.LMLibraryAPI;
import firis.lmlib.api.manager.MultiModelPackManager;
import firis.lmlib.api.manager.TexturePackManager;
import firis.lmlib.api.motion.LMMotionByebye;
import firis.lmlib.api.motion.LMMotionDance;
import firis.lmlib.api.motion.LMMotionSitdown;
import firis.lmlib.api.motion.LMMotionSleep;
import firis.lmlib.api.motion.LMMotionStrange;
import firis.lmlib.api.motion.LMMotionUtubuse;
import firis.lmlib.client.entity.EntityLittleMaidGui;
import firis.lmlib.client.renderer.RenderEntityLittleMaidGui;
import firis.lmlib.client.resources.LMSoundResourcePack;
import firis.lmlib.client.resources.LMTextureResourcePack;
import firis.lmlib.common.config.LMLConfig;
import firis.lmlib.common.helper.ResourceFileHelper;
import firis.lmlib.common.loader.LMFileLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
		modid = LMLibrary.MODID, 
		name = LMLibrary.NAME,
		version = LMLibrary.VERSION,
		dependencies = LMLibrary.MOD_DEPENDENCIES,
		acceptedMinecraftVersions = LMLibrary.MOD_ACCEPTED_MINECRAFT_VERSIONS
)
@EventBusSubscriber(modid=LMLibrary.MODID)
public class LMLibrary {

    public static final String MODID = "lmlibrary";
    public static final String NAME = "LMLibrary";
    public static final String VERSION = "1.0.8";
    public static final String MOD_DEPENDENCIES = "required-after:forge@[1.12.2-14.23.5.2768,);";
    public static final String MOD_ACCEPTED_MINECRAFT_VERSIONS = "[1.12.2]";
    
    @Instance(LMLibrary.MODID)
    public static LMLibrary INSTANCE;
    
    /** logger */
    public static Logger logger = LogManager.getLogger(LMLibrary.MODID);
    
    /**
     * コンストラクタ
     */
	public LMLibrary() {
		//カスタムリソースパック追加
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			@SuppressWarnings("unchecked")
			List<IResourcePack> defaultResourcePacks = (List<IResourcePack>)ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[] { "defaultResourcePacks", "field_110449_ao" });
			defaultResourcePacks.add(new LMSoundResourcePack());
			defaultResourcePacks.add(new LMTextureResourcePack());
		}
	}
    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	
    	//設定ファイル初期化
    	LMLConfig.init(event);
    	
    	//リソースフォルダの存在チェック
    	//存在しない場合はフォルダを作成する
    	ResourceFileHelper.isDirectory();
    	
		//リトルメイドファイルローダー
		LMFileLoader.instance.load();
		
		//マルチモデル初期化
		MultiModelPackManager.instance.init();
		
		//テクスチャパック初期化
		TexturePackManager.instance.init();
		
		//テクスチャモデル初期化
		LMLibraryAPI.instance().getTextureManager().init();
		
		//サウンドパックセットアップ
		LMLibraryAPI.instance().getSoundManager().createSounds();
    	
    	//モーション登録
    	LMLibraryAPI.instance().registerLittleMaidMotion(new LMMotionSitdown());
    	LMLibraryAPI.instance().registerLittleMaidMotion(new LMMotionSleep());
    	LMLibraryAPI.instance().registerLittleMaidMotion(new LMMotionUtubuse());
    	LMLibraryAPI.instance().registerLittleMaidMotion(new LMMotionByebye());
    	LMLibraryAPI.instance().registerLittleMaidMotion(new LMMotionDance());
    	LMLibraryAPI.instance().registerLittleMaidMotion(new LMMotionStrange());
    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {}
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}
    
	@SubscribeEvent
	protected static void registerModels(ModelRegistryEvent event) {
    	
    	//GUI用EntityLittleMaid
		RenderingRegistry.registerEntityRenderingHandler(EntityLittleMaidGui.class, new IRenderFactory<EntityLittleMaidGui>() {
			@Override
			public Render<? super EntityLittleMaidGui> createRenderFor(RenderManager manager) {
				return new RenderEntityLittleMaidGui(manager);
			}
		});
		
    }
    
}
