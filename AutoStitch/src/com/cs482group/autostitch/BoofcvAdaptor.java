package com.cs482group.autostitch;

import android.graphics.*;
import boofcv.struct.image.*;
import georegression.struct.homo.Homography2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;
import georegression.transform.homo.HomographyPointOps;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.distort.PixelTransformHomography_F32;
import boofcv.alg.distort.impl.DistortSupport;
import boofcv.alg.interpolate.impl.ImplBilinearPixel_S32;
import boofcv.alg.interpolate.InterpolatePixel;

//import boofcv.core.image.*;

public class BoofcvAdaptor {
	
	Bitmap output;
	double scale;
	int workWidth;
	int workHeight;
	
	// general image stitching example:
	// https://github.com/lessthanoptimal/BoofCV/blob/master/examples/src/boofcv/examples/ExampleImageStitching.java
	
	// convertFromSingle (buf img -> grayscale)
	// convertFromMulti (buf img -> multi)
	// convertTo (multi -> buf img)
	// configure -> stitching image A and B to canvas C
	
	@SuppressWarnings("rawtypes")
	public static <T extends ImageSingleBand> MultiSpectral<ImageSInt32>
		convertFromMulti(Bitmap src, MultiSpectral<ImageSInt32> dst)
	{
		if( dst==null ) { 
			dst = new MultiSpectral<ImageSInt32>(ImageSInt32.class,src.getWidth(),src.getHeight(),3);
		}
		
		for (int y = 0; y < dst.height; y++) {
			for (int x = 0; x < dst.width; x++) {
				int rgb = src.getPixel(x, y);
				dst.getBand(0).set(x, y, Color.red(rgb));
				dst.getBand(1).set(x, y, Color.green(rgb));
				dst.getBand(2).set(x, y, Color.blue(rgb));
			}
		}
		
		return dst;
	}
	
	// converts to gray-scale ImageFloat32
	public static ImageSInt32 convertFrom(Bitmap src, ImageSInt32 dst) {
		
		for (int y = 0; y < dst.getHeight(); y++) {
			for (int x = 0; x < dst.getWidth(); x++) {
				int c = src.getPixel(x, y);
				int r = Color.red(c);
				int g = Color.green(c);
				int b = Color.blue(c);
				
				dst.set(x, y, ((r + g + b)/3));
			}
		}
		
		return dst;
	}
	
	public static Bitmap convertTo(MultiSpectral<ImageSInt32> src, Bitmap dst) {
		
		if(dst==null) {
			dst = Bitmap.createBitmap(src.getWidth(), src.getWidth(), Bitmap.Config.RGB_565);
		}
		for (int y = 0; y < dst.getHeight(); y++) {
			for (int x = 0; x < dst.getWidth(); x++) {
				int r = src.getBand(0).get(x, y);
				int g = src.getBand(1).get(x, y);
				int b = src.getBand(2).get(x, y);
				
				dst.setPixel(x, y, Color.rgb(r, g, b));
			}
		}
		
		return dst;
	}
	
	//https://github.com/lessthanoptimal/BoofCV/blob/master/main/visualize/src/boofcv/gui/image/HomographyStitchPanel.java
	//public synchronized void configure(BufferedImage imageA, BufferedImage imageB , Homography2D_F64 fromAtoB )
	public void configure(Bitmap imageA, Bitmap imageB, Homography2D_F64 fromAtoB) {
		
		scale = 0.5;
		workWidth = imageA.getWidth();
		workHeight = imageA.getHeight();
		
		PixelTransformHomography_F32 model;
		ImageDistort<MultiSpectral<ImageSInt32>> distort;
		Point2D_I32 corners[];
		
		model = new PixelTransformHomography_F32();
		InterpolatePixel<ImageSInt32> interp = new ImplBilinearPixel_S32();
		distort = DistortSupport.createDistortMS(ImageSInt32.class, model, interp, null);

		corners = new Point2D_I32[4];
		for( int i = 0; i < corners.length; i++ ) {
			corners[i] = new Point2D_I32();
		}
		
		MultiSpectral<ImageSInt32> colorA = BoofcvAdaptor.convertFromMulti(imageA, null);
		MultiSpectral<ImageSInt32> colorB = BoofcvAdaptor.convertFromMulti(imageB, null);

		MultiSpectral<ImageSInt32> work = new MultiSpectral<ImageSInt32>(ImageSInt32.class,workWidth,workHeight,3);

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
	
}
