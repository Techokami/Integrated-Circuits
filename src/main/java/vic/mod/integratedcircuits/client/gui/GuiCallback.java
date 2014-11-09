package vic.mod.integratedcircuits.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import vic.mod.integratedcircuits.IntegratedCircuits;
import vic.mod.integratedcircuits.client.gui.GuiInterfaces.IGuiCallback;
import vic.mod.integratedcircuits.client.gui.GuiInterfaces.IHoverable;
import vic.mod.integratedcircuits.client.gui.GuiInterfaces.IHoverableHandler;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.GuiButtonExt;

public class GuiCallback<E extends GuiScreen & IGuiCallback> extends GuiScreen implements IHoverableHandler
{
	private E parent;
	private int guiWidth, guiHeight, xOff, yOff;
	private static ResourceLocation border = new ResourceLocation(IntegratedCircuits.modID, "textures/gui/border.png");
	private List<GuiLabel> labelList = Lists.newArrayList();
	private IHoverable hoveredElement;
	
	public GuiCallback(E parent, int width, int height, Action... action)
	{
		this.mc = Minecraft.getMinecraft();
		this.parent = parent;
		int w = action.length * 52;
		for(int i = 0; i < action.length; i++)
		{
			Action a = action[i];
			buttonList.add(new GuiButtonExt(a.ordinal(), i * 52 + width / 2 - w / 2, height - 24, 50, 15, a.toString()));
		}
		this.guiWidth = width;
		this.guiHeight = height;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int w, int h) 
	{
		parent.setWorldAndResolution(mc, w, h);
		this.mc = mc;
		this.fontRendererObj = mc.fontRenderer;
		this.width = w;
		this.height = h;
		this.initGui();
	}

	public static enum Action
	{
		YES("Yes"), NO("No"), OK("OK"), CANCEL("Cancel"), CUSTOM("CUSTOM");
		
		private String name;
		private Action(String name)
		{
			this.name = name;
		}
		@Override
		public String toString() 
		{
			return name;
		}
	}
	
	public void display()
	{
		mc.displayGuiScreen(this);
	}
	
	public void destory()
	{
		mc.displayGuiScreen(parent);
	}
	
	public <F extends Gui> GuiCallback addControl(F control)
	{
		if(control instanceof GuiButton)
		{
			((GuiButton)control).id += 4;
			buttonList.add(control);
		}
		else if(control instanceof GuiLabel)
		{
			labelList.add((GuiLabel)control);
		}
		else throw new IllegalArgumentException();
		return this;
	}

	@Override
	protected void actionPerformed(GuiButton button) 
	{
		if(button.id < 4)
		{
			destory();
			parent.onCallback(this, Action.values()[button.id], button.id);
		}	
		else parent.onCallback(this, Action.CUSTOM, button.id - 4);
	}

	@Override
	public void drawScreen(int x, int y, float par3) 
	{
		parent.drawScreen(-1, -1, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		super.drawDefaultBackground();
		
		xOff = width / 2 - guiWidth / 2;
		yOff = height / 2 - guiHeight / 2;		
		GL11.glTranslatef(xOff, yOff, 0);
		
		drawRect(3, 3, guiWidth - 3, guiHeight - 3, 0xFFC6C6C6);
		drawRect(4, 0, guiWidth - 4, 1, 0xFF000000);
		drawRect(4, 1, guiWidth - 4, 3, 0xFFFFFFFF);
		drawRect(4, guiHeight - 1, guiWidth - 4, guiHeight, 0xFF000000);
		drawRect(4, guiHeight - 3, guiWidth - 4, guiHeight - 1, 0xFF555555);
		drawRect(0, 4, 1, guiHeight - 4, 0xFF000000);
		drawRect(1, 4, 3, guiHeight - 4, 0xFFFFFFFF);
		drawRect(guiWidth - 1, 4, guiWidth, guiHeight - 4, 0xFF000000);
		drawRect(guiWidth - 3, 4, guiWidth - 1, guiHeight - 4, 0xFF555555);
		
		mc.renderEngine.bindTexture(border);
		GL11.glColor4f(1, 1, 1, 1);
		func_146110_a(0, 0, 0, 0, 4, 4, 8, 8);
		func_146110_a(guiWidth - 4, 0, 4, 0, 4, 4, 8, 8);
		func_146110_a(guiWidth - 4, guiHeight - 4, 4, 4, 4, 4, 8, 8);
		func_146110_a(0, guiHeight - 4, 0, 4, 4, 4, 8, 8);	
		
		hoveredElement = null;
		
		for(GuiLabel l : labelList) l.drawLabel(mc, x, y);
		super.drawScreen(x - xOff, y - yOff, par3);
		if(hoveredElement != null) 
			drawHoveringText(hoveredElement.getHoverInformation(), x - xOff, y - yOff, this.fontRendererObj);
			
		GL11.glTranslatef(-xOff, -yOff, 0);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	@Override
	protected void keyTyped(char ch, int key) 
	{
		if(key == Keyboard.KEY_ESCAPE)
		{
			destory();
			parent.onCallback(this, Action.CANCEL, -1);
		}
	}

	@Override
	public boolean doesGuiPauseGame() 
	{
		return false;
	}

	@Override
	protected void mouseClicked(int x, int y, int par3) 
	{
		super.mouseClicked(x - xOff, y - yOff, par3);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int par3) 
	{
		super.mouseMovedOrUp(x - xOff, y - yOff, par3);
	}

	@Override
	protected void mouseClickMove(int x, int y, int par3, long par4) 
	{
		super.mouseClickMove(x - xOff, y - yOff, par3, par4);
	}

	@Override
	public void setCurrentItem(IHoverable hoverable) 
	{
		this.hoveredElement = hoverable;
	}
}