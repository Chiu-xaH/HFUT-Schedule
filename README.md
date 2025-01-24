# HFUT-Schedule（聚在工大）

[<img src="https://f-droid.org/badge/get-it-on-zh-cn.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/packages/com.hfut.schedule)

## 说明:
作为本校23届学生，初到学校时了解到大家常用的可以查看课表、成绩、考试等信息的是若干微信小程序和网页，平台较多较分散，且冷启动加载速度较慢，功能较少，多数接口失效，使用起来感觉不便，恰本人那时刚学安卓开发，为解决痛点，并作为实战项目，本应用就诞生了，面向宣区校内同学（理论上合肥校区也可用）使用，更快的启动速度、更全面的功能、无广告设计

注意：开发者是宣城校区学生，理论上合肥校区可使用大部分功能，除寝室卫生评分、电费（电费可以去一卡通-慧新易校里充值查询）、校园网（有合肥校区知道自己校园网API可以PR到API文档或发邮件或issue），如果基础功能也无法使用那就没办法了...

## UI展示(4.12.2版本 与最新版本可能有所差异)
![截图](/img/screenShot.jpg)
### [Material3 一些自己写的组件与动效](/%e7%bb%84%e4%bb%b6%e5%8f%8a%e5%8a%a8%e7%94%bb%e8%ae%be%e8%ae%a1)

## 功能设计(4.12.2版本 与最新版本可能有所差异)
![导图](/img/mindMaster.png)

## 权限：
1.日历（将事项作为日程写入日历）

2.存储（导入导出课程表文件）

3.相机（洗浴扫码）

## 使用：
### 环境要求
ARM架构，支持64 Bit软件、搭载Android 7.0 （SDK 26）及以上版本的设备，初次使用要接入互联网

### 初次使用
下载好APK安装包后，进行安装，保证接入互联网的环境下，填入学号与信息门户密码，点击登录，等待加载完毕即可使用。（最好留在课表页面等待加载完毕，因为要后台登录3个平台，有两个Toast弹完后即完全登录，友友们能不能稍作等待，登陆这一次以后几十天就不用了...）

[<img src="https://f-droid.org/badge/get-it-on-zh-cn.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/com.hfut.schedule)

### 后续使用
完全登陆后获取所需的数据，会自动进行本地缓存，以后可随便断网看课表，登陆一次教务系统有效期只有3小时，另外两个平台（一卡通和智慧社区）有效期有足足几十天，不需要同学们经常登录刷新哈，关于更多细分说明在APP中子功能界面右上角会有说明按钮，点击可查看，有不懂的或者有Bug提交issue或发邮件

### 软件升级
如有更新，会在首页底栏【选项】显示小红点，点击此Tab，在选项界面会有标红卡片，提示新版本，点击下方请求更新，等待进度条即可 


## [接口文档](markdown/API.md)
供校内学生们方便参考或学习

## 构建
主语言 Kotlin 2.0.0 + Java

构建 Gradle 8.3 + JDK 1.8

SDK 目标 33 \ 最低 26 (Android 7.0)

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

鸣谢在安卓学习之路与开发实战上助我成长的书籍 :《第一行代码-Android》第三版 郭霖著、《HTTP 抓包实战》肖佳著

## [更新日志](markdown/UPDATE.md)

## 统计
使用为期3个月(2024-10-07 ~ 2025-01-06)的服务器进行统计

![visitsChart](/img/visitsChart.png)

截止到2025-01-03 23:45 统计总代码行数为356XX行（只算业务代码，且不包括空行）

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

## [联系方式](zsh0908@outlook.com)
zsh0908@outlook.com



