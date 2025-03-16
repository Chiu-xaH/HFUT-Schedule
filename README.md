# HFUT-Schedule（聚在工大）
<div align="center">

[![GitHub](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=github&label=GitHub&style=for-the-badge)](https://github.com/Chiu-xaH/HFUT-Schedule/releases/latest)
[![F-Droid](https://img.shields.io/f-droid/v/com.hfut.schedule?logo=fdroid&style=for-the-badge)](https://f-droid.org/packages/com.hfut.schedule)
[![F-Droid](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=gitee&label=Gitee&style=for-the-badge)](https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android)

</div>

## 注意:

开发者是宣城校区23级本科生，**理论**上合肥校区可使用大部分功能，除寝室卫生评分、生活缴费（已经内置慧新易校，可以充值查询）、校园网（有合肥校区知道自己校园网API可以PR到API文档或发邮件或issue）

## UI(4.12.2版本 与最新版本可能有所差异)
![截图](/img/ui.jpg)

## 功能设计(4.13.4.2版本 与最新版本可能有所差异)
![导图](/img/mindMaster.png)

## 权限：
网络、日历(将聚焦事项作为日程写入日历)、存储(导入导出课程表文件)、相机(洗浴扫码)、通知(下载更新包完成通知)

## 使用：

### 环境要求
ARM架构，支持64 Bit软件、搭载**Android 7.0 (SDK 26)**及以上版本的设备，初次使用要接入互联网(从云端拉取必要信息)

### 初次使用
从开头三个徽章之一下载好APK后，进行安装，保证接入互联网的环境下，填入学号与信息门户密码，点击登录，等待加载完毕即可使用。（最好留在课表页面等待加载完毕，因为要后台登录3个平台，有两个Toast弹完后即完全登录...）

### 后续使用
完全登陆后会获取所需的数据，自动缓存，**由于平台限制，登陆一次教务系统有效期只有3小时**，另两个平台（一卡通和智慧社区）有效期有几十天，在登录教务时会顺便刷新，更多细分说明在APP中子功能界面右上角会有说明按钮，点击可查看

### 软件升级

软件通过Gitee Release在启动时检查更新，如有更新会在首页底栏【选项】显示小红点，选项界面会有红色强调卡片，点击下方更新按钮，100%后授权安装未知应用权限，即可 

## [更新日志](markdown/UPDATE.md)


## 构建
Kotlin 2.0

Gradle 8.7 + OpenJDK 17

SDK 目标 36 \ 最低 26 (Android 7.0)

Jetpack Compose 1.7

UI: Material You (Material Design 3)

## 开源库(鸣谢)
[OkHttp](https://github.com/square/okhttp) 网络请求

[Retrofit](https://github.com/square/retrofit) 网络请求

[Gson](https://github.com/google/gson) JSON解析

[Jsoup](https://github.com/jhy/jsoup) XML/HTML解析

[Zxing](https://github.com/zxing/zxing) 二维码

[Haze](https://github.com/chrisbanes/haze) 实时模糊(SDK>=33)

[Accompanist](https://github.com/google/accompanist) 用做实现透明状态栏

[//]: # ([Monet]&#40;https://github.com/Kyant0/Monet&#41; 莫奈取色&#40;供SDK<32不支持MY取色平替&#41;)

[//]: # ([Dagger]&#40;https://github.com/google/dagger&#41; Hilt注入,辅助莫奈取色功能)

[Glide](https://github.com/bumptech/glide) 网络图片

[EdDSA Java](https://github.com/str4d/ed25519-java) 加密(供和风天气API使用)

[Konfetti](https://github.com/DanielMartinus/Konfetti) 礼花🎉动画

[Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android) 封装Tesseract4 (供识别图片验证码)

## [接口文档](markdown/API.md)
供校内学生们方便参考或学习

## 统计
使用为期3个月(2024-10-07 ~ 2025-01-06)的服务器进行统计

![visitsChart](/img/visitsChart.png)

## 助力开发
如果有功能层面改进或创新、Bug，欢迎在issue提交；如果发现了新的有用的接口，可以在接口文档书写补充，开发者会进行适配

## [联系方式](zsh0908@outlook.com) zsh0908@outlook.com

## 其他

### Material Design 3 设计参考 
[组件](https://m3.material.io/)与[图标](https://fonts.google.com/icons)

#### [Material3 一些自己写的组件与动效](/material3)

### [工具](/tools)





