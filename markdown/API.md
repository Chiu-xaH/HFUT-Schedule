# 合肥工业大学 常用API文档
💡 暂时缓更一些接口（转专业相关），期末周

供给校内同学学习和参考，如有变化及其补充，欢迎修改

这个文档是2024-11-23~24完成的，此时聚在工大已经运营一年有余，好多登陆的接口细节己经忘记了，登录还是比较麻烦的，主要是需要获取若干参数，如果你是一名开发者，可以clone下安卓源代码，用Android Studio构建一下，接口定义在logic/network/api，接口实现函数在那几个viewmodel里，UI层通过vm.XXX函数调用接口，使用Observer+LiveData监听实时返回结果，处理数据在类似getXXX(vm)的函数中，(由于我早期开发经验不足，viewmodel堆积了大量复用代码，也许后面有时间会封装起来)

# 思维导图

# 主体内容

## CAS统一认证 https://cas.hfut.edu.cn/

### execution的获取 & Cookie中JESSIONID、SESSIONID 获取
@GET cas/login
#### 请求
Query["service"] 这里看你要登录那个平台，例如 
```
教务系统 http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login
智慧社区 https://community.hfut.edu.cn/ 
信息门户 空白默认登录信息门户
```
#### 响应
Body(HTML) 检索execution，拿到所需参数
Header["Set-Cookie"] 拿到JESSIONID、SESSIONID

### LOGIN_FLAVORING的获取
@GET cas/checkInitParams
#### 请求
无
##### 响应
Header["Set-Cookie"] 提取JESSIONID

### 登录 教务系统/信息门户/智慧社区

#### 由已经登陆过的Cookie进行跳转
@GET cas/login

##### 请求
Header["Cookie"] Cookie分为三个，JESSIONID、SESSIONID、LOGIN_FLAVORING，都在上面操作获取到了

Header["User-Agent"] 必须附带电脑或手机UA 否则返回异常

Query["service"] 这里看你要登录那个平台，例如 
```
教务系统 http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login
智慧社区 https://community.hfut.edu.cn/
信息门户 空白默认登录信息门户
```

##### 响应
Header["Location"] 若末尾存在ticket=XXX 拿到ticket保存，后面会用，不存在说明Cookie过期或无效

#### 密码账号登录
@POST cas/login
##### 请求
Header["Cookie"] Cookie分为三个，JESSIONID、SESSIONID、LOGIN_FLAVORING，都在上面操作获取到了

Header["User-Agent"] 必须附带电脑或手机UA 否则返回异常

Query["service"] 这里看你要登录那个平台，例如 
```
教务系统 http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login

智慧社区 https://community.hfut.edu.cn/

信息门户 默认为空就是信息门户

指尖工大 
```

Form(表单)
```
"username"="20XXXXXXX" 学号

"password"="XXX" 密码 由Cookie中的LOGIN_FLAVORING作为Key(获取方式见上面)，对用户输入密码进行AES加密得到的字符串

"execution"="e1s1" eXSY X与Y只能为Int整数，代表第X次尝试刷新Y次，获取方式见上面

"_eventId"="submit" 固定字符串 不用动
```
##### 响应
Header["Location"] 若末尾存在ticket=XXX 拿到ticket保存，后面会用，不存在说明Cookie过期或无效

### 登录 指尖工大/信息门户
#### 由已经登陆过的Cookie进行跳转
@GET cas/oauth2.0/authorize?response_type=code
##### 请求
Header["Cookie"] Cookie分为三个，JESSIONID、SESSIONID、LOGIN_FLAVORING，都在上面操作获取到了

Header["User-Agent"] 必须附带电脑或手机UA 否则返回异常

Query["client_id"]

Query["redirect_uri"]

这两个参数看你要登录那个平台，例如 
```
指尖工大 client_id=Hfut2023Ydfwpt&redirect_uri=http://121.251.19.62/berserker-auth/cas/oauth2url?oauth2url=http://121.251.19.62/berserker-base/redirect?appId=24&type=app
信息门户 client_id=BsHfutEduPortal&redirect_uri=https://one.hfut.edu.cn/home/index
```
##### 响应
Header["Location"] 若末尾存在ticket=XXX 拿到ticket保存，后面会用，不存在说明Cookie过期或无效
#### 密码账号登录
@POST cas/oauth2.0/authorize?response_type=code
##### 请求
Header["Cookie"] Cookie分为三个，JESSIONID、SESSIONID、LOGIN_FLAVORING，都在上面操作获取到了

Header["User-Agent"] 必须附带电脑或手机UA 否则返回异常

Query["client_id"]

Query["redirect_uri"]

这两个参数看你要登录那个平台，例如 
```
指尖工大 client_id=Hfut2023Ydfwpt&redirect_uri=http://121.251.19.62/berserker-auth/cas/oauth2url?oauth2url=http://121.251.19.62/berserker-base/redirect?appId=24&type=app
信息门户 client_id=BsHfutEduPortal&redirect_uri=https://one.hfut.edu.cn/home/index
```

Form(表单)
```
"username"="20XXXXXXX" 学号

"password"="XXX" 密码 由Cookie中的LOGIN_FLAVORING作为Key(获取方式见上面)，对用户输入密码进行AES加密得到的字符串

"execution"="e1s1" eXSY X与Y只能为Int整数，代表第X次尝试刷新Y次，获取方式见上面

"_eventId"="submit" 固定字符串 不用动
```
##### 响应
Header["Location"] 若末尾存在ticket=XXX 拿到ticket保存，后面会用，不存在说明Cookie过期或无效

## 教务系统 http://jxglstu.hfut.edu.cn/eams5-student/
注意：建议Header["User-Agent"]带上手机或电脑UA
### 登录(不用CAS统一认证)
教务系统登录默认密码为"Hfut@#$%"+身份证号后六位，这里无论有没有X都时后六位，并且X要大写
#### 第一步
@GET login-salt
##### 请求
无
##### 响应
Header["Set-Cookie"] 保存Cookie中的SESSION=XXX...

Body(TEXT) 响应体就是一串"-"分割的字符串，保存它
###### 加密
明文 = 上面保存的字符串 + "-" + "用户密码" 

SHA1加密明文，得到密文，保存留下一步用

这里推荐[锤子工具](https://www.toolhelper.cn/)，里面加解密工具全面

##### 第二步
@POST login
##### 请求
Header["Cookie"] SESSION=XXXX.. 上面拿到过

Body(Raw)(JSON) 
```json
{
  "username" : "20XXXXXXXX" ,//学号，必须以字符串形式提交
  "password" : "密文",//上一步拿到过
  "captcha" : "" //固定字符串
}
```
##### 响应
Body(JOSN) 从result值判断是否登陆成功，true成功后，代表Cookie生效（教务系统的Cookie只有3h有效期），使用这段Cookie可使用各个接口
```json
{
    "result": true,
    "needCaptcha": false
}
```

### 登录(通过CAS统一认证)
上面CAS认证登录后，如果成功会302重定向，其中响应头Header["Location"]的地址(类似于http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login?ticket=ST-XXXX...
)，用该地址发送GET请求，响应头Header["Set-Cookie"]的Cookie保存下来，作为这教务系统的Cookie

@GET neusoft-sso/login
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...
#### 响应
响应如果302重定向到教务系统主页(http://jxglstu.hfut.edu.cn/eams5-student/home)，说明此Cookie已经生效(有效期3h)，附带此Cookie使用教务接口即可

### 获取学生ID
@GET for-std/course-table
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...
#### 响应
Header["Location"]里有一个地址类似/eams5-student/for-std/course-table/info/,最后接的数字就是学生ID，一般为6位数字，保存它，它是身份的凭证

如果不是302重定向或者重定向到登陆界面了，说明Cookie不对或者过期

### 课程汇总(而且获取课程表之前需要获取这个)
@GET for-std/course-table/get-data
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Query["bizTypeId"] 级数 学号前两位

Query["semesterId"] 学期代号 下面细说

Query["dataId"] 学生ID 上面有获取

##### 学期代号
```
...
234 代表 2023-2024 第一学期
254 代表 2023-2024 第二学期
274 代表 2024-2025 第一学期
...
尾数4固定，只看前两位，23-25-27，每学期数值相差2，且都是奇数，用你们扎实的数学知识写一个处理函数，即可，下面是解析学期代号的函数，转换学期代号的函数自己发挥吧 
```
上代码
```Kotlin
fun parseSemseter(semster : Int) : String {
  val codes = (semster - 4) / 10
  val year = 2017
  val code = 3

  var upordown = 0
  if(codes % 4 == 1) {
    upordown = 2
  } else if(codes % 4 == 3) {
    upordown = 1
  }

  val years= (year + (codes - code) / 4) + 1
  return years.toString() +  "~" + (years + 1).toString() + "年第" +  upordown + "学期"
}
```

#### 响应
Body(JSON) 
很长的JSON，里面有课程汇总数据，自行选择有用的保存，但是为了课程表，所有课程的lessonIds必须保存
```json
{
  "lessonIds" : [
    XXXXXX,XXXXXX,...//Int列表
  ],
  ...
}
```

### 课程表
@POST ws/schedule-table/datum
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

还记得最开头说过的建议UA加电脑或手机嘛，这里Header再加一个

Header["Content-Type"] "application/json"

Body(Raw)(JSON) 
```json
[
  XXXXXX,XXXXXX,XXXXXX,....
]
```
上面课程汇总拿到的lessonIds们，提交的ID才会显示在课程表上，别落下了
#### 响应
Body(JSON) 非常大的JSON，自己解析去吧

### 个人信息
@GET for-std/student-info/info/ + 学生ID
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

#### 响应
Body(HTML) HTML需要解析，这个我就留个Kotlin Jsoup代码吧，其他语言解析也差不多，记住索引号
```Kotlin
val doc = Jsoup.parse(response)

val userName = doc.select("li.list-group-item.text-right:contains(学号) span")?.last()?.text() //学号
val name = doc.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text() //名字
val chineseID = doc.select("li.list-group-item.text-right:contains(证件号) span")?.last()?.text() //身份证号
val elements = doc.select("dl dt, dl dd")

val infoMap = mutableMapOf<String, String>()
if (elements != null) {
    for (i in 0 until elements.size step 2) {
        val key = elements[i].text()
        val value = elements[i+1].text()
        infoMap[key] = value
    }
}

val type =infoMap[elements?.get(8)?.text()] //本科/研究生
val school =infoMap[elements?.get(10)?.text()] //院系
val major =infoMap[elements?.get(12)?.text()] //专业
val classes =infoMap[elements?.get(16)?.text()] //班级
val campus =infoMap[elements?.get(18)?.text()] //校区
val home =infoMap[elements?.get(80)?.text()] //生源地
val status =infoMap[elements?.get(20)?.text()] //学籍状态 正常 转专业 休学 ...
val program =infoMap[elements?.get(22)?.text()] //培养方案标题
val startDate = infoMap[elements?.get(38)?.text()] //入学时间
val endDate = infoMap[elements?.get(86)?.text()] //预计毕业时间
val majorDirection = infoMap[elements?.get(14)?.text()] //专业方向
val studyTime = infoMap[elements?.get(36)?.text()]//学制 4.0
```

### 培养方案
@GET for-std/program/root-module-json/ + 学生ID
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

#### 响应
Body(JSON) JSON特别大，几乎7~9MB，数据量比较庞大，但是都是嵌套结构，解析起来有迹可循

### 成绩
@GET for-std/grade/sheet/info/ + 学生ID
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Query["semester"] 学期代号，上面说过学期代号构成方式，可空，如果不提交(null)就查所有成绩，否则查固定学期

### 考试
@GET for-std/exam-arrange/info/ + 学生ID
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

### 开课查询
@GET for-std/lesson-search/semester/ + 学期代号 + /search/ + 学生ID
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Query["nameZhLike"] 班级名称 可空，空则默认都查 严格按照个人信息或者课程汇总的班级名字填，例如要搜"计算机23-X"而不是"计科23-X"

Query["queryPage__"] 页数 默认第1页

Query["courseNameZhLike"] 课程名称 可空，空则默认都查
#### 响应
Body(JSON) 此接口响应与课程汇总JSON格式完全一致，两者可以共用解析方法

### 获取教评列表
@GET for-std/lesson-survey/ + 学期代号 + /search/ + 学生ID
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...
#### 响应
Body(JSON) 解析并保存每个老师对应teacherId，教评会用


### 获取某个老师教评内容
@GET for-std/lesson-survey/start-survey/ + 上面保存的teacherId + /get-data
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Query["REDIRECT_URL"] "/for-std/lesson-survey/semester-index/"+ 学生ID

这个REDIRECT_URL参数必须有

#### 响应
Header["Set-Cookie"] 保存Cookie，提交教评时需要这个Cookie

Body(JSON) 这里面就是教评题目及其选项，自行解析

### 提交教评
@POST for-std/lesson-survey/submit-survey
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX... + ";" + 上面新的专属教评Cookie(反正就是拼接他们两个，用;隔开)
Body(Raw)(JSON) 提交教评答案，这里直接上代码
```Kotlin
//goodMode为true代表一键好评，false为差评
fun postResult(vm: LoginSuccessViewModel,goodMode: Boolean): JsonObject {
  val surveyAssoc = getSurveyAssoc(vm)
  val lessonSurveyTaskAssoc = prefs.getInt("teacherID", 0)
  val choiceList = getSurveyChoice(vm)
  val inputList = getSurveyInput(vm)
  val choiceNewList = mutableListOf<radioQuestionAnswer>()
  val inputNewList = mutableListOf<blankQuestionAnswer>()

  for (i in choiceList.indices) {
      val id = choiceList[i].id
      // 默认拿第一个选项为好评，拿最后一个为差评
      val option = if(goodMode) choiceList[i].options[0].name else choiceList[i].options.last().name
      choiceNewList.add(radioQuestionAnswer(id, option))
  }

  for (j in inputList.indices) {
      val id = inputList[j].id
      inputNewList.add(blankQuestionAnswer(id, "好"))
  }

  // 组装数据
  val postSurvey = PostSurvey(surveyAssoc, lessonSurveyTaskAssoc, choiceNewList, inputNewList)
    
  return Gson().toJsonTree(postSurvey).asJsonObject
}
```
这里面JSON需要按上面获取教评题目响应JSON写，具体不展开说了，抓包看看或者看我的源代码

### 学籍照
@GET students/avatar/ + 学生ID
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

### 选课操作验证(必须做，否则选课入口无法获取)
@GET for-std/course-select
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...
#### 响应
不需要管响应是什么，验证过就行

### 获取选课入口
@POST ws/for-std/course-select/open-turns
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Form
```
"bizTypeId"= 级数 学号前两位,
"studentId"= 学生ID,
```
#### 响应
Body(JSON)
```json
[
  {
    "id" : XXXXX, //id要记住，进入某个选课入口需要用，并且最后选/退课作为courseID
    "name" : "标题",
    "bulletin" : "选课说明",
    "selectDateTimeText" : "时间",
    "addRulesText" : [
      "XXXXX","XXXXX",.... //选课规则
    ]
  },...
]
```

### 获取选课列表
@POST ws/for-std/course-select/addable-lessons
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Form
```
"turnId"= 选课入口ID,上面保存了
```
#### 响应
Body(JSON)
```json
[
  {
    "id" : XXXXX, //这个是lessonId，要记住，获取选课人数和选退看都要用
    ...
  },...
]
```
其他参数按需获取吧，或者看我源代码

### 某门课选课人数
@POST ws/for-std/course-select/std-count
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Form
```
"lessonIds[]"= Int整数，传入lessonID，从选课列表响应JSON获取
```

#### 响应
Body(JSON)
```json
{ "" : XX }
键值对，<lessonID (String) :人数 (Int)> 
```

### 选/退课

#### 第一步 获取RequestID
@POST ws/for-std/course-select/ + 类型 + -request

类型 add 选课 drop 退课

##### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Form
```
"studentAssoc"= 学生ID,
"lessonAssoc"= lessonID,
"courseSelectTurnAssoc"= courseID，这个值是最初进入选课入口，响应的id,见[获取选课入口]
```

##### 响应
Body(TEXT) 直接响应体就是RequestID，保存它

#### 第二步 提交
@POST ws/for-std/course-select/add-drop-response
##### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Form
```
"studentId"= 学生ID,
"requestId"= 上面获取的RequestID
```

##### 响应
Body(JSON)
```json
{
  "success" : Boolean, //true为成功 false失败
  "errorMessage" : String? //可空，响应反馈
}
```

### 获取已经选的课
@POST ws/for-std/course-select/selected-lessons
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Form
```
"studentId"= 学生ID,
"turnId"= courseID，这个值是最初进入选课入口，响应的id,见[获取选课入口]
```
有人肯定好奇turnId的作用，我无论用哪个courseID获取到都是所有已经选的课，但是但是，你在退课的时候只能退courseID对应的入口所包含的课程；

这么理解吧，就是你传选修课板块入口courseID，这是从列表中退必修课就无法退，只能退选修课板块的课

#### 响应
Body(JSON) 与获取选课列表响应一致，可以共用解析方法


### 我的档案(个人信息的补充)
@GET my/profile
#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

### 转专业申请列表
@GET for-std/change-major-apply/get-applies

#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Query["auto"] Boolean 是否启用自动筛选符合申请要求的专业(没什么用,默认false吧)

Query["batchId"] 1 合肥校区 ，3 宣城校区 

Query["studentId"] 学生ID

### 我的转专业申请
@GET for-std/change-major-apply/get-my-applies

#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

Query["batchId"] 1 合肥校区 ，3 宣城校区 

Query["studentId"] 学生ID


### 我的转专业状态
@GET for-std/change-major-apply/info/2162
#### 请求
Query["studentId"] 学生ID
#### 响应
响应为HTML网页，需要自行按XML解析

### 申请/撤销转专业
这里我忘记做了，因为我就是要转专业的，不敢随便来回选退，很遗憾，现在也没机会再做了，只有大一学弟学妹才能做了

### 培养方案完成情况 1.0(只有数据信息)
@GET ws/student/home-page/programCompletionPreview

此接口响应较慢，正常现象

#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

### 培养方案完成情况 2.0(课程完成情况+培养方案)
@GET for-std/program-completion-preview/json/ + 学生ID

#### 请求
Header["Cookie"] 上面得到的Cookie，类似SESSION=XXX...

#### 响应
Body(JSON) JSON特别大，几乎7~9MB，数据量比较庞大，但是都是嵌套结构，解析起来有迹可循，可以与[培养方案]共用解析方法

注意：此接口获取的JSON每个课程与[培养方案]相比，体现了完成情况，其中"resultType"需要注意：PASSED 通过/TAKING 本学期开课(在修)/UNREPAIRED 本学期不开课(未安排)

outer 代表培养方案以外，即多修课程，转专业废弃的课程也在这里
```json
{
    "completionSummary": {
        "passedCourseNum": 17,
        "failedCourseNum": 0,
        "takingCourseNum": 17,
        "passedCredits": 30,
        "failedCredits": 0,
        "takingCredits": 30.75
    },
    "outerCompletionSummary": {
        "passedCourseNum": 10,
        "failedCourseNum": 0,
        "takingCourseNum": 0,
        "passedCredits": 26,
        "failedCredits": 0,
        "takingCredits": 0
    },
    "moduleList": [
        {
            "nameZh": "公共基础课程",
            "requireInfo": {
                "credits": 41,
                "courseNum": 13
            },
            "completionSummary": {
                "passedCourseNum": 2,
                "failedCourseNum": 0,
                "takingCourseNum": 4,
                "skipCourseNum": 0,
                "passedCredits": 7.5,
                "failedCredits": 0,
                "takingCredits": 9
            },
            "allCourseList": [
                {
                    "code": "1400071B",
                    "nameZh": "线性代数",
                    "credits": 2.5,
                    "terms": [
                        "TERM_1"
                    ],
                    "resultType": "PASSED/TAKING/UNREPAIRED",
                    "score": 79,
                    "rank": null,
                    "gp": 3
                }
            ]
        }
    ],
    "outerCourseList": [
        {
            "code": "0500011B",
            "nameZh": "大学计算机基础",
            "credits": 1,
            "resultType": "PASSED",
            "score": 91,
            "rank": null,
            "gp": 4
        }
    ]
}
```


## 智慧社区 https://community.hfut.edu.cn/

### 登录
@GET api/sys/cas/client/validateLogin?service=https://community.hfut.edu.cn/
Query["ticket"] 从CA统一认证的响应获取，详细见[CAS统一认证]

### 平均成绩
@GET api/business/score/querytotalscore
#### 请求
Header[X-Access-Token] String

### 成绩
@GET api/business/score/scoreselect
#### 请求
Header["X-Access-Token"] token登录口令

Query["xn"] 学年 必须是 "20XX-20XY" 只能20XX+1=20XY ，因为一年才有2个学期，例如"2023-2024"

Query["xq"] 学期 "1"上学期 "2"下学期

### 图书检索
@GET api/business/book/search
#### 请求
Header["X-Access-Token"] token登录口令

Query["name"] 检索内容

Query["pageNo"] 第几页 默认为"1"

Query["pageSize"] 一次请求获取数目 自行决定 推荐"20"

### 考试(这个接口原来挺好用的，后来抽风了，基本到了当周，考试才会出现)
@GET api/business/examarrangement/listselect
#### 请求
Header["X-Access-Token"] token登录口令

### 正在借阅书籍
@GET api/business/book/lending/lendingList
#### 请求
Header["X-Access-Token"] token登录口令

Query["name"] 检索内容 默认空字符串"" 默认状态检索所有借阅书籍

Query["pageNo"] 第几页 默认为"1"

Query["pageSize"] 一次请求获取数目 自行决定 推荐"20"

### 历史借阅书籍
@GET api/business/book/lending/historyList
#### 请求
Header["X-Access-Token"] token登录口令

Query["name"] 检索内容 默认空字符串"" 默认状态检索所有历史书籍

Query["pageNo"] 第几页 默认为"1"

Query["pageSize"] 一次请求获取数目 自行决定 推荐"20"

### 逾期归还书籍
@GET api/business/book/lending/overdueList
#### 请求
Header["X-Access-Token"] token登录口令

Query["name"] 检索内容 默认空字符串"" 默认状态检索所有逾期书籍

Query["pageNo"] 第几页 默认为"1"

Query["pageSize"] 一次请求获取数目 自行决定 推荐"20"

### 课程表&好友课表
@GET api/business/coursearrangement/listselect
#### 请求
Header["X-Access-Token"] token登录口令

Query["username"] 学号 为空null时获得自己的课程表 为学号时获得好友的课程表

### 今天
@GET api/mobile/community/homePage/today
#### 请求
Header["X-Access-Token"] token登录口令

### 查看好友(用于好友课表)
@GET api/business/coursefriendapply/list
#### 请求
Header["X-Access-Token"] token登录口令

### 查看有谁申请想查看我的课表
@GET api/business/coursefriendapply/apgelist
#### 请求
Header["X-Access-Token"] token登录口令

### 申请添加好友
@POST api/business/coursefriendapply/add
#### 请求
Header["X-Access-Token"] token登录口令

Body(Raw)(JSON) 
```json
{ "applyUserId" : "20XXXXXXXX" } 
```
必须以字符串传入学号

### 打开/关闭好友课表 接受/不接受好友申请
@POST api/business/sys/user/usingfriendcourse
#### 请求
Header["X-Access-Token"] token登录口令

Body(Raw)(JSON)
```json
{ "friendEnabled" : 1或0 }
```
必须以Int整数传入，1即启用好友课表，0即关闭好友课表

### 同意好友申请 共享自己的课表
@POST api/business/coursefriendapply/edit
#### 请求
Header["X-Access-Token"] token登录口令

Body(Raw)(JSON) 
```json
        {
            "id" : "20XXXXXXXX",
            "status" : 1或0 
        } 
```
必须以字符串传入学号；必须以Int整数传入，1即启用好友课表，0即关闭好友课表

## 指尖工大(慧新易校) http://121.251.19.62/

### 一卡通基本信息(余额、卡号、限额等)
@GET berserker-app/ykt/tsm/getCampusCards?synAccessSource=h5
#### 请求
Header["synjones-auth"] 登录口令

### 一卡通流水账单
@GET berserker-search/search/personal/turnover?synAccessSource=h5
#### 请求
Header["synjones-auth"] 登录口令

Query["current"] 页数 默认第1页

Query["size"] 一次请求的数量 默认推荐30

### 一卡通月流水
@GET berserker-search/statistics/turnover/sum/user?dateType=month&statisticsDateStr=day&type=2&synAccessSource=h5
#### 请求
Header["synjones-auth"] 登录口令

Query["dateStr"] YYYY-MM 只能提交此形式字符串

### 一卡通从timeFrom到timeTo的流水总额
@GET berserker-search/statistics/turnover/count?synAccessSource=h5
#### 请求
Header["synjones-auth"] 登录口令

Query["timeFrom"] YYYY-MM-DD 只能提交此形式字符串

Query["timeTo"] YYYY-MM-DD 只能提交此形式字符串

### 一卡通流水检索
@GET berserker-search/search/personal/turnover?highlightFieldsClass=text-primary&synAccessSource=h5
#### 请求
Header["synjones-auth"] 登录口令

Query["info"] 检索内容

Query["current"] 页数 默认第1页

Query["size"] 一次请求的数量 默认推荐30

### 修改限额(修改了没有用，实际刷卡消费多了还要输密码)
@POST berserker-app/ykt/tsm/modifyAcc
#### 请求
Header["synjones-auth"] 登录口令
Body(Raw)(JSON)
```json
{
  "account" : "XXXXX", //卡号，上面获取一卡通信息时可拿到，一般为五位数字，提交以字符串提交
  "autotransFlag" : "2", //固定字符
  "autotransAmt" : X00, //限额1 限额数目*100之后以Int整数提交，*100转化元为分
  "autotransLimite" : X00, //限额2 限额数目*100之后以Int整数提交，*100转化元为分
  "synAccessSource" : "h5" //固定字符
}
```

### 查询网费、电费、洗浴(宣城校区)
合肥校区应该大同小异，猜测就是feeitemid不同？自己在慧新易校抓包找找

@POST charge/feeitem/getThirdData
#### 请求
Header["synjones-auth"] 登录口令

Query["type"] "IEC"
##### 网费
Query["feeitemid"] 281

Query["level"] 0
##### 电费
Query["feeitemid"] 261

Query["rooms"] 寝室号 30DDXXXYZ DD号楼XXX寝室 Y=1为南楼 Y=2为北楼 Z=0为照明 Z=1为空调
##### 洗浴
Query["feeitemid"] 223

Query["level"] 1

Query["telPhone"] 手机号
#### 响应
Body(JSON)
```
{
  "success" : true,
  "data" : {
      ...
  }
}
```
保存data，下面有用

### 支付
@POST blade-pay/pay
#### 第一次
##### 请求
Header["synjones-auth"] 登录口令

Query["feeitemid"] 281 网费 ，261 电费，223 洗浴

Query["flag"] "choose" 固定字符

Query["paystep"] 0 代表第一步

Query["tranamt"] 支付金额

Query["third_party"] 提交JSON字符串，将查询费用接口得到的响应JSON，拿到["data"]键对应内容，在里面添加["myCustomInfo"]="房间：${寝室号}"，寝室号规则不再细说
```json
这是查询费用得到的响应，直接拿"data"的value部分
{
  "success" : true,
  "data" : {
      ...
  }
}
也就是这段
{
  ...
}
再添加键值对
{
  ...,
  "myCustomInfo" : "按上面要求填写"
}
作为字符串提交给third_party即可
```
##### 响应
Body(JSON)
```json
{
  "success" : true,
  "data" : {
    "orderid" : "XXXXX..."
  }
}
```
保存orderid，第二步会用
#### 第二次
##### 请求
Header["synjones-auth"] 登录口令

Query["feeitemid"] 261 没写错，无论洗浴、网费、电费都用261，这里意义不同

Query["paytype"] "CARDTSM" 固定字符

Query["paystep"] 2 代表第二步 没写错，就是0->2，中间没有1

Query["orderid"] 第一步拿到的orderid

Query["paytypeid"] 101 固定数字

##### 响应
Body(JSON)
```json
{
  "success" : true,
  "data" : {
    "passwordMap" : {
      "XXXXXX一串密文" : "XXXXXXXXXX" 
    }
  }
}
```
键为一串密文，作为第三步uuid

值为十个数字，0-9唯一且都出现，作为第三步密码映射

键值对都需要保存
#### 第三次
##### 请求
Header["synjones-auth"] 登录口令

Query["feeitemid"] 261 没写错，无论洗浴、网费、电费都用261，这里意义不同

Query["paytype"] "CARDTSM" 固定字符

Query["paystep"] 2 代表第三步 没写错，就是0->2->2

Query["orderid"] 第一步拿到的orderid

Query["paytypeid"] 101 固定数字

Query["isWX"] 0 固定数字

Query["password"] 用户输入的密码经过映射，得到的新密文，具体下面细说

Query["uuid"] 第二步保存的键 也就是那串密文

###### 密码映射
看Kotlin函数吧，懒得讲了
```Kotlin
//密码映射
fun getPsk(key : String) : String {
  var result = ""
  val psk = getCardPsk() //用户卡密码
  if (psk != null) {
//////映射思路/////////////////////////////////////////////
      for(i in psk.indices) {
          for(j in key.indices) {
              if(key[j] == psk[i]) {
                  result = result.plus(j) //plus追加字符串，相当于Python的join
              }
          }
      }
///////////////////////////////////////////////////////////
  }
  return result
}
//根据身份证号获取用户原来的密码，初始密码为身份证号后6位，若最后为X，则向前取，即"...123456X"时取"123456"，而"...123456"时取"123456"
fun getCardPsk() : String? {
  //...略，这个函数就是字符串处理
}
```

##### 响应
Body(JSON)
```json
{
  "msg" : "这里就会告诉结果"
}
```
将"msg"的值显示给用户即可

## 信息门户 https://one.hfut.edu.cn/

### 借阅书籍数目
@GET api/operation/library/getBorrowNum
#### 请求
Header["Authorization"] 登陆后获取，有效期大约24h

### 预约书籍数目
@GET api/operation/library/getSubscribeNum
#### 请求
Header["Authorization"] 登陆后获取，有效期大约24h

### 一卡通余额
@GET api/operation/thirdPartyApi/schoolcard/balance
#### 请求
Header["Authorization"] 登陆后获取，有效期大约24h

### 获取各个楼建筑的building_code
@GET api/operation/emptyClass/build
#### 请求
Header["Authorization"] 登陆后获取，有效期大约24h

Query["campus_code"]  01 屯溪路校区  02 翡翠湖  03 宣城

### 用building_code查空教室
@GET api/operation/emptyClass/room?current=1&size=30
#### 请求
Header["Authorization"] 登陆后获取，有效期大约24h

Query["building_code"]

### 欠缴学费
@GET api/leaver/third/finance/arrearsForPortal?type=1
#### 请求
Query["xh"] 学号

这里我没写错，不需要Authorization，随时都可查看

### 获取Authorization
@GET api/auth/oauth/getToken?type=portal
#### 请求
这个接口我不太记得了，大概应该是接上面CAS统一认证后ticket吧

Query["redirect"]

Query["code"] 

### 获取校园邮箱的跳转URL
@GET api/msg/mailBusiness/getLoginUrl
#### 请求
Header["Authorization"] 登陆后获取，有效期大约24h

Query["mail"] AES ECB模式，密钥为Cookie中的secert值，明文为邮箱20XXXXXXXX@mail.hfut.edu.cn

#### 逆向过程
F12 检索API关键字"getLoginUrl"，打断点向前分析堆栈，就能找出调用Encrycpt的地方，这里用调试台打印一下密钥、原文，学校的平台确实容易逆向
#### 响应
```json
{
  "msg": "success",
  "data": "访问链接即可直接免登录打开邮箱"
}
```


## 洗浴-呱呱物联 https://guagua.klcxkj-qzxy.cn/

### 登录
@POST user/login
#### 请求
Form
```
"telPhone"=手机号
"password"=用户密码经过MD5加密后10位字符，注意MD5密文英文必须为大写字母
"typeId"=0 固定字符
```
#### 响应
Body(JSON) 里面有个人信息、洗浴余额、loginCode等

### 账单
@GET wallet/billLis
#### 请求
Form
```
"telPhone"=手机号,
"loginCode"=登录获取过,
"typeId"=0 固定字符,
"curNum"=当前页，默认1,
"beginDate"="YYYY-MM-DD HH:MM:SS" 起始时间
"endDate"="YYYY-MM-DD HH:MM:SS" 结束时间
```

### 开始洗浴
@POST order/downRate/snWater
#### 请求
Form
```
"telPhone"=手机号,
"loginCode"=登录获取过,
"deviceSncode"=MAC地址
"appId"="10010" 固定字符
```

### 用户信息
@GET user/info
#### 请求
Query["telPhone"] 手机号

Query["loginCode"] 登录获取过

### 使用码
@GET user/useCode
#### 请求
Query["telPhone"] 手机号

Query["loginCode"] 登录获取过

### 修改使用码
@POST user/useCode/set
#### 请求
Form(表单)
```
"telPhone"=手机号,
“password”=用户密码经过MD5加密后10位字符，注意MD5密文英文必须为大写字母,
"loginCode"=登录获取过,
"securityCode"=新使用码 必须为5位数字
"appId"="10010" 固定字符
```

## 校园网-宣城 http://172.18.3.3/  http://172.18.2.2/

有两个地址，多数情况下 图书馆和宿舍为3.3，敬亭、新安学堂为2.2

### 登录
@POST 0.htm 或 a30.htm
#### 请求
FORM 
```
"DDDDD"=学号,
"upass"=密码 无任何加密 直接提交密码，
"0MKKey"="宣州Login" 固定字符
```

### 登陆后获取流量、余额信息
@GET a31.htm
#### 请求
无

### 注销
@GET F.htm
#### 请求
无

## 服务大厅 http://172.31.248.26:8988/

### 电费查询(宣城)
@POST web/Common/Tsm.html

#### 请求
Form 
```
"jsondata"="{...}",
"funname"="synjones.onecard.query.elec.roominfo" 固定字符串
"json"=true或false 布尔值
```
jsondata提交JSON格式化字符串，
```json
{
  "query_elec_roominfo": {
    "aid": "0030000000007301",
    "account": "XXXXX",//卡号，用谁的都行，只要合理，获取卡号很简单，随便抓包或者去指尖工大拿
    "room": {
      "roomid": "30XXXXXXX",//房间ID号
      "room": "30XXXXXXX"
    },
    "floor": {
      "floorid": "",
      "floor": ""
    },
    "area": {
      "area": "",
      "areaname": ""
    },
    "building": {
      "buildingid": "",
      "building": ""
    },
    "extdata": "info1="
  }
}
```
currentnum 为Int整数，第X页

## 乐跑云运动 http://210.45.246.53:8080/

由于3.0加密了JSON，我提供一下解密思路：

云运动为360加固，使用frida-dexdump进行脱壳，获取若干classes.dex

反编译classes.dex们，检索"encrypt"查找加密函数，识别关键字确定加密方式为RSA、AES，RSA需要公钥、私钥，AES需要Key

继续检索getPublicKey、getPrivateKey，不断追根溯源，追查到这两个函数最初定义是通过JNI，JNI连接Native C++库，那么这附近必定指定存在库的入口的字符串，通过识别找到"crs-sdk"

反编译云运动APK，进入so目录查找，果然有libcrs-sdk.so文件

使用IDA Pro反汇编此文件，继续检索，得到公钥、私钥，可以解密了

至于接口，自己去抓包解密搞吧

### 获取已跑里程和打卡点经纬度、健跑要求
@POST run/getHomeRunInfo
#### 请求

Header["isApp"] "app"

Header["token"] 口令

## 新闻 https://news.hfut.edu.cn/

### 新闻检索
@POST zq_search.jsp?wbtreeid=1137
#### 请求
Form 
```
"sitenewskeycode"="检索内容",
"currentnum"=X 
```
currentnum 为Int整数，第X页

## WEBVPN https://webvpn.hfut.edu.cn/

### 获取ticket
@GET login?cas_login=true
#### 请求
无
#### 响应
Body(TEXT) "wengine_vpn_ticketwebvpn_hfut_edu_cn=XXX..." 这段字符串保存下来，作为后面获取Key操作的Cookie

### 向这个接口附带Header[Cookie]发送
@GET http/77726476706e69737468656265737421f3f652d22f367d44300d8db9d6562d/cas/checkInitParams
#### 请求
Header["Cookie"] 上面[获取ticket]拿到的字符串 "wengine_vpn_ticketwebvpn_hfut_edu_cn=XXX..."

作用：向这个接口附带Header[Cookie]发送，让服务器认为这个cookie即将用来登录，登陆成功后这个cookie可以作为登陆凭证（本步骤必不可缺）

### 获取LOGIN_FLAVORING，作为AES加密Key
@GET wengine-vpn/cookie?method=get&host=cas.hfut.edu.cn&scheme=http&path=/cas/login
#### 请求
Header["Cookie"]上面[获取ticket]拿到的字符串 "wengine_vpn_ticketwebvpn_hfut_edu_cn=XXX..."

### 以WEBVPN登录
@POST http/77726476706e69737468656265737421f3f652d22f367d44300d8db9d6562d/cas/login
#### 请求
Query["service"] 这里看你要登录那个平台，例如 
```
教务系统 http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login
智慧社区 https://community.hfut.edu.cn/
信息门户 空白默认登录信息门户
```

Form(表单)
```
"username"="20XXXXXXX" 学号

"password"="XXX" 密码 由LOGIN_FLAVORING作为Key(获取方式见上面)，对用户输入密码进行AES加密得到的字符串

"execution"="e1s1" eXSY X与Y只能为Int整数，代表第X次尝试刷新Y次，获取方式调用CAS统一认证里的[获取execution]即可

"_eventId"="submit" 固定字符串 不用动
```
##### 响应
通过Body与Code判断是否成功，成功进行下一步

### 发送Cookie字符串给服务器，使cookie生效
@GET http/77726476706e69737468656265737421faef469034247d1e760e9cb8d6502720ede479/eams5-student/neusoft-sso/login
#### 请求
Header["Cookie"]上面[获取ticket]拿到的字符串 "wengine_vpn_ticketwebvpn_hfut_edu_cn=XXX..."

不需要接受响应，就是为了让服务器认得这个cookie

### 使用WEBVPN
接下来使用生效的Cookie，按下面的地址，接上教务系统的接口，用法与原接口完全一致

教务系统 https://webvpn.hfut.edu.cn/http/77726476706e69737468656265737421faef469034247d1e760e9cb8d6502720ede479/eams5-student/

只是地址不同，接口完全相同

## 寝室评分-宣城校区 http://39.106.82.121/

### 评分
@POST query/getStudentScore
#### 请求
FORM "student_code"="XDYYY" 寝室号

X号楼 ,D只能为 N/S ，代表 北楼/南楼,YYY房间号

## 通知公告-宣城校区 https://xc.hfut.edu.cn/

### 获取通知公告 
@GET 1955/list.htm

#### 请求
无
#### 响应
HTML，需自行解析，参考Kotlin代码

### 检索通知公告

@POST _web/_search/api/searchCon/create.rst?_p=YXM9MiZ0PTE0NTcmZD0zODcxJnA9MiZmPTEmbT1TTiZ8Ym5uQ29sdW1uVmlydHVhbE5hbWU9MS0m

看一下JS代码，都是用Base64处理的

#### 请求
Form seachInfo=Base64编码后的JSON
```json
[
    {
        "field": "pageIndex",
        "value": 1 //页数，默认第一页
    },
    {
        "field": "group",
        "value": 0
    },
    {
        "field": "searchType",
        "value": ""
    },
    {
        "field": "keyword",
        "value": "物理" //关键词检索
    },
    {
        "field": "recommend",
        "value": "1"
    },
    {
        "field": 4,
        "value": ""
    },
    {
        "field": 5,
        "value": ""
    },
    {
        "field": 6,
        "value": ""
    },
    {
        "field": 7,
        "value": ""
    }
]
```

#### 响应
Body(JSON) 很迷惑的响应，居然给一堆HTML，自己用Jsoup解析一下吧
```json
{ "data": "一堆HTML" }
```


## 教师主页 https://faculty.hfut.edu.cn/
### 检索教师 
@GET system/resource/tsites/advancesearch.jsp

#### 请求
Query["teacherName"] 直接提交检索文本，可选参数

Query["pagesize"] 一次请求数量，可选参数

Query["pageindex"] 页码，可选参数

Query["showlang"] 语言，可选参数 推荐加"zh_CN"否则会给英文主页

Query["searchDirection"] 研究方向，可选参数

...参数有好多，自己去[教师高级检索](https://faculty.hfut.edu.cn/search.jsp?urltype=tree.TreeTempUrl&wbtreeid=1011)抓包分析吧
#### 响应
```json
{
    "teacherData": [
        {
            "name": "名字",
            "url": "http://faculty.hfut.edu.cn/{英文姓名}/zh_CN/index.htm",//这个URL是教师主页
            "picUrl": "/_resources/group1/M00/00/03/rB_XXX.png"//这个URL前面加API是教师图片
        }
    ]
}
```


## 校务行 https://xwx.gzzmedu.com:9080/

登录需要微信的code，无法脱离微信使用，不过在有cookie前提下可以脱离，自行抓包分析吧

### 获取图片验证码
@POST openPage/getImageCode
#### 请求
无

### 登录
@POST api/login/user
#### 请求
Body(Raw)(JSON)
```json
{
  "schoolCode": XXXXXXXXXX,//整数 4000011112宣城， 4134010359 合肥
  "userId": "AES加密",//加密下面细说
  "code": "XXX...",//微信API生成的，就因为它无法脱离微信使用
  "loginType": 2,
  "password": "AES加密",//加密下面细说
  "imageCode": "XXXX" //图片验证码
}
```
加密函数，直接上代码
```Kotlin
//微信小程序校务行逆向，提取JavaScript代码中的密钥和偏移
@RequiresApi(Build.VERSION_CODES.O)
fun encryptXiaoWuXing(plainText: String): String {
  val key = "JL$<&*l9~67?:#5p" //密钥
  val iv = "{g;,9~l4'/sw`885" //偏移

  val keyBytes = key.toByteArray(Charsets.UTF_8)
  val ivBytes = iv.toByteArray(Charsets.UTF_8)

  val keySpec = SecretKeySpec(keyBytes, "AES")
  val ivSpec = IvParameterSpec(ivBytes)

  val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
  cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

  val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

  return java.util.Base64.getEncoder().encodeToString(encryptedBytes)
}
```

#### 响应
Body(JSON)
```json
{
  "errcode":"错误码",
  "errmsg":"错误信息"
}
```
```json
{
  "errcode":"0",
  "result" : {
    ...//个人信息
  }
}
```

## 合工大教务 https://jwglapp.hfut.edu.cn/

登录需要微信的code，无法脱离微信使用，意味着不能给用户做这个功能了，毕竟不是人人都是开发者，在有cookie前提下可以脱离，自行抓包分析吧

这个接口的特色功能是查询其他专业培养方案、查看课程同班同学、查看教室课表，其他的都是教务的二手数据，没什么价值

## 第二课堂 https://dekt.hfut.edu.cn/

登录需要微信的code，无法脱离微信使用，意味着不能给用户做这个功能了，毕竟不是人人都是开发者，在有cookie前提下可以脱离，自行抓包分析吧

## 学工系统(今日校园) http://stu.hfut.edu.cn/

登录的参数比较多，估计要JS逆向好好看看，有兴趣的自己分析一下

## 智慧后勤 http://xcfw.hfut.edu.cn/school/

登录的参数比较多，估计要JS逆向好好看看，有兴趣的自己分析一下