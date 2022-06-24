cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}-DNO_CRYPTO")

set(rootPath ${CMAKE_SOURCE_DIR}/../../jniLibs/${CMAKE_ANDROID_ARCH_ABI})

message(rootPath)

add_library(libfreetype SHARED IMPORTED)

set_target_properties(libfreetype PROPERTIES IMPORTED_LOCATION ${rootPath}/libfreetype.so)

include_directories(
        include/freetype2
        include/freetype2/freetype
        include/freetype2/freetype/config
        glm
)

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
        rtmp/RtmpPush.cpp
        rtmp/RtmpQueue.cpp
        callback/TXCallBack.cpp
)


target_link_libraries(
        native-lib
        libfreetype
        EGL
        GLESv2
        GLESv3
        android
        OpenSLES
        log)