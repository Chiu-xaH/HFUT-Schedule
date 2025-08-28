# 聚在工大
![封面](/src/img/cover.png)

## 下载

<div align="center">

[![GitHub](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=github&label=GitHub&style=for-the-badge)](https://github.com/Chiu-xaH/HFUT-Schedule/releases/latest)
[![F-Droid](https://img.shields.io/f-droid/v/com.hfut.schedule?logo=fdroid&style=for-the-badge)](https://f-droid.org/packages/com.hfut.schedule)
[![F-Droid](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=gitee&label=Gitee&style=for-the-badge)](https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android)

</div>

## 简介
适用于合肥工业大学(HFUT)的学校服务聚合APP，查询教务、校园卡、网电费等

🌟 由于开发者是宣城校区23级本科生，**理论**上合肥校区可使用，会缺失一些功能

## 界面(v4.12.2)
![截图](/src/img/ui.jpg)·

## 功能(v4.13.4.2)
![导图](/src/img/mindMaster.png)

## 权限
网络、日历(将聚焦事项作为日程写入日历)、存储(导入导出课程表文件)、相机(洗浴扫码)、通知(下载更新包完成通知)

## 使用

### 环境要求
**在读本科生，Android 8.0及以上，初次使用要接入互联网(从云端拉取必要信息)**

### 初次使用
保证接入互联网的环境下，填入学号与信息门户密码，点击登录，等待加载完毕(弹出一卡通登陆成功、Community登录成功的Toast后)即可使用

### 后续使用
完全登陆后会获取所需的数据，自动缓存，**由于平台限制，登陆一次教务系统有效期只有1小时且不支持多平台登录**，另两个平台(一卡通和智慧社区)有效期有几十天，智慧社区支持多平台登录，一卡通不支持多平台登录，洗浴不支持多平台登录，在登录教务时会顺便刷新，更多细分说明在APP中子功能界面右上角会有说明按钮

### 软件升级

(应用内通过Gitee Release通道分发新版本，**会存在限速**，因为下载的人多)在启动时检查更新，如有更新会在首页底栏【选项】显示小红点，选项界面会有下载提示，100%后授权安装未知应用权限即可

## [更新日志](docs/UPDATE.md)

## 构建
Kotlin v2.1.20 + Gradle v8.11.1 + Java v11

Android Gradle Plugin (AGP) v8.9.2 + OpenJDK v17.0.0.1

SDK: 目标36(Android 16.0) \ 最低26(Android 8.0)

Jetpack Compose v1.7.6

UI: Material You (Material Design 3) v1.4.0-alpha12

IDE: Android Studio Meerkat

## 鸣谢
### 库/依赖
[OkHttp](https://github.com/square/okhttp) 网络请求

[Retrofit](https://github.com/square/retrofit) 网络请求

[Gson](https://github.com/google/gson) JSON解析

[Jsoup](https://github.com/jhy/jsoup) XML/HTML解析

[Zxing](https://github.com/zxing/zxing) 二维码

[Haze](https://github.com/chrisbanes/haze) 实时模糊

[Accompanist](https://github.com/google/accompanist) 用做实现透明状态栏

[Glide](https://github.com/bumptech/glide) 网络图片

[EdDSA Java](https://github.com/str4d/ed25519-java) 加密(供和风天气API使用)

[Konfetti](https://github.com/DanielMartinus/Konfetti) 礼花🎉动画

[Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android) 封装Tesseract (供识别图片验证码)

[MaterialKolor](https://github.com/jordond/MaterialKolor) 自定义取色

[Reorderable](https://github.com/Calvin-LL/Reorderable) 列表拖拽


[Bsdiff-Lib](https://github.com/Chiu-xaH/Bsdiff-Lib) 增量更新(自己)

[Compose-Transition-Sample](https://github.com/Chiu-xaH/Compose-Transition-Sample) 转场动画(自己)

### 工具
[holiday-cn](https://github.com/NateScarlet/holiday-cn) 节假日

[Consumption-Forecast](https://github.com/Chiu-xaH/Consumption-Forecast) 饭卡消费预测(自己)

[webvpn-dlut](https://github.com/ESWZY/webvpn-dlut) WENVPN URL转换

## [联系方式](zsh0908@outlook.com) zsh0908@outlook.com

## [统计报表](/docs/CHART.md)

## [其他内容(P2)](/docs/OTHER.md)

[![Star history chart](https://api.star-history.com/svg?repos=Chiu-xaH/HFUT-SChedule&type=Date)](https://www.star-history.com/#Chiu-xaH/HFUT-Schedule&Date)






