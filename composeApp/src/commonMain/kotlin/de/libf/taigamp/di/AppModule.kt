package de.libf.taigamp.di

import de.libf.taigamp.state.Session
import de.libf.taigamp.ui.screens.main.MainViewModel
//import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

//fun inject(mainViewModel: MainViewModel)
//    fun inject(loginViewModel: LoginViewModel)
//    fun inject(dashboardViewModel: DashboardViewModel)
//    fun inject(scrumViewModel: ScrumViewModel)
//    fun inject(epicsViewModel: EpicsViewModel)
//    fun inject(projectSelectorViewModel: ProjectSelectorViewModel)
//    fun inject(sprintViewModel: SprintViewModel)
//    fun inject(commonTaskViewModel: CommonTaskViewModel)
//    fun inject(teamViewModel: TeamViewModel)
//    fun inject(settingsViewModel: SettingsViewModel)
//    fun inject(createTaskViewModel: CreateTaskViewModel)
//    fun inject(issuesViewModel: IssuesViewModel)
//    fun inject(kanbanViewModel: KanbanViewModel)
//    fun inject(profileViewModel: ProfileViewModel)
//    fun inject(wikiSelectorViewModel: WikiListViewModel)
//    fun inject(wikiPageViewModel: WikiPageViewModel)
//    fun inject(wikiCreatePageViewModel: WikiCreatePageViewModel)

val appModule = module {
    single { Session(get()) }
    viewModelOf(::MainViewModel)
}
