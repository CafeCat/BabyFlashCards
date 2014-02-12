package cafecat.colorworld;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class myImageFactory {
	
	public static Bitmap getFitableBitmap(Bitmap src,int fitWidth,int fitHeight,int sourceWidth,int sourceHeight){
		int newWidth = 0;
		int newHeight = 0;
		newHeight = fitHeight;
		newWidth = (int)Math.round(sourceWidth*((float)newHeight/(float)sourceHeight));
		if(newWidth > fitWidth){
			newWidth = fitWidth;
			newHeight = (int)Math.round(sourceHeight*((float)newWidth/(float)sourceWidth));
		}
		Bitmap bm = getResizedBitmap(src, newHeight, newWidth);
		return bm;
	}
	
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedbm;
    }
    
	public static int[][] calculateSliceOffsetAndSize(int columns,int rows, int sourceWidth, int sourceHeight){
		int total = columns*rows;
		int[][] result = new int[total][4];
		int overAllWidth = sourceWidth;
		int overAllHeight = sourceHeight;
		int columnLoc = 0;
		int rowLoc =0;
		for(int i=0; i<total; i++){
			result[i][CroppedItem.OFFSET_X.toInteger()] = (int)Math.round((float)overAllWidth/(float)columns)*columnLoc;
			result[i][CroppedItem.OFFSET_Y.toInteger()] = (int)Math.round((float)overAllHeight/(float)rows)*rowLoc;
			if(columnLoc < (columns-1)){
				result[i][CroppedItem.WIDTH.toInteger()] = ((int)Math.round((float)overAllWidth/(float)columns)*(columnLoc+1)) - result[i][CroppedItem.OFFSET_X.toInteger()];
			}else{
				result[i][CroppedItem.WIDTH.toInteger()] = overAllWidth - result[i][CroppedItem.OFFSET_X.toInteger()];
			}
			if(rowLoc < (rows-1)){
				result[i][CroppedItem.HEIGHT.toInteger()] = ((int)Math.round((float)overAllHeight/(float)rows)*(rowLoc+1)) - result[i][CroppedItem.OFFSET_Y.toInteger()];
			}else{
				result[i][CroppedItem.HEIGHT.toInteger()] = overAllHeight - result[i][CroppedItem.OFFSET_Y.toInteger()];
			}
			columnLoc = columnLoc + 1;
			if(columnLoc == columns){
				columnLoc = 0;
				rowLoc = rowLoc + 1;
			}
		}
		return result;
	}
	
	public enum CroppedItem{
		OFFSET_X(0), OFFSET_Y(1), WIDTH(2), HEIGHT(3);
		private int type;
		CroppedItem(int i){
			this.type = i;
		}
		public int toInteger(){
			return type;
		}		
	}
}
