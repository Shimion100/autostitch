package com.cs482group.autostitch;

import android.graphics.*;
import boofcv.struct.image.*;
import android.util.Log;

//import boofcv.core.image.*;

public class BoofcvAdaptor {
	
	// general image stitching example:
	// https://github.com/lessthanoptimal/BoofCV/blob/master/examples/src/boofcv/examples/ExampleImageStitching.java
	
	// convertFromSingle (buf img -> grayscale)
	// convertFromMulti (buf img -> multi)
	// convertTo (multi -> buf img)
	// configure -> stitching image A and B to canvas C
	
	private static final String TAG = "BoofcvAdaptor";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends ImageSingleBand> MultiSpectral<T>
		convertFromMulti(Bitmap src, MultiSpectral<T> dst, Class<T> type)
	{
		// the constructor for MultiSpectral calls GeneralizedImageOps.createSingleBand
		// that calls BoofTesting.convertToSpecificType()
		// that tries to access a BufferedImage
		// result: throws an Exception and does NOT create the 3 bands
		// quick fix: catch the exception and manually create bands afterwards
		// note: the bands[] array has been allocated before offending call
		// THIS IS NOT A GOOD FIX
		// extend MultiSpectral and rewrite constructor
		if( dst==null ) { 
			Log.d(TAG, "creating new MutliSpectral");
			
			try {
				dst = new MultiSpectral<T>(type,src.getWidth(),src.getHeight(),3);
			} catch (Exception e) {
				Log.d(TAG,"Reached Expected Error: " + e.getMessage());
			}
			
			Log.d(TAG,"Manually creating the 3 bands");
			dst.bands[0] = (T)new ImageFloat32(src.getWidth(), src.getHeight());
			dst.bands[1] = (T)new ImageFloat32(src.getWidth(), src.getHeight());
			dst.bands[2] = (T)new ImageFloat32(src.getWidth(), src.getHeight());
		}
		
		Log.d(TAG, "loading RGB data into 3 bands");
		for (int y = 0; y < dst.height; y++) {
			for (int x = 0; x < dst.width; x++) {
				int rgb = src.getPixel(x, y);
				((ImageFloat32) dst.getBand(0)).set(x, y, Color.red(rgb));
				((ImageFloat32) dst.getBand(1)).set(x, y, Color.green(rgb));
				((ImageFloat32) dst.getBand(2)).set(x, y, Color.blue(rgb));
			}
		}
		
		Log.d(TAG, "returning the MutliSpectral");
		return dst;
	}
	
	// converts to gray-scale ImageFloat32
	public static ImageFloat32 convertFrom(Bitmap src, ImageFloat32 dst) {
		
		if(dst==null) {
			Log.d(TAG,"creating new ImageFloat32");
			dst = new ImageFloat32(src.getWidth(),src.getHeight());
		}
		
		if(src==null) {
			Log.e(TAG, "Null Bitmap image src");
			return null;
		}
		
		for (int y = 0; y < src.getHeight(); y++) {
			for (int x = 0; x < src.getWidth(); x++) {
				int c = src.getPixel(x, y);
				int r = Color.red(c);
				int g = Color.green(c);
				int b = Color.blue(c);
				
				dst.set(x, y, ((r + g + b)/3));
			}
		}
		
		Log.d(TAG,"returning the ImageFloat32");
		return dst;
	}
	
	public static Bitmap convertTo(MultiSpectral<ImageFloat32> src, Bitmap dst) {
		
		if(dst==null) {
			Log.d(TAG,"creating new work Bitmap (W " + src.getWidth() + ", H " + src.getHeight() + ").");
			dst = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.RGB_565);
		} else {
			Log.d(TAG,"got Bitmap (W " + src.getWidth() + ", H " + src.getHeight() + ").");
		}
		
		Log.d(TAG,"loading pixel data");
		int count = 0;
		for (int y = 0; y < dst.getHeight(); y++) {
			for (int x = 0; x < dst.getWidth(); x++) {
				int r = Math.round(src.getBand(0).get(x, y));
				int g = Math.round(src.getBand(1).get(x, y));
				int b = Math.round(src.getBand(2).get(x, y));
				
				dst.setPixel(x, y, Color.rgb(r, g, b));
				count++;
			}
		}
		
		for(int y = 0; y < 10; y++) {
			int r = Math.round(src.getBand(0).get(0, y));
			int g = Math.round(src.getBand(1).get(0, y));
			int b = Math.round(src.getBand(2).get(0, y));
			Log.d(TAG,"("+r+","+g+","+b+")");
		}
		
		Log.d(TAG,"loaded " + count + " pixels");
		Log.d(TAG,"there are " + (src.getWidth()*src.getHeight()) + " pixels");
		return dst;
	}
	

	
}
