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

## [接口文档](markdown/API.md)
供校内学生们方便参考或学习

## 助力开发
如果有功能层面改进或创新、Bug，可在issue提交

如果发现了新的有用的接口，可以在接口文档书写补充，开发者会进行适配

如果会安卓开发就更好了，用的是MVVM架构(嘴下留情，我一些类、函数名字起确实奇怪，有的代码还比较史，写的时候刚学完安卓开发基础，那时候连Java都不怎么会，真没考虑到扩展性，现在重构也没时间，能用就行😂)
### 逻辑层
在APP/MyApplication中填写地址，在logic/network/api/XXXService中注册接口，在logic/network/ServerCreator中实现，在LoginSuccessViewModel中实现接口方法，在UI层调用接口
### 解析层
使用Observer+LiveData监听响应数据，定义Data Class，在getXXX(vm)方法中使用Jsoup/Gson接受数据，处理并返回
### 界面层
UI层异步加载，编写UI逻辑
#### UI设计建议 
##### 优先用声明式的Compose，而不是XML

##### Material Design 3 [组件](https://m3.material.io/)与[图标](https://fonts.google.com/icons)

##### UiUtils放置了若干预置组件

##### 卡片与列表的使用，卡片用于可点击操作，列表多数情况用于展示、引导，界面拥挤时可使用列表 卡片圆角、阴影统一且合理

##### 界面三段式(顶栏 主体 底栏) 主体要求可滚动于上下栏之下 顶栏右侧装填功能按钮 底栏切换不同的功能区 上下层级适配实时模糊

##### 界面层序(半覆盖 全覆盖 二级界面 同级翻页)

###### 半覆盖(中间弹窗 底部上弹卡片) 适用于小操作 原界面压暗，层次分明
###### 全覆盖 二级界面 适用于功能区块较多或数据较多
###### 翻页 同级别页面进行平移，无上下层之分

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

[Haze](https://github.com/chrisbanes/haze) 实时模糊

[Accompanist](https://github.com/google/accompanist) 透明状态栏

[Monet](https://github.com/Kyant0/Monet) 莫奈取色(供SDK不支持M3取色平替)

[Dagger](https://github.com/google/dagger) Hilt注入

[Glide](https://github.com/bumptech/glide) 图片

[EdDSA Java](https://github.com/str4d/ed25519-java) 加密(供和风天气API使用)

[//]: # ([AMap]&#40;https://developer.amap.com/api/android-sdk&#41; 高德地图SDK)

## [更新日志](markdown/UPDATE.md)

## [联系方式](zsh0908@outlook.com)
zsh0908@outlook.com



