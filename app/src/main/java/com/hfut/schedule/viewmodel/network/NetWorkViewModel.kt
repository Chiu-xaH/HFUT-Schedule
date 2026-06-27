package com.hfut.schedule.viewmodel.network

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.hfut.schedule.logic.enumeration.AdmissionType
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.LibraryItems
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.AcademicNewsResponse
import com.hfut.schedule.logic.model.AcademicType
import com.hfut.schedule.logic.model.AcademicXCType
import com.hfut.schedule.logic.model.AdmissionDetailBean
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.logic.model.AdmissionTokenResponse
import com.hfut.schedule.logic.model.BuildingMapResponseBean
import com.hfut.schedule.logic.model.DepartmentBean
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.model.GithubFolderBean
import com.hfut.schedule.logic.model.GithubIssueBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailBean
import com.hfut.schedule.logic.model.HaiLeNearPositionBean
import com.hfut.schedule.logic.model.HaiLeNearPositionRequestDTO
import com.hfut.schedule.logic.model.HuiXinHefeiBuildingBean
import com.hfut.schedule.logic.model.NewsResponse
import com.hfut.schedule.logic.model.OfficeHallSearchBean
import com.hfut.schedule.logic.model.PayData
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.model.QWeatherWarnBean
import com.hfut.schedule.logic.model.SecondClassActivity
import com.hfut.schedule.logic.model.SupabaseEventOutput
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.model.SupabaseLoginResponse
import com.hfut.schedule.logic.model.TeacherResponse
import com.hfut.schedule.logic.model.WorkSearchResponse
import com.hfut.schedule.logic.model.XuanquResponse
import com.hfut.schedule.logic.model.community.ApplyingLists
import com.hfut.schedule.logic.model.community.AvgResult
import com.hfut.schedule.logic.model.community.BookPositionBean
import com.hfut.schedule.logic.model.community.BorrowRecords
import com.hfut.schedule.logic.model.community.BusBean
import com.hfut.schedule.logic.model.community.DormitoryBean
import com.hfut.schedule.logic.model.community.DormitoryScoreBean
import com.hfut.schedule.logic.model.community.DormitoryWeeklyScores
import com.hfut.schedule.logic.model.community.DormitoryUser
import com.hfut.schedule.logic.model.community.FailRateRecord
import com.hfut.schedule.logic.model.community.GradeAllResult
import com.hfut.schedule.logic.model.community.GradeJxglstuDTO
import com.hfut.schedule.logic.model.community.GradeResult
import com.hfut.schedule.logic.model.community.LibRecord
import com.hfut.schedule.logic.model.community.MapBean
import com.hfut.schedule.logic.model.community.StuAppBean
import com.hfut.schedule.logic.model.community.TodayResult
import com.hfut.schedule.logic.model.huixin.BillMonth
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.MyApplyResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramBean
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramListBean
import com.hfut.schedule.logic.model.jxglstu.ProgramResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.model.jxglstu.SelectCourse
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.model.jxglstu.SurveyResponse
import com.hfut.schedule.logic.model.jxglstu.TransferResponse
import com.hfut.schedule.logic.model.jxglstu.forStdLessonSurveySearchVms
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.model.library.BorrowedStatus
import com.hfut.schedule.logic.model.library.LibraryBorrowedBean
import com.hfut.schedule.logic.model.library.LibrarySearchBean
import com.hfut.schedule.logic.model.library.LibraryStatus
import com.hfut.schedule.logic.model.one.BuildingBean
import com.hfut.schedule.logic.model.one.ClassroomBean
import com.hfut.schedule.logic.model.uniapp.UniAppBuildingBean
import com.hfut.schedule.logic.model.uniapp.UniAppClassmatesBean
import com.hfut.schedule.logic.model.uniapp.UniAppClassroomLessonBean
import com.hfut.schedule.logic.model.uniapp.UniAppEmptyClassroomBean
import com.hfut.schedule.logic.model.uniapp.UniAppGradeBean
import com.hfut.schedule.logic.model.uniapp.UniAppSearchClassroomBean
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramBean
import com.hfut.schedule.logic.model.wx.WXClassmatesBean
import com.hfut.schedule.logic.model.wx.WXPersonInfoBean
import com.hfut.schedule.logic.model.zhijian.ZhiJianCourseItemDto
import com.hfut.schedule.logic.network.repo.GithubRepository
import com.hfut.schedule.logic.network.repo.QWeatherRepository
import com.hfut.schedule.logic.network.repo.SupabaseRepository
import com.hfut.schedule.logic.network.repo.CasLoginRepository
import com.hfut.schedule.logic.network.repo.CommunityRepository
import com.hfut.schedule.logic.network.repo.GuaGuaRepository
import com.hfut.schedule.logic.network.repo.HuiXinRepository
import com.hfut.schedule.logic.network.repo.JxglstuRepository
import com.hfut.schedule.logic.network.repo.LibraryRepository
import com.hfut.schedule.logic.network.repo.LoginSchoolNetRepository
import com.hfut.schedule.logic.network.repo.SchoolNetSelfRepository
import com.hfut.schedule.logic.model.schoolnet.SchoolNetMonthPayResult
import com.hfut.schedule.logic.model.schoolnet.SchoolNetSemesterUsageResult
import com.hfut.schedule.logic.network.repo.NewsRepository
import com.hfut.schedule.logic.network.repo.OneRepository
import com.hfut.schedule.logic.network.repo.OthersRepository
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.hfut.schedule.logic.network.repo.WxRepository

import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.model.HaiLeDeviceDetailRequest
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.JxglstuExam
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.ChangeMajorInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.MyApplyInfoBean
import com.hfut.schedule.ui.screen.home.search.function.one.mail.MailResponse
import com.hfut.schedule.ui.screen.home.search.function.other.life.FloorMap
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.parsePatch
import com.xah.forecast.model.network.BillBean
import com.xah.forecast.model.result.TotalResult

class NetWorkViewModel() : ViewModel() {
    val studentId = UiStateHolder<Int>()
    suspend fun getStudentId(cookie : String) = JxglstuRepository.getStudentId(cookie,studentId)

    //bizTypeId不是年级数！  //dataId为学生ID  //semesterId为学期Id，例如23-24第一学期为234
    val bizTypeIdResponse = UiStateHolder<Int>()
    suspend fun getBizTypeId(cookie: String,studentId : Int) = JxglstuRepository.getBizTypeId(cookie,studentId,bizTypeIdResponse)

    suspend fun checkJxglstuCanUse() = JxglstuRepository.checkJxglstuCanUse()

    val wxLoginResponse = UiStateHolder<String>()
    suspend fun wxLogin() = WxRepository.wxLogin(wxLoginResponse)

    val wxPersonInfoResponse = UiStateHolder<WXPersonInfoBean>()
    suspend fun wxGetPersonInfo(auth : String) = WxRepository.wxGetPersonInfo(auth,wxPersonInfoResponse)

    val wxClassmatesResponse = UiStateHolder<WXClassmatesBean>()
    suspend fun wxGetClassmates(auth : String) = onListenStateHolderForNetwork(wxPersonInfoResponse,wxClassmatesResponse) { person ->
        WxRepository.wxGetClassmates(person.orgId,auth,wxClassmatesResponse)
    }

    val wxLoginCasResponse = UiStateHolder<Pair<String, Boolean>>()
    suspend fun wxLoginCas(auth : String,url : String) = WxRepository.wxLoginCas(url,auth,wxLoginCasResponse)

    val wxConfirmLoginResponse = UiStateHolder<String>()
    suspend fun wxConfirmLogin(auth : String,uuid : String) = WxRepository.wxConfirmLogin(uuid,auth,wxConfirmLoginResponse)

    val haiLeNearPositionResp = UiStateHolder<List<HaiLeNearPositionBean>>()
    suspend fun getHaiLeNearPosition(bean : HaiLeNearPositionRequestDTO) = OthersRepository.getHaiLeNear(bean,haiLeNearPositionResp)

    val giteeApkSizeResp = UiStateHolder<Double>()
    suspend fun getGiteeApkSize(version : String) = GithubRepository.getUpdateFileSize("$version.apk",giteeApkSizeResp)
    val giteePatchSizeResp = UiStateHolder<Double>()
    suspend fun getGiteePatchSize(patch: Patch) = GithubRepository.getUpdateFileSize(parsePatch(patch),giteePatchSizeResp)

    val haiLeDeviceDetailResp = UiStateHolder<List<HaiLeDeviceDetailBean>>()
    suspend fun getHaiLeDeviceDetail(bean : HaiLeDeviceDetailRequest) = OthersRepository.getHaiLDeviceDetail(bean,haiLeDeviceDetailResp)

    val secondClassActivitiesResp = UiStateHolder<List<SecondClassActivity>>()
    suspend fun getSecondClassActivities(cookie: String, page: Int) = OthersRepository.getSecondClassActivities(cookie,page,secondClassActivitiesResp)

    val githubStarsData = UiStateHolder<Int>()
    suspend fun getStarNum() = GithubRepository.getStarNum(githubStarsData)

    val githubFolderResp = UiStateHolder<List<GithubFolderBean>>()
    suspend fun getUpdateContents() = GithubRepository.getUpdateContents(githubFolderResp)

    val githubIssuesResp = UiStateHolder<List<GithubIssueBean>>()
    suspend fun getIssues(page : Int) = GithubRepository.getIssues(page,githubIssuesResp)

    val githubBuildingMapsResp = UiStateHolder<List<BuildingMapResponseBean>>()
    suspend fun getBuildingMaps() = GithubRepository.getBuildingMaps(githubBuildingMapsResp)

    val githubFloorXmlResp = UiStateHolder<FloorMap>()
    suspend fun getFloorXml(filename : String) = GithubRepository.getFloorXml(filename,githubFloorXmlResp)

    val workSearchResult = UiStateHolder<WorkSearchResponse>()
    suspend fun searchWorks(keyword: String?, page: Int = 1,type: Int,campus: CampusRegion) = OthersRepository.searchWorks(keyword,page,type,campus,workSearchResult)

    val supabaseTodayVisitResp = UiStateHolder<Int>()
    val supabaseUserCountResp = UiStateHolder<Int>()

    suspend fun getTodayVisit() = SupabaseRepository.getTodayVisit(supabaseTodayVisitResp)
    suspend fun getUserCount() = SupabaseRepository.getUserCount(supabaseUserCountResp)

    val supabaseRegResp = MutableLiveData<String?>()
    fun supabaseReg(password: String) = SupabaseRepository.supabaseReg(password, supabaseRegResp)

    val supabaseLoginResp = UiStateHolder<SupabaseLoginResponse>()
    suspend fun supabaseLoginWithPassword(password : String) = SupabaseRepository.supabaseLoginWithPassword(password,supabaseLoginResp)
    suspend fun supabaseLoginWithRefreshToken(refreshToken : String) = SupabaseRepository.supabaseLoginWithRefreshToken(refreshToken,supabaseLoginResp)

    val supabaseDelResp = UiStateHolder<Boolean>()
    suspend fun supabaseDel(jwt : String,id : Int) = SupabaseRepository.supabaseDel(jwt,id,supabaseDelResp)

    val supabaseAddResp = MutableLiveData<Pair<Boolean,String?>?>()
    fun supabaseAdd(jwt: String,event : SupabaseEventOutput) = SupabaseRepository.supabaseAdd(jwt,event, supabaseAddResp)

    val supabaseAddCountResp = MutableLiveData<Boolean?>()
    fun supabaseAddCount(jwt: String,eventId : Int) = SupabaseRepository.supabaseAddCount(jwt,eventId, supabaseAddCountResp)

    // 默认 展示未过期日程&&符合自己班级的日程
    val supabaseGetEventsResp = MutableLiveData<String?>()
    fun supabaseGetEvents() = SupabaseRepository.supabaseGetEvents(supabaseGetEventsResp)

    private val _eventForkCountCache = mutableStateMapOf<Int, String>()
    val eventForkCountCache: Map<Int, String> get() = _eventForkCountCache
    fun supabaseGetEventForkCount(jwt: String, eventId: Int) = SupabaseRepository.supabaseGetEventForkCount(jwt,eventId,_eventForkCountCache)

    val supabaseGetEventCountResp = UiStateHolder<String?>()
    suspend fun supabaseGetEventCount(jwt: String) = SupabaseRepository.supabaseGetEventCount(jwt,supabaseGetEventCountResp)

    val supabaseGetEventLatestResp = UiStateHolder<Boolean>()
    suspend fun supabaseGetEventLatest(jwt: String) = SupabaseRepository.supabaseGetEventLatest(jwt,supabaseGetEventLatestResp)

    // 定制 展示自己上传过的日程
    val supabaseGetMyEventsResp = UiStateHolder<List<SupabaseEventsInput>>()
    suspend fun supabaseGetMyEvents() = SupabaseRepository.supabaseGetMyEvents(supabaseGetMyEventsResp)

    val supabaseCheckResp = UiStateHolder<Boolean>()
    suspend fun supabaseCheckJwt(jwt: String) = SupabaseRepository.supabaseCheckJwt(jwt,supabaseCheckResp)

    val supabaseUpdateResp = UiStateHolder<Boolean>()
    suspend fun supabaseUpdateEvent(jwt: String, id: Int, body : Map<String,Any>) = SupabaseRepository.supabaseUpdateEvent(jwt,id,body,supabaseUpdateResp)

    val admissionTokenResp = UiStateHolder<AdmissionTokenResponse>()
    suspend fun getAdmissionToken() = OthersRepository.getAdmissionToken(admissionTokenResp)

    val admissionListResp = UiStateHolder<Pair<AdmissionType,Map<String, List<AdmissionMapBean>>>>()
    suspend fun getAdmissionList(type: AdmissionType) = OthersRepository.getAdmissionList(type,admissionListResp)

    val admissionDetailResp = UiStateHolder<AdmissionDetailBean>()
    suspend fun getAdmissionDetail(type : AdmissionType,bean : AdmissionMapBean,region: String) = OthersRepository.getAdmissionDetail(type,bean,region,admissionDetailResp,admissionTokenResp)

    val programList = UiStateHolder<List<ProgramListBean>>()
    suspend fun getProgramList(campus : CampusRegion) = GithubRepository.getProgramList(campus,programList)

    fun getMyApi() = GithubRepository.getMyApi()

    val programSearchData = UiStateHolder<ProgramSearchBean>()
    suspend fun getProgramListInfo(id : Int,campus : CampusRegion) = GithubRepository.getProgramListInfo(id,campus,programSearchData)

    fun downloadHoliday()  = GithubRepository.downloadHoliday()

    val postTransferResponse = UiStateHolder<String>()
    suspend fun postTransfer(cookie: String, batchId: String, id : String, phoneNumber : String) = JxglstuRepository.postTransfer(cookie,batchId,id,phoneNumber,studentId,postTransferResponse)

    val fromCookie = UiStateHolder<String>()
    suspend fun getFormCookie(cookie: String, batchId: String, id : String) = JxglstuRepository.getFormCookie(cookie,batchId,id,studentId,fromCookie)

    // 响应 为 302代表成功 否则为失败
    val cancelTransferResponse = UiStateHolder<Boolean>()
    suspend fun cancelTransfer(cookie: String, batchId: String, id : String) = JxglstuRepository.cancelTransfer(cookie,batchId,id,studentId,cancelTransferResponse)

    suspend fun postUser() = SupabaseRepository.postUser()

// 选课 ////////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun verify(cookie: String) = JxglstuRepository.verify(cookie)

    val selectCourseData = UiStateHolder<List<SelectCourse>>()
    suspend fun getSelectCourse(cookie: String) = JxglstuRepository.getSelectCourse(cookie,studentId, bizTypeIdResponse, selectCourseData)

    val selectCourseInfoData = UiStateHolder<List<SelectCourseInfo>>()
    suspend fun getSelectCourseInfo(cookie: String, id : Int) = JxglstuRepository.getSelectCourseInfo(cookie,id,selectCourseInfoData)

    val stdCountData = MutableLiveData<String?>()
    fun getSCount(cookie: String,id : Int) = JxglstuRepository.getSCount(cookie,id, stdCountData)

    val requestIdData = UiStateHolder<String>()
    suspend fun getRequestID(cookie: String, lessonId : Int, courseId : Int, type : String) = JxglstuRepository.getRequestID(cookie,lessonId,courseId,type,studentId,requestIdData)

    val selectedData = UiStateHolder<List<SelectCourseInfo>>()
    suspend fun getSelectedCourse(cookie: String, courseId : Int) = JxglstuRepository.getSelectedCourse(cookie,courseId,studentId,selectedData)

    val selectResultData = UiStateHolder<Pair<Boolean, String>>()
    suspend fun postSelect(cookie: String,requestId : String) = JxglstuRepository.postSelect(cookie,requestId,studentId,selectResultData)

    suspend fun checkLibraryNetwork() = LibraryRepository.checkLibraryNetwork()
// 转专业 ////////////////////////////////////////////////////////////////////////////////////////////////
    val transferData = UiStateHolder<TransferResponse>()
    suspend fun getTransfer(cookie: String,batchId: String) = JxglstuRepository.getTransfer(cookie,batchId,studentId,transferData)

    val transferListData = UiStateHolder<List<ChangeMajorInfo>>()
    suspend fun getTransferList(cookie: String) = JxglstuRepository.getTransferList(cookie,studentId,transferListData)

    val myApplyData = UiStateHolder<MyApplyResponse>()
    suspend fun getMyApply(cookie: String,batchId: String) = JxglstuRepository.getMyApply(cookie,batchId,studentId,myApplyData)

    val myApplyInfoData = UiStateHolder<MyApplyInfoBean>()
    suspend fun getMyApplyInfo(cookie: String, listId: Int) = JxglstuRepository.getMyApplyInfo(cookie,listId, studentId,myApplyInfoData)
// 新闻 ////////////////////////////////////////////////////////////////////////////////////////////////
    val newsResult = UiStateHolder<List<NewsResponse>>()
    suspend fun searchNews(title : String,page: Int = 1) = NewsRepository.searchNews(title,page,newsResult)

    val newsXuanChengResult = UiStateHolder<List<NewsResponse>>()
    fun searchXuanChengNews(title : String, page: Int = 1) = NewsRepository.searchXuanChengNews(title,page)
    suspend fun getXuanChengNews(page: Int) = NewsRepository.getXuanChengNews(page,newsXuanChengResult)

    val academicResp = UiStateHolder<AcademicNewsResponse>()
    suspend fun getAcademicNews(type: AcademicType, page: Int = 1,totalPage : Int? = null) = NewsRepository.getAcademic(type,totalPage,page,academicResp)

    val academicXCResp = UiStateHolder<List<NewsResponse>>()
    suspend fun getAcademicXCNews(type: AcademicXCType, page: Int = 1) = NewsRepository.getAcademicXC(type,page,academicXCResp)
// 核心业务 ////////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun gotoCommunity(cookie : String) = CasLoginRepository.gotoCommunity(cookie)
    suspend fun gotoZhiJian(cookie : String) = CasLoginRepository.gotoZhiJian(cookie)
    suspend fun gotoSecondClass(cookie : String) = CasLoginRepository.gotoSecondClass(cookie)
    suspend fun gotoLibrary(cookie : String) = CasLoginRepository.gotoLibrary(cookie)
    suspend fun goToStu(cookie : String) = CasLoginRepository.goToStu(cookie)
    suspend fun goToPe(cookie : String) = CasLoginRepository.goToPe(cookie)

    val checkStuLoginResp = UiStateHolder<Boolean>()
    suspend fun checkStuLogin(cookie : String) = OthersRepository.checkStuLogin(cookie,checkStuLoginResp)

    val libraryStatusResp = UiStateHolder<LibraryStatus>()
    suspend fun getLibraryStatus(token : String) = LibraryRepository.getStatus(token,libraryStatusResp)

    val checkLibraryLoginResp = UiStateHolder<Boolean>()
    suspend fun checkLibraryLogin(token : String) = LibraryRepository.checkLibraryLogin(token,checkLibraryLoginResp)

    val libraryBorrowedResp = UiStateHolder<List<LibraryBorrowedBean>>()
    suspend fun getBorrowed(token : String,page : Int,status : BorrowedStatus? = null,pageSize : Int = Constant.DEFAULT_PAGE_SIZE) = LibraryRepository.getBorrowed(token,page,status,pageSize,libraryBorrowedResp)

    val librarySearchResp = UiStateHolder<List<LibrarySearchBean>>()
    suspend fun searchLibrary(keyword : String,page : Int) = LibraryRepository.search(page,keyword,librarySearchResp)

    val loginCommunityData = UiStateHolder<String>()
    suspend fun loginCommunity(ticket : String) = CommunityRepository.loginCommunity(ticket,loginCommunityData)

    val zhiJianCourseResp = UiStateHolder<List<ZhiJianCourseItemDto>>()
    suspend fun getZhiJianCourses(studentId : String, mondayDate : String, token : String) = OthersRepository.getZhiJianCourses(studentId,mondayDate,token,zhiJianCourseResp)

    val zhiJianCheckLoginResp = UiStateHolder<Boolean>()
    suspend fun zhiJianCheckLogin(token : String) = OthersRepository.zhiJianCheckLogin(token,zhiJianCheckLoginResp)

    val jxglstuGradeData = UiStateHolder<List<GradeJxglstuDTO>>()
    suspend fun getGradeFromJxglstu(cookie: String, semester: Int?) = JxglstuRepository.getGradeFromJxglstu(cookie,semester,studentId,jxglstuGradeData)

    fun jxglstuLogin(cookie : String) = JxglstuRepository.jxglstuLogin(cookie)

    val lessonIds = UiStateHolder<lessonResponse>()
    suspend fun getLessonIds(cookie : String,studentId : Int,bizTypeId : Int) = JxglstuRepository.getLessonIds(cookie,studentId,bizTypeId,lessonIds)

    val datumData = UiStateHolder<String>()
    suspend fun getDatum(cookie : String,lessonIdList : List<Int>) = JxglstuRepository.getDatum(cookie,lessonIdList,studentId,datumData)

    suspend fun getInfo(cookie : String) = JxglstuRepository.getInfo(cookie,studentId)

    val lessonTimesResponse = UiStateHolder<List<CourseUnitBean>>()
    suspend fun getLessonTimes(cookie: String,timeCampusId : Int) = JxglstuRepository.getLessonTimes(cookie,timeCampusId,lessonTimesResponse)

    val lessonTimesResponseNext = UiStateHolder<List<CourseUnitBean>>()
    suspend fun getLessonTimesNext(cookie: String,timeCampusId : Int) = JxglstuRepository.getLessonTimes(cookie,timeCampusId,lessonTimesResponseNext)

    val programData = UiStateHolder<ProgramResponse>()
    suspend fun getProgram(cookie: String) = JxglstuRepository.getProgram(cookie,studentId,programData)

    val programCompletionData = UiStateHolder<ProgramCompletionResponse>()
    suspend fun getProgramCompletion(cookie: String) = JxglstuRepository.getProgramCompletion(cookie,programCompletionData)

    val programPerformanceData = UiStateHolder<ProgramBean>()
    suspend fun getProgramPerformance(cookie: String) = JxglstuRepository.getProgramPerformance(cookie,studentId, programPerformanceData)

    val teacherSearchData = UiStateHolder<TeacherResponse>()
    suspend fun searchTeacher(name: String = "", direction: String = "") = OthersRepository.searchTeacher(name = name,direction = direction,teacherSearchData)

    val courseSearchResponse = UiStateHolder<List<lessons>>()
    suspend fun searchCourse(cookie: String, className : String?, courseName : String?, semester : Int, courseId : String?) = JxglstuRepository.searchCourse(cookie,className,courseName,semester,courseId,studentId,courseSearchResponse)

    val surveyListData = UiStateHolder<List<forStdLessonSurveySearchVms>>()
    suspend fun getSurveyList(cookie: String, semester : Int) = JxglstuRepository.getSurveyList(cookie,semester,studentId, surveyListData)

    val surveyData = UiStateHolder<SurveyResponse>()
    suspend fun getSurvey(cookie: String, id : String) = JxglstuRepository.getSurvey(cookie,id,surveyData)

    val surveyToken = UiStateHolder<String>()
    suspend fun getSurveyToken(cookie: String, id : String) = JxglstuRepository.getSurveyToken(cookie,id, studentId,surveyToken)

    suspend fun postSurvey(cookie : String,json: JsonObject) : Int = JxglstuRepository.postSurvey(cookie,json)

    suspend fun getPhoto(cookie : String) = JxglstuRepository.getPhoto(cookie,studentId)

    val courseBookResponse = UiStateHolder<Pair<Int,Map<Long,CourseBookBean>>>()
    suspend fun getCourseBook(cookie: String,semester: Int) = JxglstuRepository.getCourseBook(cookie,semester,studentId,bizTypeIdResponse, courseBookResponse)

    suspend fun goToOne(cookie : String) = CasLoginRepository.goToOne(cookie)
    suspend fun goToHuiXin(cookie : String) = CasLoginRepository.goToHuiXin(cookie)

    val huiXinBillResult = UiStateHolder<BillBean>()
    suspend fun getCardBill(
        auth : String,
        page : Int,
        size : Int = Constant.DEFAULT_PAGE_SIZE
    ) = HuiXinRepository.getCardBill(auth,page,size,huiXinBillResult)

    val huiXinCardInfoResponse = MutableLiveData<String?>()
    fun getHuiXinCardInfo(auth : String) = HuiXinRepository.getHuiXinCardInfo(auth, huiXinCardInfoResponse)

    val huiXinCheckLoginResp = UiStateHolder<Boolean>()
    suspend fun checkHuiXinLogin(auth : String)= HuiXinRepository.checkHuiXinLogin(auth,huiXinCheckLoginResp)

    val checkPeLoginResp = UiStateHolder<Boolean>()
    suspend fun checkPeLogin(cookie : String) = OthersRepository.checkPeLogin(cookie,checkPeLoginResp)

    val checkSecondClassLoginResp = UiStateHolder<Boolean>()
    suspend fun checkSecondClassLogin(cookie : String) = OthersRepository.checkSecondClassLogin(cookie,checkSecondClassLoginResp)
    val huiXinLoginResp = UiStateHolder<String>()
    suspend fun huiXinSingleLogin(studentId : String,password: String) = HuiXinRepository.huiXinSingleLogin(studentId,password,huiXinLoginResp)

    val cardPredictedResponse = UiStateHolder<TotalResult>()
    suspend fun getCardPredicted(auth: String) = HuiXinRepository.getCardPredicted(auth,huiXinBillResult,cardPredictedResponse)

    val infoValue = MutableLiveData<String?>()
    val electricData = MutableLiveData<String?>()
    val showerData = MutableLiveData<String?>()
    val hefeiElectric = MutableLiveData<String?>()
    fun getFee(auth: String,type : FeeType,room : String? = null,phoneNumber : String? = null,building : String? = null) = HuiXinRepository.getFee(auth,type,room,phoneNumber, building,hefeiElectric,infoValue, electricData, showerData)

    /**
     * Suspend API for electric fee queries. Each call gets its own Retrofit Call,
     * so concurrent requests for different rooms cannot interfere.
     *
     * @return response body string (non-null, non-blank)
     * @throws retrofit2.HttpException on HTTP non-2xx
     * @throws java.io.IOException on empty body or read failure
     * @throws Exception on network failure
     */
    suspend fun queryElectricFee(
        auth: String,
        type: FeeType,
        room: String? = null,
        building: String? = null
    ): String = HuiXinRepository.queryFeeRaw(
        auth = auth,
        type = type,
        room = room,
        building = building
    )

    val guaGuaUserInfo = MutableLiveData<String?>()
    fun getGuaGuaUserInfo() = GuaGuaRepository.getGuaGuaUserInfo(guaGuaUserInfo)

    val orderIdData = UiStateHolder<String>()
    suspend fun payStep1(auth: String,json: String,pay : Float,type: FeeType) = HuiXinRepository.payStep1(auth,json,pay,type,orderIdData)

    val uuIdData = UiStateHolder<Map<String, String>>()
    suspend fun payStep2(auth: String,orderId : String,type : FeeType) = HuiXinRepository.payStep2(auth,orderId,type,uuIdData)

    val payResultData = UiStateHolder<String>()
    suspend fun payStep3(auth: String,orderId : String,password : String,uuid : String,type: FeeType) = HuiXinRepository.payStep3(auth,orderId,password,uuid,type,payResultData)

    val changeLimitResponse = UiStateHolder<String>()
    suspend fun changeLimit(auth: String,json: JsonObject) = HuiXinRepository.changeLimit(auth,json,changeLimitResponse)

    val huiXinRangeResult = UiStateHolder<Float>()
    suspend fun searchDate(auth : String, timeFrom : String, timeTo : String) = HuiXinRepository.searchDate(auth,timeFrom,timeTo,huiXinRangeResult)

    val huiXinSearchBillsResult = UiStateHolder<BillBean>()
    suspend fun searchBills(auth : String, info: String,page : Int) = HuiXinRepository.searchBills(auth,info,page,huiXinSearchBillsResult)

    val huiXinMonthBillResult = UiStateHolder<List<BillMonth>>()
    suspend fun getMonthBills(auth : String, dateStr: String) = HuiXinRepository.getMonthBills(auth,dateStr,huiXinMonthBillResult)

    fun loginOne(code : String) = OneRepository.loginOne(code)

    val checkOneLoginResp = UiStateHolder<Boolean>()
    suspend fun checkOneLogin(token : String) = OneRepository.checkOneLogin(token,checkOneLoginResp)

    val buildingsResponse = UiStateHolder<Pair<Campus,List<BuildingBean>>>()
    suspend fun getBuildings(campus : Campus, token : String)  = OneRepository.getBuildings(campus,token,buildingsResponse)

    val classroomResponse = UiStateHolder<List<ClassroomBean>>()
    suspend fun getClassroomInfo(code : String,token : String)  = OneRepository.getClassroomInfo(code,token,classroomResponse)

    val mailData = UiStateHolder<MailResponse>()
    suspend fun getMailURL(token : String)  = OneRepository.getMailURL(token,mailData)

    val hefeiRoomsResp = UiStateHolder<List<HuiXinHefeiBuildingBean>>()
    suspend fun getHefeiRooms(auth : String,building : String) = HuiXinRepository.getHefeiRooms(auth,building,hefeiRoomsResp)

    val hefeiBuildingsResp = UiStateHolder<List<HuiXinHefeiBuildingBean>>()
    suspend fun getHefeiBuildings(auth : String) = HuiXinRepository.getHefeiRooms(auth,null,hefeiBuildingsResp)

    val payFeeResponse = UiStateHolder<PayData>()
    suspend fun getPay() = OneRepository.getPay(payFeeResponse)

    val dormitoryResult = UiStateHolder<List<XuanquResponse>>()
    suspend fun searchDormitoryXuanCheng(code : String) = OthersRepository.searchDormitoryXuanCheng(code,dormitoryResult)

    val failRateData = UiStateHolder<Pair<String?,List<FailRateRecord>>>()
    suspend fun searchFailRate(token : String, name: String, page : Int, code : String?) = CommunityRepository.searchFailRate(token,name,page,code,failRateData)

    val checkCommunityResponse = UiStateHolder<Boolean>()
    suspend fun checkCommunityLogin(token: String) = CommunityRepository.checkCommunityLogin(token,checkCommunityResponse)

    val examResponse = UiStateHolder<List<JxglstuExam>>()
    suspend fun getExamJXGLSTU(cookie: String) = JxglstuRepository.getExam(cookie,studentId,examResponse)

    val gradeFromCommunityResponse = UiStateHolder<GradeResult>()
    suspend fun getGrade(token: String, year : String, term : String) = CommunityRepository.getGrade(token,year,term,gradeFromCommunityResponse)

    val avgData = UiStateHolder<AvgResult>()
    suspend fun getAvgGrade(token: String) = CommunityRepository.getAvgGrade(token,avgData)

    val allAvgData = UiStateHolder<List<GradeAllResult>>()
    suspend fun getAllAvgGrade(token: String) = CommunityRepository.getAllAvgGrade(token,allAvgData)

    val libraryData = UiStateHolder<List<LibRecord>>()
    suspend fun searchBooks(token: String, name: String, page: Int) = CommunityRepository.searchBooks(token,name,page,libraryData)

    val bookPositionData = UiStateHolder<List<BookPositionBean>>()
    suspend fun getBookPosition(token: String,callNo: String) = CommunityRepository.getBookPosition(token,callNo,bookPositionData)

    fun getCoursesFromCommunity(token : String, studentId: String? = null) = CommunityRepository.getCoursesFromCommunity(token,studentId)

    fun openFriend(token : String) = CommunityRepository.openFriend(token)

    val dormitoryFromCommunityResp = UiStateHolder<DormitoryBean>()
    suspend fun getDormitory(token : String) = CommunityRepository.getDormitory(token,dormitoryFromCommunityResp)

    val dormitoryInfoFromCommunityResp = UiStateHolder<List<DormitoryUser>>()
    suspend fun getDormitoryInfo(token : String) = CommunityRepository.getDormitoryInfo(token,dormitoryFromCommunityResp,dormitoryInfoFromCommunityResp)

    val addFriendApplyResponse = UiStateHolder<String>()
    suspend fun addFriendApply(token : String, username : String) = CommunityRepository.addFriendApply(token,username,addFriendApplyResponse)

    val applyFriendsResponse = UiStateHolder<List<ApplyingLists?>>()
    suspend fun getApplying(token : String) = CommunityRepository.getApplying(token,applyFriendsResponse)

    val mapsResponse = UiStateHolder<List<MapBean>>()
    suspend fun getMaps(token : String) = CommunityRepository.getMaps(token,mapsResponse)

    val officeHallSearchResponse = UiStateHolder<List<OfficeHallSearchBean>>()
    suspend fun officeHallSearch(text : String, page : Int) = OthersRepository.officeHallSearch(text,page,officeHallSearchResponse)

    val stuAppsResponse = UiStateHolder<List<StuAppBean>>()
    suspend fun getStuApps(token : String) = CommunityRepository.getStuApps(token,stuAppsResponse)

    val busResponse = UiStateHolder<List<BusBean>>()
    suspend fun getBus(token : String) = CommunityRepository.getBus(token,busResponse)

    val booksChipData = UiStateHolder<List<BorrowRecords>>()
    suspend fun communityBooks(token : String,type : LibraryItems,page : Int = 1) = CommunityRepository.communityBooks(token,type,page,booksChipData)

    val todayFormCommunityResponse = UiStateHolder<TodayResult>()
    suspend fun getToday(token : String) = CommunityRepository.getToday(token,todayFormCommunityResponse)

    fun getFriends(token : String) = CommunityRepository.getFriends(token)

    fun checkApplying(token : String, id : String, isOk : Boolean) = CommunityRepository.checkApplying(token,id,isOk)

    val weatherWarningData = UiStateHolder<List<QWeatherWarnBean>>()
    suspend fun getWeatherWarn(campus: CampusRegion) = QWeatherRepository.getWeatherWarn(campus,weatherWarningData)
    val qWeatherResult = UiStateHolder<QWeatherNowBean>()
    suspend fun getWeather(campus: CampusRegion) = QWeatherRepository.getWeather(campus,qWeatherResult)

    val loginSchoolNetResponse = UiStateHolder<Boolean>()
    suspend fun loginSchoolNet(campus: CampusRegion = getCampusRegion()) = LoginSchoolNetRepository.loginSchoolNet(campus,loginSchoolNetResponse)
    suspend fun logoutSchoolNet(campus: CampusRegion = getCampusRegion()) = LoginSchoolNetRepository.logoutSchoolNet(campus,loginSchoolNetResponse)

    val infoWebValue = UiStateHolder<WebInfo>()
    suspend fun getWebInfo() = LoginSchoolNetRepository.getWebInfo(infoWebValue)
    suspend fun getWebInfo2() = LoginSchoolNetRepository.getWebInfo2(infoWebValue)

    val schoolNetMonthPayResp = UiStateHolder<SchoolNetMonthPayResult>()
    suspend fun loginAndGetSchoolNetMonthPay(year: Int) = SchoolNetSelfRepository.loginAndGetMonthPay(year, schoolNetMonthPayResp)
    suspend fun getSchoolNetMonthPayAfterLogin(year: Int) = SchoolNetSelfRepository.getMonthPayAfterLogin(year, schoolNetMonthPayResp)

    val schoolNetSemesterUsageResp = UiStateHolder<SchoolNetSemesterUsageResult>()
    suspend fun loginAndGetSchoolNetSemesterUsage(semester: Int) = SchoolNetSelfRepository.loginAndGetSemesterUsage(semester, schoolNetSemesterUsageResp)
    suspend fun loginAndGetSchoolNetAllSemestersUsage(allSemesters: List<Int>) = SchoolNetSelfRepository.loginAndGetAllSemestersUsage(allSemesters, schoolNetSemesterUsageResp)

    val giteeUpdatesResp = UiStateHolder<GiteeReleaseResponse>()
    suspend fun getUpdate() = GithubRepository.getUpdate(giteeUpdatesResp)

    val classmatesResp = UiStateHolder<List<UniAppClassmatesBean>>()
    suspend fun getClassmates(lessonId : String,token : String) = UniAppRepository.getClassmates(lessonId,token,classmatesResp)

    val uniAppGradesResp = UiStateHolder<Map<String, List<UniAppGradeBean>>>()
    suspend fun getUniAppGrades(token : String) = UniAppRepository.getGrades(token,uniAppGradesResp)

    val searchProgramsResp = UiStateHolder<List<UniAppSearchProgramBean>>()
    suspend fun searchPrograms(token : String, page : Int , keyword : String) = UniAppRepository.searchPrograms(token,page,keyword,searchProgramsResp)

    val departmentsResp = UiStateHolder<List<DepartmentBean>>()
    suspend fun getDepartments() = OthersRepository.getDepartments(departmentsResp)

    val getProgramByIdResp = UiStateHolder<ProgramSearchBean>()
    suspend fun getProgramById(id : Int, token: String, ) = UniAppRepository.getProgramById(id,token,getProgramByIdResp)

    val dormitoryScoreResp = UiStateHolder<List<DormitoryScoreBean>>()
    suspend fun getDormitoryScore(token : String, week : Int? = null, semester : String? = null) = CommunityRepository.getDormitoryScore(token,week,semester,dormitoryScoreResp)

    val allDormitoryScoresResp = UiStateHolder<DormitoryWeeklyScores>()
    suspend fun getAllDormitoryScores(token : String, semester : String, semesterInt : Int) = CommunityRepository.getAllDormitoryScores(token,semester,semesterInt,allDormitoryScoresResp)

    val uniAppBuildingsResp = UiStateHolder<List<UniAppBuildingBean>>()
    suspend fun getBuildings(token : String) = UniAppRepository.getBuildings(token,uniAppBuildingsResp)

    val uniAppEmptyClassroomsResp = UiStateHolder<List<UniAppEmptyClassroomBean>>()
    suspend fun getEmptyClassrooms(page : Int, date : String, campus: Campus?, buildings : List<Int>?, floors : List<Int>?, token : String, ) = UniAppRepository.getEmptyClassrooms(page,date,campus,buildings,floors,token,uniAppEmptyClassroomsResp)

    val uniAppSearchClassroomsResp = UiStateHolder<List<UniAppSearchClassroomBean>>()
    suspend fun searchClassrooms(input : String, token : String, page : Int) = UniAppRepository.searchClassrooms(input,token,page,uniAppSearchClassroomsResp)

    val uniAppClassroomLessonsResp = UiStateHolder<List<UniAppClassroomLessonBean>>()
    suspend fun getClassroomLessons(semester: Int, roomId : Int, token : String) = UniAppRepository.getClassroomLessons(semester,roomId,token,uniAppClassroomLessonsResp)
}

