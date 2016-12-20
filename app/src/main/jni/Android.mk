LOCAL_PATH := $(call my-dir)
$(warning $(LOCAL_PATH))
include $(CLEAR_VARS)
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
VISAGE_LIBS := ../../../../lib/armeabi-v7a
endif
ifeq ($(TARGET_ARCH_ABI),x86)
VISAGE_LIBS := ../../../../lib/x86

endif
LOCAL_MODULE := VisageVision
LOCAL_SRC_FILES := $(VISAGE_LIBS)/libVisageVision.so
include $(PREBUILT_SHARED_LIBRARY)
     
include $(CLEAR_VARS)

 
VISAGE_HEADERS  := ../../../include

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
VISAGE_LIBS := ../../../lib/armeabi-v7a
endif
ifeq ($(TARGET_ARCH_ABI),x86)
VISAGE_LIBS := ../../../lib/x86
endif
       
LOCAL_C_INCLUDES +=  $(VISAGE_HEADERS) $(VISAGE_HEADERS)/libAfm $(VISAGE_HEADERS)/libVRMLParser $(VISAGE_HEADERS)/../source/libVision/swr
LOCAL_MODULE    := VisageWrapper
LOCAL_SRC_FILES := \
	AndroidWrapper.cpp \
	AndroidImageCapture.cpp \
	AndroidCameraCapture.cpp \
	VisageRendering.cpp

LOCAL_SHARED_LIBRARIES := VisageVision	
LOCAL_LDLIBS +=  -L$(VISAGE_LIBS) -L$(/jni) -lVisageVision -lGLESv1_CM -llog -ldl -Wl,--gc-sections  

LOCAL_CFLAGS := -DANDROID_NDK \
				-DDISABLE_IMPORTGL \
				-DANDROID \
				-DVISAGE_STATIC \
				-ffast-math -O2 -funroll-loops -Wno-write-strings

LOCAL_ARM_MODE := arm

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := videokit
LOCAL_SRC_FILES := videokit/fr_enoent_videokit_Videokit.c videokit/ffmpeg.c videokit/ffmpeg_filter.c videokit/ffmpeg_opt.c videokit/cmdutils.c
LOCAL_C_INCLUDES := $(ANDROID_NDK)/sources/ffmpeg-2.2.3
LOCAL_STATIC_LIBRARIES := libavdevice libavformat libavfilter libavcodec libwscale libavutil libswresample libswscale libpostproc
LOCAL_CFLAGS := -Wdeprecated-declarations
ANDROID_LIB := -landroid
LOCAL_LDLIBS += -llog -ljnigraphics -lz
include $(BUILD_SHARED_LIBRARY)
