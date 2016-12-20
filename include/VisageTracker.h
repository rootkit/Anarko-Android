///////////////////////////////////////////////////////////////////////////////
// 
// (c) Visage Technologies AB 2002 - 2015  All rights reserved. 
// 
// This file is part of visage|SDK(tm). 
// Unauthorized copying of this file, via any medium is strictly prohibited. 
// 
// No warranty, explicit or implicit, provided. 
// 
// This is proprietary software. No part of this software may be used or 
// reproduced in any form or by any means otherwise than in accordance with
// any written license granted by Visage Technologies AB. 
// 
/////////////////////////////////////////////////////////////////////////////


#ifndef __VisageTracker_h__
#define __VisageTracker_h__

#ifdef VISAGE_STATIC
	#define VISAGE_DECLSPEC
#else

	#ifdef VISAGE_EXPORTS
		#define VISAGE_DECLSPEC __declspec(dllexport)
	#else
		#define VISAGE_DECLSPEC __declspec(dllimport)
	#endif

#endif

#include "FaceData.h"
#include "SmoothingFilter.h"

#ifdef WIN32
#include <windows.h>
#include <process.h>
#endif

#ifdef ANDROID
extern "C" {
	VISAGE_DECLSPEC void _loadVisageVision();
}
#endif

#ifndef WIN32
#include <pthread.h>
#include <sys/types.h>
#define HANDLE pthread_t*
#endif


namespace VisageSDK
{

#define TRACK_STAT_OFF 0
#define TRACK_STAT_OK 1
#define TRACK_STAT_RECOVERING 2
#define TRACK_STAT_INIT 3

#define VISAGE_CAMERA_UP 0
#define VISAGE_CAMERA_DOWN 1
#define VISAGE_CAMERA_LEFT 2
#define VISAGE_CAMERA_RIGHT 3

#define VISAGE_FRAMEGRABBER_FMT_RGB 0
#define VISAGE_FRAMEGRABBER_FMT_BGR 1 
#define VISAGE_FRAMEGRABBER_FMT_LUMINANCE 2
#define VISAGE_FRAMEGRABBER_FMT_RGBA 3
#define VISAGE_FRAMEGRABBER_FMT_BGRA 4

#define VISAGE_FRAMEGRABBER_ORIGIN_TL 0
#define VISAGE_FRAMEGRABBER_ORIGIN_BL 1

class VisageTrackerCore;
class VisageTrackerObserver;
class Candide3Model;
class TrackerInternalInterface;
class TrackerGUIInterface;
class VisageDetector;
class ModelFitter;
class PoseEstimator;
class CFBAEncoder;

/** VisageTracker is a face tracker capable of tracking the head pose, facial features and gaze in video coming from a
* video file, camera or other sources.
*
* The tracker is fully configurable through comprehensive tracker configuration files. visage|SDK contains optimal configurations 
* for common uses such as head tracking and facial features tracking.
* Please refer to the <a href="../VisageTracker Configuration Manual.pdf">VisageTracker Configuration Manual</a> for the list of available configurations.
* \if IOS_DOXY
* Please read more details about configuration selection in the section <a href="../../doc/creatingxc.html#config_selection">Device-specific configuration selection</a>
* \endif
*
* The <a href="../VisageTracker Configuration Manual.pdf">VisageTracker Configuration Manual</a> provides full detail
* on all available configuration options, allowing to customize the tracker in terms of performance, quality, tracked features, range
* of tracked facial actions and other options and produce in effect a variety of different trackers suited for specific applications.
*
* \if ANDROID_DOXY
* Tracking from raw image input is enabled by track() and allows tracking
* from any source.
* \endif
*
* The tracker offers the following outputs, available through method getTrackingData():
* - 3D head pose,
* - facial expression,
* - gaze direction,
* - eye closure,
* - facial feature points,
* - full 3D face model, textured.
* 
*
* The tracker can apply a smoothing filter to tracking results to reduce the inevitable tracking noise. Smoothing factors 
* are adjusted separately for global face rotation, translation and different parts of the face. The smoothing settings 
* in the supplied tracker configurations are adjusted conservatively to avoid delay in tracking response, yet provide 
* reasonable smoothing. For further details please see the smoothing_factors parameter array in the 
* <a href="../VisageTracker Configuration Manual.pdf">VisageTracker Configuration Manual</a>.
*
* Frames(images) need to be passed sequentially to the VisageTracker::track() method, which immediately returns results for the given frame.
* Tracking results can also be obtained by calling method VisageTracker::getTrackingData().
* 
* \if IOS_DOXY
*
* Furthermore, the VisageTrackerObserver can be used to obtain the tracking results. This is particularly useful for synchronisation.

* To attach one or more observers, use the attach() method. The VisageTrackerObserver::Notify() function
* shall then be called by the tracker after each processed video frame (even if the face was not found in the current frame).
*
* \elseif ANDROID_DOXY
*
* Furthermore, the VisageTrackerObserver can be used to obtain the tracking results. This is particularly useful for synchronisation.

* To attach one or more observers, use the attach() method. The VisageTrackerObserver::Notify() function
* shall then be called by the tracker after each processed video frame (even if the face was not found in the current frame).
*
* \else Additional mechanisms for obtaining outputs are provided for
* convenience:
* - VisageTrackerObserver: During tracking, the VisageTrackerObserver can be used to obtain the tracking results.
* To attach one or more observers, use the attach() method. The VisageTrackerObserver::Notify() function
* shall then be called by the tracker after each processed video frame (even if the face was not found in the current frame).
* This is particularly useful for synchronisation.
* In the FaceTracker2 sample this mechanism is used to render the facial animation on a face model based on tracking
* results.
* - Writing into an FBA file: The tracking results in form of MPEG-4 Face and Body Animation Parameters can be saved in a .fba file (MPEG-FBA ecoded).
* 
* \endif
*
* The tracker requires a set of data and configuration files, available in Samples/data
*
* Please either copy the complete contents of this folder into your application's working folder, or consult <a href="../VisageTracker Configuration Manual.pdf">VisageTracker Configuration Manual</a> for detailed settings.
*
*/
class VISAGE_DECLSPEC VisageTracker
{
public:
	/** Constructor.
	*
	* @param trackerConfigFile the name of the tracker configuration file (.cfg; default configuration files are provided in Samples/data folder.
	* Constructor must be called with the full path to the configuration file. The configuration file is in the same directory as other data files needed by the tracker. Because it is in the same directory its path can be parsed to set the path for the other data files.
	* For further details see <a href="../VisageTracker Configuration Manual.pdf">VisageTracker Configuration Manual</a>\if IOS_DOXY and section on <a href="../../doc/creatingxc.html#config_selection">device-specific configuration selection</a>.\endif).
	*/
	VisageTracker(const char* trackerConfigFile);

	#ifdef EMSCRIPTEN
	/** Constructor.
	*
	* @param trackerConfigFile the name of the tracker configuration file (.cfg; default configuration files are provided in Samples/data folder.
	* Constructor must be called with the full path to the configuration file. The configuration file is in the same directory as other data files needed by the tracker. Because it is in the same directory its path can be parsed to set the path for the other data files.
	* For further details see <a href="../VisageTracker Configuration Manual.pdf">VisageTracker Configuration Manual</a>\if IOS_DOXY and section on <a href="../../doc/creatingxc.html#config_selection">device-specific configuration selection</a>.\endif).
	*/
	VisageTracker(std::string trackerConfigFile);
	#endif

	/** Destructor.
	*/
	virtual ~VisageTracker();

	/**
	* Performs face tracking in the given image and returns tracking results and status.
	* This function should be called repeatedly on a series of images in order to perform continuous tracking.  
	*   
	* If the tracker needs to be initialized, this will be done automatically before tracking is performed on the given image. 
	* Initialization means loading the tracker configuration file, required data files and allocating various data buffers to the given image size. 
	* This operation may take several seconds.
	* This happens in the following cases:
	*   - In the first frame (first call to track() function).
	*   - When frameWidth or frameHeight are changed, i.e. when they are different from the ones used in the last call to track() function.
	*   - If setTrackerConfigurationFile() function was called after the last call to track() function.
	*  
	*  The tracker results are returned in faceData.
	*  
	* @param frameWidth Width of the frame
	* @param frameHeight Height of the frame
	* @param p_imageData Pointer to image pixel data; size of the array must correspond to frameWidth and frameHeight
	* @param facedata FaceData instance that will receive the tracking results. No tracking results will be returned if NULL pointer is passed. 
	* On first call of this function, the memory for the required member variables of the passed FaceData object will be allocated and initialized automatically.
	* @param format Format of input images passed in p_imageData. It can not change during tracking. Format can be one of the following:
	* - VISAGE_FRAMEGRABBER_FMT_RGB: each pixel of the image is represented by three bytes representing red, green and blue channels, respectively.
	* - VISAGE_FRAMEGRABBER_FMT_BGR: each pixel of the image is represented by three bytes representing blue, green and red channels, respectively.
	* - VISAGE_FRAMEGRABBER_FMT_RGBA: each pixel of the image is represented by four bytes representing red, green, blue and alpha (ignored) channels, respectively.
	* - VISAGE_FRAMEGRABBER_FMT_BGRA: each pixel of the image is represented by four bytes representing blue, green, red and alpha (ignored) channels, respectively.
	* - VISAGE_FRAMEGRABBER_FMT_LUMINANCE: each pixel of the image is represented by one byte representing the luminance (gray level) of the image.
	* @param origin Origin of input images that will be passed in p_imageData. It can not change during tracking. Format can be one of the following:
	* - VISAGE_FRAMEGRABBER_ORIGIN_TL: Origin is the top left pixel of the image. Pixels are ordered row-by-row starting from top left.
	* - VISAGE_FRAMEGRABBER_ORIGIN_BL: Origin is the bottom left pixel of the image. Pixels are ordered row-by-row starting from bottom left.
	* @param widthStep Width of the image data buffer, in bytes.
	* @param timeStamp The timestamp of the the input frame. The passed value will be returned with the tracking data for that frame (FaceData::timeStamp). Alternatively, the value of -1 can be passed, in which case the tracker will return time, in milliseconds, measured from the moment when tracking started. If a VisageTrackerObserver is used, the timeStamp is also passed through VisageTrackerObserver::Notify() method.
	* @returns tracking status (TRACK_STAT_OFF, TRACK_STAT_OK, TRACK_STAT_RECOVERING and TRACK_STAT_INIT, see @ref FaceData for more details)
	*
	* @see FaceData
	*/
	virtual int track(int frameWidth, int frameHeight, const char* p_imageData, FaceData* facedata, int format = VISAGE_FRAMEGRABBER_FMT_RGB, int origin = VISAGE_FRAMEGRABBER_ORIGIN_TL, int widthStep = 0, long timeStamp = -1);

	/** Get face data and status.
	* 
	* This method fills the given FaceData structure and returns the tracking status.
	*
	* On first call of this function, the memory for the required member variables of the passed FaceData object will be allocated and initialized automatically.
	*
	* @param data FaceData structure that will be filled with current face data
	* @return tracking status (TRACK_STAT_OFF, TRACK_STAT_OK, TRACK_STAT_RECOVERING and TRACK_STAT_INIT, see @ref FaceData for more details)
	*
	* @see FaceData
	*/
	int getTrackingData(FaceData *data);

	/** Set configuration file name.
	*
	* The tracker configuration file name is set and this configuration file will be used for next tracking session (i.e. when track() is called). Default configuration files (.cfg) are provided in Samples/data folder.
	* Please refer to the  <a href="../VisageTracker Configuration Manual.pdf">VisageTracker Configuration Manual</a> for further details
	* on using the configuration files and all configurable options. \if IOS_DOXY Also, please read the section on automatic device-specific configuration selection.\endif
	* @param trackerConfigFile the name of the tracker configuration file.
	*/
	void setTrackerConfigurationFile(const char* trackerConfigFile);

	/** Attaches an observer implementation to the tracker.
	*
	* @param _obs pointer to a VisageTrackerObserver implementation.
	* @see VisageTrackerObserver, detach()
	*/
	void attach(VisageTrackerObserver * _obs) {if (nObs < 10) obs[nObs++] = _obs;};


	/** Detaches all attached observers from the tracker.
	* This function deactivates the observer mechanism.
	*
	* NOTE: the observer objects are only detached, not destroyed.
	* @see VisageTrackerObserver, attach()
	*/
	void detach() {nObs = 0;};

	/*
	* Set the upper and lower limit for each of Facial Animation Parameters, i.e., the maximum and minimum allowed values for
	* each of the 68 FAPs. The tracker will cut off any values outside these limits.
	*
	* Initially it is set to a very large range so the limits will not be reached in practice.
	*
	* @param upperLimit pointer to the FAPs object containing the upper FAP limits
	* @param lowerLimit pointer to the FAPs object containing the lower FAP limits
	* @see getUpperFAPLimits(), getLowerFAPLimits()
	*/
	void setFAPLimits(FAPs *upperLimit, FAPs *lowerLimit);

	/*
	* Returns the pointer to the FAPs object containing the upper FAP limits
	* @return the upper FAP limits.
	*/
	FAPs *getUpperFAPLimits();

	/*
	* Returns the pointer to the FAPs object containing the lower FAP limits.
	* See setFAPLimits() for details regarding the FAP limitation mechanism.
	* @return the lower FAP limits.
	*/
	FAPs *getLowerFAPLimits();
	
	/** Sets the inter pupillary distance
	* 
	* Inter pupillary distance (IPD) is used by the tracker to estimate the distance of the face from the camera. 
	* By default, IPD is set to 0.065 (65 millimeters) which is considered average. If the actual IPD of the tracked person is known, this function can be used to set this IPD. As a result, the calculated distance from the camera will be accurate (as long as the camera focal lenght is also set correctly).
	* This is important for applications that require accurate distance. For example, in Augmented Reality applications objects such as virtual eyeglasses can be rendered at appropriate distance and will thus appear in the image with real-life scale.
	* 
	* @param value the inter pupillary distance (IPD) in meters.
	* @see getIPD()
	*/
	void setIPD(float value);

	/** Returns the inter pupillary distance.
	* Returns the current inter pupillary distance (IPD) setting.
	* IPD setting is used by the tracker to estimate the distance of the face from the camera. See setIPD() for further details.
	* @return current setting of inter pupillary distance (IPD) in meters.
	* @see setIPD()
	*/
	float getIPD();
	
	/**Stops the tracking.
	*
	*/
	void stop();

	/** Reset tracking
	*
	* Resets the tracker. Tracker will reinitialise.
	*
	*/
	void reset();

#ifdef IOS
	/** \if IOS_DOXY\ Set data bundle
	 * 
	 * Used to set bundle from which data files will be read. Default is main bundle.
	 *
	 * \endif
	 */
	void setDataBundle(NSBundle *bundle);
#endif

	bool IsAutoStopped; // reason for stopping

private:

	int Init(void);
	int Update(void);
	void Finish(void);

	static void processEyesClosure(VsImage* frame, FDP* fdp, float* t, float* r, float* out);
	static void GetPupilCoords(FDP* points, VsMat* eyes_coords, int w, int h);
	
	TrackerGUIInterface *guiIface;
	TrackerInternalInterface *internalIface;

	float init_pose_dev; /* Allowed deviation from ideal initialisation pose. This value is used only in automatic initialisation mode. It controls the level of quality of the head pose that triggers the initialisation of the tracker. The value of 0 means the tracker will require the best head pose before it starts; higher values relay the requirements.*/
	float init_yaw_threshold; /* This value is used during automatic initialisation or, in manual initialization mode while the tracker initially searches for the best frame on which to perform initialization. It controls the amount of yaw (left-right rotation of the head) allowed at initialisation; the tracker waits until the head pose is within this limit before it initialises and starts tracking. The value of 0 means the tracker will require perfectly frontal head pose before it starts (it is not recommended to set it to 0 because the tracker may then never start); higher values relax the requirements.*/
	float init_roll_threshold; /* This value is used during automatic initialisation or, in manual initialization mode while the tracker initially searches for the best frame on which to perform initialization. It controls the amount of roll (tilt of the head) allowed at initialisation; the tracker waits until the head pose is within this limit before it initialises and starts tracking. The value of 0 means the tracker will require perfectly frontal head pose before it starts (it is not recommended to set it to 0 because the tracker may then never start); higher values relax the requirements. */
	float init_velocity_threshold; /* This value is used only in automatic initialisation mode. It controls the speed of head movement allowed at initialisation; the tracker waits until the head stabilises below this speed limit before it initialises and starts tracking. It is expressed in meters per second. The value of 0 means the tracker will require perfectly still head before it starts (it is not recommended to set it to 0 because the tracker may then never start); higher values relax the requirements. */
	float init_timeout; /* This value is used only in automatic initialisation mode. It controls the time allowed at initialisation, in milliseconds. If the desired head pose was not found during this time, the tracker chooses the best available image seen during this time. The timing starts from the moment when face is detected.*/
	int init_timeout_enable; /* This value is used during automatic initialisation or, in manual initialization mode while the tracker initially searches for the best frame on which to perform initialization. It enables or disables the initialization timeout mechanism; when it is disabled, the parameter init_timeout (see above) is ignored and initialization continues until the desired head pose is reached. The setting is separate for camera, video file and raw image input modes and determined by the first, second and third bit of the value, respectively. Thus value 1 means that the timeout mechanism is enabled when tracking from camera; 2 means it is enabled when tracking from video file; 4 means it is enabled when using the raw image interface and 0 means it is always disabled; combinations are allowed, e.g. 6 enables timeout in video and raw image input modes.*/
	int init_display_status; /* This value is used during automatic initialisation or, in manual initialization mode while the tracker initially searches for the best frame on which to perform initialization. It enables or disables the initialization status display. When enabled, the initialization status is displayed interactively on the screen during initialization in order to help the user to position the head. The setting is separate for camera, video file and raw image input modes and determined by the first, second and third bit of the value, respectively. Thus value 1 means that the display is enabled when tracking from camera; 2 means it is enabled when tracking from video file; 4 means it is enabled when using the raw image interface and 0 means it is always disabled; combinations are allowed, e.g. 6 enables display in video and raw image input modes.*/
	float recovery_timeout; /* This value is used only in automatic initialisation mode. It is used when the tracker looses the face and can not detect any face in the frame. This value tells the tracker how long it should wait before considering that the current user is gone and initialising the full re-initialisation procedure.  If the face is detected before this time elapses, the tracker considers that it is the same person and it recovers, i.e. continues tracking it using the previous settings. The time is expressed in milliseconds. */

	friend class VisageFeaturesDetector;

	VsImage* frame_input; // Current video frame, input; may be color or grayscale
	VsImage* frame_gray; // Current video frame converted to grayscale; all processing is done on grayscale images
	VsImage* frame_gray_prev; // Previous video frame converted to grayscale; used for optical flow tracking

protected:
	SmoothingFilter sf;
	VsMat *smoothing_factors;
	FaceData* trackingData;

	void smoothFeaturePoints(FDP *fp);

	int frameCount; // frame count from beginning of tracking
	double frameTime; // duration of one frame in milliseconds
	long pts; // presentation time stamp, calculated by grabFrame()
	long pts_data; // time stamp
	int pts_frame;

	volatile bool active;

private:
	float bdts_trees_factor;

	long getCurrentTimeMs(bool init);

	bool grabFrame(bool init);

	VsImage *frame_buffers[3];
	int a_frame;
	int p_frame;
	int v_frame;

	bool fitModelToFace(FDP *latestFP, bool init_tracker, bool reinit = false);
	bool verifyPose(FDP *f);
	bool testPose(FDP *f, FDP *best);
	void writeBits(FILE *streamHandle, unsigned char *bits, int size);

	int faceLostTime; //time when face was lost from view
	int faceFoundTime; //time when face was detected
	float bestPoseValue;

	int trackerState;

	bool init_successful;

	//interesting FAP groups are 2,3,4,5,8 (actually 1,2,3,4,7 as the counting starts from 0)
	FBAPs *fbaps; /*!Pointer to a Face and Body Animation Parameters object*/
	FBAPs *fbaps0;

	FAPs* upperLimits;
	FAPs* lowerLimits;

	VisageTrackerObserver *obs[10]; /*!<observers list*/
	int nObs; /*!number of attached observers*/

	//face rotation and translation
	float face_translation_abs[3];
	float face_translation_comp[3];
	float face_rotation[3];

	// values holding the initialization status
	bool init_face_detected;
	float init_yaw;
	float init_roll;
	float init_velocity;

	//various flags
	bool toFile;
	bool fitting; //used by display function to display the wireframe model in different color while face fitting process is on, mainly for debugging purposes

#if defined (IOS)
	NSAutoreleasePool *pool;
#endif

	CFBAEncoder* fbaEncoder; /*!encoder object*/
	FILE *fbaFileHandle; /*!handle for the FBA file*/

	char fbaFileName[500];/*!fba file name*/

	unsigned char byte_fba;
	int bitcnt;

	std::string configuration_filename; //tracker configuration file

	float gaze[2]; //gaze direction

#ifdef IOS
	NSBundle *dataBundle;
#endif

	//stuff copied from main.cpp in Nils' original project
	volatile bool tracking_ok; //tracking status OK
	float trackingFrameRate; // measured tracking frame rate

	int m_width; //frame width
	int m_height; //frame height
	int m_format; //frame format for the frame image (RGB, BGR or LUMINANCE)

	void loadConfig();  // read application settings from configuration file

	float m_IPD;

	//stuff for calculating FAPs and FDPs
	float calculateFAPU( int fapu );
	void calculateFAPUs();
	float fapuValue[6];
	int calculateFAP( int fap ) ;
	void calculateFAPs( FBAPs* fbaps);
	void calculateFDP(FDP* f, int w, int h, VsMat* vert, bool _3D);
	void setFDPIndices(FDP* f);

	FDP *featurePoints2D;
	FDP *featurePoints3D;
	FDP *featurePoints3DR;

	FDP *m_bestFP;

	ModelFitter* modelFitter;
	int nFPs; // number of feature points used for fitting

	void initTrackingData(void);
	void swapTrackingData(void);
	//void smoothTrackingData(void); NOT A VALID IMPLEMENTATION -> NEEDS FIXING

	volatile bool inSwap;
	volatile bool inRead;
	volatile int turn;

#if defined(IOS) || defined(ANDROID) || defined(MAC_OS_X) || defined(LINUX)
	pthread_mutex_t trackingData_mutex;
	pthread_mutex_t track_mutex;
#endif

#ifdef WIN32
	CRITICAL_SECTION trackingData_mutex;
	CRITICAL_SECTION track_mutex;
#endif

	std::string bdts_data_path; // path to the folder containing boosted decision trees
#ifdef EMSCRIPTEN
	public: VisageDetector* m_DetectorBDFS;
	private:
#else
	VisageDetector* m_DetectorBDFS;
#endif

	VisageTrackerCore* m_trackerCore;
	//SimpleTrackerCore* m_trackerCore;

	Candide3Model* model;
	PoseEstimator* poseEstimator;

	bool fitted;

	bool inited;

	long fg_pts;

	bool configChanged;

	char m_trackCfgDataPath[300];

	FILE *log_file; //log file - used for debugging, set via log_filename configuration parameter
};

}

#endif // __VisageTracker_h__

