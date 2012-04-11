package com.cs482group.autostitch;

import android.graphics.*;
import boofcv.struct.image.*;

//import boofcv.core.image.*;

public class BoofcvAdaptor {
	
	// general image stitching example:
	// https://github.com/lessthanoptimal/BoofCV/blob/master/examples/src/boofcv/examples/ExampleImageStitching.java
	
	// convertFromSingle (buf img -> grayscale)
	// convertFromMulti (buf img -> multi)
	// convertTo (multi -> buf img)
	// configure -> stitching image A and B to canvas C
	
	
	
	@SuppressWarnings("rawtypes")
	public static <T extends ImageSingleBand> MultiSpectral<ImageFloat32>
		convertFromMulti(Bitmap src, MultiSpectral<ImageFloat32> dst)
	{
		if( dst==null ) { 
			dst = new MultiSpectral<ImageFloat32>(ImageFloat32.class,src.getWidth(),src.getHeight(),3);
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
	public static ImageFloat32 convertFrom(Bitmap src, ImageFloat32 dst) {
		
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
	
	public static Bitmap convertTo(MultiSpectral<ImageFloat32> src, Bitmap dst) {
		
		if(dst==null) {
			dst = Bitmap.createBitmap(src.getWidth(), src.getWidth(), Bitmap.Config.RGB_565);
		}
		for (int y = 0; y < dst.getHeight(); y++) {
			for (int x = 0; x < dst.getWidth(); x++) {
				int r = Math.round(src.getBand(0).get(x, y));
				int g = Math.round(src.getBand(1).get(x, y));
				int b = Math.round(src.getBand(2).get(x, y));
				
				dst.setPixel(x, y, Color.rgb(r, g, b));
			}
		}
		
		return dst;
	}
	

	
}
