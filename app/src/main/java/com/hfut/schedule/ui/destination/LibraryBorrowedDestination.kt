package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.library.screen.LibraryBorrowedScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.util.language.UiText
import com.xah.common.util.language.res

object LibraryBorrowedDestination : NavDestination() {
    override val key: String = "library_borrowed"
    override val title: UiText = res(R.string.navigation_label_library_borrowed)
    override val icon = R.drawable.book_5

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        LibraryBorrowedScreen(vm)
    }
}