LOCAL_PATH := $(call my-dir)

# Device executable.
# =========================================================

include $(CLEAR_VARS)

LOCAL_MODULE := iperf

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/src

LOCAL_SRC_FILES := $(call all-c-files-under, src)

LOCAL_CFLAGS := -Wno-error -DIPERF_HOST=0

LOCAL_MODULE_TAGS := debug

LOCAL_FORCE_STATIC_EXECUTABLE := true

include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)

LOCAL_MODULE := iperf_server

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/server

LOCAL_SRC_FILES := $(call all-c-files-under, server)

LOCAL_CFLAGS := -Wno-error -DIPERF_HOST=0

LOCAL_MODULE_TAGS := debug

LOCAL_FORCE_STATIC_EXECUTABLE := true

include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)

LOCAL_MODULE := iperf_client

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/client

LOCAL_SRC_FILES := $(call all-c-files-under, client)

LOCAL_CFLAGS := -Wno-error -DIPERF_HOST=0

LOCAL_MODULE_TAGS := debug

LOCAL_FORCE_STATIC_EXECUTABLE := true

include $(BUILD_EXECUTABLE)
# Host executable.
# =========================================================

include $(CLEAR_VARS)

LOCAL_MODULE := iperf_x86

LOCAL_C_INCLUDES := $(LOCAL_PATH)/src

LOCAL_SRC_FILES := $(call all-c-files-under, src)

LOCAL_CFLAGS := -Wno-error -DIPERF_HOST=1

LOCAL_LDFLAGS := -static

LOCAL_MODULE_TAGS := debug

include $(BUILD_HOST_EXECUTABLE)


include $(call first-makefiles-under,$(LOCAL_PATH))
