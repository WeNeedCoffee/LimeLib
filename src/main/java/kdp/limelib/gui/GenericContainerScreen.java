package kdp.limelib.gui;


import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public class GenericContainerScreen extends ContainerScreen {

    protected GuiDrawer drawer;

    public GenericContainerScreen(Container screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize);
    }
    @Override
    public void tick() {
        super.tick();
        buttons.stream().filter(w -> w instanceof ITickable).forEach(w -> ((ITickable) w).tick());
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
