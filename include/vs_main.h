/*M///////////////////////////////////////////////////////////////////////////////////////
//
//  IMPORTANT: READ BEFORE DOWNLOADING, COPYING, INSTALLING OR USING.
//
//  By downloading, copying, installing or using the software you agree to this license.
//  If you do not agree to this license, do not download, install,
//  copy or use the software.
//
//
//                           License Agreement
//                For Open Source Computer Vision Library
//
// Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
// Copyright (C) 2009, Willow Garage Inc., all rights reserved.
// Third party copyrights are property of their respective owners.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
//   * Redistribution's of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//
//   * Redistribution's in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//
//   * The name of the copyright holders may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// This software is provided by the copyright holders and contributors "as is" and
// any express or implied warranties, including, but not limited to, the implied
// warranties of merchantability and fitness for a particular purpose are disclaimed.
// In no event shall the Intel Corporation or contributors be liable for any direct,
// indirect, incidental, special, exemplary, or consequential damages
// (including, but not limited to, procurement of substitute goods or services;
// loss of use, data, or profits; or business interruption) however caused
// and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of
// the use of this software, even if advised of the possibility of such damage.
//
//M*/

#ifndef __VS_OLD_VS_H__
#define __VS_OLD_VS_H__

#if defined(_MSC_VER)
    #define VS_DO_PRAGMA(x) __pragma(x)
    #define __CVSTR2__(x) #x
    #define __CVSTR1__(x) __CVSTR2__(x)
    #define __CVMSVCLOC__ __FILE__ "("__CVSTR1__(__LINE__)") : "
    #define VS_MSG_PRAGMA(_msg) VS_DO_PRAGMA(message (__CVMSVCLOC__ _msg))
#elif defined(__GNUC__)
    #define VS_DO_PRAGMA(x) _Pragma (#x)
    #define VS_MSG_PRAGMA(_msg) VS_DO_PRAGMA(message (_msg))
#else
    #define VS_DO_PRAGMA(x)
    #define VS_MSG_PRAGMA(_msg)
#endif
#define VS_WARNING(x) VS_MSG_PRAGMA("Warning: " #x)

//VS_WARNING("This is a deprecated opencv header provided for compatibility. Please include a header from a corresponding opencv module")

#include "vs_core_core_c.h"
#include "vs_core_core.h"
#include "vs_imgproc_imgproc_c.h"
#include "vs_imgproc_imgproc.h"

#if !defined(VS_IMPL)
#define VS_IMPL extern "C"
#endif //VS_IMPL

#if defined(__cplusplus)
#include "vs_core_internal.h"
#endif //__cplusplus

#endif // __VS_OLD_VS_H_
