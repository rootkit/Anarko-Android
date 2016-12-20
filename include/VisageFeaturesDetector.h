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


#ifndef __VisageFeaturesDetector_h__
#define __VisageFeaturesDetector_h__

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

using namespace std;

namespace VisageSDK
{

class VisageDetector;
class Candide3Model;

/** Faces and facial features detector implementation.
* 
* This class detects one or more faces and their facial features in an image. The input is an image bitmap or an image file in one of the supported file formats: JPEG, PNG, BMP or PPM.
* The results are, for each detected face, the 3D head pose, the coordinates of facial feature points, e.g. chin tip, nose tip, lip corners etc. and 3D face model fitted to the face.
* The results are returned in one or more FaceData objects, one for each detected face. Please refer to the FaceData documentation for detailed description of returned data.
*
*
* To use the detector, first initialise it by calling the function @ref Initialize(), then call the function @ref detectFacialFeatures()
* to perform facial features detection on the image. 
* Several data files are needed for facial features detection and the path to the folder "bdtsdata" (located in Samples/data) containing these data files must be passed to the initialization function, for example:
*	
* \code
* std::string dataPath("."); //Assuming the current working folder contains data files.	
*
* m_Detector->Initialize(dataPath.c_str());
* \endcode
*
* Additionally, the following files (located in Samples/data) are expected to be located in the same folder as the /bdtsdata folder: candide3.fdp, candide3.wfm, Face Detector.cfg.
* The whole "bdtsdata" folder and 3 additional files must be distributed with an application using VisageFeaturesDetector, and its path passed to the initialization function.
*
* Implemented in libVisageVision.lib
*
* \if IOS_DOXY
* Demonstrated in <a href="../facedetect.html">FaceDetector</a> sample project.
* \elseif MACOSX_DOXY
* Demonstrated in <a href="../facedetect.html">FaceDetector</a> sample project.
* \elseif LINUX_DOXY
* Demonstrated in <a href="../../../Samples/Linux/build/msvc100/VisageDetectorDemo/doc/index.html">FaceDetector</a> sample project.
* \elseif REDHAT_DOXY
* Demonstrated in <a href="../../../Samples/Linux/build/msvc100/VisageDetectorDemo/doc/index.html">FaceDetector</a> sample project.
* \else
* Demonstrated in <a href="../../../Samples/OpenGL/build/msvc140/FaceDetector/doc/index.html">FaceDetector</a> sample project.
* \endif
*
*/
class VISAGE_DECLSPEC VisageFeaturesDetector {

public:

	/** Constructor.
	*	
	*/
	VisageFeaturesDetector();

	/** Destructor.
	*/
	~VisageFeaturesDetector();

	/** Initialise the feature detector. 
	*
	* Several data files are needed for facial features detection and the path to the folder "bdtsdata" (located in Samples/data) containing these data files must be passed to the initialization function, for example:
	*	
	* \code
	* std::string dataPath("."); //Assuming the current working folder contains data files(from "bdtsdata" folder).	
	*
	* m_Detector->Initialize(dataPath.c_str());
	* \endcode
	*
	* Additionally, the following files (located in Samples/data) are expected to be located in the same folder as the /bdtsdata folder: candide3.fdp, candide3.wfm, Face Detector.cfg.
	*
	* The whole "bdtsdata" folder and 3 additional files must be distributed with an application using VisageFeaturesDetector, and its path passed to the initialization function.
	*
	* 
	* @param path the path to the detector data files
	* @return true if successful
	*/
	bool Initialize(const char* path);

	/**
	* Performs faces and facial features detection in a still image.
	* 
	* The algorithm detects one or more faces and their features. The results are, for each detected face, the 3D head pose, gaze direction, eye closure, the coordinates of facial feature points, e.g. chin tip, nose tip, lip corners etc and 3D face model fitted to the face.
	*
	* The results are returned in form of FaceData objects. An array of FaceData objects passed to this method as output parameter should be allocated to maxFaces size. 
	* For example:
	* 
	* \code
	* FaceData* data = new FaceData[maxFaces];
	*
	* n = this->m_Detector->detectFacialFeatures(image, data, maxFaces, minFaceScale);
	* \endcode
	*
	* After this call, n contains the number of faces actually detected. The first n members of the data array are filled with resulting data for each detected face.
	* Please refer to the FaceData documentation for detailed description of returned parameters. If maxFaces is smaller than the number of faces actually present in the image, the function will return only first maxFaces detected faces, starting from top left corner of the image.
	*
	* VsImage is the image storage class similar to IplImage from OpenCV, it has the same structure and members so it can be used like IplImage. Please refer to 
	* OpenCV documentation for details of accessing IplImage data members; the basic members are the size of the image (frame->width, frame->height) and the pointer to the actual pixel data of the image (frame->imageData).
	*
	* @param frame the input image. 
	* @param output pointer to an array of FaceData objects in which the results will be returned.
	* @param maxFaces maximum number of faces to be detected
	* @param minFaceScale scale of smallest face to be searched for, defined as percentage [0-1] of input image size (min(width, height))
	* @show 
	* @return number of detected faces (0 or more)
	*
	* @see FaceData
	*/
	int detectFacialFeatures(VsImage* frame, FaceData* output, int maxFaces = 1, float minFaceScale = 0.1f);

private:

	friend class VisageTracker;
	VisageFeaturesDetector(const char *dataPath);

	FDP* fdps;/*!Pointer to a Facial Definition Point object*/
	FDP* featurePoints2D;

	void calculateFDP(FDP* f, int w, int h, VsMat* vert, bool _3D);
	void setFDPIndices(FDP* f);

	bool fitModelToFace(FDP* input, FaceData* output, VsImage * frame);
	bool initModel(VsImage* frame);

	VisageDetector* detector;
	Candide3Model* model;
	char* cfg;

	FaceData* data;
	int faces;

	bool initialised;

	char m_detectCfgDirPath[300];
};

}
#endif // __VisageFeaturesDetector_h__

