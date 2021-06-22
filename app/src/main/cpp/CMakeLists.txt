
cmake_minimum_required(VERSION 3.4.1)

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
        )


target_link_libraries(
        native-lib
        EGL
        GLESv2
        android
        log)