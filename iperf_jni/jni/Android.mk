LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := iperf3
LOCAL_SRC_FILES :=  iperf/src/cjson.c            \
					iperf/src/iperf_api.c        \
					iperf/src/iperf_client_api.c \
					iperf/src/iperf_error.c      \
					iperf/src/locale.c     \
					iperf/src/iperf_sctp.c       \
					iperf/src/iperf_server_api.c \
					iperf/src/iperf_tcp.c        \
					iperf/src/iperf_udp.c        \
					iperf/src/iperf_util.c       \
					iperf/src/main.c             \
					iperf/src/net.c              \
					iperf/src/tcp_info.c         \
                    iperf/src/tcp_window_size.c  \
                    iperf/src/timer.c            \
                    iperf/src/units.c
# 这3个 t_ 开头的文件是测试用的，不用包含，否则会有多个main方法入口
#					iperf/src/test/t_timer.c     \
#					iperf/src/test/t_units.c     \
#					iperf/src/test/t_uuid.c
LOCAL_CFLAGS += -pie -fPIE -fPIC -s
LOCAL_C_INCLUDES += $(LOCAL_PATH)/iperf/src
include $(BUILD_EXECUTABLE)
