LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)


LOCAL_SRC_FILES :=  \
		RenderDrawByC.c
		
LOCAL_CFLAGS := -O2 $(INCLUDES) 
		

LOCAL_C_INCLUDES += $(JNI_H_INCLUDE) 

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lm -lGLESv2 -landroid  
LOCAL_MODULE    := libDrawVideoC


include $(BUILD_SHARED_LIBRARY)
