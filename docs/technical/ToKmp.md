# 聚在工大iOS适配设计

## 背景
外界呼声
## 条件
1. Mac ×
2. 时间充裕，无紧要事 ×
## 方案1：原生iOS开发 ×
不行，不会 Swift，而且后期打算适配 PC 端
## 方案2：新建Kotlin单端iOS项目 ×
维护成本高，每次有更新还要同步 iOS 仓
## 方案3：改造现有Android项目为KMP √
### 前期铺垫（必要条件）
1. mirror 模块迁移回主仓 (已完成)
2. 探索 Skia shader 在 KMP 的适配写法（参考 AndroidLiquidGlass 库），并适配 KMP SharedNav  (已完成)
3. 所有页面管理归一化(Card、Shower、Xwx、Supabase 回归 SharedNav 管理) ，WebView、Fix 仍保持，WidgetConfig 删掉，Success 并入主逻辑，对 Starter 类进行重构  (正在进行,除 Success 其余已完成)
4. 模块整理：shared 模块改名 common-logic，common 模块改名 common-ui  (已完成)
5. 模块整理：model 整理（定规矩，只有顶层数据类带Response/Request后缀，非顶层的数据类如果被其他Response/Request共用则独立文件，否则放在所属Response/Request的文件中。）新建 RepoInf 
6. 模块整理：新建 network-api 模块，承载 repo 和解析层。network 改成 network-core ，model移入 
7. 彻底去掉 SP (正在进行，剩余60+)
8. 彻底去掉 LiveData (正在进行，剩余16)
### 开始改造
1. 改造 Gradle，彻底弃用 Groovy 使用 Kotlin
示例： 
```Kotlin
// build.gradle
plugins {
    id 'org.jetbrains.kotlin.multiplatform'
    id 'com.android.kotlin.multiplatform.library'   // 你 version catalog 里已有
    id 'org.jetbrains.compose'                       // 如果有 Compose
}

kotlin {
    androidLibrary {
        namespace = "com.hfut.schedule.network"
        compileSdk = 36
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.retrofit)
                implementation(libs.okhttp)
                implementation(libs.gson)
                implementation(libs.jsoup)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
    }
}
```
1. 所有依赖中，能适配 KMP 的，换为**同版本号**的 KMP 依赖。不能适配 KMP 的，如果是自己的库就适配，不是自己的库就在iOS端找平替库，独自实现，不要影响到安卓端
2. 传统 View 替换为 Compose，不能替换的各自实现（目前大头就剩 WebView) 
3. Compose 中使用安卓特有的地方都需要重新封装 except fun，例如 LocalContext.current LocalActicity.current LocalConfiguration.current
示例：
```Kotlin

// commonMain

@Composable
expect fun LocalPlatformContext() : PlatformContext

expect class PlatformContext

expect fun getPackageName(context: PlatformContext) : String


@Composable
@Preview
fun App() {
    val context = LocalPlatformContext()
    val packageName = getPackageName(context)

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
        ) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    showContent = !showContent
                }
            ) {
                Text(packageName)
            }
        }
    }
}

// androidMain

@Composable
actual fun getPlatformContext() : PlatformContext {
    val context = LocalContext.current
    return PlatformContext(context)
}

actual class PlatformContext(val context: Context)

actual fun getPackageName(context: PlatformContext) : String {
    return context.context.packageName
}

// iosMain

@Composable
actual fun getPlatformContext() : PlatformContext {
    return PlatformContext()
}

actual class PlatformContext

actual fun getPackageName(context: PlatformContext): String {
    return NSBundle.mainBundle.bundleIdentifier ?: "unknown"
}
```
4. 安卓 Resourses 迁移为 Compose Resourses
5. 网络层安卓端保持不变 Retrofit/OkHttp+Gson+Jsoup，iOS 端使用 Ktor/Ktorfit+Kotlin.Serialized+Ksoup，只将 Repo 变成公共接口，在两端分别实现
6. 剩余业务逻辑层无法统一的，分别两端实现

### 预期时间
目前 91000+ 行代码，秋招后开始，乐观预计2~3个月左右（不算前期铺垫），但完成度几乎很高