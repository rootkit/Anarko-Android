package com.oppous.anarko.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.util.Log;

import com.oppous.anarko.R;
import com.oppous.anarko.classes.AppManager;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

/** AKTrackerRenderer class provides implementation of rendering functions required for displaying tracking results.
 */
public class AKTrackerRenderer implements GLSurfaceView.Renderer
{
	private 	static final String 		TAG = "AKTrackerRenderer";
	private 	Context 					mContext;
	private 	int 						mWidth;
	private 	int 						mHeight;

	private 	float[]                 	mFacialPoints;

	private 	int[]						mTextures = new int[3];

	boolean drawLogo = true;

	AKTrackerRenderer(Context context)
	{
		mContext = context;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glGenTextures(1, mTextures, 0);
	}

	public void onDrawFrame(GL10 gl) {
		drawLogo = VisageBridge.displayTrackingStatus(mWidth, mHeight);

		if (VisageBridge.getTrackingStatus()) { //Tracking status is ok
			mFacialPoints = VisageBridge.getFacialPoints();
		}
	}

}
