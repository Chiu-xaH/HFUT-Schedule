package com.hfut.schedule.ui.screen.report

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.theme.AppTheme
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

enum class TermReportExportModule(val title: String) {
    ACADEMIC_REPORT("学业报表"),
    ACADEMIC_ANALYSIS("学业分析"),
    EXPENSE_ANALYSIS("消费分析"),
    LIBRARY("图书馆报告"),
    LIFE("生活报告")
}

enum class TermReportExportAction {
    SAVE_TO_GALLERY,
    SHARE
}

data class GraduationInfo(
    val startYear: Int,
    val graduationYear: Int,
    val totalSemesters: Int
)

fun detectGraduation(semesters: List<Int>): GraduationInfo? {
    if (semesters.size < 7) return null

    val years = semesters.mapNotNull { sem ->
        val text = SemesterParser.parseSemester(sem) ?: return@mapNotNull null
        val match = Regex("""(\d+)~(\d+)""").find(text) ?: return@mapNotNull null
        match.groupValues[1].toIntOrNull()
    }.distinct().sorted()

    if (years.size < 4) return null

    val startYear = years.first()
    val graduationYear = startYear + 4

    return GraduationInfo(
        startYear = startYear,
        graduationYear = graduationYear,
        totalSemesters = semesters.size
    )
}

@Composable
fun TermReportExportContent(
    vm: NetWorkViewModel,
    semester: Int,
    modules: Set<TermReportExportModule>,
    isGraduating: Boolean = false,
    allSemesters: List<Int> = emptyList()
) {
    val periodLabel = if (isGraduating) "四年" else "本学期"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = CARD_NORMAL_DP)
        ) {
            if (TermReportExportModule.ACADEMIC_REPORT in modules) {
                AcademicReportSection(
                    vm = vm,
                    semester = semester,
                    onLatestSemester = {}
                )
            }
            if (TermReportExportModule.ACADEMIC_ANALYSIS in modules) {
                AcademicAnalysisSection(vm, semester, periodLabel)
            }
            if (TermReportExportModule.EXPENSE_ANALYSIS in modules) {
                ExpenseAnalysisSection(vm, semester, periodLabel)
            }
            if (TermReportExportModule.LIBRARY in modules) {
                LibraryReportSection(vm, periodLabel)
            }
            if (TermReportExportModule.LIFE in modules) {
                LifeReportSection(vm, semester, allSemesters = if (isGraduating) allSemesters else emptyList())
            }
        }
    }
}

suspend fun exportTermReportBitmap(
    activity: Activity,
    vm: NetWorkViewModel,
    semester: Int,
    modules: Set<TermReportExportModule>,
    widthPx: Int,
    maxBitmapBytes: Long = 180L * 1024L * 1024L,
    isGraduating: Boolean = false,
    allSemesters: List<Int> = emptyList()
): Bitmap = withContext(Dispatchers.Main.immediate) {
    require(modules.isNotEmpty()) { "请选择至少一个导出模块" }

    val root = activity.window.decorView as ViewGroup

    val composeView = ComposeView(activity).apply {
        translationX = -100000f
        translationY = -100000f

        layoutParams = ViewGroup.LayoutParams(
            widthPx,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setContent {
            AppTheme {
                TermReportExportContent(
                    vm = vm,
                    semester = semester,
                    modules = modules,
                    isGraduating = isGraduating,
                    allSemesters = allSemesters
                )
            }
        }
    }

    root.addView(composeView)

    try {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            widthPx,
            View.MeasureSpec.EXACTLY
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )

        composeView.measure(widthSpec, heightSpec)

        val measuredWidth = composeView.measuredWidth
        val measuredHeight = composeView.measuredHeight

        require(measuredWidth > 0 && measuredHeight > 0) {
            "报告内容测量失败：width=$measuredWidth, height=$measuredHeight"
        }

        val bitmapBytes = measuredWidth.toLong() * measuredHeight.toLong() * 4L
        require(bitmapBytes <= maxBitmapBytes) {
            "报告过长，预计占用 ${bitmapBytes / 1024 / 1024}MB，已超过限制，请改为分段导出或 PDF"
        }

        composeView.layout(
            0,
            0,
            measuredWidth,
            measuredHeight
        )

        val bitmap = createBitmap(measuredWidth, measuredHeight)

        val canvas = Canvas(bitmap)
        composeView.draw(canvas)

        bitmap
    } finally {
        root.removeView(composeView)
    }
}

suspend fun exportTermReport(
    activity: Activity,
    vm: NetWorkViewModel,
    semester: Int,
    modules: Set<TermReportExportModule>,
    action: TermReportExportAction,
    isGraduating: Boolean = false,
    allSemesters: List<Int> = emptyList()
) = withContext(Dispatchers.IO) {
    val bitmap = exportTermReportBitmap(
        activity = activity,
        vm = vm,
        semester = semester,
        modules = modules,
        widthPx = activity.resources.displayMetrics.widthPixels,
        isGraduating = isGraduating,
        allSemesters = allSemesters
    )

    try {
        val isSingle = modules.size == 1
        val nameSuffix = if (isSingle) "_${modules.first().title}" else ""
        val fileName = "学期报告${nameSuffix}_${System.currentTimeMillis()}"

        when (action) {
            TermReportExportAction.SAVE_TO_GALLERY -> {
                val success = saveBitmapToGallery(activity, bitmap, fileName)
                if (!success) throw IllegalStateException("保存失败")
            }
            TermReportExportAction.SHARE -> {
                shareBitmap(activity, bitmap, fileName)
            }
        }
    } finally {
        bitmap.recycle()
    }
}

fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    name: String
): Boolean {
    val resolver = context.contentResolver

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/HFUT-Schedule"
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val uri = resolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    ) ?: return false

    resolver.openOutputStream(uri)?.use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    } ?: return false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }

    return true
}

fun shareBitmap(
    context: Context,
    bitmap: Bitmap,
    fileName: String
) {
    val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
    val imageFile = File(cacheDir, "$fileName.png")

    imageFile.outputStream().use { output ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "分享学期报告"))
}

tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
