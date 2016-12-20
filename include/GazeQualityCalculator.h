#ifndef GAZEQUALITYCALCULATR_H
#define GAZEQUALITYCALCULATR_H

#include "TrackerGazeCalibrator.h"
#include "ScreenSpaceGazeMapping.h"

namespace VisageSDK
{
	class GazeQuality
	{
		public:
			virtual float calculate() = 0;

			float covariance;
			float trackingPercentage;
			float trackingQuality;
			float headTranslation;
			float headRotation;
	};

	class FrameQuality: public GazeQuality
	{
		public:
			FrameQuality(ScreenSpaceGazeRepository* calibrator, TrainingGazeData& data, float avgCalibTranslation[3], float avgCalibRotation[3]);
			FrameQuality(float covariance, float trackingPercentage, float trackingQuality, float headTranslation, float headRotation);
			float calculate();

	private:
		static const float covarianceThresholds[5];
		static const float trackingPercentageThesholds[5];
		static const float headTranslationThresholds[5];
		static const float headRotationThresholds[5];

		static const int nThresholds;
		static const int nParams;

	};

	class SessionQuality: public GazeQuality
	{
		public:
			SessionQuality();
			float calculate();
			void add(FrameQuality& frameQuality);
			void setCalibrator(ScreenSpaceGazeRepository* calibrator);

		private:
			ScreenSpaceGazeRepository* calibrator;
			int nFrames;
			int nCovFrames;
			int nHeadFrames;
	};

}

#endif