# 聚在工大
![封面](/src/img/cover.png)

## ⬇️ 下载
<div align="center">

[![GitHub](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=github&label=GitHub&style=for-the-badge)](https://github.com/Chiu-xaH/HFUT-Schedule/releases/latest)
[![F-Droid](https://img.shields.io/f-droid/v/com.hfut.schedule?logo=fdroid&style=for-the-badge)](https://f-droid.org/packages/com.hfut.schedule)
[![F-Droid](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=gitee&label=Gitee&style=for-the-badge)](https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android)

</div>

## ✨ 亮点
界面: 使用现代化技术栈开发；将实时模糊元素与Material Design 3融合，既有统一的风格、规范，又有独特的设计

动效：构建[全局转场动画体系](https://github.com/Chiu-xaH/Compose-Transition-Sample)，遵循一镜到底、收放自如的原则

更新：200+版本迭代，平均每周都有新更新，带来新的功能、重构及优化

性能：冷启动迅速，开屏即展示关键信息(聚焦)；内存与存储占用适中

## 🔶 简介
适用于合肥工业大学(HFUT)本科生的校园服务聚合Android App，采用现代化技术栈开发，内含查询教务、校园卡、网电费等40+功能

## 📱 界面
v4.17.7.1,实际以最新版本为准

![截图](/src/img/ui.jpg)·

## 📕 使用
- 要求：**Android 8.0及以上，接入互联网**
- 初次使用：保证接入互联网的环境下，填入学号与信息门户密码，登录，等待出现加载完成(底栏由暗变正常)，即可使用 
- 刷新登陆：登陆后会获取所需的数据，自动缓存(例如课程表教务源、课程汇总等)，**由于平台限制，登陆一次教务系统有效期只有1小时且不支持多平台登录**，一共刷新9个平台，在登录时会将过期的平台选择性地刷新 
- 软件升级：在启动时自动检查更新，如有更新会在首页底栏【选项】显示小红点，选项界面会有下载提示(有三个通道供选择，分别为Gitee下载全量包、Gitee下载增量包、Github下载最新版本，选任意即可)(Gitee会存在限速约600KB/S，因为下载的人多)100%后点击安装，授权安装未知应用权限即可 
- 不同系统上的差异：并未所有功能在机型上可用，因Android版本、厂商定制UI差异而不同，主要是在UI层面，不影响任何实用性功能，详情查看APP的 选项-维护关于-功能功能性支持 或 [这里](/docs/CONTRAST.md)
- 校区本土化差异：由于开发者是宣城校区本科生，合肥校区也可使用，会缺失一小部分特定功能

## 💻 构建
OpenJDK v17.0.0.1

SDK: 目标36(Android 16.0) \ 最低26(Android 8.0)

## 🫶 鸣谢
### 第三方依赖
[OkHttp](https://github.com/square/okhttp) 网络请求

[Retrofit](https://github.com/square/retrofit) 网络请求

[Gson](https://github.com/google/gson) JSON解析

[Jsoup](https://github.com/jhy/jsoup) XML/HTML解析

[Zxing](https://github.com/zxing/zxing) 二维码

[Haze](https://github.com/chrisbanes/haze) 层级模糊

[Accompanist](https://github.com/google/accompanist) 扩展工具包

[Glide](https://github.com/bumptech/glide) 图片

[EdDSA Java](https://github.com/str4d/ed25519-java) 加密(供和风天气API使用)

[Konfetti](https://github.com/DanielMartinus/Konfetti) 礼花动画

[Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android) 封装Tesseract (供识别图片验证码)

[MaterialKolor](https://github.com/jordond/MaterialKolor) 取色

[Reorderable](https://github.com/Calvin-LL/Reorderable) 列表拖拽

[LeakCanary](https://github.com/square/leakcanary) 内存泄漏工具

[AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) 液态玻璃

[Bsdiff-Lib](https://github.com/Chiu-xaH/Bsdiff-Android-Library) 增量更新(自己)

[Navigation-Transition-Share](https://github.com/Chiu-xaH/Compose-Transition-Sample) 转场动画(自己)

[Mirror-Android](https://github.com/Chiu-xaH/Mirror-Android) 镜面效果(自己)

### 开源项目
[holiday-cn](https://github.com/NateScarlet/holiday-cn) 节假日数据源

[webvpn-dlut](https://github.com/ESWZY/webvpn-dlut) WebVpn转换

[Tesseract](https://github.com/tesseract-ocr/tesseract) 用于训练OCR识别验证码的[基础模型](https://github.com/tesseract-ocr/tessdata)

### 开源社区
初期参考了Space课表(微信小程序)的一些功能设计，后期参考了若干开源App，还有对一些操作系统的借鉴，在此不一一列举了

感谢其他高校开发者对本项目的肯定与参考，在此不一一列举了

linsui帮助上架F-Droid

若干用户的帮助
- James-Zhang2提供GPA评定数据源
- tinyvan提交RR修复Bug
- 其他用户帮助推广、通过邮件、issue等提供反馈和建议等，在此不一一列举了

## 📑 [更新日志](docs/update)

## 📊 [统计报表](/docs/CHART.md)

## 📧 [联系方式](zsh0908@outlook.com) zsh0908@outlook.com

## ⚙️ [一些工具](/tools)
[图片验证码训练模型](/tools/Captcha-Ocr) [所有培养方案爬取](tools\All-Programs-Get-Python) [校园网登录](/tools/Login-Web-Python) [WebVpn](tools/WebVpn) [用户流量统计](/tools/SQL-Chart-Python) [PC版(集成WebVpn、校园网等工具)(待开发)](/tools/Lite-For-PC)

## 🫥 未来适配

开发者将在2027年毕业，App大部分功能都是端侧处理，可以独立运行，如果学校抽风了换接口，可以联系我，应该可以修，或者有能力者提交PR

- **低于Android 8的设备**有使用需求可以**联系我**，可以向下适配
- **鸿蒙NEXT**可以用**卓易通**运行本App
- **iOS**，理解iOS用户的呼声，我自己主力机就是苹果，但是我没苹果本子，就为了开发一个App太贵了... iOS在大学某些方面不如安卓便利，建议**搞一台安卓**，日常很方便的
- **桌面端**，电脑用学校的网页体验也很好了，非要用也是可以的，Windows 10+可以用**WSA**运行本App，后续可能会出PC版集成一些WebVpn、校园网功能
- 唯一的跨平台适配可能性交给**Kotlin/Compose Multiplatform**，这是最低成本的方案了，可以将现有Android项目改造成跨平台项目，但是这其中需要完全重写网络层，由Retrofit&OkHttp换为Ktor，而且逻辑层很多内容都需要做分平台适配，好在这项技术可以让本App已有的Compose写的UI能够跨平台，UI层改动小一些






