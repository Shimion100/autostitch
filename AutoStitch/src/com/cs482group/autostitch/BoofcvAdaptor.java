package com.cs482group.autostitch;

import android.graphics.*;
import boofcv.struct.image.*;
import georegression.struct.homo.Homography2D_F64;
//import boofcv.core.image.*;

public class BoofcvAdaptor {
	
	// general image stitching example:
	// https://github.com/lessthanoptimal/BoofCV/blob/master/examples/src/boofcv/examples/ExampleImageStitching.java
	
	// convertFromSingle (buf img -> grayscale)
	// convertFromMulti (buf img -> multi)
	// convertTo (multi -> buf img)
	// configure -> stitching image A and B to canvas C
	
	@SuppressWarnings("rawtypes")
	public static <T extends ImageSingleBand> MultiSpectral<ImageSInt32>
		convertFromMulti(Bitmap src, MultiSpectral<ImageSInt32> dst , Class<T> type )
	{
		
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
		
		
		return dst;
	}
	
	public static Bitmap convertTo(MultiSpectral<ImageSInt32> src, Bitmap dst) {

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
		
	}
}
