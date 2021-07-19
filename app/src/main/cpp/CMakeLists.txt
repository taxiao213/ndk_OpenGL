
cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}-DNO_CRYPTO")

add_library(
        native-lib
        SHARED

        native-lib.cpp
        egl/TXEglHelp.cpp
        egl/TXEglThread.cpp
        opengl/TXBaseOpengl.cpp
        opengl/TXOpengl.cpp
        opengl/TXOpenglFilterOne.cpp
        opengl/TXOpenglFilterTwo.cpp
        opengl/TXOpenglFilterYUV.cpp
        audio/TXOpenSLES.cpp
        audio/TXRecordBuffer.cpp
        rtmp/amf.c
        rtmp/hashswf.c
        rtmp/log.c
        rtmp/parseurl.c
        rtmp/rtmp.c
)


target_link_libraries(
        native-lib
        EGL
        GLESv2
        android
        OpenSLES
        log)