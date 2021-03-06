package vic.mod.integratedcircuits.net;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import vic.mod.integratedcircuits.client.gui.GuiPCBLayout;
import vic.mod.integratedcircuits.ic.CircuitProperties;
import vic.mod.integratedcircuits.proxy.CommonProxy;
import vic.mod.integratedcircuits.tile.TileEntityPCBLayout;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

public class PacketPCBClear extends PacketTileEntity<PacketPCBClear>
{
	private byte size;
	
	public PacketPCBClear() {}
	
	public PacketPCBClear(byte size, int xCoord, int yCoord, int zCoord)
	{
		super(xCoord, yCoord, zCoord);
		this.size = size;
	}
	
	@Override
	public void read(PacketBuffer buffer) throws IOException 
	{
		super.read(buffer);
		size = buffer.readByte();
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException 
	{
		super.write(buffer);
		buffer.writeByte(size);
	}

	@Override
	public void process(EntityPlayer player, Side side) 
	{
		TileEntityPCBLayout te = (TileEntityPCBLayout)player.worldObj.getTileEntity(xCoord, yCoord, zCoord);
		if(te != null)
		{
			if(side == side.SERVER)
				te.cache.create(player.getGameProfile().getId());
			
			boolean changed = te.getCircuitData().hasChanged();
			te.getCircuitData().clear(size);
			if(!te.getCircuitData().supportsBundled()) te.getCircuitData().getProperties().setCon(0);
			
			te.in = new int[4];
			te.out = new int[4];
			for(int i = 0; i < 4; i++)
				if(te.getCircuitData().getProperties().getModeAtSide(i) == CircuitProperties.ANALOG) te.in[i] = 1;
			
			if(side == side.SERVER && changed)
			{
				te.cache.capture(player.getGameProfile().getId());
				CommonProxy.networkWrapper.sendToAllAround(this, 
					new TargetPoint(te.getWorldObj().getWorldInfo().getVanillaDimension(), xCoord, yCoord, zCoord, 8));
			}
			else if(Minecraft.getMinecraft().currentScreen instanceof GuiPCBLayout)
				((GuiPCBLayout)Minecraft.getMinecraft().currentScreen).refreshUI();
		}
	}
}
