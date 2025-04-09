# HFUT-Schedule 聚在工大
![封面](/img/cover.png)

<div align="center">

[![GitHub](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=github&label=GitHub&style=for-the-badge)](https://github.com/Chiu-xaH/HFUT-Schedule/releases/latest)
[![F-Droid](https://img.shields.io/f-droid/v/com.hfut.schedule?logo=fdroid&style=for-the-badge)](https://f-droid.org/packages/com.hfut.schedule)
[![F-Droid](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=gitee&label=Gitee&style=for-the-badge)](https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android)

</div>

## 说明:
适用于合肥工业大学(HFUT)的学校服务聚合APP，查询教务、校园卡、网电费等

🌟 由于开发者是宣城校区23级本科生，**理论**上合肥校区可使用大部分功能，除寝室卫生评分、生活缴费(已经内置慧新易校)、校园网

## UI(v4.12.2)
![截图](/img/ui.jpg)

## 功能设计(v4.13.4.2)
![导图](/img/mindMaster.png)

## 权限：
网络、日历(将聚焦事项作为日程写入日历)、存储(导入导出课程表文件)、相机(洗浴扫码)、通知(下载更新包完成通知)

## 使用：

### 环境要求
搭载**Android 7.0**及以上版本的设备，初次使用要接入互联网(从云端拉取必要信息)

### 初次使用
从开头三个徽章之一下载好APK后，进行安装，保证接入互联网的环境下，填入学号与信息门户密码，点击登录，等待加载完毕(弹出一卡通登陆成功、Community登录成功的Toast后)即可使用

### 后续使用
完全登陆后会获取所需的数据，自动缓存，**由于平台限制，登陆一次教务系统有效期只有3小时且不支持多平台登录**，另两个平台(一卡通和智慧社区)有效期有几十天，智慧社区支持多平台登录，一卡通不支持多平台登录，洗浴不支持多平台登录，在登录教务时会顺便刷新，更多细分说明在APP中子功能界面右上角会有说明按钮

### 软件升级

通过Gitee Release通道分发新版本，在启动时检查更新，如有更新会在首页底栏【选项】显示小红点，选项界面会有红色强调卡片，点击下方更新按钮，100%后授权安装未知应用权限

## [更新日志](docs/UPDATE.md)

## 构建
Kotlin 2.0

Gradle 8.7 + OpenJDK 17

SDK 目标36(Android 16) \ 最低26(Android 7)

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

[Glide](https://github.com/bumptech/glide) 网络图片

[EdDSA Java](https://github.com/str4d/ed25519-java) 加密(供和风天气API使用)

[Konfetti](https://github.com/DanielMartinus/Konfetti) 礼花🎉动画

[Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android) 封装Tesseract4 (供识别图片验证码)

[Bsdiff-Lib](https://github.com/Chiu-xaH/Bsdiff-Lib) 增量更新

## [其他内容(P2)](/docs/OTHER.md)







