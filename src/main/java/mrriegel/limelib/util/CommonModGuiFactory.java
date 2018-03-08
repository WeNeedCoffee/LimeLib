package mrriegel.limelib.util;

import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class CommonModGuiFactory implements IModGuiFactory {

	Function<GuiScreen, GuiScreen> guiFunc;

	public CommonModGuiFactory(Function<GuiScreen, GuiScreen> guiFunc) {
		super();
		this.guiFunc = Validate.notNull(guiFunc);
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return guiFunc.apply(parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

}
