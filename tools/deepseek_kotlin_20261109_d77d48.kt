data class Enrollment(
    val studentId: Int,
    val trainingType: String,    // 培养类型
    val department: String,      // 专业院系
    val major: String,           // 专业
    val majorDirection: String,  // 专业方向
    val educationLevel: String,  // 学历层次
    val studentCategory: String, // 学生类别
    val status: String           // 学籍状态
)

@JvmStatic
private fun parseEnrollments(html: String): List<Enrollment> {
    val document = Jsoup.parse(html)

    val list = document.select(".student-panel-block").mapNotNull { block ->
        val id = block.selectFirst("button[value]")
            ?.attr("value")?.trim()
            ?.takeIf { it.matches(Regex("""\d+""")) }
            ?.toIntOrNull() ?: return@mapNotNull null

        val fields = parseDl(block.selectFirst("dl"))

        Enrollment(
            studentId = id,
            trainingType = fields["培养类型"].orEmpty(),
            department = fields["专业院系"].orEmpty(),
            major = fields["专业"].orEmpty(),
            majorDirection = fields["专业方向"].orEmpty(),
            educationLevel = fields["学历层次"].orEmpty(),
            studentCategory = fields["学生类别"].orEmpty(),
            status = fields["学籍状态"].orEmpty()
        )
    }

    if (list.isEmpty()) error("未找到StudentId，请联系开发者")
    return list
}

private fun parseDl(dl: Element?): Map<String, String> {
    if (dl == null) return emptyMap()
    val result = LinkedHashMap<String, String>()
    val children = dl.children()
    var i = 0
    while (i < children.size - 1) {
        val dt = children[i]
        val dd = children[i + 1]
        if (dt.tagName() == "dt" && dd.tagName() == "dd") {
            result[dt.text().trim()] = dd.text().trim()
            i += 2
        } else {
            i++
        }
    }
    return result
}