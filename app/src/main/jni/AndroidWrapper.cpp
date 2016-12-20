#include <jni.h>
#include <EGL/egl.h> 
#include <GLES/gl.h>
#include <vector>
#include <stdio.h>
#include <unistd.h>
#include "VisageTracker.h"
#include "VisageRendering.h"
#include "AndroidImageCapture.h"
#include "AndroidCameraCapture.h"
//#include "AndroidObserver.h"
#include "WrapperOpenCV.h"

#include <android/log.h>
#define  LOG_TAG    "TrackerWrapper"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

using namespace std;
using namespace VisageSDK;

static AndroidImageCapture *a_cap_image = 0;
static AndroidCameraCapture *a_cap_camera = 0;

static float m_fps = 0;

void Sleep(int ms) {usleep(ms*1000);}

// neccessary prototype declaration for licensing
namespace VisageSDK
{
void initializeLicenseManager(JNIEnv* env, jobject obj, const char *licenseKeyFileName, void (*alertFunction)(const char*) = 0);
}

/** \file AndroidWrapper.cpp
 * Implementation of simple interface around visage|SDK VisageTracker functionality.
 *
 * In order for Android application, which uses Java as its primary programming language, to communicate with visage|SDK functionality, 
 * which uses C++ as its primary language it is necessary to use Java Native Interface as a framework between the two.  
 *
 * Key members of wrapper are:
 * - m_Tracker: the VisageTracker object
 * - trackingData: the TrackingData object used for retrieving and holding tracking data
 * - displayTrackingResults: method that demonstrates how to acquire, use and display tracking data and 3D face model
 * 
 */

extern "C" {

static VisageTracker *m_Tracker = 0;
static FaceData trackingData;
int trackingStatus = 0;

GLuint texIds[3];

/**
* Texture ID for displaying frames from the tracker.
*/
GLuint frameTexId = 0;

GLuint instructionsTexId = 0;

/**
* Texture coordinates for displaying frames from the tracker.
*/
float xTexCoord;

/**
* Texture coordinates for displaying frames from the tracker.
*/
float yTexCoord;

/**
* Size of the texture for displaying frames from the tracker.
*/
int xTexSize;

/**
* Size of the texture for displaying frames from the tracker.
*/
int yTexSize;

/**
* Aspect of the video.
*/
float videoAspect;

/**
* Size of the OpenGL view.
*/
int glWidth;

/**
* Size of the OpenGL view.
*/
int glHeight;

/**
* Switch that indicates that method for displaying results is called for the first time. 
*/
bool displayed = false;

float displayFramerate = 0;
bool isTracking = false;
int timestamp = 0;

int camOrientation;
int camHeight;
int camWidth;
int camFlip;
bool trackerStopped;
pthread_mutex_t mutex;
pthread_mutex_t mutex2;
VsImage *last_img = 0;
JNIEnv* _env;
jobject _obj;


void AlertCallback(const char* warningMessage)
{
	jclass dataClass = _env->FindClass("com/visagetechnologies/visagetrackerdemo/TrackerActivity");
	if (_env->ExceptionCheck())
			_env->ExceptionClear();
	if (dataClass != NULL)
	{
		jclass javaClassRef = (jclass) _env->NewGlobalRef(dataClass);
		jmethodID javaMethodRef = _env->GetMethodID(javaClassRef, "AlertDialogFunction", "(Ljava/lang/String;)V");
		if (_env->ExceptionCheck())
			_env->ExceptionClear();
		jstring message = _env->NewStringUTF(warningMessage);
		if (javaMethodRef != 0)
			_env->CallVoidMethod(_obj, javaMethodRef, message);

		_env->DeleteGlobalRef(javaClassRef);
		_env->DeleteLocalRef(message);
	}
}

/**
 * Method for initializing the tracker.
 *
 * This method creates a new VisageTracker objects and initializes the tracker.
 */
void Java_com_oppous_anarko_camera_VisageBridge_TrackerInit(JNIEnv *env, jobject obj, jstring configFilename)
{
	_env = env;
	_obj = obj;

	const char *_configFilename = env->GetStringUTFChars(configFilename, 0);
	trackerStopped = false;

	frameTexId = 0;
	instructionsTexId = 0;
	displayed = false;
	timestamp = 0;

	//initialize licensing
	//example how to initialize license key
	//initializeLicenseManager(env, obj, "/data/data/com.visagetechnologies.visagetrackerdemo/files/AllPlatform_7.2.vlc", AlertCallback);

	pthread_mutex_destroy(&mutex);
	pthread_mutex_init(&mutex, NULL);

	pthread_mutex_destroy(&mutex2);
	pthread_mutex_init(&mutex2, NULL);

	// delete previously allocated objects
	delete a_cap_camera;
	a_cap_camera = 0;
	delete a_cap_image;
	a_cap_image = 0;

	m_Tracker = new VisageTracker(_configFilename);

	LOGI("%s", _configFilename);
	env->ReleaseStringUTFChars(configFilename, _configFilename);
}

/**
 * Method for tracking in image
 */
void Java_com_oppous_anarko_camera_VisageBridge_TrackFromImage(JNIEnv *env, jobject obj, jint width, jint height )
{
	while(!trackerStopped)
	{
		if (m_Tracker && a_cap_image && !trackerStopped)
		{
			pthread_mutex_lock(&mutex);
			if (trackerStopped)
			{
				pthread_mutex_unlock(&mutex);
				return;
			}
			long ts;
			trackingStatus = m_Tracker->track(width, height, (const char*) a_cap_image->GrabFrame(ts), 0, VISAGE_FRAMEGRABBER_FMT_RGB, VISAGE_FRAMEGRABBER_ORIGIN_TL, 0, -1);
			isTracking = true;
			pthread_mutex_unlock(&mutex);
		}
		else
			Sleep(1);

			}
	return;
}

/**
 * Method that sets frame parameters for tracking from camera
 */
int Java_com_oppous_anarko_camera_VisageBridge_setParameters(JNIEnv *env, jobject obj, jint width, jint height, jint orientation, jint flip)
{
	camOrientation = orientation;
	camHeight = height;
	camWidth = width;
	camFlip = flip;
}

/**
 * Method for tracking from camera
 */
void Java_com_oppous_anarko_camera_VisageBridge_TrackFromCam(JNIEnv *env, jobject obj)
{
	while (!trackerStopped)
	{
		if (m_Tracker && a_cap_camera && !trackerStopped)
		{
			pthread_mutex_lock(&mutex);
			if (trackerStopped)
				{
					pthread_mutex_unlock(&mutex);
					return;
				}
			long ts;
			if (camOrientation == 90 || camOrientation == 270)
				trackingStatus = m_Tracker->track(camHeight, camWidth, (const char*) a_cap_camera->GrabFrame(ts), 0, VISAGE_FRAMEGRABBER_FMT_RGB, VISAGE_FRAMEGRABBER_ORIGIN_TL, 0, -1);
			else
				trackingStatus = m_Tracker->track(camWidth, camHeight, (const char*) a_cap_camera->GrabFrame(ts), 0, VISAGE_FRAMEGRABBER_FMT_RGB, VISAGE_FRAMEGRABBER_ORIGIN_TL, 0, -1);
			isTracking = true;
			pthread_mutex_unlock(&mutex);
		}
		else
			Sleep(1);
	}
	return;
}


/**
 * Stops the tracker.
 */
void Java_com_oppous_anarko_camera_VisageBridge_TrackerStop(JNIEnv *env, jobject obj)
{
	if (m_Tracker)
	{
		trackerStopped = true;
		pthread_mutex_lock(&mutex);
		pthread_mutex_lock(&mutex2);
		m_Tracker->stop();
		delete m_Tracker;
		m_Tracker = 0;
		pthread_mutex_unlock(&mutex2);
		pthread_mutex_unlock(&mutex);

	}
}


/**
* Writes raw image data into @ref VisageSDK::AndroidImageCapture object. VisageTracker reads this image and performs tracking.
* @param frame byte array with image data
* @param width image width
* @param height image height
*/
void Java_com_oppous_anarko_camera_VisageBridge_WriteFrameImage(JNIEnv *env, jobject obj, jbyteArray frame, jint width, jint height)
{
	if (!a_cap_image)
		a_cap_image = new AndroidImageCapture(width, height, VISAGE_FRAMEGRABBER_FMT_RGB);
	jbyte *f = env->GetByteArrayElements(frame, 0);
	a_cap_image->WriteFrame((unsigned char *)f, (int)width, (int)height);
	env->ReleaseByteArrayElements(frame, f, 0);
}


/**
* Writes raw image data into @ref VisageSDK::AndroidCameraCapture object. VisageTracker reads this image and performs tracking. User should call this 
* function whenever new frame from camera is available. Data inside frame should be in Android NV21 (YUV420sp) format and @ref VisageSDK::AndroidCameraCapture
* will perform conversion to RGB.
* @param frame byte array with image data
*/
void Java_com_oppous_anarko_camera_VisageBridge_WriteFrameCamera(JNIEnv *env, jobject obj, jbyteArray frame, jint cameraId)
{
    int c_ID = (int)cameraId;

	if (trackerStopped)
		return;
	if (!a_cap_camera) {
		a_cap_camera = new AndroidCameraCapture(camWidth, camHeight, camOrientation, camFlip);
		LOGI("C++Library - Camera is installed");
	}

	//if (!a_cap_camera)
	    //delete a_cap_camera;

    //if (cameraId == 0)
	    //a_cap_camera = new AndroidCameraCapture(camWidth, camHeight, 90, camFlip);
    //else
        ///a_cap_camera = new AndroidCameraCapture(camWidth, camHeight, 270, camFlip);
	//

	if (c_ID == 0) {
	    a_cap_camera->setOrientation(90);
	    a_cap_camera->setFlip(0);
	} else if (c_ID == 1) {
	    a_cap_camera->setOrientation(270);
	    a_cap_camera->setFlip(1);
	}

	jbyte *f = env->GetByteArrayElements(frame, 0);
	a_cap_camera->WriteFrameYUV((unsigned char *)f);
	env->ReleaseByteArrayElements(frame, f, 0);
}


/**
 * Method for initiliazing video texture through OpenGL.
 * This texture is used to display tracking image and results.
 * The texture must have dimensions that are power of 2, so video data will cover only part of the texture.
 *
 * @param image frame to init video display with
 */
void initFrameTexture(VsImage *image)
{
	xTexSize = 1;
    yTexSize = 1;
    while((image->width / xTexSize) > 0) xTexSize <<= 1;
    while((image->height / yTexSize) > 0) yTexSize <<= 1;

	glGenTextures(1, &frameTexId);
	//LOGI("Tex info  %d %d %d %d %d", xTexSize, yTexSize, image->width, image->height, image->nChannels);
	frameTexId += 50;
 
	glBindTexture(GL_TEXTURE_2D, frameTexId);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

	switch (image->nChannels) 
	{
		case 1:
			glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, xTexSize, yTexSize, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, 0);
			break;
		case 3:
		case 4:
		default:
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, xTexSize, yTexSize, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
			break;
	}
	xTexCoord = image->width / (float) xTexSize;
	yTexCoord = image->height / (float) yTexSize;

    videoAspect = image->width / (float) image->height;

	float tmp;
    if(image->width < image->height)
    {    
		tmp = glHeight;
    	glHeight = glWidth / videoAspect;
		if (glHeight > tmp)
		{
			glWidth  = glWidth*tmp/glHeight;
			glHeight = tmp;
		}
    }
    else
    {
		tmp = glWidth;
    	glWidth = glHeight * videoAspect;
		if (glWidth > tmp)
		{
			glHeight  = glHeight*tmp/glWidth;
			glWidth = tmp;
		}		
    }
}


/**
 * Method for refreshing video texture through OpenGL.
 *
 * The part of the texture containing video data is refreshed with current frame data.
 *
 * @param image frame to refresh video display with
 */
void setFrameTexture(VsImage *image)
{
	glClear(GL_COLOR_BUFFER_BIT);
	glBindTexture(GL_TEXTURE_2D, frameTexId);	
	switch (image->nChannels) 
	{
		case 3:
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image->width, image->height, GL_RGB, GL_UNSIGNED_BYTE, image->imageData);
			break;
		case 4:
			//glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image->width, image->height, GL_BGRA, GL_UNSIGNED_BYTE, image->imageData);
			break;
		case 1:
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image->width, image->height, GL_LUMINANCE, GL_UNSIGNED_BYTE, image->imageData);
			break;
		default:
			return; 
		} 
}


/**
 * Method for displaying video through OpenGL.
 */
void DrawImage()
{
	glDisable(GL_DEPTH_TEST);

	glEnable(GL_TEXTURE_2D);

	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadIdentity();									// Reset The Projection Matrix
	
	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();									// Reset The Projection Matrix
	glOrthof(-1.0f, 1.0f, -1.0f, 1.0f, -10.0f, 10.0f);

	const GLfloat vertices[] = {
		-1.0f,-1.0f,-5.0f,
		1.0f,-1.0f,-5.0f,
		-1.0f,1.0f,-5.0f,
		1.0f,1.0f,-5.0f,
	};
	
	//tex coords are flipped upside down instead of an image
	const GLfloat texcoords[] = {
		0.0f,		yTexCoord,
		xTexCoord,	yTexCoord,
		0.0f,		0.0f,
		xTexCoord,	0.0f,
	};
				
	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);					// Set Color To White
	
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	glEnableClientState(GL_VERTEX_ARRAY);
	
	glVertexPointer(3, GL_FLOAT, 0, vertices);
	glTexCoordPointer(2, GL_FLOAT, 0, texcoords);
	
	glViewport(0, 0, glWidth, glHeight);
	
	glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	glDisableClientState(GL_VERTEX_ARRAY);
	
	glDisable(GL_TEXTURE_2D);
	
	glPopMatrix();
	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();

	glClear(GL_DEPTH_BUFFER_BIT);
}


#define MEASURE_FRAMES 10

long last_times[MEASURE_FRAMES];
int framecount = -1;
int last_pts = 0;

/**
 * Method for displaying tracking results.
 *
 * This method is periodically called by the application rendering thread to get and display tracking results. The results are retrieved using VisageSDK::TrackingData structure and VisageTracker::getTrackingData() function and displayed OpenGL ES for visual data (frames from camera and 3D face model). It shows how to properly interpret tracking data and setup the OpenGL scene to display 3D face model retrieved from the tracker correctly aligned to the video frame. 
 *
 * @param width width of GLSurfaceView used for rendering.
 * @param height height of GLSurfaceView used for rendering.
 */
bool Java_com_oppous_anarko_camera_VisageBridge_displayTrackingStatus(JNIEnv* env, jobject obj, jint width, jint height)
{
	pthread_mutex_lock(&mutex2);
	if(!displayed)
	{
		glWidth = width;
		glHeight = height;
		displayed = true;
	}
	if (!m_Tracker || trackerStopped || !isTracking)
	{
		pthread_mutex_unlock(&mutex2);
		return false;
	}
	int pts;
	int counter = 0;
	do
	{
		trackingStatus = m_Tracker ? m_Tracker->getTrackingData(&trackingData) : 0;
		pts = trackingData.timeStamp;

		if(last_pts == pts){
			Sleep(5);
		}
		counter++;
	} while(last_pts == pts && counter < 20);

	last_pts = pts;

	struct timeval tv;
	gettimeofday(&tv, NULL);

	long currentTime = (long)(tv.tv_usec/1000 + (tv.tv_sec & 0xfffff) * 1000);
	if(framecount == -1)
	{
		framecount = 0;
		for(int i=0;i<10;i++)
			last_times[i]=0;
	}
	framecount++;
	if(framecount == MEASURE_FRAMES) framecount = 0;
	displayFramerate = MEASURE_FRAMES * 1000.0f / (float)(currentTime - last_times[framecount]);
	last_times[framecount] = currentTime;

	if(trackingStatus != TRACK_STAT_OFF)
	{
		//1. DRAW FRAME
		if (trackingData.frame != 0)
		{
			if(frameTexId == 0)
				initFrameTexture((VsImage*)trackingData.frame);
			setFrameTexture((VsImage*)trackingData.frame);
			last_img = (VsImage*)trackingData.frame;
			DrawImage();
		}
	}
	else
	{
		setFrameTexture(last_img);
		DrawImage();
	}

#define DRAW_RESULTS
	
	if(trackingStatus == TRACK_STAT_OK)
		VisageRendering::DisplayResults(&trackingData, trackingStatus, glWidth, glHeight, 15);

	pthread_mutex_unlock(&mutex2);

	return true;
}



/**
* Method for getting frame rate information from the tracker.
*
* @return float value of frame rate obtained from the tracker.
*/
float Java_com_oppous_anarko_camera_VisageBridge_GetFps(JNIEnv* env, jobject obj)
{
	return trackingData.frameRate;
}


/**
* Method for getting the tracking status information from tracker.
*
* @return tracking status information as string.
*/
JNIEXPORT jstring JNICALL Java_com_oppous_anarko_camera_VisageBridge_GetStatus(JNIEnv* env, jobject obj)
{
	char* msg;
	if(trackingStatus == TRACK_STAT_OK)
	{
		return env->NewStringUTF("OK");
	}
	if(trackingStatus == TRACK_STAT_RECOVERING)
	{
		return env->NewStringUTF("RECOVERING");
	}
	if(trackingStatus == TRACK_STAT_INIT)
	{
		return env->NewStringUTF("INITIALIZING");
	}
	if(trackingStatus == TRACK_STAT_OFF)
	{
		return env->NewStringUTF("OFF");
	}
	return env->NewStringUTF("");
}

float Java_com_oppous_anarko_camera_VisageBridge_getTrackerFps( JNIEnv*  env )
{
	return m_fps;
}


float Java_com_oppous_anarko_camera_VisageBridge_GetDisplayFps(JNIEnv* env)
{
	return displayFramerate;
}

bool Java_com_oppous_anarko_camera_VisageBridge_IsAutoStopped(JNIEnv* env)
{
	//RESET VARIABLES IN WRAPPER
	return m_Tracker ? m_Tracker->IsAutoStopped : false;
}

/*
    0,1 : LeftEye
    2,3 : RightEye
    4,5 : FaceTop
    6,7 : FaceBottom
    8,9 : LeftEar
    10,11 : RightEar
*/
jfloatArray Java_com_oppous_anarko_camera_VisageBridge_getFacialPoints(JNIEnv*  env, jobject obj) {
    jfloatArray  result = env->NewFloatArray(12);

    jfloat facialPoints[12];

    facialPoints[0] = 0;
    facialPoints[1] = 0;
    facialPoints[2] = 0;
    facialPoints[3] = 0;
    facialPoints[4] = 0;
    facialPoints[5] = 0;
    facialPoints[6] = 0;
    facialPoints[7] = 0;
    facialPoints[8] = 0;
    facialPoints[9] = 0;
    facialPoints[10] = 0;
    facialPoints[11] = 0;


    if (trackingData.frame != 0 && trackingStatus == TRACK_STAT_OK) {
		//LOGI("C++Library - FrameStatus is OK");

        facialPoints[0] = trackingData.featurePoints2D->getFP(3, 6).pos[0];
        facialPoints[1] = trackingData.featurePoints2D->getFP(3, 6).pos[1];
        facialPoints[2] = trackingData.featurePoints2D->getFP(3, 5).pos[0];
        facialPoints[3] = trackingData.featurePoints2D->getFP(3, 5).pos[1];
        facialPoints[4] = trackingData.featurePoints2D->getFP(11, 5).pos[0];
        facialPoints[5] = trackingData.featurePoints2D->getFP(11, 5).pos[1];
        facialPoints[6] = trackingData.featurePoints2D->getFP(2, 1).pos[0];
        facialPoints[7] = trackingData.featurePoints2D->getFP(2, 1).pos[1];
        facialPoints[8] = trackingData.featurePoints2D->getFP(10, 2).pos[0];
        facialPoints[9] = trackingData.featurePoints2D->getFP(10, 2).pos[1];
        facialPoints[10] = trackingData.featurePoints2D->getFP(10, 1).pos[0];
        facialPoints[11] = trackingData.featurePoints2D->getFP(10, 1).pos[1];
    }

    env->SetFloatArrayRegion(result, 0, 12, facialPoints);

    return result;

}

bool Java_com_oppous_anarko_camera_VisageBridge_getTrackingStatus(JNIEnv*  env, jobject obj) {
	bool ret = false;
	if(trackingStatus == TRACK_STAT_OK) {
		ret = true;
	}

	return ret;
}

int Java_com_oppous_anarko_camera_VisageBridge_getFaceStatus(JNIEnv*  env, jobject obj) {
	int ret = 0;
	if (trackingStatus == TRACK_STAT_OK) {
		//LOGI("MouthStatus - %f, %f", trackingData.eyeClosure[0], trackingData.eyeClosure[1]);
		if (trackingData.eyeClosure[0] == 0 && trackingData.eyeClosure[1] == 0) {
			ret = 1;
		}
	}

	if (trackingStatus == TRACK_STAT_OK) {
		float mouthUpPointY = trackingData.featurePoints2D->getFP(2, 2).pos[1];
		float mouthDownPointY = trackingData.featurePoints2D->getFP(2, 3).pos[1];
		//LOGI("MouthStatus - %f", mouthUpPointY - mouthDownPointY);
		if (mouthUpPointY - mouthDownPointY > 0.008) {
			ret = 2;
		}
	}

	return ret;

}

}
