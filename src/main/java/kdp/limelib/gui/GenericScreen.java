package kdp.limelib.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.util.text.ITextComponent;

public class GenericScreen extends Screen {

    protected int xSize = 176;
    protected int ySize = 166;
    protected int guiLeft;
    protected int guiTop;
    protected GuiDrawer drawer;

    protected GenericScreen(ITextComponent textComponent) {
        super(textComponent);
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize);
    }

    @Override
    public void tick() {
        super.tick();
        buttons.stream().filter(w -> w instanceof ITickable).forEach(w -> ((ITickable) w).tick());
    }
}
