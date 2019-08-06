package kdp.limelib;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kdp.limelib.gui.GenericButton;
import kdp.limelib.gui.GenericScreen;
import kdp.limelib.gui.GuiDrawer;
import kdp.limelib.helper.RecipeHelper;
import kdp.limelib.helper.nbt.NBTBuilder;
import kdp.limelib.helper.nbt.NBTHelper;
import kdp.limelib.network.AbstractMessage;
import kdp.limelib.network.PacketHandler;
import kdp.limelib.util.ClientEventHandler;
import kdp.limelib.util.EventHandler;

@Mod(LimeLib.MOD_ID)
public class LimeLib {

    public static final String MOD_ID = "limelib";

    public static final Logger LOG = LogManager.getLogger(LimeLib.class);

    public LimeLib() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.addListener((LivingJumpEvent event) -> {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                if (event.getEntityLiving().world.isRemote) {
                    PacketHandler.sendToServer(new M());
                } else {
                    ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
                    PacketHandler.sendToPlayers(new M(), player);
                }
            }
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        NBTHelper.init();
        WorldAddition.register();
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~+++");
        //RecipeHelper.addCraftingRecipe(new ItemStack(Items.APPLE, 3), null, true, "rtr"," r ",'r',Items.BAKED_POTATO,'t',Blocks.EMERALD_BLOCK);
        //RecipeHelper.addSmeltingRecipe(new ItemStack(Blocks.EMERALD_BLOCK), Items.EMERALD, 3., 35);
        RecipeHelper.generateFiles();

        PacketHandler.register(M.class);
        PacketHandler.init();
        //throw new RuntimeException(LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT).getClass().toString());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
    }

    public static class M extends AbstractMessage {

        public M() {
            NBTBuilder.of(nbt).set("eins", RandomStringUtils.randomNumeric(5))
                    .set("zwei", new BlockPos(3, 88, -1000000));
        }

        @Override
        public void handleMessage(PlayerEntity player) {
            System.out.println(player.getClass() + " " + Thread.currentThread());
            System.out.println(EffectiveSide.get() + ": " + nbt);
            if (player instanceof ClientPlayerEntity) {
                Minecraft.getInstance().displayGuiScreen(new Sc());
            }
        }

        class Sc extends GenericScreen {

            protected Sc() {
                super(new StringTextComponent(TextFormatting.BLUE + "IrO"));
                xSize = 250;
                ySize = 150;
            }

            @Override
            public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
                drawer.drawBackgroundTexture();
                super.render(p_render_1_, p_render_2_, p_render_3_);
                drawer.drawSlot(30, 30);
                drawer.drawFrame(50, 30, 12, 19, 1, Color.green.getRGB());
                drawer.drawEnergyBarH(10, 60, 134, .999f - ((System.currentTimeMillis() / 8) % 100) / 100f);
                drawer.drawItemStack(new ItemStack(Items.EMERALD), 100, 2);
                drawer.drawProgressArrow(2,
                        80,
                        ((System.currentTimeMillis() / 16) % 100) / 100f,
                        GuiDrawer.Direction.LEFT);
                drawer.drawStopSign(100, 100);
                drawer.drawFlame(130, 130, .7f);
            }

            @Override
            protected void init() {
                super.init();
                addButton(new GuiButtonExt(20 + guiLeft, 2 + guiTop, 200, 10, "Eber", b -> {
                    addButton(new GuiButtonExt(Minecraft.getInstance().world.rand.nextInt(100) + guiLeft,
                            Minecraft.getInstance().world.rand.nextInt(100) + guiTop,
                            44,
                            12,
                            "new",
                            bb -> {
                            }));
                }));
                addButton(new GenericButton(2, 100, 60, 20, "Hell", null).setDesign(GenericButton.Design.SIMPLE)
                        .setButtonColor(0x528B8B));

            }
        }

    }
}
