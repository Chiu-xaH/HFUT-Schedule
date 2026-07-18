package com.hfut.schedule.logic.model.supabase

import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail

data class SupabaseLoginWithPwdRequest(
    val email : String? = getSchoolEmail(),
    val password : String
)