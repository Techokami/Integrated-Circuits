package vic.mod.integratedcircuits.client.model;

import vic.mod.integratedcircuits.client.Part7SegmentRenderer;
import vic.mod.integratedcircuits.client.Resources;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.uv.IconTransformation;
import codechicken.lib.vec.Transformation;

public class ModelSocket implements IComponentModel
{
	private static CCModel[] models = new CCModel[24];
	private static CCModel base = generateModel();
	
	static
	{
		for(int i = 0; i < 24; i++)
			models[i] = Part7SegmentRenderer.bakeCopy(base, i);
	}

	@Override
	public void renderModel(Transformation t, int orient)
	{
		models[orient % 24].render(t, new IconTransformation(Resources.ICON_IC_SOCKET));
	}
	
	private static CCModel generateModel()
	{
		CCModel m1 = CCModel.quadModel(120);
		m1.generateBlock(0, 3 / 16F, 2 / 16F, 2 / 16F, 13 / 16F, 3 / 16D, 14 / 16F);
		m1.generateBlock(24, 2 / 16F, 2 / 16F, 1 / 16F, 3 / 16F, 5 / 16D, 15 / 16F);
		m1.generateBlock(48, 13 / 16F, 2 / 16F, 1 / 16F, 14 / 16F, 5 / 16D, 15 / 16F);
		m1.generateBlock(72, 2 / 16F, 2 / 16F, 1 / 16F, 14 / 16F, 5 / 16D, 2 / 16F);
		m1.generateBlock(96, 2 / 16F, 2 / 16F, 14 / 16F, 14 / 16F, 5 / 16D, 15 / 16F);
		m1.computeNormals();
		return m1;
	}
}