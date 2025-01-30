# HFUT-Schedule（聚在工大）
<div align="center">

[![GitHub](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=github&label=GitHub&style=for-the-badge)](https://github.com/Chiu-xaH/HFUT-Schedule/releases/latest)
[![F-Droid](https://img.shields.io/f-droid/v/com.hfut.schedule?logo=fdroid&style=for-the-badge)](https://f-droid.org/packages/com.hfut.schedule)
[![F-Droid](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=gitee&label=Gitee&style=for-the-badge)](https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android)

</div>

## 说明:
作为本校23届学生，初到学校时了解到大家常用的可以查看课表、成绩、考试等信息的是若干微信小程序和网页，平台较多较分散，且冷启动加载速度较慢，功能较少，多数接口失效，使用起来感觉不便，恰本人那时刚学安卓开发，为解决痛点，并作为实战项目，本应用就诞生了，面向宣区校内同学（理论上合肥校区也可用）使用，更快的启动速度、更全面的功能、无广告设计

注意：开发者是宣城校区学生，理论上合肥校区可使用大部分功能，除寝室卫生评分、电费（电费可以去一卡通-慧新易校里充值查询）、校园网（有合肥校区知道自己校园网API可以PR到API文档或发邮件或issue），如果基础功能也无法使用那就没办法了...

## UI(4.12.2版本 与最新版本可能有所差异)
![截图](/img/screenShot.jpg)

## 功能设计(4.12.2版本 与最新版本可能有所差异)
![导图](/img/mindMaster.png)

## 权限：
网络、日历(将聚焦事项作为日程写入日历)、存储(导入导出课程表文件)、相机(洗浴扫码)

## 使用：
### 环境要求
ARM架构，支持64 Bit软件、搭载Android 7.0 （SDK 26）及以上版本的设备，初次使用要接入互联网

### 初次使用
从开头三个徽章之一下载好APK后，进行安装，保证接入互联网的环境下，填入学号与信息门户密码，点击登录，等待加载完毕即可使用。（最好留在课表页面等待加载完毕，因为要后台登录3个平台，有两个Toast弹完后即完全登录...）

### 后续使用
完全登陆后会获取所需的数据，自动缓存，以后可随便断网看课表，登陆一次教务系统有效期只有3小时，另外两个平台（一卡通和智慧社区）有效期有几十天，不需要同学们经常登录刷新，关于更多细分说明在APP中子功能界面右上角会有说明按钮，点击可查看

### 软件升级
如有更新，会在首页底栏【选项】显示小红点，点击此Tab，在选项界面会有标红卡片，提示新版本，点击下方请求更新，等待进度条即可 

## [接口文档](markdown/API.md)
供校内学生们方便参考或学习

## 构建
主语言 Kotlin 2.0

构建 Gradle 8.3 + OpenJDK 17

SDK 目标 33 \ 最低 26 (Android 7.0)

Jetpack Compose 2.0

UI设计 Material You (Material Design 3)

## 开源库(鸣谢)
[OkHttp](https://github.com/square/okhttp) 网络请求

[Retrofit](https://github.com/square/retrofit) 网络请求

[Gson](https://github.com/google/gson) JSON解析

[Jsoup](https://github.com/jhy/jsoup) XML/HTML解析

[Zxing](https://github.com/zxing/zxing) 二维码

[Haze](https://github.com/chrisbanes/haze) 实时模糊(SDK>=33)

[Accompanist](https://github.com/google/accompanist) 用做实现透明状态栏

[Monet](https://github.com/Kyant0/Monet) 莫奈取色(供SDK<32不支持MY取色平替)

[Dagger](https://github.com/google/dagger) Hilt注入,辅助莫奈取色功能

[Glide](https://github.com/bumptech/glide) 网络图片

[EdDSA Java](https://github.com/str4d/ed25519-java) 加密(供和风天气API使用)

[Konfetti](https://github.com/DanielMartinus/Konfetti) 礼花🎉动画

## [更新日志](markdown/UPDATE.md)

## 统计
使用为期3个月(2024-10-07 ~ 2025-01-06)的服务器进行统计

![visitsChart](/img/visitsChart.png)

## 助力开发
如果有功能层面改进或创新、Bug，欢迎在issue提交；如果发现了新的有用的接口，可以在接口文档书写补充，开发者会进行适配
### Material Design 3 设计参考 
[组件](https://m3.material.io/)与[图标](https://fonts.google.com/icons)
#### [Material3 一些自己写的组件与动效](/material3)

## [工具](/tools)

## [联系方式](zsh0908@outlook.com)
zsh0908@outlook.com



