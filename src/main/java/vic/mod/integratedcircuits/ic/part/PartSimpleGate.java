package vic.mod.integratedcircuits.ic.part;

import net.minecraftforge.common.util.ForgeDirection;
import vic.mod.integratedcircuits.ic.ICircuit;
import vic.mod.integratedcircuits.misc.PropertyStitcher.BooleanProperty;
import vic.mod.integratedcircuits.misc.Vec2;

/** Has only one type of output **/
public abstract class PartSimpleGate extends PartCPGate
{
	public final BooleanProperty PROP_OUT = new BooleanProperty("PROP_OUT", stitcher);
	private final BooleanProperty PROP_TMP = new BooleanProperty("PROP_TMP", stitcher);
	
	protected final boolean getOutput(Vec2 pos, ICircuit parent)
	{
		return getProperty(pos, parent, PROP_OUT);
	}
	
	protected final void setOutput(Vec2 pos, ICircuit parent, boolean output)
	{
		setProperty(pos, parent, PROP_OUT, output);
	}
	
	protected abstract void calcOutput(Vec2 pos, ICircuit parent);
	
	/** already rotated **/
	protected abstract boolean hasOutputToSide(Vec2 pos, ICircuit parent, ForgeDirection fd);
	
	@Override
	public boolean getOutputToSide(Vec2 pos, ICircuit parent, ForgeDirection side)
	{
		return hasOutputToSide(pos, parent, toInternal(pos, parent, side)) && getProperty(pos, parent, PROP_TMP);
	}
	
	@Override
	public void onInputChange(Vec2 pos, ICircuit parent, ForgeDirection side)
	{
		updateInput(pos, parent);
		calcOutput(pos, parent);
		ForgeDirection s2 = toInternal(pos, parent, side);
		if(canConnectToSide(pos, parent, side) && !hasOutputToSide(pos, parent, s2)) 
			scheduleTick(pos, parent);
	}

	@Override
	public void onScheduledTick(Vec2 pos, ICircuit parent)
	{
		setProperty(pos, parent, PROP_TMP, getProperty(pos, parent, PROP_OUT));
		setProperty(pos, parent, PROP_OUT, false);
		notifyNeighbours(pos, parent);
	}

	@Override
	public void onPlaced(Vec2 pos, ICircuit parent) 
	{
		updateInput(pos, parent);
		calcOutput(pos, parent);
		setProperty(pos, parent, PROP_TMP, getProperty(pos, parent, PROP_OUT));
		setProperty(pos, parent, PROP_OUT, false);
		notifyNeighbours(pos, parent);
	}

	@Override
	public void onClick(Vec2 pos, ICircuit parent, int button, boolean ctrl) 
	{
		super.onClick(pos, parent, button, ctrl);
		onPlaced(pos, parent);
	}
}