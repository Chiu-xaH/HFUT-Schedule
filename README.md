# 聚在工大（HFUT-Schedule）
![封面](/src/img/cover.png)

## 下载
<div align="center">

[![GitHub](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=github&label=GitHub&style=for-the-badge)](https://github.com/Chiu-xaH/HFUT-Schedule/releases/latest)
[![F-Droid](https://img.shields.io/f-droid/v/com.hfut.schedule?logo=fdroid&style=for-the-badge)](https://f-droid.org/packages/com.hfut.schedule)
[![F-Droid](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=gitee&label=Gitee&style=for-the-badge)](https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android)

</div>


## 简介
为合肥⼯业⼤学（HFUT）本科⽣开发的校园服务聚合类 Android 应⽤，覆盖了教学、⽣活、校内服务等 40+ 场景与功能，⽀持离⽹使⽤；使⽤现代的技术栈开发，提供全⾯、简洁、⾼效的使⽤体验。

界面展示（v4.20.1.3,实际以最新版本为准）：

![截图](/src/img/ui.jpg)

## 亮点
界面: 使用着色器、实时模糊等特效，与 Material Design 3 融合，简约高效

动效：自研第二代全局转场动画体系，符合直觉的一镜到底动效

更新：250+版本迭代，平均每周至少更新一次，带来新的功能、重构及优化

性能：冷启动迅速，开屏即展示关键信息；内存与存储占用适中

## 开始使用
### 要求
Android 8.0及以上，接入互联网
### 初次使用
保证接入互联网的环境下，填入学号与信息门户密码，登录，等待出现加载完成(底栏由暗变正常)，即可使用
### 刷新登陆
登陆后会获取所需的数据，自动缓存(例如课程表教务源、课程汇总等)，由于平台限制，有些平台不支持持久登录，在登录时会将过期的平台选择性地刷新
### 软件升级
在启动时自动检查更新，如有更新会在首页底栏【选项】显示小红点，选项界面会有下载提示，下载完成后点击安装，授权安装未知应用权限即可

## 文档库
### [开发文档](docs/Developer.md) (正在完善)

### [更新日志](docs/update)

### [统计报表](/docs/CHART.md)

用户量及日流量，通过Supabase平台托管，安全统计，无隐私数据，不定期更新

## [联系方式](zsh0908@outlook.com)
开发者: zsh0908@outlook.com

## [其他工具](/tools)
[图片验证码训练模型](/tools/Captcha-Ocr)

[校园网登录](/tools/Login-Web-Python)

[WebVpn](tools/WebVpn)

[PC版(集成WebVpn、校园网等工具)](/tools/Lite-For-PC) (待开发)

## 鸣谢
### 第三方库
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

[DeviceCompat](https://github.com/getActivity/DeviceCompat) 设备识别 (供判断鸿蒙NEXT环境)

### 从本App解耦的库

[DiffUpdater](https://github.com/Chiu-xaH/DiffUpdater) 增量更新

[SharedNav](https://github.com/Chiu-xaH/SharedNav) 页面管理、容器共享、浮窗体系

[Mirror-Android](https://github.com/Chiu-xaH/Mirror-Android) 镜面效果（着色器）

### 开源项目
[holiday-cn](https://github.com/NateScarlet/holiday-cn) 节假日数据源

[webvpn-dlut](https://github.com/ESWZY/webvpn-dlut) WebVpn转换

[Tesseract](https://github.com/tesseract-ocr/tesseract) 用于训练OCR识别验证码的[基础模型](https://github.com/tesseract-ocr/tessdata)

[Supabase](https://github.com/supabase/supabase) 托管供提供数据库

### 开源社区
初期参考了Space课表(微信小程序)的一些功能设计，后期参考了若干开源App(例如 [师韵-SmartHNU](https://github.com/JiaLiFuNia/SmartHNU))，还有对一些移动操作系统的借鉴，在此不一一列举了

感谢其他高校开发者对本项目的肯定与参考，在此不一一列举了

若干开发者和用户的帮助：
- linsui 帮助上架F-Droid
- James-Zhang2 提供GPA评定数据源
- tinyvan,Today1337,zxbmmmmmmmmm,Junpgle提交RR助力开发
- 其他用户帮助推广、通过邮件、issue等提供功能建议与反馈等，在此不一一列举了

#### Pull Request 须知（参与本项目）

##### 模块分布：
- 主仓：本仓库，约8w行代码，MVVM架构，Compose作为UI框架，体量属中型App，可善用IDE的搜索功能进行定位。
- 网络模块：本仓库network模块，放置网络接口
- 增量更新：DiffUpdater库，一般无需修改
- KMP跨平台仓：暂未开始，计划筹备中
- 基础能力（骨架）：如需修改页面导航、转场动效以及容器共享等功能，请转到[SharedNav](https://github.com/Chiu-xaH/SharedNav)库。

##### 规范：
- 编程语言无特殊规范，技术栈无特殊规范，如需额外引入依赖，请与开发者提前协商并征求同意。
- 代码风格无特殊规范，按自己习惯即可，但必须对高频敏感区块使用 `try { } catch { e : Exception -> LogUtil.error(e) }`,防止应用轻易发生Crash。
- 代码文件的放置位置无特殊规范，按需放置即可，后续开发者会不定期整理。
- 对于新引入资源，string 文案直接在代码中硬编码即可，方便快速开发，后续开发者会做不定期迁移到`string.xml`中；drawable 素材参考其余素材的命名以及位置，放置即可。
- commit 的标题建议与其他提交风格一致，例如 新特性、修改优化等涉及代码的改动使用feature:XXX,修复代码存在的问题使用bugfix:XXX,重构代码使用refactor:XXX,更新非代码内容使用update:XXX…… 时间相近、功能相同的追加 commit 建议使用 `git commit --amend`合并为一个 commit。
- 如涉及 UI&UX，建议与本应用内部风格契合，尽量减少割裂感。
- 建议在开发过程中，将app模块的build.gradle文件中的versionName后缀加上Dev，例如“4.20 Dev”，可被判定为内部开发版本，不会触发多余的上报埋点。
- 一次PR建议应专注于一个区块，遵循最小改动化，对于多个不同的改动，可以考虑提若干个PR。
- 对于Bug修复或者只是小改动（优化、微调等），可直接提PR。若大范围改动已有正常功能时，请与开发者提前协商并征求同意，因为有可能此改动不在更新计划内，会打乱原定计划，只能将其放置到侧分支中。
- 不要信任AI生成的代码，做好质量把关（Review），对于Android，极易发生意想不到的情况，轻则发热卡顿，重则崩溃闪退，务必谨慎。

##### 自测：
- 若改动较小且集中在一处，可酌情简单局部测试即可。
- 若涉及较大范围的改动，建议选几个关键的安卓版本，且分别在正常升级、升级清除应用数据的情况下做验证。
- 注意避免内存泄漏风险（跟随生命周期及时释放）与ANR风险（合理使用初始值+协程调度器），Global协程推荐只在全局生命周期的地方使用。
- 若后续发布后发生了问题属于正常情况，因个人的测试范围有限以及Android生态广泛，所以更要严格遵守加try catch的原则。

##### 提交：
- 经过自测后，可向 dev 分支提交，stage 分支仅用于已发行版本；
