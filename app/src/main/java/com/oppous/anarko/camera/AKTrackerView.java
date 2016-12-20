package com.oppous.anarko.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.oppous.anarko.R;

/** Base class used for defining surface for displaying tracking results.
 * Derives from SurfaceView class provided by Android. SurfaceView class provides basic implementation that handles layout of and interaction with user interface.
 * Implements SurfaceHolder.Callback interface. SurfaceHolder.Callback interface is used to obtain information about changes to the surface object.
 */
public abstract class AKTrackerView extends SurfaceView implements SurfaceHolder.Callback
{
	protected int _width;
	protected int _height;
	protected AKTrackerGLSurfaceView _tview;
	protected Context context;

	public AKTrackerView(Context context)
	{
		super(context);
		this.context = context;
	}

	/** Called to set GLSurfaceView used for rendering with OpenGL.
	 * 
	 * @param tview GLSurfaceView object.
	 */
	public void setGLView(AKTrackerGLSurfaceView tview)
	{
		_tview = tview;
	}

	public AKTrackerGLSurfaceView getGLView() {
		return _tview;
	}

}
