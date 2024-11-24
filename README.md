# HFUT-Schedule

## 说明:
作为本校23届学生，初到学校时提前了解到大家常用的可以查看课表、成绩、考试等信息的是一个微信小程序，但小程序缺乏有效维护，冷启动加载速度较慢，功能较少，多数功能接口已失效了，使用起来感觉不便，恰本人此时刚学安卓开发，于是为解决需求，并作为实战项目，本应用就诞生了，面向校内同学（理论上合肥校区也可用）使用，更快的启动速度、更全面的功能、无广告设计
## UI展示(4.12.2版本 与最新版本可能有所差异)
![截图](/img/screenShot.jpg)
## 功能设计(4.12.2版本 与最新版本可能有所差异)
![导图](/img/mindMaster.png)
## 权限：
1.网络（默认允许权限）

1.日历（将事项作为日程写入日历）

2.存储（导入导出课程表文件）

3.相机（洗浴扫码）
## 使用：
### 环境要求
ARM架构，支持64 Bit软件、搭载Android 5.0 （SDK 24）以上版本的系统设备，要求设备已接入互联网
### 安装与使用
下载好APK安装包后，进行安装，保证接入互联网的环境下，填入学号与信息门户密码，点击登录，等待加载完毕即可使用。
## [接口开发文档](https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/markdown/API.md)
供他人学习，或后届学生开发
## 构建
主语言 Kotlin 2.0.0 + Java

构建 Gradle 8.3 + JDK 1.8

SDK 目标 33 \ 最低 24 (Android 5.0)

UI构建 Jetpack Compose 2.0.0

UI设计 Material You (Material Design 3) + Material Design
## 开源库(鸣谢)
[OkHttp](https://github.com/square/okhttp) 网络请求

[Retrofit](https://github.com/square/retrofit) 网络请求

[Gson](https://github.com/google/gson) JSON处理

[Haze](https://github.com/chrisbanes/haze) 实时模糊UI

[Accompanist](https://github.com/google/accompanist) 透明状态栏

[Monet](https://github.com/Kyant0/Monet) 莫奈取色(供SDK不支持M3取色平替)

[Dagger](https://github.com/google/dagger) Hilt注入

[AMap](https://developer.amap.com/api/android-sdk) 高德地图SDK
## [更新日志](https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/markdown/UPDATE.md)
## [联系方式](zsh0908@outlook.com)
zsh0908@outlook.com



