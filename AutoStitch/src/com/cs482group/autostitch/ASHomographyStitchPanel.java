package com.cs482group.autostitch;

import georegression.struct.homo.Homography2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;
import georegression.transform.homo.HomographyPointOps;
import android.graphics.Bitmap;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.distort.PixelTransformHomography_F32;
import boofcv.alg.distort.impl.DistortSupport;
import boofcv.alg.interpolate.InterpolatePixel;
import boofcv.alg.interpolate.impl.ImplBilinearPixel_F32;
//import boofcv.gui.image.HomographyStitchPanel;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

public class ASHomographyStitchPanel {
	
	Bitmap output;
	double scale;
	int workWidth;
	int workHeight;
	PixelTransformHomography_F32 model;
	ImageDistort<MultiSpectral<ImageFloat32>> distort;
	Point2D_I32 corners[];
	
	public ASHomographyStitchPanel(double new_scale, int work_width, int work_height) {
		
		this.scale = 0.5;
		this.workWidth = work_width;
		this.workHeight = work_height;
		
		model = new PixelTransformHomography_F32();
		InterpolatePixel<ImageFloat32> interp = new ImplBilinearPixel_F32();
		distort = DistortSupport.createDistortMS(ImageFloat32.class, model, interp, null);

		corners = new Point2D_I32[4];
		for( int i = 0; i < corners.length; i++ ) {
			corners[i] = new Point2D_I32();
		}
		
	}
	
	//https://github.com/lessthanoptimal/BoofCV/blob/master/main/visualize/src/boofcv/gui/image/HomographyStitchPanel.java
	//public synchronized void configure(BufferedImage imageA, BufferedImage imageB , Homography2D_F64 fromAtoB )
	public void configure(Bitmap imageA, Bitmap imageB, Homography2D_F64 fromAtoB) {
		
		MultiSpectral<ImageFloat32> colorA = BoofcvAdaptor.convertFromMulti(imageA, null);
		MultiSpectral<ImageFloat32> colorB = BoofcvAdaptor.convertFromMulti(imageB, null);

		MultiSpectral<ImageFloat32> work = new MultiSpectral<ImageFloat32>(ImageFloat32.class,workWidth,workHeight,3);

		Homography2D_F64 fromWorkToA = createFromWorkToA(colorA);
		model.set(fromWorkToA);
		
		distort.apply(colorA,work);

		Homography2D_F64 fromWorkToB = fromWorkToA.concat(fromAtoB,null);
		model.set(fromWorkToB);

		distort.apply(colorB,work);

		//output = new BufferedImage(work.width,work.height,imageA.getType());
		BoofcvAdaptor.convertTo(work,output);

		// save the corners of the distorted image
		Homography2D_F64 fromBtoWork = fromWorkToB.invert(null);
		corners[0] = renderPoint(0,0,fromBtoWork);
		corners[1] = renderPoint(colorB.width,0,fromBtoWork);
		corners[2] = renderPoint(colorB.width,colorB.height,fromBtoWork);
		corners[3] = renderPoint(0,colorB.height,fromBtoWork);

		//setPreferredSize(new Dimension(output.getWidth(),output.getHeight()));
	}
	
	@SuppressWarnings("rawtypes")
	private Homography2D_F64 createFromWorkToA( ImageBase grayA ) {
		Homography2D_F64 fromAToWork = new Homography2D_F64(scale,0,grayA.width/4,0,scale,grayA.height/4,0,0,1);
		return fromAToWork.invert(null);
	}
	
	private Point2D_I32 renderPoint( int x0 , int y0 , Homography2D_F64 fromBtoWork ) {
		Point2D_F64 result = new Point2D_F64();
		HomographyPointOps.transform(fromBtoWork,new Point2D_F64(x0,y0),result);
		return new Point2D_I32((int)result.x,(int)result.y);
	}
	
	public Bitmap getOutput() {
		return output;
	}
	
	public void closePanel() {
		model = null;
		distort = null;
		corners = null;
	}

}
