# EasyPR_Android
本程序基于公网已经有的EasyPR_Android(https://github.com/linuxxx/EasyPR_Android)，结合新版的EasyPR1.6alpha和OpenCV3.2.0进行移植。
开发环境为Android Studio 2.3.3.

#说明
1. 程序直接集成了OpencvNative 3.2.0库，无需下载OpenCV。
2. 本程序直接将模型文件放置在res/raw目录下，在运行时自行拷贝到手机文件中，无需模型文件的手动放置。
