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


#ifndef __VisageTrackerObserver_h__
#define __VisageTrackerObserver_h__

#ifdef VISAGE_STATIC
	#define VISAGE_DECLSPEC
#else

	#ifdef VISAGE_EXPORTS
		#define VISAGE_DECLSPEC __declspec(dllexport)
	#else
		#define VISAGE_DECLSPEC __declspec(dllimport)
	#endif

#endif

#include "VisageTracker.h"

namespace VisageSDK
{

/** VisageTrackerObserver interface for user-defined callback functions triggered and called by the tracker, typically used for synchronizing the application with the tracker

* This virtual class allows to get notification from VisageTracker.
*
* The notification happens through the callback function Notify().
* This function is implemented by the user in a specific class derived from
* the VisageTrackerObserver class. One or more instances of this class may be attached to the VisageTracker
* using the VisageTracker::attach() function, and the functions implemented
* by the user are called when events occur.
*
* Notify() function is called for every frame of the video processed by the tracker except the first one which is used for initialisation.
*
* \if IOS_DOXY
* Demonstrated in <a href="../tracker.html">VisageTrackerDemo</a> sample project.
* \elseif MACOSX_DOXY
* Demonstrated in <a href="../tracker.html">VisageTrackerDemo</a> sample project.
* \elseif LINUX_DOXY
* Demonstrated in <a href="../../../Samples/Linux/build/VisageTrackerDemo/doc/index.html">FaceTracker2 sample project</a>.
* \elseif REDHAT_DOXY
* Demonstrated in <a href="../../../Samples/Linux/build/VisageTrackerDemo/doc/index.html">FaceTracker2 sample project</a>.
* \elseif ANDROID_DOXY
* \else
* Demonstrated in <a href="../../../Samples/OpenGL/build/msvc140/FaceTracker2/doc/index.html">FaceTracker2 sample project</a>.
* \endif
*/
class VISAGE_DECLSPEC VisageTrackerObserver {
	public:

	/** Notification function.
	* This function is called by VisageTracker during tracking 
	* whenever a new video frame is processed. It gets the frame time stamp and it also receives the pointer to the actual tracker object
	* that can be used to retreive all other data from the tracker.
	* The function will not be called for the first frame as that frame is used for initialisation purposes.
	*
	* This mechanism is commonly used to synchronise the application with the tracker.
	*
	* @param vt instance of the VisageTracker that called the function; 
	* this can be used to access further information from the tracker,
	* or for identification if more than one tracker object is used at the same time.
	* @param timeStamp  when tracking, timeStamp returns the value passed to timeStamp argument in VisageTracker::track() method if it is different than -1, otherwise it returns time, in milliseconds, measured from the moment when tracking started.
	*
	* @see VisageTracker::getTrackingData(), FaceData, VisageTracker::attach(), VisageTracker::detach()
	*/
	virtual void Notify(VisageTracker *vt, long timeStamp) = 0;


};

}
#endif // __VisageTrackerObserver_h__

